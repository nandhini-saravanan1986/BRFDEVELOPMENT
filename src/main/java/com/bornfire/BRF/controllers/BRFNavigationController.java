package com.bornfire.BRF.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bornfire.BRF.config.PasswordEncryption;
import com.bornfire.BRF.entities.AccessAndRoles;
import com.bornfire.BRF.entities.AccessandRolesRepository;
import com.bornfire.BRF.entities.RRReportRepo;
import com.bornfire.BRF.entities.UserProfile;
import com.bornfire.BRF.entities.UserProfileRep;
import com.bornfire.BRF.services.AccessAndRolesServices;
import com.bornfire.BRF.services.LoginServices;
import com.bornfire.BRF.services.MappingAccountService;
import com.bornfire.BRF.services.ReportServices;
import com.sun.xml.messaging.saaj.util.FinalArrayList;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import net.bytebuddy.asm.Advice.Return;

import com.bornfire.BRF.entities.BRFValidations;
import com.bornfire.BRF.entities.BRFValidationsRepo;
import com.bornfire.BRF.entities.RRReport;
import com.bornfire.BRF.services.CalculationService;

@Controller
@ConfigurationProperties("default")
public class BRFNavigationController {

	@Autowired
	LoginServices loginServices;

	@Autowired
	AccessandRolesRepository accessandrolesrepository;

	@Autowired
	AccessAndRolesServices AccessRoleService;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	ReportServices reportServices;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	RRReportRepo rrReportlist;

	@Autowired
	RRReportRepo rrReportRepo;

	@Autowired
	BRFValidationsRepo brfValidationsRepo;
	
	private final MappingAccountService mappingAccountService;
	
	public BRFNavigationController(MappingAccountService mappingAccountService) {
		this.mappingAccountService = mappingAccountService;
	}
	
	@GetMapping("/BaseMappingParam/list")
	@ResponseBody
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "")   String search,
            @RequestParam(defaultValue = "1")  int    page,
            @RequestParam(defaultValue = "20") int    size) {

        // guard: page must be >= 1
        if (page < 1) page = 1;
        if (size < 1) size = 20;

        Map<String, Object> result = mappingAccountService.getBaseMappingParamList(search, page, size);
        return ResponseEntity.ok(result);
    }
	
	@PostMapping("/BaseMappingParam/save")
	@ResponseBody
	public ResponseEntity<String> save(@RequestBody Map<String, String> body) {

	    String result = mappingAccountService.saveBaseMappingParam(body);

	    // Duplicate check
	    if (result.startsWith("Account ID already exists")) {
	        return ResponseEntity.badRequest().body(result);   // 400
	    }

	    // Validation check
	    if (result.contains("required")) {
	        return ResponseEntity.badRequest().body(result);   // 400
	    }

	    // Insert failure (optional but good)
	    if (result.startsWith("Insert failed")) {
	        return ResponseEntity.status(500).body(result);    // 500
	    }

	    // Success
	    return ResponseEntity.ok(result);
	}
	 
		@PutMapping("/BaseMappingParam/update")
		@ResponseBody
	    public ResponseEntity<String> update(@RequestBody Map<String, String> body) {
	        String result = mappingAccountService.updateBaseMappingParam(body);
	        if ("SUCCESS".equals(result)) {
	            return ResponseEntity.ok(result);
	        }
	        return ResponseEntity.badRequest().body(result);
	    }
	
		@DeleteMapping("/BaseMappingParam/delete/{id}")
		@ResponseBody
	    public ResponseEntity<String> delete(@PathVariable("id") String accountId) {
	        String result = mappingAccountService.deleteBaseMappingParam(accountId);
	        if ("SUCCESS".equals(result)) {
	            return ResponseEntity.ok(result);
	        }
	        return ResponseEntity.badRequest().body(result);
	    }
	

	@GetMapping("/systemotp")
	public String showOtpForm() {
		return "BRFOtpvalidation.html";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String userOtp, HttpSession session) {
		String actualOtp = (String) session.getAttribute("otp");
		if (actualOtp != null && actualOtp.equals(userOtp)) {
			session.removeAttribute("otp"); // Clear OTP after success
			return "redirect:/BRFDashboard";
		}
		return "redirect:login?invalidotp";
	}

	@RequestMapping(value = "BRFDashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public String dashboard(@RequestParam(name = "frequency", required = false) String frequency, Model md,
			HttpServletRequest req) {

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		String userid = (String) req.getSession().getAttribute("USERID");
		String Dashboardpage = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		System.out.println("Dashboard page is:" + Dashboardpage);
		System.out.println("Branchcode is : " + BRANCHCODE);
		md.addAttribute("menu", "Dashboard");
		md.addAttribute("checkpassExpiry", loginServices.checkpassexpirty(userid));
		md.addAttribute("checkAcctExpiry", loginServices.checkAcctexpirty(userid));
		md.addAttribute("changepassword", loginServices.checkPasswordChangeReq(userid));

		md.addAttribute("menu", "Dashboard");
		return "BRFDashboard";
	}

	@RequestMapping(value = "AccessandRoles", method = { RequestMethod.GET, RequestMethod.POST })
	public String IPSAccessandRoles(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "ACCESS AND ROLES");
			md.addAttribute("menuname", "ACCESS AND ROLES");
			md.addAttribute("formmode", "list");
			md.addAttribute("AccessandRoles", accessandrolesrepository.rulelist());
		} else if (formmode.equals("add")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - ADD");
			md.addAttribute("formmode", "add");
		} else if (formmode.equals("edit")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - EDIT");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		} else if (formmode.equals("view")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - INQUIRY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("verify")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - VERIFY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("delete")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - DELETE");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		}

		md.addAttribute("adminflag", "adminflag");
		md.addAttribute("userprofileflag", "userprofileflag");

		return "AccessandRoles";
	}

	@RequestMapping(value = "createAccessRole", method = RequestMethod.POST)
	@ResponseBody
	public String createAccessRoleEn(@RequestParam("formmode") String formmode,
			@RequestParam(value = "adminValue", required = false) String adminValue,
			@RequestParam(value = "BRF_ReportsValue", required = false) String BRF_ReportsValue,
			@RequestParam(value = "Basel_ReportsValue", required = false) String Basel_ReportsValue,
			@RequestParam(value = "ArchivalValue", required = false) String ArchivalValue,
			@RequestParam(value = "Audit_InquiriesValue", required = false) String Audit_InquiriesValue,
			@RequestParam(value = "RBR_ReportsValue", required = false) String RBR_ReportsValue,
			@RequestParam(value = "VAT_LedgerValue", required = false) String VAT_LedgerValue,
			@RequestParam(value = "Invoice_DataValue", required = false) String Invoice_DataValue,
			@RequestParam(value = "ReconciliationValue", required = false) String ReconciliationValue,
			@RequestParam(value = "finalString", required = false) String finalString,
			@ModelAttribute AccessAndRoles alertparam, Model md, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		String msg = AccessRoleService.addPARAMETER(alertparam, formmode, adminValue, BRF_ReportsValue,
				Basel_ReportsValue, ArchivalValue, Audit_InquiriesValue, RBR_ReportsValue, ReconciliationValue,
				VAT_LedgerValue, Invoice_DataValue, finalString, userid);

		return msg;

	}

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	@RequestMapping(value = "UserProfile", method = { RequestMethod.GET, RequestMethod.POST })
	public String userprofile(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String loginuserid = (String) req.getSession().getAttribute("USERID");
		String WORKCLASSAC = (String) req.getSession().getAttribute("WORKCLASS");
		String ROLEIDAC = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("RuleIDType", accessandrolesrepository.roleidtype());
		// System.out.println("Size of roleid list : " +
		// accessandrolesrepository.roleidtype().size());

		// System.out.println("work class is : " + WORKCLASSAC);
		// Logging Navigation
		loginServices.SessionLogging("USERPROFILE", "M2", req.getSession().getId(), loginuserid, req.getRemoteAddr(),
				"ACTIVE");
		Session hs1 = sessionFactory.getCurrentSession();
		md.addAttribute("menu", "USER PROFILE"); // To highlight the menu

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list");// to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("WORKCLASSAC", WORKCLASSAC);
			md.addAttribute("ROLEIDAC", ROLEIDAC);
			md.addAttribute("loginuserid", loginuserid);

			Iterable<UserProfile> user = loginServices.getUsersList(ROLEIDAC);

			md.addAttribute("userProfiles", user);

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getUser(userid));

		} else if (formmode.equals("verify")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getUser(userid));

		} else {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getUser(""));

		}

		return "BRFUserprofile";
	}

	@RequestMapping(value = "createUser", method = RequestMethod.POST)
	@ResponseBody
	public String createUser(@RequestParam("formmode") String formmode, @ModelAttribute UserProfile userprofile,
			Model md, HttpServletRequest rq) {
		String MOB = (String) rq.getSession().getAttribute("MOBILENUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		String msg = loginServices.addUser(userprofile, formmode, userid, username, MOB, ROLE);

		return msg;

	}

	@RequestMapping(value = "deleteuser", method = RequestMethod.POST)
	@ResponseBody
	public String deleteuser(@RequestParam("formmode") String userid, Model md, HttpServletRequest rq) {

		String msg = loginServices.deleteuser(userid);

		return msg;

	}

	@RequestMapping(value = "defaultpasswordReset", method = RequestMethod.POST)
	@ResponseBody
	public String DefaultpasswordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.DefaultpasswordReset(userprofile, userid);

		return msg;

	}

	@GetMapping("/getRoleDetails")
	@ResponseBody
	public AccessAndRoles getRoleDetails(@RequestParam String roleId) {
		System.out.println("role id for fetching is : " + roleId);
		return accessandrolesrepository.findById(roleId).orElse(null);
	}

	@RequestMapping(value = "resetPassword1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showResetPasswordPage(Model md, HttpServletRequest req) {
		String Passworduser = (String) req.getSession().getAttribute("USERID");
		String Passwordresest = (String) req.getSession().getAttribute("PASSWORDERROR");

		md.addAttribute("Resetuserid", Passworduser);
		md.addAttribute("Resetreason", Passwordresest);
		return "BRFresetPassword"; // Name of the HTML file (resetPassword.html)
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String userid, @RequestParam String newPassword)
			throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		Optional<UserProfile> userOptional = userProfileRep.findById(userid);
		String encryptedPassword = PasswordEncryption.getEncryptedPassword(newPassword);
		if (userOptional.isPresent()) {
			UserProfile user = userOptional.get();
			user.setPassword(encryptedPassword); // Encrypt the new password
			String localdateval = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			LocalDate date = LocalDate.parse(localdateval);
			BigDecimal passexpdays = new BigDecimal(user.getPass_exp_days());
			LocalDate date2 = date.plusDays(passexpdays.intValue());
			user.setLog_in_count("1");
			user.setNo_of_attmp(0);
			user.setUser_status("Active");
			user.setUser_status("Active");
			user.setDisable_flg("N");
			user.setUser_locked_flg("N");
			user.setPass_exp_date(new SimpleDateFormat("yyyy-MM-dd").parse(date2.toString()));// Reset the flag
			userProfileRep.save(user);
			return "redirect:login?resetSuccess";
		}

		return "redirect:resetPassword1?error=User not found";
	}

	@RequestMapping(value = "verifyUser", method = RequestMethod.POST)
	@ResponseBody
	public String verifyUser(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.verifyUser(userprofile, userid);

		return msg;

	}
	
	@GetMapping("/ReportCodeMapping")
	public String reportCodeMapping() {
        return "ReportCodeMapping";
    }
	
	@GetMapping("/GLMapping")
	public String glConfig() {
		return "Basemappingparameter";
	}

	@RequestMapping(value = "monthly1", method = { RequestMethod.GET, RequestMethod.POST })
	public String monthly1(Model md, HttpServletRequest req,
			@RequestParam(value = "report_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date report_date) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("menu", "Monthly 1 - BRF Report");
		md.addAttribute("reportlist", rrReportlist.findReportsByRemarks("M1"));

		if (report_date != null && !report_date.equals(null)) {
			System.out.println("report_date" + report_date);
			md.addAttribute("reportlist", rrReportlist.findDataByDate(report_date, "M1"));
			// md.addAttribute("reportlist",
			// rrReportlist.findDataMissing(report_date))//missing data for this date
			md.addAttribute("reportDate", report_date);
		}
		return "RR/RRReports";
	}

	@GetMapping("/checkDomainFlag")
	@ResponseBody
	public ResponseEntity<String> checkDomainFlag(@RequestParam String rptcode) {

	    List<RRReport> report = rrReportRepo.getParticularReport3list(rptcode);

	    if (report != null && !report.isEmpty()) {

	        for (RRReport each : report) {
	            String domain = each.getDOMAIN();

	            if ("Y".equalsIgnoreCase(domain)) {
	                return ResponseEntity.ok("ENABLED");
	            }
	        }
	        return ResponseEntity.ok("DISABLED");

	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
	    }
	}
	@Autowired
	CalculationService CalculationService;


	@RequestMapping(value = "BRFValidations", method = { RequestMethod.GET, RequestMethod.POST })
	public String BRFValidations(Model md,
	        @RequestParam(value = "rptcode", required = false) String rptcode,
	        @RequestParam(value = "todate", required = false) String todate,
	        HttpServletRequest req) {

	    String roleId = (String) req.getSession().getAttribute("ROLEID");

	    System.out.println("Report Code: " + rptcode);
	    System.out.println("Report Date: " + todate);

	    try {
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	        LocalDate parsedDate = LocalDate.parse(todate, inputFormatter);
	        String formattedDate = parsedDate.format(outputFormatter);

	        md.addAttribute("reportlist1",
	                rrReportlist.getReportbyrptcodeandtodate(rptcode, formattedDate));

	        md.addAttribute("rpt_date", todate);
	        md.addAttribute("rptcode", rptcode);
	        md.addAttribute("RoleId", roleId);

	        List<BRFValidations> list = brfValidationsRepo.getValidationList(rptcode);

	        if (list != null && !list.isEmpty()) {
	            for (BRFValidations data : list) {

	                if (data.getSRC_FORMULA() != null &&
	                    data.getDEST_FORMULA() != null) {

	                    BigDecimal srcvalue =
	                            CalculationService.calculate(data.getSRC_FORMULA(), formattedDate);

	                    BigDecimal destvalue =
	                            CalculationService.calculate(data.getDEST_FORMULA(), formattedDate);

	                    if (srcvalue != null && destvalue != null &&
	                        srcvalue.compareTo(destvalue) == 0) {

	                        data.setCur_status("Y");
	                    } else {
	                        data.setCur_status("N");
	                    }
	                }
	            }
	        }

	        md.addAttribute("reportlist", list);

	    } catch (Exception e) {
	        System.out.println("Error in BRFValidations: " + e.getMessage());
	    }

	    return "RR/BRFValidations";
	}
}
