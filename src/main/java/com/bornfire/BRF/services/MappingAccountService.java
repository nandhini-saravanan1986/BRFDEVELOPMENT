package com.bornfire.BRF.services;

import com.bornfire.BRF.entities.BrfBaseMapping;
import com.bornfire.BRF.entities.BrfCommonMapping;
import com.bornfire.BRF.entities.BrfBaseMappingRepository;
import com.bornfire.BRF.entities.BrfCommonMappingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class MappingAccountService {

    private final BrfCommonMappingRepository commonMappingRepo;
    private final BrfBaseMappingRepository   baseMappingRepo;

    public MappingAccountService(BrfCommonMappingRepository commonMappingRepo,
                                 BrfBaseMappingRepository   baseMappingRepo) {
        this.commonMappingRepo = commonMappingRepo;
        this.baseMappingRepo   = baseMappingRepo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAPPED ACCOUNTS
    // Returns rows from BRF_COMMON_MAPPING_TABLE for the given reportCode.
    // Columns: GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID,
    //          REPORT_CODE, ROW_ID, COLUMN_ID, SOL_ID
    // ─────────────────────────────────────────────────────────────────────────

    public List<Map<String, String>> getMappedAccounts(String reportCode) {
        if (reportCode == null || reportCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<BrfCommonMapping> rows = commonMappingRepo.findByReportCodeAndAccountIdBacidNotLike(reportCode);
//        System.out.println("MAPPED ACCOUNTS QUERY -> reportCode=[" + reportCode + "] rowsReturned=" + rows.size());

        List<Map<String, String>> result = new ArrayList<>();

        for (BrfCommonMapping row : rows) {
            if (row == null) continue;

            Map<String, String> view = new LinkedHashMap<>();

            view.put("GL_HEAD",          nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",  nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID", nvl(row.getAccountIdBacid()));
            view.put("REPORT_CODE",      nvl(row.getReportCode()));
            view.put("ROW_ID",           nvl(row.getRowId()));
            view.put("COLUMN_ID",        nvl(row.getColumnId()));
            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));
            view.put("DATA_TYPE", nvl(row.getDataType()));
            view.put("SOL_ID", nvl(row.getSolId()));
            // Extra fields needed by Edit Mapped modal
            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
            view.put("CURRENCY",            nvl(row.getCurrency()));
            view.put("ACCOUNT_BALANCE_LC",  nvl(row.getAccountBalanceLc()));
            view.put("CONSTITUTION_CODE",   nvl(row.getConstitutionCode()));
            view.put("LEGAL_ENTITY_TYPE",   nvl(row.getLegalEntityType()));
            view.put("HNI_NETWORTH",        nvl(row.getHniNetworth()));
            view.put("TURNOVER",            nvl(row.getTurnover()));
            view.put("FILTER_COLUMNS",      nvl(row.getFilterColumns()));
            view.put("SCHEME_TYPE",      nvl(row.getSchemeType()));
            view.put("ASST_CLS",         nvl(row.getAsstCls()));    
            view.put("PURPOSE_OF_ADVN",  nvl(row.getPurposeOfAdvn())); 

            result.add(view);
        }
//        System.out.println("MAPPED ACCOUNTS RESULT -> resultSize=" + result.size());

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UNMAPPED ACCOUNTS
    // Returns rows from BRF_BASE_MAPPING_TABLE whose ACCOUNT_ID_BACID does
    // NOT exist in BRF_COMMON_MAPPING_TABLE for the given reportCode.
    // ─────────────────────────────────────────────────────────────────────────
//    public List<Map<String, String>> getUnmappedAccounts(String reportCode) {
//        if (reportCode == null || reportCode.trim().isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<BrfBaseMapping> rows = baseMappingRepo.findUnmappedAccounts(reportCode);
//        System.out.println("UNMAPPED COUNT = " + rows.size());
//
//        List<Map<String, String>> result = new ArrayList<>();
//
//        for (BrfBaseMapping row : rows) {
//            if (row == null) continue;
//
//            Map<String, String> view = new LinkedHashMap<>();
//
//            view.put("GL_HEAD",               nvl(row.getGlHead()));
//            view.put("GL_SUBHEAD_CODE",       nvl(row.getGlSubheadCode()));
//            view.put("ACCOUNT_ID_BACID",      nvl(row.getAccountIdBacid()));
//            view.put("REPORT_CODE",           nvl(row.getReportCode()));
//            view.put("ROW_ID",                nvl(row.getRowId()));
//            view.put("COLUMN_ID",             nvl(row.getColumnId()));
//            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
//            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
//            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));
//         // Extra fields needed by Edit UnMapped modal
//            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
//            view.put("CURRENCY",            nvl(row.getCurrency()));
//            view.put("ACCOUNT_BALANCE_LC",  nvl(row.getAccountBalanceLc()));
//
//            result.add(view);
//        }
//
//        return result;
//    }
    
    public List<Map<String, String>> getUnmappedAccounts(String reportCode) {
        if (reportCode == null || reportCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<BrfBaseMapping> rows = baseMappingRepo.findUnmappedAccounts(reportCode);
        System.out.println("UNMAPPED COUNT = " + rows.size());

        // Fetch all GL_SUBHEAD_CODEs already present in the common mapping table
        // for this reportCode — used to flag partially-mapped subheads
        List<String> mappedSubheads = commonMappingRepo.findMappedGlSubheadCodes(reportCode);
        Set<String> mappedSubheadSet = new HashSet<>(mappedSubheads);

        List<Map<String, String>> highlighted = new ArrayList<>();
        List<Map<String, String>> normal      = new ArrayList<>();

        for (BrfBaseMapping row : rows) {
            if (row == null) continue;

            Map<String, String> view = new LinkedHashMap<>();

            view.put("GL_HEAD",                nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",        nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID",       nvl(row.getAccountIdBacid()));
            view.put("REPORT_CODE",            nvl(row.getReportCode()));
            view.put("ROW_ID",                 nvl(row.getRowId()));
            view.put("COLUMN_ID",              nvl(row.getColumnId()));
            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));
            // Extra fields needed by Edit UnMapped modal
            view.put("ACCOUNT_DESCRIPTION",    nvl(row.getAccountDescription()));
            view.put("CURRENCY",               nvl(row.getCurrency()));
            view.put("ACCOUNT_BALANCE_LC",     nvl(row.getAccountBalanceLc()));

            // HIGHLIGHT_FLG = "Y" if this account's GL_SUBHEAD_CODE already has
            // at least one OTHER account mapped under the same reportCode
            String subhead = nvl(row.getGlSubheadCode());
            boolean isHighlighted = !subhead.isEmpty() && mappedSubheadSet.contains(subhead);
            view.put("HIGHLIGHT_FLG", isHighlighted ? "Y" : "N");

            if (isHighlighted) {
                highlighted.add(view);
            } else {
                normal.add(view);
            }
        }

        // Highlighted rows (green) come first
        List<Map<String, String>> result = new ArrayList<>();
        result.addAll(highlighted);
        result.addAll(normal);
        return result;
    }


    private String nvl(String value) {
        return value != null ? value : "";
    }
    
    // BASE MAPPING PARAM — LIST (paged + searched)
    public Map<String, Object> getBaseMappingParamList(String search, int page, int size) {
        String s = (search == null || search.trim().isEmpty()) ? "" : search.trim();

        long total = baseMappingRepo.countBySearch(s);
        List<BrfBaseMapping> rows = baseMappingRepo.findPagedList(s, page, size);

        List<Map<String, String>> data = new ArrayList<>();
        for (BrfBaseMapping row : rows) {
            Map<String, String> view = new LinkedHashMap<>();
            view.put("GL_HEAD",             nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",     nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID",    nvl(row.getAccountIdBacid()));
            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
            view.put("CURRENCY",            nvl(row.getCurrency()));
            view.put("DATA_TYPE",           nvl(row.getDataType()));
            data.add(view);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalRecords", total);
        response.put("data", data);
        return response;
    }
    
 // BASE MAPPING PARAM — INSERT
    public String saveBaseMappingParam(Map<String, String> body) {
    	
        String accountId = body.get("accountIdBacid");
        String currency = body.get("currency");
        //System.out.println("Currency :  " + currency);
        
        if (accountId == null || accountId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }
        // CHECK DUPLICATE
		if (baseMappingRepo.findByAccountIdBacidAndCurrency(accountId.trim(), currency.trim()).isPresent()) {
            return "Account ID already exists: " + accountId;
        }

        int inserted = baseMappingRepo.insertRecord(
            accountId.trim(),
            nvl(body.get("glHead")),
            nvl(body.get("glSubHeadCode")),
            nvl(body.get("accountDescription")),
            nvl(body.get("currency")),
            nvl(body.get("dataType"))
        );

        return inserted > 0 ? "SUCCESS" : "Insert failed for: " + accountId;
    }
    
 // BASE MAPPING PARAM — UPDATE
    public String updateBaseMappingParam(Map<String, String> body) {

        String oldId = body.get("oldAccountId");        //  OLD ID
        String oldCurrency = body.get("oldCurrency");   // OLD CURRENCY
        String newId = body.get("accountIdBacid");      //  NEW ID
        String newCurrency = body.get("currency");		// OLD CURRENCY
        
        if (newId == null || newId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }
        
        if (newCurrency == null || newCurrency.trim().isEmpty()) {
            return "CURRENCY is required";
        }
        
        boolean keyChanged = !newId.trim().equals(oldId.trim())
                || !newCurrency.trim().equals(oldCurrency.trim());

		if (keyChanged) {
		 int existing = baseMappingRepo.countByAccountIdAndCurrency(newId.trim(), newCurrency.trim());
		 if (existing > 0) {
		     return "A record with ACCOUNT_ID_BACID " + newId + " and CURRENCY " + newCurrency + " already exists";
		 }
		}

        int updated = baseMappingRepo.updateRecord(
            nvl(body.get("glHead")),
            nvl(body.get("glSubHeadCode")),
            nvl(body.get("accountDescription")),
            nvl(body.get("currency")),
            nvl(body.get("dataType")),
            newId.trim(),     // NEW ID (SET)
            oldId.trim(),      // OLD ID (WHERE)
            oldCurrency.trim() // OLD CURRENCY (WHERE)
        );

        return updated > 0 ? "SUCCESS" : "No active record found for: " + oldId;
    }
    
 // BASE MAPPING PARAM — SOFT DELETE
    public String deleteBaseMappingParam(String accountId, String currency) {
        if (accountId == null || accountId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }
        if (currency == null || currency.trim().isEmpty()) {
            return "CURRENCY is required";
        }
        
        int existing = baseMappingRepo.countByAccountIdAndCurrency(accountId.trim(), currency.trim());
        if (existing == 0) {
            return "No active record found for: " + accountId + "/" + currency;
        }

        int deleted = baseMappingRepo.deleteRecord(accountId.trim(), currency.trim());
        return deleted > 0 ? "SUCCESS" : "No active record found for: " + accountId + "/" + currency;
    }
}