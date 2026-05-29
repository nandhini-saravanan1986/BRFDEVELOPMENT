package com.bornfire.BRF.services;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;

import javax.validation.constraints.NotNull;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

//import com.bornfire.BRF.entities.BRBS.YourFormData;

import net.sf.jasperreports.engine.JRException;

@Component
@Service
@Transactional
@ConfigurationProperties("output")
public class RegulatoryReportServices {

	private static final Logger logger = LoggerFactory.getLogger(RegulatoryReportServices.class);

	@NotNull
	private String exportpath;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	BRF002ReportService brf002ReportService;
	@Autowired
	BRF003ReportService brf003ReportService;
	@Autowired
	BRF004ReportService brf004ReportService;
	@Autowired
	BRF010ReportService brf010ReportService;
	@Autowired
	BRF011ReportService brf011ReportService;
	@Autowired
	BRF012ReportService brf012ReportService;
	@Autowired
	BRF013ReportService brf013ReportService;
	@Autowired
	BRF014ReportService brf014ReportService;
	
	
	@Autowired
	BRF005ReportService brf005ReportService;
	@Autowired
	BRF007ReportService brf007ReportService;
	@Autowired
	BRF008ReportService brf008ReportService;
	@Autowired
	BRF009ReportService brf009ReportService;

	String getExportpath() {
		return exportpath;
	}

	public void setExportpath(String exportpath) {
		this.exportpath = exportpath;
	}

	public ModelAndView getReportView(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no, String req) {

		ModelAndView repsummary = new ModelAndView();

		logger.info("Getting View for the Report :" + reportId);
		switch (reportId) {

		case "BRF001":
			repsummary = brf001ReportService.getBRF001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF002":
			repsummary = brf002ReportService.getBRF002View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF003":
			repsummary = brf003ReportService.getBRF003View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF004":
			repsummary = brf004ReportService.getBRF004View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
			
		case "BRF010":
		        repsummary = brf010ReportService.getBRF010View(reportId, fromdate, todate, currency, dtltype, pageable);
		        break;

		case "BRF005":
			repsummary = brf005ReportService.getBRF005View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF007":
			repsummary = brf007ReportService.getBRF007View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF008":
			repsummary = brf008ReportService.getBRF008View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF009":
			repsummary = brf009ReportService.getBRF009View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

	    case "BRF011":
		    	repsummary = brf011ReportService.getBRF011View( reportId, fromdate, todate, currency, dtltype, pageable);
		        break;

		case "BRF012":
		        repsummary = brf012ReportService.getBRF012View(reportId, fromdate, todate, currency, dtltype, pageable);
		        break;
		
	    case "BRF013":
			    	repsummary = brf013ReportService.getBRF013View( reportId, fromdate, todate, currency, dtltype, pageable);
			        break;

	    case "BRF014":
			        repsummary = brf014ReportService.getBRF014View(reportId, fromdate, todate, currency, dtltype, pageable);
			        break;
			}
		    return repsummary;
		}

	public ModelAndView getReportSummary(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no, String req) {

		ModelAndView repsummary = new ModelAndView();
		logger.info("Getting Summary for the Report :" + reportId);
		switch (reportId) {

		case "BRF001":
			repsummary = brf001ReportService.getBRF001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF002":
			repsummary = brf002ReportService.getBRF002View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF003":
			repsummary = brf003ReportService.getBRF003View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF004":
			repsummary = brf004ReportService.getBRF004View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF010":
				repsummary = brf010ReportService.getBRF010View(reportId, fromdate, todate, currency, dtltype, pageable);
				break;
	    case "BRF011":
	    	repsummary=brf011ReportService.getBRF011View(reportId,fromdate,todate,currency,dtltype,pageable);
			break;
	    
	    case "BRF012":
	    	repsummary=brf012ReportService.getBRF012View(reportId,fromdate,todate,currency,dtltype,pageable);
	    break;
	    case "BRF013":
	    	repsummary=brf013ReportService.getBRF013View(reportId,fromdate,todate,currency,dtltype,pageable);
			break;
	    
	    case "BRF014":
	    	repsummary=brf014ReportService.getBRF014View(reportId,fromdate,todate,currency,dtltype,pageable);
	    break;
	    

		case "BRF005":
			repsummary = brf005ReportService.getBRF005View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF007":
			repsummary = brf007ReportService.getBRF007View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF008":
			repsummary = brf008ReportService.getBRF008View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF009":
			repsummary = brf009ReportService.getBRF009View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		}

		return repsummary;
	}


	public ModelAndView getReportDetails(String reportId, String instanceCode, String asondate, String fromdate,
			String todate, String currency, String reportingTime, String dtltype, String subreportid, String secid,
			Pageable pageable, String Filter, String searchVal) {

		ModelAndView repdetail = new ModelAndView();
		logger.info("Getting Details for the Report :" + reportId);

		switch (reportId) {

		case "BRF001":
			repdetail = brf001ReportService.getBRF001currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;
		case "BRF002":
			repdetail = brf002ReportService.getBRF002currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;

		case "BRF003":
			repdetail = brf003ReportService.getBRF003currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;

		case "BRF004":
			repdetail = brf004ReportService.getBRF004currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;

		case "BRF010":
			repdetail = brf010ReportService.getBRF010currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;

		case "BRF011":
			repdetail = brf011ReportService.getBRF011currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;
		case "BRF012":
			repdetail = brf012ReportService.getBRF012currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;

		case "BRF013":
			repdetail = brf013ReportService.getBRF013currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);
			break;
		case "BRF014":
			repdetail = brf014ReportService.getBRF014currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter, searchVal);

		case "BRF005":
			repdetail = brf005ReportService.getBRF005currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF007":
			repdetail = brf007ReportService.getBRF007currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF008":
			repdetail = brf008ReportService.getBRF008currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF009":
			repdetail = brf009ReportService.getBRF009currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);

			break;
		}
		return repdetail;
	}

	public File getDownloadFile(String reportId, String asondate, String fromdate, String todate, String currency,
			String subreportid, String secid, String dtltype, String reportingTime, String filetype,
			String instancecode, String filter) throws JRException, SQLException, IOException {

		File repfile = null;

		logger.info("Getting Report File for : " + reportId + " in " + filetype + " format");

		switch (reportId) {

		case "BRF001":
			repfile = brf001ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype, filter);
			break;
		case "BRF002":
			repfile = brf002ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype, filter);
			break;

		/*
		 * case "BRF003": repfile = brf003ReportService.getFile(reportId, fromdate,
		 * todate, currency, dtltype, filetype, filter); break;
		 */
		case "BRF004":
			repfile = brf004ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF010":
			repfile = brf010ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF011":
			repfile = brf011ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF012":
			repfile = brf012ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF013":
			repfile = brf013ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF014":
			repfile = brf014ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF003":
//			repfile = brf003ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype, filter);
			repfile = brf003ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF005":
			repfile = brf005ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF007":
			repfile = brf007ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF008":
			repfile = brf008ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF009":
			repfile = brf009ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
			
		}

		return repfile;

	}

	/****** arch view ****/
	public ModelAndView getArchiveReportView(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no) {

		ModelAndView repsummary = new ModelAndView();

		logger.info("Getting View for the Report :" + reportId);
		switch (reportId) {

		case "BRF001":
			repsummary = brf001ReportService.getArchieveBRF001View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF002":
			repsummary = brf002ReportService.getArchieveBRF002View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF003":
			repsummary = brf003ReportService.getArchieveBRF003View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF004":
			repsummary = brf004ReportService.getArchieveBRF004View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF010":
			repsummary = brf010ReportService.getArchieveBRF010View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF011":
			repsummary = brf011ReportService.getArchieveBRF011View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF012":
			repsummary = brf012ReportService.getArchieveBRF012View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF013":
			repsummary = brf013ReportService.getArchieveBRF013View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF014":
			repsummary = brf014ReportService.getArchieveBRF014View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		}

		return repsummary;
	}

	public String preCheckReportRBS(String reportid, String fromdate, String todate) {

		String msg = "";

		logger.info("Report precheck : " + reportid);

		switch (reportid) {
		case "BRF001":
			msg = brf001ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF002":
			msg = brf002ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF003":
			msg = brf003ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF004":
			msg = brf004ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF010":
			msg = brf010ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF011":
			msg = brf011ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF012":
			msg = brf012ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF013":
			msg = brf013ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF014":
			msg = brf014ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF005":
			msg = brf005ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF007":
			msg = brf007ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF008":
			msg = brf008ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF009":
			msg = brf009ReportService.preCheck(reportid, fromdate, todate);

			break;
		default:
			logger.info("default -> preCheck()");
			msg = "Master - need to process";
		}

		return msg;
	}

	public ModelAndView ArchgetReportDetails(String reportId, String instanceCode, String asondate, String fromdate,
			String todate, String currency, String reportingTime, String dtltype, String subreportid, String secid,
			Pageable pageable, String Filter) throws ParseException {

		ModelAndView repdetail = new ModelAndView();
		logger.info("Getting Details for the Report :" + reportId);

		switch (reportId) {
		case "BRF001":
			repdetail = brf001ReportService.ARCHgetBRF001currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF002":
			repdetail = brf002ReportService.ARCHgetBRF002currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF003":
			repdetail = brf003ReportService.ARCHgetBRF003currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF004":
			repdetail = brf004ReportService.ARCHgetBRF004currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF010":
			repdetail = brf010ReportService.ARCHgetBRF010currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF011":
			repdetail = brf011ReportService.ARCHgetBRF011currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF012":
			repdetail = brf012ReportService.ARCHgetBRF012currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF013":
			repdetail = brf013ReportService.ARCHgetBRF013currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF014":
			repdetail = brf014ReportService.ARCHgetBRF014currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		}

		return repdetail;
	}
}