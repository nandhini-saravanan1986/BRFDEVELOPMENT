package com.bornfire.BRF.controllers;

// ── REMOVED IMPORTS (JDBC no longer needed — commented below) ──────────────
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.ResultSetMetaData;
// import org.springframework.beans.factory.annotation.Value;
// import java.util.stream.Collectors;

import com.bornfire.BRF.entities.BrfBaseMapping;
import com.bornfire.BRF.entities.BrfBaseMappingRepository;
import com.bornfire.BRF.entities.BrfCommonMapping;
import com.bornfire.BRF.entities.BrfCommonMappingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class BRFBaseTableController {

    // ── ❌ COMMENTED OUT: Raw JDBC connection properties ──────────────────────
    // @Value("${datasrc.url}")
    // private String brfUrl;

    // @Value("${datasrc.username}")
    // private String brfUsername;

    // @Value("${datasrc.password}")
    // private String brfPassword;
    // ─────────────────────────────────────────────────────────────────────────

    // ── REPLACED WITH: Spring Data JPA Repositories ───────────────────────
    @Autowired
    private BrfBaseMappingRepository baseMappingRepo;

    @Autowired
    private BrfCommonMappingRepository commonMappingRepo;
    // ─────────────────────────────────────────────────────────────────────────


    // ═════════════════════════════════════════════════════════════════════════
    // PAGE LOAD  (unchanged)
    // ═════════════════════════════════════════════════════════════════════════
    @GetMapping("/BRFBaseTable")
    public String loadPage(Model model) {
        model.addAttribute("menu", "BRF Base Table");
        return "BRFBaseTable";
    }


    // ═════════════════════════════════════════════════════════════════════════
    // SHOW ACCOUNTS  (AJAX – GET)
    // ═════════════════════════════════════════════════════════════════════════
    @GetMapping("/BRFBaseTable/showAccounts")
    @ResponseBody
    public List<Map<String, Object>> showAccounts(
            @RequestParam(required = false, defaultValue = "") String reportCode,
            @RequestParam(required = false, defaultValue = "BANKING") String source,
            @RequestParam(required = false, defaultValue = "") String glHead,
            @RequestParam(required = false, defaultValue = "") String glSubHead) {

        List<Map<String, Object>> result = new ArrayList<>();

        boolean isTreasury   = "TREASURY".equalsIgnoreCase(source.trim());
        boolean hasGlHead    = !glHead.trim().isEmpty();
        boolean hasGlSubHead = !glSubHead.trim().isEmpty();

        // ── ❌ COMMENTED OUT: Dynamic SQL build ───────────────────────────────
        // List<String> conditions = new ArrayList<>();
        // if (isTreasury)   conditions.add("UPPER(DATA_TYPE) = 'TREASURY'");
        // if (hasGlHead)    conditions.add("UPPER(GL_HEAD) LIKE UPPER(?)");
        // if (hasGlSubHead) conditions.add("UPPER(GL_SUBHEAD_CODE) LIKE UPPER(?)");
        //
        // StringBuilder sql = new StringBuilder(
        //     "SELECT GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, CURRENCY, ACCOUNT_BALANCE_LC " +
        //     "FROM BRF_BASE_MAPPING_TABLE");
        // if (!conditions.isEmpty()) {
        //     sql.append(" WHERE ").append(String.join(" AND ", conditions));
        // }
        // System.out.println("BRFBaseTable SQL: " + sql);
        // ─────────────────────────────────────────────────────────────────────

        // ── ❌ COMMENTED OUT: JDBC connection + ResultSet loop ─────────────────
        // try (Connection conn = DriverManager.getConnection(brfUrl, brfUsername, brfPassword);
        //      PreparedStatement ps = conn.prepareStatement(sql.toString())) {
        //
        //     System.out.println("DB USER = " + conn.getMetaData().getUserName());
        //
        //     int idx = 1;
        //     if (hasGlHead)    ps.setString(idx++, "%" + glHead.trim() + "%");
        //     if (hasGlSubHead) ps.setString(idx++, "%" + glSubHead.trim() + "%");
        //
        //     try (ResultSet rs = ps.executeQuery()) {
        //         ResultSetMetaData meta = rs.getMetaData();
        //         int cols = meta.getColumnCount();
        //         while (rs.next()) {
        //             Map<String, Object> row = new LinkedHashMap<>();
        //             for (int i = 1; i <= cols; i++) {
        //                 row.put(meta.getColumnName(i), rs.getObject(i));
        //             }
        //             result.add(row);
        //         }
        //     }
        // } catch (Exception e) {
        //     System.err.println("BRFBaseTable ERROR: " + e.getMessage());
        //     e.printStackTrace();
        // }
        // ─────────────────────────────────────────────────────────────────────

        // ── ❌ COMMENTED OUT: findAll() + in-memory Java stream filter ─────────
        // (Removed because dataset is large — DB-level filtering used instead)
        //
        // try {
        //     List<BrfBaseMapping> allRows = baseMappingRepo.findAll();
        //
        //     List<BrfBaseMapping> filtered = allRows.stream()
        //         .filter(r -> !isTreasury ||
        //                      "TREASURY".equalsIgnoreCase(r.getDataType()))
        //         .filter(r -> !hasGlHead ||
        //                      (r.getGlHead() != null &&
        //                       r.getGlHead().toUpperCase().contains(glHead.trim().toUpperCase())))
        //         .filter(r -> !hasGlSubHead ||
        //                      (r.getGlSubheadCode() != null &&
        //                       r.getGlSubheadCode().toUpperCase().contains(glSubHead.trim().toUpperCase())))
        //         .collect(Collectors.toList());
        //
        //     for (BrfBaseMapping r : filtered) {
        //         Map<String, Object> row = new LinkedHashMap<>();
        //         row.put("GL_HEAD",             r.getGlHead());
        //         row.put("GL_SUBHEAD_CODE",     r.getGlSubheadCode());
        //         row.put("ACCOUNT_ID_BACID",    r.getAccountIdBacid());
        //         row.put("ACCOUNT_DESCRIPTION", r.getAccountDescription());
        //         row.put("CURRENCY",            r.getCurrency());
        //         row.put("ACCOUNT_BALANCE_LC",  r.getAccountBalanceLc());
        //         result.add(row);
        //     }
        // } catch (Exception e) {
        //     System.err.println("BRFBaseTable ERROR: " + e.getMessage());
        //     e.printStackTrace();
        // }
        // ─────────────────────────────────────────────────────────────────────

        // ── REPLACED WITH: DB-level @Query filter via repository ───────────
        // NULL param = skip that filter in DB (Oracle: :param IS NULL OR col LIKE :param)
        // isTreasury=true  → pass "TREASURY" so DB filters DATA_TYPE = 'TREASURY'
        // isTreasury=false → pass null        so DB skips that condition entirely
        String dataTypeParam  = isTreasury   ? "TREASURY"                   : null;
        String glHeadParam    = hasGlHead    ? "%" + glHead.trim()    + "%" : null;
        String glSubHeadParam = hasGlSubHead ? "%" + glSubHead.trim() + "%" : null;

        try {
            List<BrfBaseMapping> filtered =
                baseMappingRepo.findByFilters(dataTypeParam, glHeadParam, glSubHeadParam);

            for (BrfBaseMapping r : filtered) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("GL_HEAD",             r.getGlHead());
                row.put("GL_SUBHEAD_CODE",     r.getGlSubheadCode());
                row.put("ACCOUNT_ID_BACID",    r.getAccountIdBacid());
                row.put("ACCOUNT_DESCRIPTION", r.getAccountDescription());
                row.put("CURRENCY",            r.getCurrency());
                row.put("ACCOUNT_BALANCE_LC",  r.getAccountBalanceLc());
                result.add(row);
            }
        } catch (Exception e) {
            System.err.println("BRFBaseTable ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("BRFBaseTable rows returned: " + result.size());
        return result;
    }


    // ═════════════════════════════════════════════════════════════════════════
    // SUBMIT ACCOUNTS  (POST)
    // ═════════════════════════════════════════════════════════════════════════
    @PostMapping("/BRFBaseTable/submit")
    @ResponseBody
    public Map<String, Object> submitAccounts(@RequestBody List<Map<String, String>> selectedRows) {
System.out.println("HIII");
        Map<String, Object> response = new LinkedHashMap<>();

        if (selectedRows == null || selectedRows.isEmpty()) {
            response.put("inserted", 0);
            response.put("updated",  0);
            response.put("blocked",  new ArrayList<>());
            return response;
        }

        int totalInserted = 0;
        int totalUpdated  = 0;
        List<Map<String, String>> blockedList = new ArrayList<>();

        try {
            for (Map<String, String> row : selectedRows) {

                // 1. Extract existing fields
                String accountId  = row.get("accountId");
                String balanceLc  = row.get("balanceLc");
                String reportCode = row.get("reportCode");
                String rowId      = row.get("rowId");
                String columnId   = row.get("columnId");
                String solId      = row.get("solId");
                
                String constCode   = row.get("constitutionCode");
                String legalEntity = row.get("legalEntityType");
                String hniNetworth = row.get("hniNetworth");
                String turnover    = row.get("turnover");
                String filterColumns    = row.get("filterColumns");

                if (accountId == null || accountId.trim().isEmpty()) continue;
                
                // 2. Handle nulls
                if (reportCode  == null) reportCode  = "";
                if (rowId       == null) rowId       = "";
                if (columnId    == null) columnId    = "";
                if (solId       == null) solId       = "";
                if (constCode   == null) constCode   = "";
                if (legalEntity == null) legalEntity = "";
                if (hniNetworth == null) hniNetworth = "";
                if (turnover    == null) turnover    = "";
                if (filterColumns    == null) filterColumns    = "";

                // 3. Clean and finalize variables
                final String fAccountId   = accountId.trim();
                final String fReportCode  = reportCode.trim();
                final String fRowId       = rowId.trim();
                final String fColumnId    = columnId.trim();
                final String fSolId       = solId.trim();
                final String fBalanceLc   = balanceLc;
                
                final String fConstCode   = constCode.trim();
                final String fLegalEntity = legalEntity.trim();
                final String fHniNetworth = hniNetworth.trim();
                final String fTurnover    = turnover.trim();
                final String ffilterColumns    = filterColumns.trim();

                Optional<BrfCommonMapping> conflicting =
                    commonMappingRepo.findConflictingMapping(
                        fAccountId, fRowId, fColumnId, fReportCode);

                if (conflicting.isPresent()) {
                    Map<String, String> blocked = new LinkedHashMap<>();
                    blocked.put("accountId", fAccountId);
                    blocked.put("savedIn",   conflicting.get().getReportCode());
                    blockedList.add(blocked);
                    System.out.println("BRFBaseTable BLOCKED: accountId=" + fAccountId
                        + " (row=" + fRowId + ", col=" + fColumnId + ") already in "
                        + conflicting.get().getReportCode());
                    continue;
                }

                Optional<BrfCommonMapping> existing =
                    commonMappingRepo.findByAccountIdBacidAndRowIdAndColumnIdAndReportCode(
                        fAccountId, fRowId, fColumnId, fReportCode);

                if (existing.isPresent()) {

                    // Update existing record
                    BrfCommonMapping toUpdate = existing.get();
                    toUpdate.setAccountBalanceLc(fBalanceLc);
                    toUpdate.setSolId(fSolId);
                    
                 System.out.println("Filter column : "+ffilterColumns);
                    toUpdate.setConstitutionCode(fConstCode);
                    toUpdate.setLegalEntityType(fLegalEntity);
                    toUpdate.setHniNetworth(fHniNetworth);
                    toUpdate.setTurnover(fTurnover);
                    
                    toUpdate.setFilterColumns(ffilterColumns);
                    
                    commonMappingRepo.save(toUpdate);
                    totalUpdated++;

                } else {

                    // ── NEW: Pass the extra parameters to your insert query ──
                	int inserted = commonMappingRepo.insertFromBase(
                	    fAccountId, fReportCode, fRowId, fColumnId, fBalanceLc, fSolId,
                	    fConstCode, fLegalEntity, fHniNetworth, fTurnover, ffilterColumns);
                	    
                	totalInserted += inserted;
                }
            }

        } catch (Exception e) {
            System.err.println("BRFBaseTable SUBMIT ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("inserted", 0);
            response.put("updated",  0);
            response.put("blocked",  new ArrayList<>());
            response.put("message",  "Error: " + e.getMessage());
            return response;
        }
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("BRFBaseTable SUBMIT inserted: " + totalInserted
            + ", updated: " + totalUpdated
            + ", blocked: " + blockedList.size());

        response.put("inserted", totalInserted);
        response.put("updated",  totalUpdated);
        response.put("blocked",  blockedList);
        return response;
    }
}