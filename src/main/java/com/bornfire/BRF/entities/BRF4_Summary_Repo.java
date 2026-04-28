package com.bornfire.BRF.entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BRF4_Summary_Repo extends JpaRepository<BRF4_ENTITY, Date>{
//	@Query(value = "SELECT * FROM BRF4_SUMMARYTABLE " +
//            "WHERE report_code = :reportCode " +
//            "AND TRUNC(report_date) <> TRUNC(:reportDate) " +
//            "ORDER BY report_date ASC",
//    nativeQuery = true)
//	List<BRF1_REPORT_ENTITY> findMismatchReports(
//     @Param("reportCode") String reportCode,
//     @Param("reportDate") Date reportDate);
	
	@Query(value = "SELECT * FROM BRF4_SUMMARYTABLE " +
            "WHERE report_code = :reportCode " +
            "AND report_date = :reportDate ",
//            "AND version = 'Archival'",
    nativeQuery = true)
	List<BRF4_ENTITY> findByReportCodeAndDate(
     @Param("reportCode") String reportCode,
     @Param("reportDate") String reportDate);
}
