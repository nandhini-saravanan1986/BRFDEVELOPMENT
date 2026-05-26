package com.bornfire.BRF.entities;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DataMapping_Repo extends JpaRepository<DataMapping_Entity, BigDecimal> {

	@Query(value = "SELECT COALESCE(MAX(MAPPING_ID), 0) FROM BRF_DATA_MAPPINGS", nativeQuery = true)
	BigDecimal findMaxId();
	
}
