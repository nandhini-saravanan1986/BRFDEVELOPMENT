package com.bornfire.BRF.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerationService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void generateFullReport(String reportDate, String reportCode) {
		System.out.println("Triggering Database Procedure for Report Code: " + reportCode);

		jdbcTemplate.setQueryTimeout(600);

		String sql = "CALL GENERATE_BRF_REPORT(?, ?, ?)";
		String tableName = getTablename(reportCode);
		if (reportCode != null && !reportCode.equals(null)) {
			jdbcTemplate.update(sql, reportDate, reportCode, tableName);
			System.out.println("Stored procedure finished.");
		} else {
			System.out.println("Error");
		}
	}

	public String getTablename(String reportcode) {
		String tablename = null;
		switch (reportcode) {
		case "BRF001":
			tablename = "BRF1_MAPPING_TABLE";
			break;
		case "BRF002":
			tablename = "BRF2_MAPPING_TABLE";
			break;
		default:
			System.out.println("The report Report is not found ");
			break;
		}
		return tablename;
	}
}