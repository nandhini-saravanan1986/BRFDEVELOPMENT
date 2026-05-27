

package com.bornfire.BRF.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BRF5_DetaiRep1 extends JpaRepository<BRF5_Detail_Entity, String> {
	@Query(value = "SELECT * FROM  BRF5_DETAILTABLE WHERE foracid =?1", nativeQuery = true)
	BRF5_Detail_Entity getallDetails(String foracid);
	@Query(value = "SELECT * FROM BRF5_DETAILTABLE WHERE foracid = ?1 AND report_addl_criteria_2 = ?2 ", nativeQuery = true)
	BRF5_Detail_Entity findByCustomCriteria(
       String foracid,
       String report_addl_criteria_2
    );
	
	@Query(
		    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
		            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
		            "FROM BRF5_DETAILTABLE " +
		            "ORDER BY cust_id, report_label_1",
		    nativeQuery = true
		)
		List<Object[]> find();

}
