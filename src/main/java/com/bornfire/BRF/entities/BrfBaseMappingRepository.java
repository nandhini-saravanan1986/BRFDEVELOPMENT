package com.bornfire.BRF.entities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrfBaseMappingRepository
        extends JpaRepository<BrfBaseMapping, String> {

    /**
     * Returns all base-mapping rows whose ACCOUNT_ID_BACID does NOT appear
     * in BRF_COMMON_MAPPING_TABLE for the given reportCode.
     * These are the "unmapped" accounts.
     */
	@Query(
		    value = "SELECT * FROM BRF_BASE_MAPPING_TABLE b " +
		            "WHERE b.ACCOUNT_ID_BACID NOT IN ( " +
		            "SELECT c.ACCOUNT_ID_BACID " +
		            "FROM BRF_COMMON_MAPPING_TABLE c " +
		            "WHERE c.REPORT_CODE = :reportCode)",
		    nativeQuery = true
		)
		List<BrfBaseMapping> findUnmappedAccounts(@Param("reportCode") String reportCode);
}