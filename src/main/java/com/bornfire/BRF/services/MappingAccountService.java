package com.bornfire.BRF.services;

import com.bornfire.BRF.entities.BrfBaseMapping;
import com.bornfire.BRF.entities.BrfCommonMapping;
import com.bornfire.BRF.entities.BrfBaseMappingRepository;
import com.bornfire.BRF.entities.BrfCommonMappingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

        List<BrfCommonMapping> rows = commonMappingRepo.findByReportCode(reportCode);

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

            result.add(view);
        }

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UNMAPPED ACCOUNTS
    // Returns rows from BRF_BASE_MAPPING_TABLE whose ACCOUNT_ID_BACID does
    // NOT exist in BRF_COMMON_MAPPING_TABLE for the given reportCode.
    // ─────────────────────────────────────────────────────────────────────────
    public List<Map<String, String>> getUnmappedAccounts(String reportCode) {
        if (reportCode == null || reportCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<BrfBaseMapping> rows = baseMappingRepo.findUnmappedAccounts(reportCode);
        System.out.println("UNMAPPED COUNT = " + rows.size());

        List<Map<String, String>> result = new ArrayList<>();

        for (BrfBaseMapping row : rows) {
            if (row == null) continue;

            Map<String, String> view = new LinkedHashMap<>();

            view.put("GL_HEAD",               nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",       nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID",      nvl(row.getAccountIdBacid()));
            view.put("REPORT_CODE",           nvl(row.getReportCode()));
            view.put("ROW_ID",                nvl(row.getRowId()));
            view.put("COLUMN_ID",             nvl(row.getColumnId()));
            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));

            result.add(view);
        }

        return result;
    }


    private String nvl(String value) {
        return value != null ? value : "";
    }
}