package com.bornfire.BRF.entities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
	
	// Used in submitAccounts() 
	// All 3 params are optional:
    //   :dataType  → pass "TREASURY" if source=TREASURY, else pass NULL
    //   :glHead    → pass "%value%" if provided,          else pass NULL
    //   :glSubHead → pass "%value%" if provided,          else pass NULL
    //
    // Oracle trick: (:param IS NULL OR col LIKE :param)
    //   → when param is NULL  the condition is skipped (full scan on that col)
    //   → when param has value the LIKE filter is applied at DB level
	@Query(
	        value = "SELECT * FROM BRF_BASE_MAPPING_TABLE " +
	                "WHERE (:dataType  IS NULL OR UPPER(DATA_TYPE)       = UPPER(:dataType))  " +
	                "AND   (:glHead    IS NULL OR UPPER(GL_HEAD)         LIKE UPPER(:glHead)) " +
	                "AND   (:glSubHead IS NULL OR UPPER(GL_SUBHEAD_CODE) LIKE UPPER(:glSubHead))",
	        nativeQuery = true
	    )
    List<BrfBaseMapping> findByFilters(
        @Param("dataType")  String dataType,
        @Param("glHead")    String glHead,
        @Param("glSubHead") String glSubHead
    );
    
 // Spring Data derives the SQL automatically from the method name.
    // No @Query needed.
    // Returns empty Optional if account not found.
    Optional<BrfBaseMapping> findByAccountIdBacid(String accountIdBacid);
}