package com.bornfire.BRF.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import com.bornfire.BRF.controllers.BRFReportsController;

import com.bornfire.BRF.entities.AccessandRolesRepository;

import com.bornfire.BRF.entities.UserAuditLevel_Entity;
import com.bornfire.BRF.entities.UserAuditRepo;

import com.bornfire.BRF.entities.UserProfile;
import com.bornfire.BRF.entities.UserProfileRep;

import com.bornfire.BRF.services.LoginServices;

@Configuration
@EnableWebSecurity
public class BRFWebSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	LoginServices loginServices;

	@Autowired
	SequenceGenerator sequence;
	
	@Autowired
	UserAuditRepo userAuditRepo;

	@Autowired
	AccessandRolesRepository accessRepository;

	private static final Logger logger = LoggerFactory.getLogger(BRFReportsController.class);

	private final Integer SESSION_TIMEOUT_IN_SECONDS = 650000;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/webjars/**", "/images/**", "/login*", "/systemotp", "/verify-otp", "/resetPassword1",
						"/resetPassword", "/freezeColumn/**", "favicon.ico")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.failureHandler(BRFAuthFailHandle()).successHandler(BRFAuthSuccessHandle()).usernameParameter("userid")
				.and().logout().permitAll().and().logout().logoutSuccessHandler(BRFLogoutSuccessHandler()).permitAll()
				.and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);

		http.csrf().disable();

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean

	public AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider ap = new DaoAuthenticationProvider() {

			@Override
			@Transactional
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				String userid = authentication.getName();
				String password = authentication.getCredentials().toString();

				Optional<UserProfile> up = userProfileRep.findById(userid);

				try {

					if (up.isPresent()) {
						UserProfile usr = up.get();
						if (usr.getLog_in_count().equals("0")) {
							throw new CredentialsExpiredException("Password reset required:" + userid);
						} else if (!usr.isCredentialsNonExpired()) {

							throw new CredentialsExpiredException("Credentials Expired:" + userid);

						} else if (!usr.isAccountNonLocked()) {

							throw new LockedException("Account Locked");

						} else if (!usr.isEnabledUser()) {

							throw new LockedException("Account Disabled");

						} else if (!usr.isverifiedUser()) {

							throw new LockedException("Account Not verified");

						} else if (!PasswordEncryption.validatePassword(password, usr.getPassword())) {

							logger.info("Passing Userid :" + userid);

							Session hs = sessionFactory.getCurrentSession();
							Transaction tr = hs.getTransaction();
							hs.createQuery(
									"update UserProfile a set a.no_of_attmp=nvl(a.no_of_attmp,0)+1, a.user_locked_flg=decode(nvl(a.no_of_attmp,0)+1,'3','Y','N'), a.disable_flg=decode(nvl(a.no_of_attmp,0)+1,'3','Y','N') where userid=?1")
									.setParameter(1, userid).executeUpdate();
							tr.commit();
							hs.close();
							throw new BadCredentialsException("Authentication failed");

						} else {

							return new UsernamePasswordAuthenticationToken(userid, password, Collections.emptyList());

						}

					} else {

						throw new UsernameNotFoundException("Invalid User Name");
					}

				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
					authentication.setAuthenticated(false);
				}
				return authentication;

			}

			@Override
			public boolean supports(Class<?> aClass) {
				return aClass.equals(UsernamePasswordAuthenticationToken.class);
			}

		};

		ap.setHideUserNotFoundExceptions(false);
		ap.setUserDetailsService(userDetailsService());

		return ap;
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {

		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

				Optional<UserProfile> up = userProfileRep.findByusername(username);

				if (up.isPresent()) {
					return up.get();
				} else {
					return new UserProfile();
				}

			}

		};
	}

	@Bean
	public AuthenticationFailureHandler BRFAuthFailHandle() {
		return new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				if (exception instanceof CredentialsExpiredException) {
					System.out.println(exception.getMessage().split(":")[1]);
					request.getSession().setAttribute("USERID", exception.getMessage().split(":")[1]);
					request.getSession().setAttribute("PASSWORDERROR", exception.getMessage().split(":")[0]);
					response.sendRedirect("resetPassword1");
				} else {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					logger.info(exception.getMessage());
					response.sendRedirect("login?error=" + exception.getMessage());
				}

			}

		};

	}

	@Bean
	public AuthenticationSuccessHandler BRFAuthSuccessHandle() {

		return new AuthenticationSuccessHandler() {

			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {

				try {

					String auditID = sequence.generateRequestUUId();

					Optional<UserProfile> up = userProfileRep
							.findById(authentication.getName()); /* This gets menu list from DB based on role */

					if (!up.isPresent()) {

						response.sendRedirect("login?error=Invalid User");
						return;
					}

					UserProfile user = up.get();

/*<<<<<<< Updated upstream
					// RESET LOGIN ATTEMPTS
					user.setNo_of_attmp(0);
					user.setUser_locked_flg("N");
=======*/
				String roleId = user.getRole_id();
				System.out.println("THE LOGIN ROLE ID IS " + roleId); 
				
				UserAuditLevel_Entity audit = new UserAuditLevel_Entity();
				LocalDateTime currentDateTime = LocalDateTime.now();
				Date dateValue = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
				audit.setAudit_date(new Date());
				audit.setEntry_time(dateValue);
				audit.setEntry_user(user.getUserid());
				audit.setFunc_code("LOGIN");
				audit.setRemarks("Login Successfully");
				audit.setAudit_table("BRFUSERPROFILETABLE");
				audit.setAudit_screen("LOGIN");
				audit.setEvent_id(user.getUserid());
				audit.setEvent_name(user.getUsername());
				audit.setModi_details("Login Successfully");
				UserProfile auth_user = userProfileRep.getRole(user.getUserid());
				String auth_user_val = auth_user.getAuth_user();
				Date auth_user_date = auth_user.getAuth_time();
				audit.setAuth_user(auth_user_val);
				audit.setAuth_time(auth_user_date);
				audit.setAudit_ref_no(auditID.toString());
				userAuditRepo.save(audit);

					userProfileRep.save(user);

					// SESSION LOGGING
					loginServices.SessionLogging("LOGIN", "M1", request.getSession().getId(), user.getUserid(),
							request.getRemoteAddr(), "ACTIVE");

					// GET MENU LIST
					String menus = accessRepository.getMenulist(user.getRole_id());

					System.out.println("MENUS : " + menus);
					System.out.println("ROLE ID : " + user.getRole_id());

					// SESSION TIMEOUT
					request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);

					// SESSION ATTRIBUTES
					request.getSession().setAttribute("USERID", user.getUserid());

					request.getSession().setAttribute("USERNAME", user.getUsername());

					request.getSession().setAttribute("ROLEID", user.getRole_id());

					request.getSession().setAttribute("DOMAINID", user.getDomain_id());

					request.getSession().setAttribute("PERMISSIONS", user.getPermissions());

					request.getSession().setAttribute("WORKCLASS", user.getWork_class());

					request.getSession().setAttribute("MOBILENUMBER", user.getMob_number());

					request.getSession().setAttribute("ROLEDESC", user.getRole_desc());

					request.getSession().setAttribute("ACCESSCODE", user.getAcct_access_code());

					request.getSession().setAttribute("BRANCHCODE", user.getBranch_code());

					request.getSession().setAttribute("BRANCHNAME", user.getBranch_name());

					// STORE MENU LIST
					request.getSession().setAttribute("MENULIST", menus);

					// GENERATE OTP
					String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

					System.out.println("Your Login OTP is : " + otp);

					// SEND OTP
					loginServices.sendclientotp(otp, user.getRole_id(), user);

					// STORE OTP IN SESSION
					request.getSession().setAttribute("otp", otp);

					System.out.println("THE LOGIN ROLE ID IS : " + user.getRole_id());

					// REDIRECT TO OTP PAGE
					response.sendRedirect("systemotp");

				} catch (Exception e) {

					e.printStackTrace();

					response.sendRedirect("login?error=Authentication Failed");
				}
			}
		};
	}

	@Bean
	public LogoutSuccessHandler BRFLogoutSuccessHandler() {

		return new LogoutSuccessHandler() {

			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {

				System.out.println("onLogoutSuccess called");
				System.out.println("authentication = " + authentication);
				System.out.println(
						"authentication.getName() = " + (authentication != null ? authentication.getName() : "null"));

				if (authentication == null || authentication.getName() == null) {
					System.out.println(
							"Logout request with no authentication - possibly due to session timeout or already logged out.");
					response.sendRedirect("login?logout");
					return;
				}

				try {
					Optional<UserProfile> up = userProfileRep.findById(authentication.getName());

					if (!up.isPresent()) {
						System.out.println("User profile not found for: " + authentication.getName());
						response.sendRedirect("login?logout");
						return;
					}
					UserProfile user1 = up.get();
					String roleId = user1.getRole_id();
					System.out.println("THE LOGOUT ROLE ID IS " + roleId);
					UserProfile user = up.get();
					UserAuditLevel_Entity audit = new UserAuditLevel_Entity();
					String Number1 = sequence.generateRequestUUId();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(user.getUserid());
					audit.setFunc_code("LOGOUT");
					audit.setRemarks("Logout Successfully");
					audit.setAudit_table("BRFUSERPROFILETABLE");
					audit.setAudit_screen("LOGOUT");
					audit.setEvent_id(user.getUserid());
					audit.setEvent_name(user.getUsername());
					UserProfile auth_user = userProfileRep.getRole(user.getUserid());
					String auth_user_val = auth_user.getAuth_user();
					Date auth_user_date = auth_user.getAuth_time();
					audit.setAuth_user(auth_user_val);
					audit.setAuth_time(auth_user_date);
					audit.setModi_details("Logout Successfully");
					audit.setAudit_ref_no(Number1.toString());
					userAuditRepo.save(audit);

				} catch (Exception e) {
					System.out.println("Exception in logout handler: " + e.getMessage());
				}
				response.sendRedirect("login?logout");

			}

		};
	}
}
