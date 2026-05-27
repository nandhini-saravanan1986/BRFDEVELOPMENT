package com.bornfire.BRF.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BRF8_DetaiRep extends JpaRepository<BRF8_DETAIL_ENTITY, String> {
	@Query(value = "SELECT * FROM  BRF8_DETAIL_ENTITY WHERE foracid =?1", nativeQuery = true)
	BRF8_DETAIL_ENTITY getallDetails(String foracid);
	
	@Query(
		    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
		            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
		            "FROM BRF8_DETAILTABLE " +
		            "ORDER BY cust_id, report_label_1",
		    nativeQuery = true
		)
		List<Object[]> find();
}
