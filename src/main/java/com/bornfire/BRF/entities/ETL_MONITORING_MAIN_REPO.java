package com.bornfire.BRF.entities;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ETL_MONITORING_MAIN_REPO extends JpaRepository<ETL_MONITORING_MAIN_ENTITY, BigDecimal> {

	@Query(value = "select * from ETL_MONITORING_MAIN_TABLE ", nativeQuery = true)
	List<ETL_MONITORING_MAIN_ENTITY> getdatalist();

}