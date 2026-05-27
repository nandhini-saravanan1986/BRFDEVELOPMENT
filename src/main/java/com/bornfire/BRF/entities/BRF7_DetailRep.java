package com.bornfire.BRF.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BRF7_DetailRep extends JpaRepository<BRF7_DETAIL_ENTITY, String> {
	@Query(value = "SELECT * FROM  BRF7_DETAILTABLE WHERE acct_no =?1", nativeQuery = true)
	BRF7_DETAIL_ENTITY getallDetails(String acct_no);
	
	@Query(
		    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
		            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
		            "FROM BRF7_DETAILTABLE " +
		            "ORDER BY cust_id, report_label_1",
		    nativeQuery = true	
		)
		List<Object[]> find();
}
