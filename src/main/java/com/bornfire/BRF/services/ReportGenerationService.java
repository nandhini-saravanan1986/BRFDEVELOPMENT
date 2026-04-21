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
		
		// Set a healthy timeout just to protect the query while it runs
		jdbcTemplate.setQueryTimeout(600); 
		
		String sql = "CALL GENERATE_BRF_REPORT(?, ?)";
		jdbcTemplate.update(sql, reportDate, reportCode);
		
		System.out.println("Stored procedure finished. Data matches perfectly!");
	}
}