package com.bornfire.BRF.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.BRF.entities.BRFReportsMasterRep;

@Service
@Transactional
@ConfigurationProperties("output")
public class ReportServices {

	@Autowired
	BRFReportsMasterRep brfReportsMasterRep;

	public class ReportTitle {

		String reportName;
		String reportId;
		Date report_date;
		String domain;
		Character completedFlg;
		String frequency;

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}

		public String getReportId() {
			return reportId;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}

		public Date getReport_date() {
			return report_date;
		}

		public void setReport_date(Date report_date) {
			this.report_date = report_date;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Character getCompletedFlg() {
			return completedFlg;
		}

		public void setCompletedFlg(Character completedFlg) {
			this.completedFlg = completedFlg;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public ReportTitle(String reportName, String reportId) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
		}

		public ReportTitle(String reportName, String reportId, Date reportDate, String domain, Character completedFlg,
				String frequency) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
			this.report_date = reportDate;
			this.domain = domain;
			this.completedFlg = completedFlg;
			this.frequency = frequency;
		}

	}

	public List<String> getDomainList() {

		return brfReportsMasterRep.getDomainList();
	}

}
