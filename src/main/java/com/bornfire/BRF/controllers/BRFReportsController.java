package com.bornfire.BRF.controllers;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bornfire.BRF.services.ReportServices;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.BRF.config.SequenceGenerator;

/*import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF62_DETAIL_ENTITY;*/

//import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
//import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
//import com.bornfire.xbrl.entities.BRBS.BRF94_DETAILENTITY;

import com.bornfire.BRF.entities.BRF1_DETAIL_ENTITY;
import com.bornfire.BRF.entities.UserProfileRep;
//import com.bornfire.xbrl.services.BRF94ReportService;
import com.bornfire.BRF.services.BRF001ReportService;
import com.bornfire.BRF.services.LoginServices;
import com.bornfire.BRF.services.RegulatoryReportServices;
import com.bornfire.BRF.services.ReportServices;

import net.sf.jasperreports.engine.JRException;


@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "Reports")
public class BRFReportsController {
	
	private ReportServices reportService;
	
	public BRFReportsController(ReportServices reportService) {
		super();
		this.reportService = reportService;
	}


	@GetMapping("/reportTemplate")
	@ResponseBody
	public Map<String,Object> getTemplate(@RequestParam String code) throws Exception {

	    return reportService.readTemplate(code);

	}
	
	@GetMapping("/glHeads")
	@ResponseBody
	public List<String> getGLHeads(@RequestParam String dataType) {
	    return reportService.getGLHeads(dataType);
	}

	@GetMapping("/glSubHeads")
	@ResponseBody
	public List<Map<String, String>> getGLSubHeads(@RequestParam String dataType,
	                                                @RequestParam String glHead) {
	    return reportService.getGLSubHeads(dataType, glHead);
	}
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(BRFReportsController.class);

	@Autowired
	ReportServices reportServices;
	@Autowired
	LoginServices loginServices;
//	  @Autowired BRF94ReportService BRF94ReportService;
//	 
	@Autowired
	BRF001ReportService brf001ReportService;


	@Autowired
	RegulatoryReportServices regreportServices;
	// @Autowired
	// BRF153ReportService BRF153ReportService;

//	@Autowired
//	BRF74ReportService brf74ReportService;
//	@Autowired
//	BRF008ReportService bRF008ReportService;
//	@Autowired
//	BRF012ReportService brf012ReportService;
//	@Autowired
//	CustomerDetailService customerDetailService;
//	@Autowired
//	BRF56ReportService brf056ReportService;
//	@Autowired
//	BRF59ReportService brf059ReportService;
//	@Autowired
//	BRF67ReportService BRF67ReportService;

//	@Autowired
//	BRF62ReportService BRF062ReportService;
//

	@Autowired
	BRF001ReportService BRF001ReportService;

//	@Autowired
//	BRF002ReportService BRF002ReportService;

//	@Autowired
//	BRF003ReportService bRF003ReportService;

//	@Autowired
//	BRF57ReportService brf57ReportService;

//	@Autowired
//	BRF206AReportService bRF206AReportService;

//	@Autowired
//	BRF66AReportService BRF066Areportservice;

//	@Autowired
//	BRF65ReportService brf65ReportService;

//	@Autowired
//	BRF151ReportService brf151ReportService;

//	@Autowired
//	BRF154ReportService BRF154ReportService;
	// @Autowired
//	BRF181AReportService bRF181AReportService;

	// @Autowired
	// BRF101ReportService brf101ReportService;

//	@Autowired
	// BRF71ReportService BRF71reportservice;

	// @Autowired
//	BRF78ReportService BRF78reportservice;

//	@Autowired
//	BRF095AReportService brf095AReportService;
//	@Autowired
//	BRF96AReportService brf96ReportService;

//	@Autowired
//	BRF69ReportService brf069ReportService;

//	@Autowired
//	BRF83ReportService brf83ReportService;

//
//	@Autowired
//	BRF001_FORT_SERVICE brf001_FORT_SERVICE;

//	@Autowired
//	BRF86ReportService brf86ReportService;

	// @Autowired
	// BRF76ReportService brf76ReportService;

//	@Autowired
	// BBUSDReportService BBUSDReportService;

//	@Autowired
//	BBGBPReportService BBGBPReportService;

	// @Autowired
	// BBEURReportService BBEURReportService;

	// @Autowired
	// BBOTHERCURRENCYReportService BBOTHERCURRENCYReportService;

	// @Autowired
	// Banking_BookReportService banking_bookReportService;


	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	// To show the required report at the first stage
	@RequestMapping(value = "{reportid}", method = RequestMethod.POST)
	public ModelAndView reportView(@PathVariable("reportid") String reportid,
			@RequestParam(value = "function", required = false) String function,
			@RequestParam("asondate") String asondate, @RequestParam(required = false) String fromdate,
			@RequestParam("todate") String todate, @RequestParam(value = "currency", required = false) String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md,
			HttpServletRequest req, BigDecimal srl_no) {

		String userid = (String) req.getSession().getAttribute("USERID");
		String roleid = (String) req.getSession().getAttribute("ROLEID");
		String accesscode = (String) req.getSession().getAttribute("ACCESSCODE");
		// Logging Navigation
		if (dtltype.equals("report")) {
			md.addAttribute("menu", "XBRLReports");
			loginServices.SessionLogging("REPORTS" + reportid, "M8", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		} else {
			md.addAttribute("menu", "XBRLArchives");
			loginServices.SessionLogging("ARCHREPORTS" + reportid, "M9", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		}

		logger.info("Get Report :" + reportid);
		logger.info("Get Report :" + asondate);
		try {
			asondate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(asondate));
			fromdate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(fromdate));
			todate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(todate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		logger.info("Get Report :" + asondate);
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		// Assigning required Modal Attributes
		md.addAttribute("UserId", userid);
		md.addAttribute("RoleId", roleid);
		md.addAttribute("UserCol", accesscode);

		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("type", type);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = new ModelAndView();
		System.out.println("tttttttttttttt" + userid);

		mv = regreportServices.getReportView(reportid, asondate, fromdate, todate, currency, dtltype, subreportid,
				secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no, userid);

		// System.out.println("----------------------");

		// Page<Object> sup0700RepPage = (Page<Object>)
		// mv.getModelMap().get("reportsummary");

		// sup0700RepPage.getContent().forEach((a)-> System.out.println(a.toString()));

		return mv;

	}

	// To check report data availability and Pending verification before

	/*
	 * @RequestMapping(value = "{reportid}/nilReport", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String nilReport(@PathVariable("reportid") String
	 * reportid, @RequestParam("asondate") String asondate,
	 * 
	 * @RequestParam("fromdate") String fromdate, @RequestParam("todate") String
	 * todate,
	 * 
	 * @RequestParam(value = "reportingTime", required = false) String
	 * reportingTime, Model md, HttpServletRequest req) {
	 * 
	 * logger.info("Get Report Verify Screen" + reportid);
	 * 
	 * String userid = (String) req.getSession().getAttribute("USERID");
	 * 
	 * Date asondate1 = null; Date fromdate1 = null; Date todate1 = null;
	 * 
	 * try { asondate1 = new SimpleDateFormat("dd-MM-yyyy").parse(asondate);
	 * fromdate1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate); todate1 = new
	 * SimpleDateFormat("dd-MM-yyyy").parse(todate); } catch (ParseException e) {
	 * e.printStackTrace(); }
	 * 
	 * try { asondate = dateFormat.format(new
	 * SimpleDateFormat("dd-MM-yyyy").parse(asondate)); fromdate =
	 * dateFormat.format(new SimpleDateFormat("dd-MM-yyyy").parse(fromdate)); todate
	 * = dateFormat.format(new SimpleDateFormat("dd-MM-yyyy").parse(todate)); }
	 * catch (ParseException e) { e.printStackTrace(); }
	 * 
	 * String status = reportServices.updateReportStatus(reportid, asondate1,
	 * fromdate1, todate1, userid, "Y"); String msg =
	 * reportServices.saveReport(reportid, asondate, fromdate, todate, "MUR");
	 * 
	 * return status;
	 * 
	 * }
	 */

	@RequestMapping(value = "{reportid}/Summary", method = RequestMethod.GET)
	public ModelAndView reportSummay(@PathVariable("reportid") String reportid,
			@RequestParam("asondate") String asondate, @RequestParam("fromdate") String fromdate,
			@RequestParam("todate") String todate, @RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md, BigDecimal srl_no,
			HttpServletRequest req) {

		logger.info("Getting Report Summary :" + reportid);

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("type", type);
		md.addAttribute("currency", currency);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("displaymode", "summary");

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		System.out.println("role id issssssssssssssssssssssssssss" + roleId);
		md.addAttribute("operation", roleId);

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.getReportSummary(reportid, asondate, fromdate, todate, currency, dtltype,
				subreportid, secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no, roleId);

		return mv;

	}

	@RequestMapping(value = "{reportid}/Details", method = RequestMethod.GET)
	public ModelAndView reportDetail(@PathVariable("reportid") String reportid,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam(value = "filter", required = false) String filter, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "searchVal", required = false) String searchVal, Model md) {

		logger.info("Getting Report Details :" + reportid);
		logger.info("Assigning Model Attributes :" + reportid);

		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("filter", filter);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportingTime", reportingTime);
		// md.addAttribute("instancecode", Integer.parseInt(instancecode));
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("displaymode", "detail");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(100);

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.getReportDetails(reportid, instancecode, asondate, fromdate, todate,
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter,
				searchVal);

		return mv;

	}

	/*
	 * @RequestMapping(value = "{reportid}/Details1", method = RequestMethod.GET)
	 * public ModelAndView reportDetail1(@PathVariable("reportid") String reportid,
	 * 
	 * @RequestParam(value = "instancecode", required = false) String instancecode,
	 * 
	 * @RequestParam(value = "filter", required = false) String
	 * filter, @RequestParam("asondate") String asondate,
	 * 
	 * @RequestParam("fromdate") String fromdate, @RequestParam("todate") String
	 * todate,
	 * 
	 * @RequestParam("currency") String currency,
	 * 
	 * @RequestParam(value = "subreportid", required = false) String subreportid,
	 * 
	 * @RequestParam(value = "secid", required = false) String secid,
	 * 
	 * @RequestParam(value = "dtltype", required = false) String dtltype,
	 * 
	 * @RequestParam(value = "page", required = false) Optional<Integer> page,
	 * 
	 * @RequestParam(value = "size", required = false) Optional<Integer> size,
	 * 
	 * @RequestParam(value = "reportingTime", required = false) String
	 * reportingTime, Model md) {
	 * 
	 * logger.info("Getting Report Details :" + reportid);
	 * logger.info("Assigning Model Attributes :" + reportid);
	 * 
	 * md.addAttribute("menu", "XBRLReports"); md.addAttribute("reportid",
	 * reportid); md.addAttribute("asondate", asondate); md.addAttribute("fromdate",
	 * fromdate); md.addAttribute("todate", todate); md.addAttribute("filter",
	 * filter); md.addAttribute("currency", currency); md.addAttribute("dtltype",
	 * dtltype); md.addAttribute("reportingTime", reportingTime); //
	 * md.addAttribute("instancecode", Integer.parseInt(instancecode));
	 * md.addAttribute("reportTitle", reportServices.getReportName(reportid));
	 * md.addAttribute("displaymode", "detail"); md.addAttribute("displaymode",
	 * "detail1");
	 * 
	 * int currentPage = page.orElse(0); int pageSize =
	 * size.orElse(Integer.parseInt(pagesize));
	 * 
	 * logger.info("Getting ModelandView :" + reportid); ModelAndView mv =
	 * regreportServices.getReportDetails1(reportid, instancecode, asondate,
	 * fromdate, todate, currency, reportingTime, dtltype, subreportid, secid,
	 * PageRequest.of(currentPage, pageSize), filter);
	 * 
	 * return mv;
	 * 
	 * }
	 */


	@RequestMapping(value = "{reportid}/Download", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<InputStreamResource> XBRLDownload(HttpServletResponse response,
			@PathVariable("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam("filetype") String filetype, @RequestParam(value = "filter", required = false) String filter)
			throws IOException, SQLException {
		response.setContentType("application/octet-stream");

		try {
			logger.info(
					"Getting download File :" + reportid + ", FileType :" + filetype + ", SubreportId :" + subreportid);

			File repfile = regreportServices.getDownloadFile(reportid, asondate, fromdate, todate, currency,
					subreportid, secid, dtltype, reportingTime, filetype, instancecode, filter);
			System.out.println(filter + "filter");

			// **CALL COMMON AUDIT FUNCTION HERE**
			//auditService.saveCommonAudit(reportid, filetype);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", repfile.getName());

			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(repfile.length()).body(resource);
		} catch (JRException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/*-----consolidate download for BASEL brf095-car-----created by sanjeev-*/
	/*
	 * @RequestMapping(value = "{reportid}/Downloadconsolidate", method = {
	 * RequestMethod.GET, RequestMethod.POST })
	 * 
	 * @ResponseBody public ResponseEntity<InputStreamResource>
	 * Downloadconsolidate(HttpServletResponse response,
	 * 
	 * @PathVariable("reportid") String reportid, @RequestParam("asondate") String
	 * asondate,
	 * 
	 * @RequestParam("fromdate") String fromdate, @RequestParam("todate") String
	 * todate,
	 * 
	 * @RequestParam("currency") String currency,
	 * 
	 * @RequestParam(value = "subreportid", required = false) String subreportid,
	 * 
	 * @RequestParam(value = "secid", required = false) String secid,
	 * 
	 * @RequestParam(value = "dtltype", required = false) String dtltype,
	 * 
	 * @RequestParam(value = "reportingTime", required = false) String
	 * reportingTime,
	 * 
	 * @RequestParam(value = "instancecode", required = false) String instancecode,
	 * 
	 * @RequestParam("filetype") String filetype, @RequestParam(value = "filter",
	 * required = false) String filter) throws IOException, SQLException {
	 * 
	 * System.out.println("entering controller");
	 * response.setContentType("application/octet-stream");
	 * 
	 * try { logger.info( "Getting download File :" + reportid + ", FileType :" +
	 * filetype + ", SubreportId :" + subreportid); // System.out.println(asondate);
	 * File repfile = regreportServices.getconsolidateDownloadFile(reportid,
	 * asondate, fromdate, todate, currency, subreportid, secid, dtltype,
	 * reportingTime, filetype, instancecode, filter); System.out.println(filter +
	 * "filter");
	 * 
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	 * headers.setContentDispositionFormData("attachment", repfile.getName());
	 * 
	 * InputStreamResource resource = new InputStreamResource(new
	 * FileInputStream(repfile));
	 * 
	 * return ResponseEntity.ok().headers(headers).contentType(MediaType.
	 * APPLICATION_OCTET_STREAM) .contentLength(repfile.length()).body(resource); }
	 * catch (JRException e) { e.printStackTrace(); return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); } }
	 */

	/*
	 * @RequestMapping(value = "{reportid}/save", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String saveReport(@PathVariable("reportid") String
	 * reportid, @RequestParam("asondate") String asondate,
	 * 
	 * @RequestParam("fromdate") String fromdate, @RequestParam("todate") String
	 * todate,
	 * 
	 * @RequestParam(value = "reportingTime", required = false) String
	 * reportingTime,
	 * 
	 * @RequestParam("currency") String currency, HttpServletRequest req) {
	 * 
	 * String userid = (String) req.getSession().getAttribute("USERID");
	 * 
	 * String msg = ""; Date asondate1 = null; Date fromdate1 = null; Date todate1 =
	 * null; try {
	 * 
	 * asondate1 = new SimpleDateFormat("dd-MMM-yyyy").parse(asondate); fromdate1 =
	 * new SimpleDateFormat("dd-MMM-yyyy").parse(fromdate); todate1 = new
	 * SimpleDateFormat("dd-MMM-yyyy").parse(todate); logger.info("Saving Report :"
	 * + reportid);
	 * 
	 * if (reportid.equals("FIM0500")) { msg =
	 * reportServices.saveFIM0500Report(reportid, asondate, fromdate, todate,
	 * currency, reportingTime); }
	 * 
	 * else { msg = reportServices.saveReport(reportid, asondate, fromdate, todate,
	 * currency); }
	 * 
	 * // To update report Status Info table on
	 * reportServices.updateReportStatus(reportid, asondate1, fromdate1, todate1,
	 * userid, "N"); } catch (Exception e) { e.printStackTrace();
	 * logger.info(e.getMessage()); msg =
	 * "error occured. Please contact Administrator"; }
	 * 
	 * return msg; }
	 */

	/*
	 * @GetMapping("/ReportDownloadXLSX") public ResponseEntity<InputStreamResource>
	 * AMLDownloadExcel(HttpServletResponse response,
	 * 
	 * @RequestParam(value = "userid", required = false) String userid,
	 * 
	 * @RequestParam(value = "filename", required = false) String filename,
	 * 
	 * @RequestParam(value = "ref_id", required = false) String ref_id,
	 * 
	 * @RequestParam(value = "Param1", required = false) String input1,
	 * 
	 * @RequestParam(value = "Param2", required = false) String input2,
	 * 
	 * @RequestParam(value = "Param3", required = false) String input3,
	 * 
	 * @RequestParam(value = "Param4", required = false) String input4,
	 * 
	 * @RequestParam(value = "Param5", required = false) String input5,
	 * 
	 * @RequestParam(value = "rptname", required = false) String reportname) throws
	 * IOException, SQLException, JRException, ParseException {
	 * response.setContentType("application/octet-stream"); DateFormat dateFormat =
	 * new SimpleDateFormat("dd-MMM-yyyy"); SimpleDateFormat dateFormat1 = new
	 * SimpleDateFormat("dd/MM/yyyy"); SimpleDateFormat formatter1 = new
	 * SimpleDateFormat("dd-MMM-yyyy");
	 * 
	 * String toDAte = null;
	 * 
	 * Date ConToDate = new Date(); String strDate1 = formatter1.format(ConToDate);
	 * toDAte = formatter1.format(new
	 * SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.add("Content-Disposition",
	 * "attachment; filename=" + filename + "_" + toDAte + ".xlsx");
	 * 
	 * return ResponseEntity.ok().headers(headers).body(new
	 * InputStreamResource(reportServices .getDownloadFileExcel(null, ref_id,
	 * input1, input2, input3, input4, input5, filename, reportname)));
	 * 
	 * }
	 */

	/*
	 * @RequestMapping(value = "/ReportDownloadByScript", method = {
	 * RequestMethod.GET, RequestMethod.POST })
	 * 
	 * @ResponseBody public String AMLDownload(HttpServletRequest request,
	 * HttpServletResponse response,
	 * 
	 * @RequestParam(value = "userid", required = false) String userid,
	 * 
	 * @RequestParam(value = "filename", required = false) String filename,
	 * 
	 * @RequestParam(value = "ref_id", required = false) String ref_id,
	 * 
	 * @RequestParam(value = "Param1", required = false) String input1,
	 * 
	 * @RequestParam(value = "Param2", required = false) String input2,
	 * 
	 * @RequestParam(value = "Param3", required = false) String input3,
	 * 
	 * @RequestParam(value = "Param4", required = false) String input4,
	 * 
	 * @RequestParam(value = "Param5", required = false) String input5,
	 * 
	 * @RequestParam(value = "rptname", required = false) String reportname,
	 * 
	 * @RequestParam(value = "username", required = false) String username) throws
	 * IOException, SQLException {
	 * response.setContentType("application/octet-stream"); String currency = null;
	 * String msg = ""; InputStreamResource resource = null; try { String repfile =
	 * null; File Master = null; try { msg =
	 * reportServices.getDownloadFileFromScript(userid, username, ref_id, input1,
	 * input2, input3, input4, input5, filename, reportname); // Master =
	 * reportServices.getDownloadFile(repfile);
	 * 
	 * } catch (ParseException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // response.setHeader("Content-Disposition", "attachment; filename=" + //
	 * Master.getName()); // resource = new InputStreamResource(new
	 * FileInputStream(Master)); } catch (JRException e) { e.printStackTrace(); }
	 * return msg; }
	 */

	/*
	 * @RequestMapping(value = "/ReportDownloadByData", method = {
	 * RequestMethod.GET, RequestMethod.POST })
	 * 
	 * @ResponseBody public InputStreamResource
	 * ReportDownloadByData(HttpServletRequest request, HttpServletResponse
	 * response,
	 * 
	 * @RequestParam(value = "userid", required = false) String userid,
	 * 
	 * @RequestParam(value = "filepath", required = false) String filepath,
	 * 
	 * @RequestParam(value = "ref_id", required = false) String ref_id,
	 * 
	 * @RequestParam(value = "username", required = false) String username,
	 * 
	 * @RequestParam(value = "downloadondelete", required = false) String
	 * downloadondelete) throws IOException, SQLException {
	 * response.setContentType("application/octet-stream"); String currency = null;
	 * InputStreamResource resource = null; try { File repfile = null; try { repfile
	 * = reportServices.getDownloadFileFromdata(userid, username, ref_id, filepath,
	 * downloadondelete); } catch (ParseException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * response.setHeader("Content-Disposition", "attachment; filename=" +
	 * repfile.getName()); resource = new InputStreamResource(new
	 * FileInputStream(repfile)); } catch (JRException e) { e.printStackTrace(); }
	 * return resource; }
	 */

	/**********************************
	 * RBS
	 *******************************************************************/

	/**********************************
	 * Regulatory Reports
	 *******************************************************************/
	@RequestMapping(value = "{reportid}/PrecheckRR", method = RequestMethod.GET)
	@ResponseBody
	public String reportPreCheckRR(@PathVariable("reportid") String reportid,

			@RequestParam(required = false) String fromdate, @RequestParam("todate") String todate)
			throws ParseException {

		logger.info("Precheck for Report :" + reportid);

		if (todate.length() == 10) {
			return regreportServices.preCheckReportRBS(reportid, fromdate, todate);
		} else {

			try {
				todate = new SimpleDateFormat("dd-MM-yyyy").format(dateFormat.parse(todate));

			} catch (ParseException e) {

				e.printStackTrace();
			}

			return regreportServices.preCheckReportRBS(reportid, fromdate, todate);
		}

	}


	/*
	 * @RequestMapping(value = "RBSDownload", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public InputStreamResource
	 * RBSREPORTGENERATIONDownload(HttpServletResponse response,
	 * 
	 * @RequestParam("reportid") String reportid, @RequestParam("asondate") String
	 * asondate,
	 * 
	 * @RequestParam("fromdate") String fromdate, @RequestParam("todate") String
	 * todate,
	 * 
	 * @RequestParam("currency") String currency,
	 * 
	 * @RequestParam(value = "subreportid", required = false) String subreportid,
	 * 
	 * @RequestParam(value = "secid", required = false) String secid,
	 * 
	 * @RequestParam(value = "dtltype", required = false) String dtltype,
	 * 
	 * @RequestParam(value = "reportingTime", required = false) String
	 * reportingTime,
	 * 
	 * @RequestParam(value = "instancecode", required = false) String instancecode,
	 * 
	 * @RequestParam("filetype") String filetype) throws IOException, SQLException,
	 * ParseException { response.setContentType("application/octet-stream");
	 * 
	 * InputStreamResource resource = null; try { logger.info(
	 * "Getting download File :" + reportid + ", FileType :" + filetype +
	 * ", SubreportId :" + subreportid); // System.out.println(asondate); // String
	 * reportids = "reportId"; File repfile =
	 * regreportServices.getDownloadFile1(reportid, asondate, fromdate, todate,
	 * currency, subreportid, secid, dtltype, reportingTime, filetype,
	 * instancecode);
	 * 
	 * response.setHeader("Content-Disposition", "attachment; filename=" +
	 * repfile.getName()); resource = new InputStreamResource(new
	 * FileInputStream(repfile)); } catch (JRException e) { e.printStackTrace(); }
	 * return resource; }
	 */

	// To show the required report at the first stage
	@RequestMapping(value = "{reportid}/ArchiveRR", method = RequestMethod.POST)
	public ModelAndView ArchievereportView(@PathVariable("reportid") String reportid,
			@RequestParam(value = "function", required = false) String function,
			@RequestParam("asondate") String asondate,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "currency", required = false) String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md,
			HttpServletRequest req, BigDecimal srl_no) {

		System.out.println(reportid);
		System.out.println(todate);

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		if (dtltype.equals("report")) {
			md.addAttribute("menu", "XBRLReports");
			loginServices.SessionLogging("REPORTS" + reportid, "M8", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		} else {
			md.addAttribute("menu", "XBRLArchives");
			loginServices.SessionLogging("ARCHREPORTS" + reportid, "M9", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		}

		logger.info("Get Report :" + reportid);
		logger.info("Get Report :" + asondate);
		try {
			asondate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(asondate));
			// fromdate = dateFormat.format(new
			// SimpleDateFormat("dd/MM/yyyy").parse(fromdate));
			todate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(todate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		logger.info("Get Report :" + asondate);
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		// Assigning required Modal Attributes

		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);

		md.addAttribute("todate", todate);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("type", type);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = new ModelAndView();

		mv = regreportServices.getArchiveReportView(reportid, asondate, fromdate, todate, currency, dtltype,
				subreportid, secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no);

		// System.out.println("----------------------");

		// Page<Object> sup0700RepPage = (Page<Object>)
		// mv.getModelMap().get("reportsummary");

		// sup0700RepPage.getContent().forEach((a)-> System.out.println(a.toString()));

		return mv;

	}

	/*****
	 * Archeve details
	 * 
	 * @throws ParseException
	 ******/
	@RequestMapping(value = "{reportid}/DetailsARCH", method = RequestMethod.GET)
	public ModelAndView ARCHreportDetail(@PathVariable("reportid") String reportid,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam(value = "filter", required = false) String filter, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md)
			throws ParseException {

		logger.info("Getting Report Details :" + reportid);
		logger.info("Assigning Model Attributes :" + reportid);

		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("filter", filter);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportingTime", reportingTime);
//md.addAttribute("instancecode", Integer.parseInt(instancecode));
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("displaymode", "detail");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.ArchgetReportDetails(reportid, instancecode, asondate, fromdate, todate,
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter);

		return mv;

	}

	/*
	 * @RequestMapping(value = "mappingDownload", method = RequestMethod.GET)
	 * 
	 * public String invoiceReportDownload(HttpServletResponse response,
	 * 
	 * @RequestParam(value = "report_name_1", required = false) String
	 * report_name_1,
	 * 
	 * @RequestParam(value = "filetype", required = false) String filetype) throws
	 * IOException, SQLException {
	 * 
	 * System.out.println("gggggggggggg"); System.out.println("gggggggggggg" +
	 * report_name_1); response.setContentType("application/octet-stream");
	 * 
	 * InputStreamResource resource = null; System.out.println(report_name_1);
	 * System.out.println(filetype); try {
	 * 
	 * logger.info("Getting download File :" + report_name_1 + ", FileType :" +
	 * filetype + "");
	 * 
	 * File repfile = reportServices.getFile1(report_name_1, filetype);
	 * 
	 * if (repfile.isFile()) { System.out.println("file is present");
	 * System.out.println(repfile.getName());
	 * System.out.println(repfile.getAbsolutePath()); } if (repfile.isDirectory()) {
	 * System.out.println("directory is present"); }
	 * response.setContentType("application/vnd.ms-excel");
	 * response.setHeader("Content-Disposition", "attachment; filename=" +
	 * repfile.getName()); resource = new InputStreamResource(new
	 * FileInputStream(repfile));
	 * 
	 * FileInputStream fileInputStream = (FileInputStream)
	 * resource.getInputStream(); if (fileInputStream != null) {
	 * fileInputStream.close(); }
	 * 
	 * 
	 * } catch (JRException e) {
	 * 
	 * e.printStackTrace(); } return "XBRLReportRefCode"; // return resource;
	 * 
	 * }
	 */

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf1", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf1(@ModelAttribute("singledetail") BRF1_DETAIL_ENTITY
	 * detail, HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
	 * 
	 * @RequestParam("report_name_1") String report_name_1,
	 * 
	 * @RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
	 * System.out.println("foracid " + foracid); System.out.println("ReportLabel" +
	 * report_label_1); return BRF001ReportService.detailChanges1(detail,
	 * report_label_1, act_balance_amt_lc, foracid, report_name_1,
	 * report_addl_criteria_1);
	 * 
	 * }
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 08-JAN-2024 REPORT NAME : BRF56 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF45 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF73 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF74 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 11-apr-2024 REPORT NAME : BRF62 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF64 PURPOSE :
	 * TESTING
	 */

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf2", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf2(@ModelAttribute("singledetail") BRF2_DETAIL_ENTITY
	 * detail, HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
	 * 
	 * @RequestParam("report_name_1") String report_name_1,
	 * 
	 * @RequestParam("report_addl_criteria_1") String
	 * report_addl_criteria_1,@RequestParam("report_date") String report_date,
	 * 
	 * @RequestParam(value = "reason", required = false) String reason) {
	 * System.out.println("foracid " + foracid); System.out.println("ReportLabel" +
	 * report_label_1); AuditReasonDTO dto = new AuditReasonDTO();
	 * dto.setReason(reason); return BRF002ReportService.detailChanges2(detail,
	 * report_label_1, act_balance_amt_lc, foracid, report_name_1,
	 * report_addl_criteria_1,report_date,dto);
	 * 
	 * }
	 */



	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 08-JAN-2024 REPORT NAME : BRF67 PURPOSE :
	 * TESTING
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 20-feb-2024 REPORT NAME : BRF54 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF76 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf1", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf76(@ModelAttribute("singledetail") BRF1_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1, @RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		System.out.println("Report Date " + report_date);
		//AuditReasonDTO dto = new AuditReasonDTO();
		//dto.setReason(reason);

		return BRF001ReportService.detailChanges1(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1, report_date);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF104 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF40 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 21-MAR-2024 REPORT NAME : BRF44 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF156 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 09-MAR-2024 REPORT NAME : BRF102 PURPOSE
	 * :Development
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 16-MAR-2024 REPORT NAME : BRF39 PURPOSE
	 * :Development
	 */

	/*
	 * /*
	 * 
	 * @RequestMapping(value = "CustomerDetailEditBrf67", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf67(@ModelAttribute("singledetail") BRF67_DETAILENTITY
	 * detail,
	 * 
	 * HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no " + foracid);
	 * 
	 * return brf96ReportService.detailChanges96(detail, foracid,
	 * report_addl_criteria_1, act_balance_amt_lc, report_label_1, report_name_1); }
	 */

//	@RequestMapping(value = "CustomerDetailEditBrf94", method = RequestMethod.POST)
//
//	@ResponseBody
//	public String CustomerDetailEditBrf94(@ModelAttribute("singledetail") BRF94_DETAILENTITY detail,
//			HttpServletRequest hs, @RequestParam("foracid") String foracid,
//
//			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
//
//			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
//
//			@RequestParam("report_label_1") String report_label_1,
//
//			@RequestParam("report_name_1") String report_name_1,
//
//			@RequestParam("report_addl_criteria_2") String report_addl_criteria_2,
//
//			@RequestParam("purpose_of_rem") String purpose_of_rem) {
//		System.out.println("edit");
//		System.out.println("Acct no " + foracid);
//		System.out.println("Acct no " + report_addl_criteria_2);
//		System.out.println("Acct no " + purpose_of_rem);
//		return BRF94ReportService.detailChanges94(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
//				report_label_1, report_name_1, report_addl_criteria_2, purpose_of_rem);
//	}

	/*
	 * CREATED BY : DHANALAKSHMI CREATED ON : 15-02-2024 REPORT NAME : BRF14 PURPOSE
	 * : DEVELOPMENT
	 */



	/*
	 * @PostMapping("modifyRecord80")
	 * 
	 * @ResponseBody public String Modifybrf80( @RequestParam("fromdate") String
	 * fromdate,
	 * 
	 * @RequestParam("todate") String todate,
	 * 
	 * @RequestParam(value = "asondate", required = false) String userID,
	 * 
	 * @RequestParam(value = "ROW104", required = false) String ROW104,
	 * 
	 * @RequestParam(value = "ROW105", required = false) String ROW105,
	 * 
	 * @RequestParam(value = "ROW106", required = false) String ROW106,
	 * 
	 * @RequestParam(value = "ROW107", required = false) String ROW107,
	 * 
	 * @RequestParam(value = "ROW108", required = false) String ROW108,
	 * 
	 * @RequestParam(value = "ROW109", required = false) String ROW109,
	 * 
	 * @RequestParam(value = "ROW110", required = false) String ROW110,
	 * 
	 * @RequestParam(value = "ROW111", required = false) String ROW111,
	 * 
	 * @RequestParam(value = "ROW112", required = false) String ROW112,
	 * 
	 * @RequestParam(value = "ROW113", required = false) String ROW113, Model md,
	 * HttpServletRequest req) throws IOException, ParseException {
	 * 
	 * List<BRF80_ENTITY> BRF80_ENTITY1 = BRF80_ENTITY_REP.findAllData(todate);
	 * 
	 * if(BRF80_ENTITY1.size() == 1 ) {
	 * 
	 * for (BRF80_ENTITY BRF80_ENT :BRF80_ENTITY1 ) {
	 * 
	 * ROW104 = (ROW104 == null )? "0":ROW104; ROW105 = (ROW105 == null )?
	 * "0":ROW105; ROW106 = (ROW106 == null )? "0":ROW106; ROW107 = (ROW107 == null
	 * )? "0":ROW107; ROW108 = (ROW108 == null )? "0":ROW108; ROW109 = (ROW109 ==
	 * null )? "0":ROW109; ROW110 = (ROW110 == null )? "0":ROW110; ROW111 = (ROW111
	 * == null )? "0":ROW111; ROW112= (ROW112== null )? "0":ROW112; ROW113 = (ROW113
	 * == null )? "0":ROW113;
	 * 
	 * ROW104 = (ROW104 == "" )? "0":ROW104; ROW105 = (ROW105 == "" )? "0":ROW105;
	 * ROW106 = (ROW106 == "" )? "0":ROW106; ROW107 = (ROW107 == "" )? "0":ROW107;
	 * ROW108 = (ROW108 == "" )? "0":ROW108; ROW109 = (ROW109 == "" )? "0":ROW109;
	 * ROW110 = (ROW110 == "" )? "0":ROW110; ROW111 = (ROW111 == "" )? "0":ROW111;
	 * ROW112 = (ROW112 == "" )? "0":ROW112; ROW113 = (ROW113 == "" )? "0":ROW113;
	 * 
	 * BigDecimal R1_unused_unfunded_ccf = new BigDecimal(ROW107);
	 * R1_unused_unfunded_ccf = R1_unused_unfunded_ccf.setScale(0,
	 * BigDecimal.ROUND_UP); BigDecimal R2_unused_unfunded_ccf = new
	 * BigDecimal(ROW112); R2_unused_unfunded_ccf =
	 * R2_unused_unfunded_ccf.setScale(0, BigDecimal.ROUND_UP);
	 * 
	 * BRF80_ENT.setR1_credit_rating(ROW104); BRF80_ENT.setR1_credit_type(ROW105);
	 * BRF80_ENT.setR1_economic_sec(ROW106);
	 * BRF80_ENT.setR1_unused_unfunded_ccf(R1_unused_unfunded_ccf);
	 * BRF80_ENT.setR1_remarks(ROW108); BRF80_ENT.setR2_credit_rating(ROW109);
	 * BRF80_ENT.setR2_credit_type(ROW110); BRF80_ENT.setR2_economic_sec(ROW111);
	 * BRF80_ENT.setR2_unused_unfunded_ccf(R2_unused_unfunded_ccf);
	 * BRF80_ENT.setR2_remarks(ROW113);
	 * 
	 * BigDecimal R6_amt = new BigDecimal(0) ;
	 * 
	 * BigDecimal R1_amt = BRF7_ENT.getR1_amount();
	 * 
	 * BigDecimal R2_amt = BRF7_ENT.getR2_amount();
	 * 
	 * BigDecimal R5_amt = BRF7_ENT.getR5_amount();
	 * 
	 * BigDecimal R6_amt1 = R6_amt.add(R3_amount).add(R1_amt).add(R2_amt);
	 * 
	 * R6_amt1 = R6_amt1.subtract(R4_amount);
	 * 
	 * R6_amt1 = R6_amt1.add(R5_amt);
	 * 
	 * System.out.println(R6_amt1); BRF7_ENT.setR6_amount(R6_amt1);
	 * 
	 * BigDecimal R26_amt = R6_amt1;
	 * 
	 * if (R26_amt.compareTo(BigDecimal.ZERO) == 0) {
	 * 
	 * } else {
	 * 
	 * // Calculate the percentage BigDecimal percentage = R6_amt1.divide(R26_amt,
	 * 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
	 * BRF7_ENT.setR27_amount(percentage);
	 * 
	 * System.out.println(percentage); }
	 * 
	 * BRF80_ENTITY_REP.save(BRF80_ENT);
	 * 
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * return todate;
	 * 
	 * }
	 */

	@RequestMapping(value = "Generateloginotp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String Generateloginotp(@RequestParam("Userid") String Userid) {
		String msg = "success";
		System.out.println(msg);
		return msg;
	}

}




