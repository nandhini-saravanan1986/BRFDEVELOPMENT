package com.bornfire.BRF.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
import com.bornfire.BRF.entities.ReportTemplateConfig;
import com.bornfire.BRF.entities.ReportTemplateConfigRepository;
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
import com.bornfire.BRF.services.ReportGenerationService;

import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;


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
	

	@Autowired
    ReportTemplateConfigRepository configRepo;
	
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

	@Value("${output.exportpathtemp}")
	private String templateFolder;
	
	private static final DataFormatter DATA_FORMATTER = new DataFormatter();
    private static final Pattern STARTS_WITH_DIGIT = Pattern.compile("^\\d.*");
    private static final List<String> HEADER_LABELS = Arrays.asList("no", "s.n.", "s.no", "s. no.", "sr. no.", "sr.no");
    @PostMapping("/saveTemplateConfig")
    @ResponseBody
    public ResponseEntity<String> saveTemplateConfig(@RequestBody ReportTemplateConfig config) {
        configRepo.save(config);
        return ResponseEntity.ok("Template layout saved successfully!");
    }
    @GetMapping(value = "/previewExcel", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String previewExcelTemplate(
            @RequestParam("code") String reportCode,
            @RequestParam(value = "hdr", required = false) Integer uiHdr,
            @RequestParam(value = "data", required = false) Integer uiData,
            @RequestParam(value = "stop", required = false) Integer uiStop,
            @RequestParam(value = "col", required = false) Integer uiCol) {
        
        try {
            File templateFile = reportServices.findTemplate(reportCode);
            if (templateFile == null || !templateFile.exists()) {
                return "<div class='alert alert-danger p-3'>Template file not found for code: " + reportCode + "</div>";
            }

            try (Workbook workbook = WorkbookFactory.create(templateFile)) {
                Sheet sheet = workbook.getSheetAt(0);
                StringBuilder html = new StringBuilder();
                
                // ── 1. HARD CAP: Prevent Java OutOfMemory on massive formatted sheets ──
                int absoluteMaxRow = sheet.getLastRowNum();
                if (absoluteMaxRow > 2000) absoluteMaxRow = 2000; // Safety limit for preview

                // ── 2. RUN SCANNER EARLY: Find where the data actually stops ──
                int firstDataRow, firstStopRow, headerStart, dataStartCol;
                boolean isFromDB = false;
                Map<Integer, String> rowLabelsMap; 
                
                if (uiHdr != null && uiData != null && uiStop != null && uiCol != null) {
                    headerStart = uiHdr;
                    firstDataRow = uiData;
                    firstStopRow = uiStop;
                    dataStartCol = uiCol;
                    rowLabelsMap = generateRowLabels(sheet, absoluteMaxRow, firstDataRow, firstStopRow, dataStartCol);
                } else {
                    java.util.Optional<ReportTemplateConfig> savedConfig = configRepo.findById(reportCode);
                    if (savedConfig.isPresent()) {
                        headerStart  = savedConfig.get().getHeaderStartRow();
                        firstDataRow = savedConfig.get().getFirstDataRow();
                        firstStopRow = savedConfig.get().getFirstStopRow();
                        dataStartCol = savedConfig.get().getDataStartCol();
                        isFromDB = true;
                        rowLabelsMap = generateRowLabels(sheet, absoluteMaxRow, firstDataRow, firstStopRow, dataStartCol);
                    } else {
                        rowLabelsMap = new HashMap<>();
                        int[] boundaries = executeStructuralScan(sheet, absoluteMaxRow, rowLabelsMap);
                        firstDataRow = boundaries[0];
                        firstStopRow = boundaries[1]; 
                        headerStart  = boundaries[2];
                        dataStartCol = (boundaries[3] == 0) ? 2 : 4; 
                    }
                }

                // ── 3. SMART RENDER LIMITS: Only render what we need to see ──
                // Render up to the stop row + 5 blank rows for breathing room
                int renderMaxRow = firstStopRow + 5; 
                if (renderMaxRow > absoluteMaxRow) renderMaxRow = absoluteMaxRow;

                // Calculate max columns ONLY for the rows we are actually rendering
                int renderMaxCol = 0;
                for (int i = 0; i <= renderMaxRow; i++) {
                    Row r = sheet.getRow(i);
                    if (r != null && r.getLastCellNum() > renderMaxCol) {
                        renderMaxCol = r.getLastCellNum();
                    }
                }
                // Cap columns to prevent horizontal bloat (usually mapping is within first 30 cols)
                if (renderMaxCol > 50) renderMaxCol = 50; 

                // ── 3.5 NEW: GENERATE CONTINUOUS COLUMN LABELS ──
                Map<Integer, String> colLabelsMap = new HashMap<>();
                int logicalColIndex = 0; // Always start at 0 so the first data column is 'A'
                
                for (int c = dataStartCol; c < renderMaxCol; c++) {
                    // Convert our relative counter (0, 1, 2) into letters (A, B, C)
                    String colLetter = getColumnLetter(logicalColIndex++); 
                    colLabelsMap.put(c, "COL" + colLetter); 
                }
                // ── 4. MERGED REGIONS (Optimized to render boundaries) ──
                boolean[][] skipCells = new boolean[renderMaxRow + 1][renderMaxCol];
                Map<String, String> spanMap = new HashMap<>();

                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i);
                    int r1 = region.getFirstRow(), r2 = region.getLastRow();
                    int c1 = region.getFirstColumn(), c2 = region.getLastColumn();

                    // If the merged region is entirely outside our render window, ignore it!
                    if (r1 > renderMaxRow || c1 >= renderMaxCol) continue;
                    
                    // Cap the spans so they don't break our HTML table width/height
                    int safeR2 = Math.min(r2, renderMaxRow);
                    int safeC2 = Math.min(c2, renderMaxCol - 1);

                    int rowspan = safeR2 - r1 + 1;
                    int colspan = safeC2 - c1 + 1;
                    spanMap.put(r1 + "," + c1, " rowspan='" + rowspan + "' colspan='" + colspan + "' ");

                    for (int r = r1; r <= safeR2; r++) {
                        for (int c = c1; c <= safeC2; c++) {
                            if (r != r1 || c != c1) skipCells[r][c] = true;
                        }
                    }
                }

                // ── 5. BUILD THE TUNING TOOLBAR ──
                String statusBadge = isFromDB 
                    ? "<span class='badge badge-success'>Loaded from DB</span>" 
                    : "<span class='badge badge-warning text-dark'>Auto-Predicted</span>";

                html.append("<div style='padding: 10px 15px; background: #fff; border-bottom: 2px solid #dcdcdc; display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; left: 0; z-index: 11;'>")
                    .append("<div><b>Layout Tuner</b> ").append(statusBadge).append("</div>")
                    .append("<div style='display: flex; gap: 15px; align-items: center; font-size: 12px;'>")
                    .append("Header Starts (Row): <input type='number' id='tuneHdr' value='").append(headerStart + 1).append("' style='width: 60px; border: 1px solid #ccc; padding: 2px 5px;'>")
                    .append("Data Starts (Row): <input type='number' id='tuneData' value='").append(firstDataRow + 1).append("' style='width: 60px; border: 1px solid #ccc; padding: 2px 5px;'>")
                    .append("Last Data (Row): <input type='number' id='tuneStop' value='").append(firstStopRow).append("' style='width: 60px; border: 1px solid #ccc; padding: 2px 5px;'>")
                    .append("Data Col (Index): <input type='number' id='tuneCol' value='").append(dataStartCol + 1).append("' style='width: 60px; border: 1px solid #ccc; padding: 2px 5px;' title='A=1, B=2, C=3...'>")
                    .append("<button class='btn btn-sm btn-secondary' onclick='testLayout()'>Test Layout</button>")
                    .append("<button class='btn btn-sm' style='background-color:#E77400; color:white; font-weight:bold;' onclick='saveLayout()'>Save to DB</button>")
                    .append("</div></div>");

                // ── 6. BUILD THE COLOR LEGEND ──
                html.append("<div style='padding: 10px 15px; background: #fdfdfd; border-bottom: 2px solid #dcdcdc; font-family: verdana; font-size: 12px; display: flex; flex-wrap: wrap; gap: 20px; position: sticky; top: 45px; left: 0; z-index: 10;'>")
                    .append("<div style='display: flex; align-items: center; gap: 6px;'><span style='width: 14px; height: 14px; background: #E77400; border: 1px solid #c86400; border-radius: 2px;'></span> <b>Row Label (ROW ID)</b></div>")
                    .append("<div style='display: flex; align-items: center; gap: 6px;'><span style='width: 14px; height: 14px; background: #376275; border: 1px solid #2a4f5e; border-radius: 2px;'></span> <b>Column Headers</b></div>")
                    .append("<div style='display: flex; align-items: center; gap: 6px;'><span style='width: 14px; height: 14px; background: #eef4fb; border: 1px solid #cdd8e6; border-radius: 2px;'></span> <b>Row Headers (Desc)</b></div>")
                    .append("<div style='display: flex; align-items: center; gap: 6px;'><span style='width: 14px; height: 14px; background: #e9ecf0; border: 1px solid #ccc; border-radius: 2px;'></span> <b>Excel Row/Col #</b></div>")
                    .append("<div style='display: flex; align-items: center; gap: 6px;'><span style='width: 14px; height: 14px; background: #ffffff; border: 1px solid #dcdcdc; border-radius: 2px;'></span> <b>Data Cells</b></div>")
                    .append("</div>");
             // ... (Your Color Legend logic remains above this) ...

                // ── 7. BUILD THE HTML TABLE (Using SMART boundaries) ──
                // The outer container must be position: relative for sticky elements inside to anchor correctly.
                html.append("<div class='table-responsive' style='width: 100%; height: calc(100vh - 350px); min-height: 400px; overflow: auto; position: relative;'>");
                html.append("<table class='table table-bordered table-sm' style='font-family: Calibri, verdana, sans-serif; font-size: 13px; width: max-content; background: white; margin-bottom: 0; border-collapse: separate; border-spacing: 0;'>");

                // ── STRICT STICKY HEADERS (Top Row) ──
                html.append("<thead><tr>");
                
                // z-index: 10 ensures the top-left corners stay above everything else when scrolling down AND right.
                html.append("<th style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background-color: #376275; color: white; text-align: center; position: sticky; top: 0; left: 0; z-index: 10; box-shadow: inset -1px -1px 0 #ccc;'>ROW LABEL</th>");
                html.append("<th style='box-sizing: border-box; width: 55px; min-width: 55px; max-width: 55px; background-color: #376275; color: white; text-align: center; position: sticky; top: 0; left: 85px; z-index: 10; box-shadow: inset -1px -1px 0 #ccc;'>ROW</th>");
                
                // ── RENDER EXCEL LETTERS + ORANGE COLUMN BADGES (Sticky Top) ──
                for (int c = 0; c < renderMaxCol; c++) {
                    String colLetter = (c < 26)
                        ? String.valueOf((char)('A' + c))
                        : String.valueOf((char)('A' + (c / 26) - 1)) + (char)('A' + (c % 26));
                        
                    String colId = colLabelsMap.get(c);

                    // z-index: 5 ensures these column headers stay above the scrolling data rows.
                    html.append("<th style='background-color: #e9ecf0; color: #333; text-align: center; position: sticky; top: 0; z-index: 5; box-shadow: inset 0 -1px 0 #ccc; white-space: nowrap; vertical-align: bottom;'>");
                    
                    if (colId != null) {
                        html.append("<div style='background:#E77400; color:white; border-radius:3px; padding:1px 5px; font-size:10px; margin-bottom:2px; font-weight:bold; box-shadow: 0 1px 2px rgba(0,0,0,0.2);'>")
                            .append(colId).append("</div>");
                    }
                    
                    html.append(colLetter).append("</th>");
                }
                html.append("</tr></thead><tbody>");

                // LOOP USING renderMaxRow INSTEAD OF absoluteMaxRow
                for (int r = 0; r <= renderMaxRow; r++) {
                    Row row = sheet.getRow(r);
                    html.append("<tr>");

                    // ── STRICT STICKY DATA CELLS (Left Columns) ──
                    String label = rowLabelsMap.get(r);
                    
                    // z-index: 4 ensures the left columns stay above the scrolling data cells (which are z-index: 1 or auto)
                    if (label != null) {
                        html.append("<td style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background-color: #E77400; color: white; font-weight: bold; text-align: center; position: sticky; left: 0; z-index: 4; box-shadow: inset -1px 0 0 #ccc;'>")
                            .append(label).append("</td>");
                    } else {
                        html.append("<td style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background-color: #f8f9fa; position: sticky; left: 0; z-index: 4; box-shadow: inset -1px 0 0 #ccc;'></td>");
                    }

                    // Notice left is exactly 85px so it docks perfectly next to the label!
                    html.append("<td style='box-sizing: border-box; width: 55px; min-width: 55px; max-width: 55px; background-color: #e9ecf0; font-weight: bold; text-align: center; color: #555; position: sticky; left: 85px; z-index: 4; box-shadow: inset -2px 0 0 #ccc;'>")
                        .append(r + 1).append("</td>");

                    // Data Cell Loop
                    for (int c = 0; c < renderMaxCol; c++) {
                        if (skipCells[r][c]) continue; 

                        Cell cell = (row == null) ? null : row.getCell(c);
                        String val = (cell == null) ? "" : DATA_FORMATTER.formatCellValue(cell).trim();
                        String spans = spanMap.getOrDefault(r + "," + c, "");
                        
                        String styleStr = "border: 1px solid #dcdcdc; padding: 4px 8px; vertical-align: middle; z-index: 1; "; // Default z-index for data cells
                        
                        // ── THE SEMANTIC ENGINE ──
                        boolean isColHeaderArea = (r >= headerStart && r < firstDataRow && c >= dataStartCol);
                        boolean isDataZone = (r >= firstDataRow && r < firstStopRow);
                        boolean isCellNumeric = (cell != null && (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA));
                        
                        // Area Logic
                        if (isColHeaderArea) {
                            styleStr += "background-color: #376275; color: white; font-weight: bold; text-align: center; ";
                        } 
                        else if (isDataZone) {
                            if (c < dataStartCol) {
                                styleStr += "background-color: #eef4fb; color: #376275; font-weight: bold; ";
                            } else {
                                if (isCellNumeric) {
                                    styleStr += "background-color: #ffffff; text-align: right; ";
                                } else {
                                    styleStr += "background-color: #fdfdfd; "; 
                                }
                            }
                        } else {
                            styleStr += "background-color: #ffffff; color: #666; font-style: italic; ";
                        }

                        if (cell != null && cell.getCellStyle() != null) {
                            CellStyle style = cell.getCellStyle();
                            if (style.getAlignmentEnum() == HorizontalAlignment.CENTER) styleStr += "text-align: center; ";
                        }

                        if (val.isEmpty()) val = "&nbsp;";
                        html.append("<td").append(spans).append(" style='").append(styleStr).append("'>").append(val.replace("\n", "<br>")).append("</td>");
                    }
                    html.append("</tr>");
                }
                html.append("</tbody></table></div>");
                return html.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<div class='alert alert-danger p-3'>Error generating preview: " + e.getMessage() + "</div>";
        }
    }

    // New helper to generate labels purely based on the boundaries
    private Map<Integer, String> generateRowLabels(Sheet sheet, int maxRow, int firstDataRow, int firstStopRow, int dataStartCol) {
        Map<Integer, String> map = new HashMap<>();
        int rowLabel = 101;
        
        // Loop through every row index from start to stop
        for (int i = firstDataRow; i < firstStopRow; i++) {
            // We assign the label to the index 'i' regardless of whether the row object is null
            // or if the cells are empty. This ensures the 101, 102, 103 sequence is unbroken.
            map.put(i, "ROW" + rowLabel++);
        }
        return map;
    }
    private int[] executeStructuralScan(Sheet sheet, int maxRow, Map<Integer, String> rowLabelsMap) {
        int firstDataRow = -1;
        int lastBCNumRow = -1;
        int firstStopRow = maxRow + 1;
        boolean fileHasNumB = false;
        int lastAllStringRow = -1; 

        // 1. BOTTOM-UP SCAN: Safely find the absolute Last Data Row
        int bottomUpStopRow = -1;
        for (int i = maxRow; i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            boolean hasContentInDataArea = false;
            String fullRowText = "";
            for (int c = 0; c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                String val = getCellValue(cell);
                fullRowText += val + " ";
                if (c >= 2 && !val.isEmpty()) hasContentInDataArea = true;
            }

            if (isStopRowContent(fullRowText)) continue;

            if (hasContentInDataArea) {
                bottomUpStopRow = i + 1; 
                break;
            }
        }
        if (bottomUpStopRow != -1) {
            firstStopRow = bottomUpStopRow;
        }

        // 2. YOUR EXACT ORIGINAL LOGIC: Pre-Scan for Data Boundaries
        for (int i = 0; i < firstStopRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String b = getCellValue(row.getCell(1));
            String c = getCellValue(row.getCell(2));
            String d = getCellValue(row.getCell(3));

            if (isStopRow(b, c, d)) { 
                if (i < firstStopRow) firstStopRow = i; 
                break; 
            }

            boolean bNum = isNumericLabel(b);
            boolean cNum = isNumericLabel(c);
            if (bNum) fileHasNumB = true;

            if (firstDataRow == -1) {
                if (bNum) {
                    firstDataRow = i;
                } else if (!fileHasNumB) {
                    boolean hasAnyCol = false;
                    boolean allString = true;
                    int lastCol = row.getLastCellNum();
                    for (int col = 2; col < lastCol; col++) {
                        Cell dc = row.getCell(col);
                        if (dc == null || dc.getCellTypeEnum() == CellType.BLANK) continue;
                        hasAnyCol = true;
                        if (dc.getCellTypeEnum() != CellType.STRING) { allString = false; break; }
                    }
                    if (hasAnyCol && allString) lastAllStringRow = i;
                }
            }
            if (bNum || cNum) lastBCNumRow = i;
        }

        if (!fileHasNumB && firstDataRow == -1 && lastAllStringRow >= 0) {
            firstDataRow = lastAllStringRow + 1;
        }
        if (firstDataRow == -1) firstDataRow = 0;

        // 3. YOUR EXACT ORIGINAL LOGIC: Determine Structure & Data Start Column
        int structure;
        if (!fileHasNumB) {
            structure = 0;
        } else {
            boolean cHasDecimal = false;
            for (int i = firstDataRow; i <= Math.min(lastBCNumRow, firstStopRow - 1); i++) {
                Row r = sheet.getRow(i);
                if (r != null && isDecimalLabel(getCellValue(r.getCell(2)))) {
                    cHasDecimal = true; break;
                }
            }
            structure = cHasDecimal ? 3 : 2;
        }

        int dataStartCol = (structure == 0) ? 2 : 4; 

        // 4. YOUR EXACT ORIGINAL LOGIC: Accurately Calculate Header Start Row
        int headerStart = Math.max(0, firstDataRow - 1); 
        
        if (firstDataRow > 0) {
            for (int r = firstDataRow - 1; r >= 0; r--) {
                Row hr = sheet.getRow(r);
                boolean hasHeaderText = false;
                
                if (hr != null) {
                    int lastCol = hr.getLastCellNum();
                    for (int col = dataStartCol; col < lastCol; col++) {
                        Cell cell = hr.getCell(col);
                        if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
                            if (!cell.getStringCellValue().trim().isEmpty()) {
                                hasHeaderText = true;
                                break;
                            }
                        }
                    }
                }
                
                if (hasHeaderText) {
                    headerStart = r; 
                } else {
                    break; 
                }
            }
        }

        // 5. ASSIGN ROW LABELS
        int rowLabelCounter = 101;
        for (int i = firstDataRow; i < firstStopRow; i++) {
            rowLabelsMap.put(i, "ROW" + (rowLabelCounter++));
        }

        return new int[]{firstDataRow, firstStopRow, headerStart, dataStartCol};
    }
    // Helper to check row content for stop keywords
    private boolean isStopRowContent(String content) {
        String c = content.toUpperCase();
        return c.contains("NOTE") || c.contains("FORM NO") || c.contains("BSD/BRF") || c.contains("COMPILED BY");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────────
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: {
                String formatted = DATA_FORMATTER.formatCellValue(cell).trim();
                if (!formatted.isEmpty()) return formatted;
                double n = cell.getNumericCellValue();
                return (n == (long) n) ? String.valueOf((long) n) : String.valueOf(n);
            }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return "";
            default:      return "";
        }
    }

    private boolean isNumericLabel(String s) {
        return s != null && STARTS_WITH_DIGIT.matcher(s.trim()).matches();
    }

    private boolean isStopRow(String b, String c, String d) {
        String bU = b.toUpperCase(), cU = c.toUpperCase(), dU = d.toUpperCase();
        if (bU.startsWith("NOTE") || cU.startsWith("NOTE") || dU.startsWith("NOTE")) return true;
        String all = bU + " " + cU + " " + dU;
        return all.contains("FORM NO") || all.contains("BSD/BRF");
    }

    private boolean isHeaderLabel(String level1) {
        return HEADER_LABELS.contains(level1.toLowerCase().trim());
    }

    private boolean isIntegerLabel(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().matches("^\\d+$");
    }

    private boolean isDecimalLabel(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().matches("^\\d+\\.\\d+.*");
    }
    
    private String getColumnLetter(int column) {
        StringBuilder result = new StringBuilder();
        while (column >= 0) {
            int remainder = column % 26;
            result.insert(0, (char) ('A' + remainder));
            column = (column / 26) - 1;
        }
        return result.toString();
    }
    
    @GetMapping(value = "/previewRawExcel", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String previewRawExcel(@RequestParam("code") String reportCode) {
        try {
            File templateFile = reportServices.findTemplate(reportCode);
            if (templateFile == null || !templateFile.exists()) {
                return "<div class='alert alert-danger p-3'>Template file not found for code: " + reportCode + "</div>";
            }

            try (Workbook workbook = WorkbookFactory.create(templateFile)) {
                Sheet sheet = workbook.getSheetAt(0);

                // Safety cap
                int maxRow = Math.min(sheet.getLastRowNum(), 1999);

                // ── CHECK DATABASE FOR SAVED BOUNDARIES FIRST ──
                int firstDataRow, firstStopRow, headerStart, dataStartCol;
                boolean isFromDB = false;
                Map<Integer, String> rowLabelsMap = new HashMap<>();

                java.util.Optional<ReportTemplateConfig> savedConfig = configRepo.findById(reportCode);
                
                if (savedConfig.isPresent()) {
                    headerStart  = savedConfig.get().getHeaderStartRow();
                    firstDataRow = savedConfig.get().getFirstDataRow();
                    firstStopRow = savedConfig.get().getFirstStopRow();
                    dataStartCol = savedConfig.get().getDataStartCol();
                    isFromDB = true;
                    rowLabelsMap = generateRowLabels(sheet, maxRow, firstDataRow, firstStopRow, dataStartCol);
                } else {
                    int[] boundaries = executeStructuralScan(sheet, maxRow, rowLabelsMap);
                    firstDataRow = boundaries[0];
                    firstStopRow = boundaries[1];
                    headerStart  = boundaries[2];
                    dataStartCol = (boundaries[3] == 0) ? 2 : 4; 
                }

                // Find max columns across all rendered rows
                int maxCol = 0;
                for (int r = 0; r <= maxRow; r++) {
                    Row row = sheet.getRow(r);
                    if (row != null && row.getLastCellNum() > maxCol) {
                        maxCol = row.getLastCellNum();
                    }
                }
                if (maxCol > 50) maxCol = 50; // Cap columns

                // ── NEW: GENERATE CONTINUOUS COLUMN LABELS ──
                Map<Integer, String> colLabelsMap = new HashMap<>();
                int logicalColIndex = 0; // Always start at 0 so the first data column is 'A'
                
                for (int c = dataStartCol; c < maxCol; c++) {
                    String colLetter = getColumnLetter(logicalColIndex++); 
                    colLabelsMap.put(c, "COL" + colLetter); 
                }
                // Build merged region maps
                boolean[][] skipCells = new boolean[maxRow + 1][maxCol];
                Map<String, String> spanMap = new HashMap<>();

                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i);
                    int r1 = region.getFirstRow(), r2 = region.getLastRow();
                    int c1 = region.getFirstColumn(), c2 = region.getLastColumn();

                    if (r1 > maxRow || c1 >= maxCol) continue;

                    int safeR2 = Math.min(r2, maxRow);
                    int safeC2 = Math.min(c2, maxCol - 1);
                    int rowspan = safeR2 - r1 + 1;
                    int colspan = safeC2 - c1 + 1;
                    spanMap.put(r1 + "," + c1, " rowspan='" + rowspan + "' colspan='" + colspan + "'");

                    for (int r = r1; r <= safeR2; r++) {
                        for (int c = c1; c <= safeC2; c++) {
                            if (r != r1 || c != c1) skipCells[r][c] = true;
                        }
                    }
                }

                StringBuilder html = new StringBuilder();

                // ── UPDATED: Info bar with DB Status Badge ──
                String statusBadge = isFromDB 
                    ? "<span class='badge badge-success' style='margin-left: 15px;'>Loaded from DB</span>" 
                    : "<span class='badge badge-warning text-dark' style='margin-left: 15px;'>Auto-Predicted</span>";

                html.append("<div style='padding: 8px 14px; background: #f8f9fa; border-bottom: 1px solid #dee2e6; "
                          + "font-family: verdana; font-size: 12px; color: #555; display: flex; align-items: center;'>")
                    .append("<div><b>Visual Mapper:</b> Click any row with an orange label to quickly map it.</div>")
                    .append(statusBadge)
                    .append("</div>");

                // Table
                html.append("<div style='overflow: auto; width: 100%; height: 460px;'>")
                    .append("<table id='rawExcelTable' style='border-collapse: collapse; font-family: Calibri, verdana, sans-serif; "
                          + "font-size: 13px; width: max-content; background: white;'>")
                    .append("<thead><tr>")
                    // ── STRICT STICKY HEADERS (Notice the explicit widths) ──
                    .append("<th style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background:#376275; color:white; border:1px solid #c0c0c0; padding:4px 8px; text-align:center; position:sticky; top:0; left:0; z-index:5;'>ROW LABEL</th>")
                    .append("<th style='box-sizing: border-box; width: 55px; min-width: 55px; max-width: 55px; background:#e9ecf0; color:#333; border:1px solid #c0c0c0; padding:4px 8px; text-align:center; position:sticky; top:0; left:85px; z-index:5;'>#</th>");

                // ── RENDER EXCEL LETTERS + ORANGE COLUMN BADGES ──
                for (int c = 0; c < maxCol; c++) {
                    String colLetter = (c < 26)
                        ? String.valueOf((char)('A' + c))
                        : String.valueOf((char)('A' + (c / 26) - 1)) + (char)('A' + (c % 26));
                        
                    String colId = colLabelsMap.get(c);

                    html.append("<th style='background:#e9ecf0; color:#333; border:1px solid #c0c0c0; ")
                        .append("padding:4px 10px; text-align:center; position:sticky; top:0; z-index:2; ")
                        .append("white-space:nowrap; vertical-align:bottom;'>");
                        
                    if (colId != null) {
                        html.append("<div style='background:#E77400; color:white; border-radius:3px; padding:1px 5px; font-size:10px; margin-bottom:2px; font-weight:bold; box-shadow: 0 1px 2px rgba(0,0,0,0.2);'>")
                            .append(colId).append("</div>");
                    }
                    
                    html.append(colLetter).append("</th>");
                }
                html.append("</tr></thead><tbody>");

                // Data rows
                for (int r = 0; r <= maxRow; r++) {
                    Row row = sheet.getRow(r);
                    
                    String label = rowLabelsMap.get(r);
                    boolean hasLabel = (label != null);

                    // Add OnClick Event to the Row
                    if (hasLabel) {
                        html.append("<tr class='mappable-row' style='cursor:pointer;' onclick='openRowMappingModal(\"").append(label).append("\")' title='Click to map ").append(label).append("'>");
                    } else {
                        html.append("<tr>");
                    }

                    // ── STRICT STICKY DATA CELLS ──
                    if (hasLabel) {
                        html.append("<td style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background:#E77400; color:white; font-weight:bold; border:1px solid #c0c0c0; padding:3px 8px; text-align:center; position:sticky; left:0; z-index:3; box-shadow: 2px 0 5px rgba(0,0,0,0.1);'>")
                            .append(label).append("</td>");
                    } else {
                        html.append("<td style='box-sizing: border-box; width: 85px; min-width: 85px; max-width: 85px; background:#f8f9fa; border:1px solid #c0c0c0; position:sticky; left:0; z-index:3;'></td>");
                    }

                    html.append("<td style='box-sizing: border-box; width: 55px; min-width: 55px; max-width: 55px; background:#e9ecf0; color:#555; border:1px solid #c0c0c0; padding:3px 8px; text-align:center; font-size:11px; position:sticky; left:85px; z-index:3;'>")
                        .append(r + 1).append("</td>");

                    for (int c = 0; c < maxCol; c++) {
                        if (skipCells[r][c]) continue;

                        Cell cell = (row == null) ? null : row.getCell(c);
                        String val = (cell == null) ? "" : DATA_FORMATTER.formatCellValue(cell).trim();
                        String spans = spanMap.getOrDefault(r + "," + c, "");

                        boolean isBold = false;
                        if (cell != null && cell.getCellStyle() != null) {
                            Font font = workbook.getFontAt(cell.getCellStyle().getFontIndex());
                            isBold = font != null && font.getBold();
                        }

                        String fontWeight = isBold ? "font-weight:bold;" : "";
                        String displayVal = val.isEmpty() ? "&nbsp;" : val.replace("\n", "<br>");

                        html.append("<td").append(spans)
                            .append(" style='border:1px solid #d0d0d0; padding:4px 8px; ")
                            .append("vertical-align:middle; white-space:nowrap; background:#ffffff; ")
                            .append(fontWeight).append("'>")
                            .append(displayVal).append("</td>");
                    }
                    html.append("</tr>");
                }

                html.append("</tbody></table></div>");
                return html.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "<div class='alert alert-danger p-3'>Error generating preview: " + e.getMessage() + "</div>";
        }
    }

	@GetMapping("/checkDomainFlagwithdate")
	@ResponseBody
	public ResponseEntity<String> checkDomainFlagwithdate(@RequestParam String rptcode,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,Model md) {
		//System.out.println("Date : " + date);
		md.addAttribute("reportDate", "2025-12-31");
		List<Date> datelist = rrReportRepo.getdatelist(rptcode);
		for (Date eachdate : datelist) {
			//System.out.println("Each date : " + eachdate);
			if (eachdate != null && eachdate.compareTo(date) == 0) {
				return ResponseEntity.ok("ENABLED");
			}
		}
		return ResponseEntity.ok("DISABLED");
	}
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 

		@GetMapping("/datavalidationdetails")
		public ResponseEntity<List<Map<String, String>>> getModifyDetails(
				@RequestParam("report_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date report_date) {

			List<Map<String, String>> parsedList = new ArrayList<>();
			Map<String, String> rowMap = new HashMap<>();
			rowMap.put("tableName", "CBS DATA");

			String sql = "SELECT CASE "
					+ "WHEN NVL(SUM(ACT_BALANCE_AMT_LC),0) >= 0 AND NVL(SUM(ACT_BALANCE_AMT_LC),0) < 1 "
					+ "AND NVL(SUM(ACCT_BALANCE_AMT_AC),0) >= 0 AND NVL(SUM(ACCT_BALANCE_AMT_AC),0) < 1 "
					+ "THEN 'VALID' " + "ELSE 'INVALID' " + "END AS validation_result " + "FROM GENERAL_MASTER_TABLE "
					+ "WHERE report_date = ?";
			String result = jdbcTemplate.queryForObject(sql, new Object[] { report_date }, String.class);

			String sql1 = "SELECT COUNT(*) " + "FROM GENERAL_MASTER_TABLE " + "WHERE  report_date = ?";
			Integer result1 = jdbcTemplate.queryForObject(sql1, new Object[] { report_date }, Integer.class);
			String dataValidation = "VALID";
			String status = "OK";
			if (result1 == 0) {
				dataValidation = "INVALID";
			}
			if (result1 == 0 || "INVALID".equals(result)) {
				status = "FAIL";
			}
			rowMap.put("dataValidation", dataValidation);
			rowMap.put("logicValidation", result);
			rowMap.put("status", status);

			parsedList.add(rowMap);

			// String tablename="BRF_TREASURY_INVEST_TB";

			List<String> tablelist = Arrays.asList("BRF_TREASURY_INVEST_TB", "BRF_TREASURY_MASTER_TB",
					"BRF_TREASURY_PLACEMENT_ID","BRF_FORWARD_REVEAL_MANUAL_TABLE");
			List<String> tablelisttodisplay = Arrays.asList("TREASURY INVESTMENT DATA", "TREASURY MASTER DATA",
					"TREASURY PLACEMENT DATA","FORWARD DEAL DATA");
			for (int i = 0; i < tablelist.size(); i++) {
				
				String tablename = tablelist.get(i);
			    String displayName = tablelisttodisplay.get(i);

				Map<String, String> looprowMap = new HashMap<>();
				looprowMap.put("tableName", displayName);

				String countsql = "SELECT COUNT(*) " + "FROM " + tablename + " WHERE  report_date = ?";
				Integer countresult = jdbcTemplate.queryForObject(countsql, new Object[] { report_date },
						Integer.class);
				dataValidation = "VALID";
				status = "OK";
				if (countresult == 0) {
					dataValidation = "INVALID";
					status = "FAIL";
				}

				looprowMap.put("dataValidation", dataValidation);
				looprowMap.put("logicValidation", "-");
				looprowMap.put("status", status);
				parsedList.add(looprowMap);
			}

			return ResponseEntity.ok(parsedList);
		}
		
		//@Autowired
		//Procedure_Common_Service Procedure_Common_Service;
		
		@Autowired
		ReportGenerationService ReportGenerationService;
		
		@PostMapping("/executeProcedure")
		public ResponseEntity<String> executeProcedure(@RequestParam(value = "reportDate", required = false) String reportDate,
				@RequestParam(value = "srl_no", required = false) Integer srl_no, HttpServletRequest req) {
			try {
				System.out.println("Report_Date : " + reportDate);
				System.out.println("srl_no : " + srl_no);
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate parsedDate;
				//String formattedDate = parsedDate.format(dateFormatter);
				String formattedDate;
				String rpt_code;
				Date utildate;
				if (srl_no != null && !srl_no.equals(null) && srl_no.toString() != "null" && !srl_no.toString().equals("null")) {
					Optional<RRReport> data = rrReportlist.findById(srl_no);
					rpt_code = data.get().getRpt_code();
				} else {
					rpt_code = "ALL";
				}
				if (srl_no != null && !srl_no.equals(null) && srl_no.toString() != "null" && !srl_no.toString().equals("null")) {
					Optional<RRReport> data = rrReportlist.findById(srl_no);
					System.out.println("Data's Report date : "+data.get().getEnd_date());			
					utildate = data.get().getEnd_date();
					LocalDate localDate = utildate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					formattedDate = inputFormatter.format(localDate);					
					parsedDate = LocalDate.parse(formattedDate, inputFormatter);
					formattedDate = parsedDate.format(dateFormatter);
				}
				else {
					parsedDate = LocalDate.parse(reportDate, inputFormatter);
					formattedDate = parsedDate.format(dateFormatter);
					utildate = java.sql.Date.valueOf(parsedDate);
				}
				System.out.println("Report_Date Formatted Date : " + formattedDate +"utildate : "+utildate);
				ReportGenerationService.generateFullReport(formattedDate, rpt_code);
				String sql = "CALL COMMON_TRIGGERING_PROCEDURE(?, ?)";
				jdbcTemplate.update(sql, formattedDate, rpt_code);
				//Procedure_Common_Service.executeCommonProcedure(formattedDate, rpt_code);				
				int nextSerialno = rrReportlist.findMaxSerialNo() + 1;
				//Date utildate = java.sql.Date.valueOf(parsedDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(utildate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				Date firstDayOfMonth = cal.getTime();
				if (srl_no != null && !srl_no.equals(null) && srl_no.toString() != "null" && !srl_no.toString().equals("null")) {		
					System.out.println("Entered Sigle Procedure");
					Optional<RRReport> entities = rrReportlist.findById(srl_no);
					RRReport entity = entities.get();
					RRReport newentity = new RRReport();
					BeanUtils.copyProperties(entity, newentity);
					System.out.println("Report date : " + utildate + " - serial no :" + nextSerialno
							+ " - Rport code : " + newentity.getRpt_code());
					newentity.setSrl_no(nextSerialno);
					newentity.setStart_date(firstDayOfMonth);
					newentity.setEnd_date(utildate);
					newentity.setEntry_date(new Date());
					newentity.setModify_user(null);
					newentity.setVerify_suer(null);
					newentity.setEntry_user((String) req.getSession().getAttribute("USERID"));
					newentity.setModify_date(null);
					newentity.setVerify_date(null);
					if(newentity.getEnd_date()!=entity.getEnd_date() && !newentity.getEnd_date().equals(entity.getEnd_date())) {
						rrReportlist.save(newentity);	
					}									
				} else {
					//System.out.println("Entered Multiple Procedure");
					List<RRReport> list = rrReportlist.findDataMissing(utildate, "M1");
					if (list != null && !list.equals(null)) {
						for (RRReport entity : list) {
							RRReport newentity = new RRReport();
							BeanUtils.copyProperties(entity, newentity);
							System.out.println("Report date : " + utildate + " - serial no :" + nextSerialno
									+ " - Rport code : " + newentity.getRpt_code());
							newentity.setSrl_no(nextSerialno++);
							newentity.setStart_date(firstDayOfMonth);
							newentity.setEnd_date(utildate);
							newentity.setEntry_date(new Date());
							newentity.setModify_user(null);
							newentity.setVerify_suer(null);
							newentity.setEntry_user((String) req.getSession().getAttribute("USERID"));
							newentity.setModify_date(null);
							newentity.setVerify_date(null);
							rrReportlist.save(newentity);
						}
					}
				}
				return ResponseEntity.ok("{\"message\": \"Procedure executed successfully!\"}");
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("{\"error\": \"Failed to execute procedure: " + e.getMessage() + "\"}");
			}
		}
		
		
}
