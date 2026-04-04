package com.bornfire.BRF.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bornfire.BRF.services.LoginServices;
import com.bornfire.BRF.services.ReportServices.ReportTitle;
import com.bornfire.BRF.services.ReportServices;
import com.bornfire.BRF.entities.UserProfile;
import com.bornfire.BRF.entities.AccessAndRoles;
import com.bornfire.BRF.entities.AccessandRolesRepository;
import com.bornfire.BRF.services.AccessAndRolesServices;

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
		//System.out.println("Size of roleid list : " + accessandrolesrepository.roleidtype().size());

		//System.out.println("work class is : " + WORKCLASSAC);
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
	
}
