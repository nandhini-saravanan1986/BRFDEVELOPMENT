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

        Map<String, Object> response = new LinkedHashMap<>();

        if (selectedRows == null || selectedRows.isEmpty()) {
            response.put("inserted", 0);
            response.put("updated",  0);
            response.put("blocked",  new ArrayList<>());
            return response;
        }

        // ── ❌ COMMENTED OUT: Raw SQL query strings ───────────────────────────
        // String checkOtherSql = "SELECT REPORT_CODE FROM BRF_COMMON_MAPPING_TABLE "
        //                      + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? AND COLUMN_ID = ? "
        //                      + "AND REPORT_CODE != ? AND ROWNUM = 1";
        //
        // String checkSameSql  = "SELECT COUNT(*) FROM BRF_COMMON_MAPPING_TABLE "
        //                      + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? AND COLUMN_ID = ? "
        //                      + "AND REPORT_CODE = ?";
        //
        // String updateCommon  = "UPDATE BRF_COMMON_MAPPING_TABLE "
        //                      + "SET ACCOUNT_BALANCE_LC = ?, SOL_ID = ? "
        //                      + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? "
        //                      + "AND COLUMN_ID = ? AND REPORT_CODE = ?";
        //
        // String insertSql     = "INSERT INTO BRF_COMMON_MAPPING_TABLE "
        //                      + "(GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, "
        //                      + " CURRENCY, DATA_TYPE, ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, "
        //                      + " ENTRY_USER, MODIFY_USER, AUTH_USER, ENTRY_TIME, MODIFY_TIME, AUTH_TIME, "
        //                      + " REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, REPORT_CODE, REPORT_DESC, "
        //                      + " ROW_ID, COLUMN_ID, REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, "
        //                      + " REPORT_ADDL_CRITERIA_3, ACCOUNT_BALANCE_LC, SOL_ID) "
        //                      + "SELECT GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, "
        //                      + "       CURRENCY, DATA_TYPE, ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, "
        //                      + "       ENTRY_USER, MODIFY_USER, AUTH_USER, ENTRY_TIME, MODIFY_TIME, AUTH_TIME, "
        //                      + "       REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, "
        //                      + "       ?, REPORT_DESC, ?, ?, "
        //                      + "       REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, "
        //                      + "       REPORT_ADDL_CRITERIA_3, ?, ? "
        //                      + "FROM BRF_BASE_MAPPING_TABLE WHERE ACCOUNT_ID_BACID = ?";
        // ─────────────────────────────────────────────────────────────────────

        int totalInserted = 0;
        int totalUpdated  = 0;
        List<Map<String, String>> blockedList = new ArrayList<>();

        // ── ❌ COMMENTED OUT: JDBC Connection + manual transaction block ───────
        // try (Connection conn = DriverManager.getConnection(brfUrl, brfUsername, brfPassword)) {
        //     conn.setAutoCommit(false);
        //     try {
        //         ... all JDBC loop logic was here ...
        //         conn.commit();
        //     } catch (Exception e) {
        //         conn.rollback();
        //         throw e;
        //     }
        // }
        // ─────────────────────────────────────────────────────────────────────

        // ──  REPLACED WITH: Repository calls (Spring manages transactions) ──
        try {
            for (Map<String, String> row : selectedRows) {

                String accountId  = row.get("accountId");
                String balanceLc  = row.get("balanceLc");
                String reportCode = row.get("reportCode");
                String rowId      = row.get("rowId");
                String columnId   = row.get("columnId");
                String solId      = row.get("solId");

                if (accountId == null || accountId.trim().isEmpty()) continue;
                if (reportCode == null) reportCode = "";
                if (rowId      == null) rowId      = "";
                if (columnId   == null) columnId   = "";
                if (solId      == null) solId      = "";

                final String fAccountId  = accountId.trim();
                final String fReportCode = reportCode.trim();
                final String fRowId      = rowId.trim();
                final String fColumnId   = columnId.trim();
                final String fSolId      = solId.trim();
                final String fBalanceLc  = balanceLc;

                // ── ❌ COMMENTED OUT: JDBC Step 1 – check different report code ──
                // String otherReportCode = null;
                // try (PreparedStatement chk = conn.prepareStatement(checkOtherSql)) {
                //     chk.setString(1, accountId.trim());
                //     chk.setString(2, rowId.trim());
                //     chk.setString(3, columnId.trim());
                //     chk.setString(4, reportCode.trim());
                //     try (ResultSet rs = chk.executeQuery()) {
                //         if (rs.next()) otherReportCode = rs.getString(1);
                //     }
                // }
                // if (otherReportCode != null) {
                //     Map<String, String> blocked = new LinkedHashMap<>();
                //     blocked.put("accountId", accountId.trim());
                //     blocked.put("savedIn",   otherReportCode);
                //     blockedList.add(blocked);
                //     continue;
                // }
                // ─────────────────────────────────────────────────────────────

                // Step 1: Block if same key exists under a DIFFERENT report code
                // Uses: commonMappingRepo.findConflictingMapping()
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

                // ── ❌ COMMENTED OUT: JDBC Step 2 – check same report code ────────
                // boolean sameExists = false;
                // try (PreparedStatement chk = conn.prepareStatement(checkSameSql)) {
                //     chk.setString(1, accountId.trim());
                //     chk.setString(2, rowId.trim());
                //     chk.setString(3, columnId.trim());
                //     chk.setString(4, reportCode.trim());
                //     try (ResultSet rs = chk.executeQuery()) {
                //         if (rs.next() && rs.getInt(1) > 0) sameExists = true;
                //     }
                // }
                // ─────────────────────────────────────────────────────────────

                // Step 2: Update if same key exists under the SAME report code
                // Uses: commonMappingRepo.findByAccountIdBacidAndRowIdAndColumnIdAndReportCode()
                Optional<BrfCommonMapping> existing =
                    commonMappingRepo.findByAccountIdBacidAndRowIdAndColumnIdAndReportCode(
                        fAccountId, fRowId, fColumnId, fReportCode);

                if (existing.isPresent()) {

                    // ── ❌ COMMENTED OUT: JDBC Update ─────────────────────────────
                    // try (PreparedStatement upd = conn.prepareStatement(updateCommon)) {
                    //     upd.setString(1, balanceLc);
                    //     upd.setString(2, solId.trim());
                    //     upd.setString(3, accountId.trim());
                    //     upd.setString(4, rowId.trim());
                    //     upd.setString(5, columnId.trim());
                    //     upd.setString(6, reportCode.trim());
                    //     upd.executeUpdate();
                    // }
                    // totalUpdated++;
                    // ─────────────────────────────────────────────────────────────

                    // Update ACCOUNT_BALANCE_LC and SOL_ID, then save
                    BrfCommonMapping toUpdate = existing.get();
                    toUpdate.setAccountBalanceLc(fBalanceLc);
                    toUpdate.setSolId(fSolId);
                    commonMappingRepo.save(toUpdate);
                    totalUpdated++;

                } else {

                    // ── ❌ COMMENTED OUT: JDBC Insert (INSERT...SELECT from base) ──
                    // try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    //     ins.setString(1, reportCode.trim());   // REPORT_CODE override
                    //     ins.setString(2, rowId.trim());        // ROW_ID override
                    //     ins.setString(3, columnId.trim());     // COLUMN_ID override
                    //     ins.setString(4, balanceLc);           // ACCOUNT_BALANCE_LC override
                    //     ins.setString(5, solId.trim());        // SOL_ID
                    //     ins.setString(6, accountId.trim());    // WHERE ACCOUNT_ID_BACID = ?
                    //     totalInserted += ins.executeUpdate();
                    // }
                    // ─────────────────────────────────────────────────────────────

                    //  Fetch base record → copy all fields → override submission
                    // fields → save to BRF_COMMON_MAPPING_TABLE
                    // Uses: baseMappingRepo.findByAccountIdBacid()
                	// Single DB call — replicates original JDBC INSERT...SELECT exactly
                	int inserted = commonMappingRepo.insertFromBase(
                	    fAccountId, fReportCode, fRowId, fColumnId, fBalanceLc, fSolId);
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