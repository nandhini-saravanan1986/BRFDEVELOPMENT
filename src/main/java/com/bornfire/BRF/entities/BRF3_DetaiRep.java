package com.bornfire.BRF.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bornfire.BRF.entities.BRFDETAILID;

public interface BRF3_DetaiRep extends JpaRepository<BRF3_DETAILTABLE, BRFDETAILID> {
	@Query(value = "SELECT * FROM  BRF3_DETAILTABLE WHERE foracid =?1", nativeQuery = true)
	BRF3_DETAILTABLE getallDetails(String foracid);
	
	@Query(value = "SELECT * FROM  BRF3_DETAILTABLE WHERE FORACID =?1 AND REPORT_DATE=?2", nativeQuery = true)
	BRF3_DETAILTABLE getbyaccnoanddate(String acct_no,String report_date);
	
	@Query(
		    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
		            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
		            "FROM BRF3_DETAILTABLE " +
		            "ORDER BY cust_id, report_label_1",
		    nativeQuery = true
		)
		List<Object[]> find();

}
