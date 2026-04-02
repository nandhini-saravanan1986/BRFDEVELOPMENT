package com.bornfire.BRF.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bornfire.BRF.services.LoginServices;

@Controller
@ConfigurationProperties("default")
public class BRFNavigationController {

	@Autowired
	LoginServices loginServices;

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
}
