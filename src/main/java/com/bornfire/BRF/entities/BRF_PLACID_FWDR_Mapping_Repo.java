package com.bornfire.BRF.entities;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BRF_PLACID_FWDR_Mapping_Repo extends JpaRepository<BRF_PLACID_FWDR_Mapping, Long> {

	@Query(value = "SELECT * FROM BRF_PLACID_FWDR_MAPPING_TABLE WHERE REPORT_CODE = :reportCode AND SCREEN_NAME = :screenName", nativeQuery = true)
	List<BRF_PLACID_FWDR_Mapping> findByReportCodeAndScreenName(@Param("reportCode") String reportCode,
			@Param("screenName") String screenName);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM BRF_PLACID_FWDR_MAPPING_TABLE WHERE ID = :id", nativeQuery = true)
	void deleteByMappingId(@Param("id") Long id);

}