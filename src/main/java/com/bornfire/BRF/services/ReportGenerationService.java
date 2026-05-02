package com.bornfire.BRF.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerationService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void executeCommonTrigger(String reportDate, String reportCode) {

		System.out.println("Triggering Master Procedure in the background for: " + reportCode);

		CompletableFuture.runAsync(() -> {
			try {
				System.out.println("Background thread started. Executing heavy Oracle procedure...");

				jdbcTemplate.setQueryTimeout(7200);

				String sql = "CALL COMMON_TRIGGERING_PROCEDURE(?, ?)";
				jdbcTemplate.update(sql, reportDate, reportCode);

				System.out.println("Background procedure finished successfully!");

			} catch (Exception e) {
				System.err.println("Background execution failed: " + e.getMessage());
			}
		});
		System.out.println("Handed off to database. Freeing up the network socket.");
	}

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
		case "BRF004":
			tablename = "BRF4_MAPPING_TABLE";
			break;
		default:
			System.out.println("The report Report is not found ");
			break;
		}
		return tablename;
	}
}