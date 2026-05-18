package com.bornfire.BRF.entities;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ETL_MONITORING_DETAILS_REPO extends JpaRepository<ETL_MONITORING_DETAILS_ENTITY, BigDecimal> {

	@Query(value = "select * from ETL_MONITORING_DETAILS_TABLE ", nativeQuery = true)
	List<ETL_MONITORING_DETAILS_ENTITY> getdatalist();

	@Query(value = "select * from ETL_MONITORING_DETAILS_TABLE where MODULE_ID = ?1 ", nativeQuery = true)
	List<ETL_MONITORING_DETAILS_ENTITY> getbymoduleid(BigDecimal id);

	@Query(value = "SELECT * FROM ETL_MONITORING_DETAILS_TABLE WHERE MODULE_ID = ?1 AND STATUS = ?2 AND DEL_FLG = 'N' ORDER BY RECR_TIME DESC", nativeQuery = true)
	List<ETL_MONITORING_DETAILS_ENTITY> getbymoduleidandstatus(BigDecimal id, String status);

	@Query(value = "SELECT MAX(ERROR_ID) FROM ETL_MONITORING_DETAILS_TABLE", nativeQuery = true)
	BigDecimal findMaxidvalue();
}
