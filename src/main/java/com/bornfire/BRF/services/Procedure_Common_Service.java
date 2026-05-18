package com.bornfire.BRF.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.bornfire.BRF.entities.ETL_MONITORING_DETAILS_ENTITY;
import com.bornfire.BRF.entities.ETL_MONITORING_DETAILS_REPO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Procedure_Common_Service {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	ETL_MONITORING_DETAILS_REPO ETL_MONITORING_DETAILS_REPO;

	@Autowired
	PlatformTransactionManager transactionManager;

	public void executeCommonProcedure(String formattedDate, String rptCode) {
		String sql = "CALL COMMON_TRIGGERING_PROCEDURE(?, ?)";
		ETL_MONITORING_DETAILS_ENTITY entity = new ETL_MONITORING_DETAILS_ENTITY();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		//SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");//for producing error for ETL testing
		entity.setError_id(ETL_MONITORING_DETAILS_REPO.findMaxidvalue().add(BigDecimal.ONE));
		entity.setModule_id("3");
		entity.setModule_name("Monthly 1 Common Procedure");
		entity.setProcedure_name("COMMON_TRIGGERING_PROCEDURE('" + formattedDate + "','" + rptCode + "')");
		entity.setRecr_time(new Date());
		entity.setDel_flg("N");
		Date date = null;
		try {
			date = formatter.parse(formattedDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Date in executeCommonProcedure : "+date);
			jdbcTemplate.update(sql, formattedDate, rptCode);
			//jdbcTemplate.update(sql, date, rptCode);;//for producing error for ETL testing
			System.out.println("procedure executed successfully for report: " + rptCode);

			entity.setReport_date(date);
			entity.setError_details("Procedure Executed Successfully");
			entity.setStatus("Y");
			ETL_MONITORING_DETAILS_REPO.save(entity);

		} catch (Exception e) {
			System.out.println("Error executing procedure: " + e.getMessage());

			entity.setReport_date(date);
			entity.setError_details(e.getMessage());
			entity.setStatus("N");

			// Create a new transaction strictly for saving the error log
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			transactionTemplate.execute(status -> {
				ETL_MONITORING_DETAILS_REPO.save(entity);
				return null;
			});

			e.printStackTrace();
			throw new RuntimeException("Failed to execute common procedure", e);
		}
	}

	public void executeetlProcedure(BigDecimal id) {

		ETL_MONITORING_DETAILS_ENTITY data = ETL_MONITORING_DETAILS_REPO.findById(id).get();
		data.setDel_flg("Y");
		ETL_MONITORING_DETAILS_REPO.save(data);

		String sql = "CALL " + data.getProcedure_name();
		ETL_MONITORING_DETAILS_ENTITY entity = new ETL_MONITORING_DETAILS_ENTITY();
		entity.setError_id(ETL_MONITORING_DETAILS_REPO.findMaxidvalue().add(BigDecimal.ONE));
		entity.setModule_id("3");
		entity.setModule_name("Monthly 1 Common Procedure");
		entity.setProcedure_name(data.getProcedure_name());
		entity.setRecr_time(new Date());
		entity.setReport_date(data.getReport_date());

		try {
			jdbcTemplate.update(sql);
			System.out.println("ETL procedure executed successfully for report: ");
			
			entity.setError_details("Procedure Executed Successfully");
			entity.setStatus("S");
			entity.setDel_flg("N");
			ETL_MONITORING_DETAILS_REPO.save(entity);

		} catch (Exception e) {
			System.out.println("Error executing procedure: " + e.getMessage());

			entity.setError_details(e.getMessage());
			entity.setStatus("N");
			entity.setDel_flg("N");

			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			transactionTemplate.execute(status -> {
				ETL_MONITORING_DETAILS_REPO.save(entity);
				return null;
			});

			e.printStackTrace();
			throw new RuntimeException("Failed to execute common procedure", e);
		}
	}

}
