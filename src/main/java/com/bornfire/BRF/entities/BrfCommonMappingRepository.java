package com.bornfire.BRF.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrfCommonMappingRepository
        extends JpaRepository<BrfCommonMapping, String> {

    /**
     * Returns all rows for a specific report code.
     * Used to populate the "Mapped Accounts" tab.
     */
    List<BrfCommonMapping> findByReportCode(@Param("reportCode") String reportCode);

    /**
     * Returns only the distinct ACCOUNT_ID_BACID values already mapped
     * for a given report code — used internally by the unmapped query.
     */
    @Query(
    	    value = "SELECT DISTINCT c.ACCOUNT_ID_BACID " +
    	            "FROM BRF_COMMON_MAPPING_TABLE c " +
    	            "WHERE c.REPORT_CODE = :reportCode",
    	    nativeQuery = true
    	)
    	List<String> findMappedAccountIds(@Param("reportCode") String reportCode);
}