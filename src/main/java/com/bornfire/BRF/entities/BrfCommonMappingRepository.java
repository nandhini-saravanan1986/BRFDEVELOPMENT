package com.bornfire.BRF.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrfCommonMappingRepository
        extends JpaRepository<BrfCommonMapping, BrfCommonMappingId> {

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
    
    
    // Used in submitAccounts() Replaces: checkOtherSql
 // Returns a record with the same key (accountId+rowId+columnId) but under
    // a DIFFERENT report code — used to BLOCK duplicate cross-report mapping.
    @Query(
        value = "SELECT * FROM BRF_COMMON_MAPPING_TABLE " +
                "WHERE ACCOUNT_ID_BACID = :accountId " +
                "AND ROW_ID = :rowId " +
                "AND COLUMN_ID = :columnId " +
                "AND REPORT_CODE != :reportCode " +
                "AND ROWNUM = 1",
        nativeQuery = true
    )
    Optional<BrfCommonMapping> findConflictingMapping(
        @Param("accountId")  String accountId,
        @Param("rowId")      String rowId,
        @Param("columnId")   String columnId,
        @Param("reportCode") String reportCode
    );
    
//Used in submitAccounts()    
 // Returns a record with the same key under the SAME report code
    // — used to decide UPDATE vs INSERT.
    // Spring Data auto-derives the SQL from the method name (no @Query needed).
    Optional<BrfCommonMapping> findByAccountIdBacidAndRowIdAndReportCode(
        String accountIdBacid,
        String rowId,
        String reportCode
    );
    
    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO BRF_COMMON_MAPPING_TABLE " +
                "(GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, " +
                " CURRENCY, DATA_TYPE, ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, " +
                " ENTRY_USER, MODIFY_USER, AUTH_USER, ENTRY_TIME, MODIFY_TIME, AUTH_TIME, " +
                " REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, REPORT_DESC, " +
                " REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, REPORT_ADDL_CRITERIA_3, " +
                " REPORT_CODE, ROW_ID, COLUMN_ID, ACCOUNT_BALANCE_LC, SOL_ID, " +
                " CONSTITUTION_CODE, LEGAL_ENTITY_TYPE, HNI_NETWORTH, TURNOVER, FILTERCOLUMNS) " +
                "SELECT GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, " +
                "       CURRENCY, DATA_TYPE, ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, " +
                "       ENTRY_USER, MODIFY_USER, AUTH_USER, ENTRY_TIME, MODIFY_TIME, AUTH_TIME, " +
                "       REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, REPORT_DESC, " +
                "       REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, REPORT_ADDL_CRITERIA_3, " +
                "       :reportCode, :rowId, :columnId, :balanceLc, :solId, " +
                "       :constCode, :legalEntity, :hniNetworth, :turnover , :filterColumns " +
                "FROM BRF_BASE_MAPPING_TABLE WHERE ACCOUNT_ID_BACID = :accountId",
        nativeQuery = true
    )
    int insertFromBase(
        @Param("accountId")   String accountId,
        @Param("reportCode")  String reportCode,
        @Param("rowId")       String rowId,
        @Param("columnId")    String columnId,
        @Param("balanceLc")   String balanceLc,
        @Param("solId")       String solId,
        @Param("constCode")   String constCode,
        @Param("legalEntity") String legalEntity,
        @Param("hniNetworth") String hniNetworth,
        @Param("turnover")    String turnover,
        @Param("filterColumns") String filterColumns
    );
    
	@Query(value = "SELECT * FROM BRF_COMMON_MAPPING_TABLE " + "WHERE REPORT_CODE = :reportCode ", nativeQuery = true)
	List<BrfCommonMapping> findbyreportcode(@Param("reportCode") String reportCode);
        
    
	@Query(value = "SELECT DISTINCT REPORT_CODE, COALESCE(ROW_ID, ' - '), COALESCE(COLUMN_ID, ' - ') "
			+ "FROM BRF_COMMON_MAPPING_TABLE " + "WHERE GL_SUBHEAD_CODE = :glSubHead", nativeQuery = true)
	List<Object[]> findDistinctCombinationsByGlSubHead(@Param("glSubHead") String glSubHead);

	@Query(value = "SELECT ACCOUNT_ID_BACID, SOL_ID, CONSTITUTION_CODE, LEGAL_ENTITY_TYPE , "
			+ "SCHEME_TYPE, HNI_NETWORTH, TURNOVER, ACCOUNT_BALANCE_LC, FILTERCOLUMNS  " + "FROM BRF_COMMON_MAPPING_TABLE "
			+ "WHERE REPORT_CODE = :reportCode " + "AND (ROW_ID = :rowLabel OR (:rowLabel IS NULL AND ROW_ID IS NULL)) "
			+ "AND (COLUMN_ID = :colLabel OR (:colLabel IS NULL AND COLUMN_ID IS NULL)) " 
			+ "AND GL_SUBHEAD_CODE = :glSubHead", nativeQuery = true)
	List<Object[]> findMappingParameters(@Param("reportCode") String reportCode, @Param("rowLabel") String rowLabel,
			@Param("colLabel") String colLabel, @Param("glSubHead") String glSubHead);
	
	@Query(value = "SELECT DISTINCT REPORT_CODE, COALESCE(ROW_ID, ' - '), COALESCE(COLUMN_ID, ' - ') " +
            "FROM BRF_COMMON_MAPPING_TABLE " +
            "WHERE ACCOUNT_ID_BACID = :accountId",
            nativeQuery = true)
	List<Object[]> findDistinctCombinationsByAccountId(@Param("accountId") String accountId);
	
	@Query(
		    value = "SELECT DISTINCT GL_SUBHEAD_CODE " +
		            "FROM BRF_COMMON_MAPPING_TABLE " +
		            "WHERE REPORT_CODE = :reportCode " +
		            "AND GL_SUBHEAD_CODE IS NOT NULL",
		    nativeQuery = true
		)
	List<String> findMappedGlSubheadCodes(@Param("reportCode") String reportCode);
	
	@Query(value = "SELECT DISTINCT REPORT_CODE, COALESCE(ROW_ID, ' - '), COALESCE(COLUMN_ID, ' - '), "
			+ " SOL_ID, CONSTITUTION_CODE, LEGAL_ENTITY_TYPE, SCHEME_TYPE "
			+ "FROM BRF_COMMON_MAPPING_TABLE " + "WHERE GL_SUBHEAD_CODE = :glSubHead AND REPORT_CODE IN ('BRF001','BRF002','BRF004') ", nativeQuery = true)
	List<Object[]> findDistinctCombinationsByGlSubHeadunmappedal(@Param("glSubHead") String glSubHead);
	
	@Query(value = "SELECT DISTINCT REPORT_CODE, COALESCE(ROW_ID, ' - '), COALESCE(COLUMN_ID, ' - '), "
	        + " SOL_ID, CONSTITUTION_CODE, LEGAL_ENTITY_TYPE, SCHEME_TYPE "
	        + "FROM BRF_COMMON_MAPPING_TABLE " 
	        + "WHERE ACCOUNT_ID_BACID = :accountId AND CURRENCY = :currency AND REPORT_CODE IN ('BRF001','BRF002','BRF004') ", nativeQuery = true)
	List<Object[]> findExistingMappingsByAccountAndCurrency(@Param("accountId") String accountId, @Param("currency") String currency);
	
	@Query(
		    value = "SELECT * FROM BRF_COMMON_MAPPING_TABLE c " +
		            "WHERE c.REPORT_CODE = :reportCode",
		    nativeQuery = true
		)
		List<BrfCommonMapping> findOtherMappings(@Param("reportCode") String reportCode);
	
	@Query("SELECT c.accountIdBacid FROM BrfCommonMapping c " +
		       "WHERE c.reportCode = :reportCode AND c.accountIdBacid LIKE 'OTHMAPP%'")
		List<String> findAccountIdsByReportCodeAndPrefix(@Param("reportCode") String reportCode);

	@Query("SELECT c FROM BrfCommonMapping c WHERE c.reportCode = :reportCode " +
		       "AND c.rowId = :rowId AND c.columnId = :columnId " +
		       "AND COALESCE(c.currency, '') = :currency " +
		       "AND COALESCE(c.glSubheadCode, '') = :glSubheadCode " +
		       "AND COALESCE(c.solId, '') = :solId " +
		       "AND COALESCE(c.constitutionCode, '') = :constitutionCode " +
		       "AND COALESCE(c.legalEntityType, '') = :legalEntityType " +
		       "AND COALESCE(c.schemeType, '') = :schemeType " +
		       "AND COALESCE(c.asstCls, '') = :asstCls " +
		       "AND COALESCE(c.purposeOfAdvn, '') = :purposeOfAdvn " +
		       "AND c.accountIdBacid LIKE 'OTHMAPP%'")
		Optional<BrfCommonMapping> findExistingOtherMapping(@Param("reportCode") String reportCode,
		                                                      @Param("rowId") String rowId,
		                                                      @Param("columnId") String columnId,
		                                                      @Param("currency") String currency,
		                                                      @Param("glSubheadCode") String glSubheadCode,
		                                                      @Param("solId") String solId,
		                                                      @Param("constitutionCode") String constitutionCode,
		                                                      @Param("legalEntityType") String legalEntityType,
		                                                      @Param("schemeType") String schemeType,
		                                                      @Param("asstCls") String asstCls,
		                                                      @Param("purposeOfAdvn") String purposeOfAdvn);
	
}
