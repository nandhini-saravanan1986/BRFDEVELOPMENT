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
            view.put("SOL_ID",           nvl(row.getSolId()));

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
            view.put("ACCOUNT_DESCRIPTION",   nvl(row.getAccountDescription()));
            view.put("CURRENCY",              nvl(row.getCurrency()));
            view.put("DATA_TYPE",             nvl(row.getDataType()));
            view.put("REPORT_CODE",           nvl(row.getReportCode()));
            view.put("REPORT_DESC",           nvl(row.getReportDesc()));
            view.put("ROW_ID",                nvl(row.getRowId()));
            view.put("COLUMN_ID",             nvl(row.getColumnId()));
            view.put("ACCOUNT_BALANCE_LC",    nvl(row.getAccountBalanceLc()));

            result.add(view);
        }

        return result;
    }


    private String nvl(String value) {
        return value != null ? value : "";
    }
}