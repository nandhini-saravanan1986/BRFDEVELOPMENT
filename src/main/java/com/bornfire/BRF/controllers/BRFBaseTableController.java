package com.bornfire.BRF.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BRFBaseTableController {

    @Value("${datasrc.url}")
    private String brfUrl;

    @Value("${datasrc.username}")
    private String brfUsername;

    @Value("${datasrc.password}")
    private String brfPassword;

    // ── Page load ──
    @GetMapping("/BRFBaseTable")
    public String loadPage(Model model) {
        model.addAttribute("menu", "BRF Base Table");
        return "BRFBaseTable";
    }

    // ── Show Accounts AJAX ──
    @GetMapping("/BRFBaseTable/showAccounts")
    @ResponseBody
    public List<Map<String, Object>> showAccounts(
            @RequestParam(required = false, defaultValue = "") String reportCode,
            @RequestParam(required = false, defaultValue = "BANKING") String source,
            @RequestParam(required = false, defaultValue = "") String glHead,
            @RequestParam(required = false, defaultValue = "") String glSubHead) {

        List<Map<String, Object>> result = new ArrayList<>();

        boolean hasReportCode = !reportCode.trim().isEmpty();
        boolean isTreasury    = "TREASURY".equalsIgnoreCase(source.trim());
        boolean hasGlHead     = !glHead.trim().isEmpty();
        boolean hasGlSubHead  = !glSubHead.trim().isEmpty();

        // Build WHERE conditions dynamically (report code is used for submit/tagging only, not for filtering display)
        List<String> conditions = new ArrayList<>();
        if (isTreasury)   conditions.add("UPPER(DATA_TYPE) = 'TREASURY'");
        if (hasGlHead)    conditions.add("UPPER(GL_HEAD) LIKE UPPER(?)");
        if (hasGlSubHead) conditions.add("UPPER(GL_SUBHEAD_CODE) LIKE UPPER(?)");

        StringBuilder sql = new StringBuilder(
            "SELECT GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, CURRENCY, ACCOUNT_BALANCE_LC " +
            "FROM BRF_BASE_MAPPING_TABLE");
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        System.out.println("BRFBaseTable SQL: " + sql);

        try (Connection conn = DriverManager.getConnection(brfUrl, brfUsername, brfPassword);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            System.out.println("DB USER = " + conn.getMetaData().getUserName());

            // Bind params in the same order as conditions
            int idx = 1;
            // isTreasury is a literal in SQL — no bind param needed
            if (hasGlHead)    ps.setString(idx++, "%" + glHead.trim() + "%");
            if (hasGlSubHead) ps.setString(idx++, "%" + glSubHead.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    result.add(row);
                }
            }

        } catch (Exception e) {
            System.err.println("BRFBaseTable ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("BRFBaseTable rows returned: " + result.size());
        return result;
    }

    // ── Submit: save selected rows ONLY to BRF_COMMON_MAPPING_TABLE
    //           BRF_BASE_MAPPING_TABLE is read-only (no updates)
    //           Deduplication key: ACCOUNT_ID_BACID + ROW_ID + COLUMN_ID
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

        // Check same ACCOUNT_ID_BACID + ROW_ID + COLUMN_ID under a DIFFERENT report code → block
        String checkOtherSql = "SELECT REPORT_CODE FROM BRF_COMMON_MAPPING_TABLE "
                             + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? AND COLUMN_ID = ? "
                             + "AND REPORT_CODE != ? AND ROWNUM = 1";

        // Check same ACCOUNT_ID_BACID + ROW_ID + COLUMN_ID under the SAME report code → update
        String checkSameSql  = "SELECT COUNT(*) FROM BRF_COMMON_MAPPING_TABLE "
                             + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? AND COLUMN_ID = ? AND REPORT_CODE = ?";

        // Update ACCOUNT_BALANCE_LC for existing record
        String updateCommon = "UPDATE BRF_COMMON_MAPPING_TABLE SET ACCOUNT_BALANCE_LC = ?, SOL_ID = ? "
                + "WHERE ACCOUNT_ID_BACID = ? AND ROW_ID = ? AND COLUMN_ID = ? AND REPORT_CODE = ?";

        // Insert full row from BRF_BASE_MAPPING_TABLE but override REPORT_CODE, ROW_ID, COLUMN_ID, ACCOUNT_BALANCE_LC
        String insertSql = "INSERT INTO BRF_COMMON_MAPPING_TABLE "
                + "(GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, CURRENCY, DATA_TYPE, "
                + " ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, ENTRY_USER, MODIFY_USER, AUTH_USER, "
                + " ENTRY_TIME, MODIFY_TIME, AUTH_TIME, REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, "
                + " REPORT_CODE, REPORT_DESC, ROW_ID, COLUMN_ID, "
                + " REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, REPORT_ADDL_CRITERIA_3, ACCOUNT_BALANCE_LC, SOL_ID) "  // ← added SOL_ID
                + "SELECT GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID, ACCOUNT_DESCRIPTION, CURRENCY, DATA_TYPE, "
                + "       ENTITY_FLG, AUTH_FLG, MODIFY_FLG, DEL_FLG, ENTRY_USER, MODIFY_USER, AUTH_USER, "
                + "       ENTRY_TIME, MODIFY_TIME, AUTH_TIME, REPORT_DATE, REPORT_VERSION, REPORT_FREQUENCY, "
                + "       ?, REPORT_DESC, ?, ?, "
                + "       REPORT_ADDL_CRITERIA_1, REPORT_ADDL_CRITERIA_2, REPORT_ADDL_CRITERIA_3, ?, ? "  // ← added extra ?
                + "FROM BRF_BASE_MAPPING_TABLE WHERE ACCOUNT_ID_BACID = ?";

        int totalInserted = 0;
        int totalUpdated  = 0;
        List<Map<String, String>> blockedList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(brfUrl, brfUsername, brfPassword)) {
            conn.setAutoCommit(false);
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

                    // Step 1: check if same ACCOUNT_ID_BACID + ROW_ID + COLUMN_ID exists under a DIFFERENT report code
                    String otherReportCode = null;
                    try (PreparedStatement chk = conn.prepareStatement(checkOtherSql)) {
                        chk.setString(1, accountId.trim());
                        chk.setString(2, rowId.trim());
                        chk.setString(3, columnId.trim());
                        chk.setString(4, reportCode.trim());
                        try (ResultSet rs = chk.executeQuery()) {
                            if (rs.next()) otherReportCode = rs.getString(1);
                        }
                    }

                    if (otherReportCode != null) {
                        Map<String, String> blocked = new LinkedHashMap<>();
                        blocked.put("accountId", accountId.trim());
                        blocked.put("savedIn",   otherReportCode);
                        blockedList.add(blocked);
                        System.out.println("BRFBaseTable BLOCKED: accountId=" + accountId + " (row=" + rowId + ",col=" + columnId + ") already in " + otherReportCode);
                        continue;
                    }

                    // Step 2: check if same combination exists under the SAME report code → update
                    boolean sameExists = false;
                    try (PreparedStatement chk = conn.prepareStatement(checkSameSql)) {
                        chk.setString(1, accountId.trim());
                        chk.setString(2, rowId.trim());
                        chk.setString(3, columnId.trim());
                        chk.setString(4, reportCode.trim());
                        try (ResultSet rs = chk.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) sameExists = true;
                        }
                    }

                    if (sameExists) {
                        // Update ACCOUNT_BALANCE_LC only
                        try (PreparedStatement upd = conn.prepareStatement(updateCommon)) {
                            upd.setString(1, balanceLc);
                            upd.setString(2, solId != null ? solId.trim() : "");  // ← SOL_ID update
                            upd.setString(3, accountId.trim());
                            upd.setString(4, rowId.trim());
                            upd.setString(5, columnId.trim());
                            upd.setString(6, reportCode.trim());
                            upd.executeUpdate();
                        }
                        totalUpdated++;
                    } else {
                        // New record — insert into BRF_COMMON_MAPPING_TABLE only
                        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                            ins.setString(1, reportCode.trim());
                            ins.setString(2, rowId.trim());
                            ins.setString(3, columnId.trim());
                            ins.setString(4, balanceLc);
                            ins.setString(5, solId.trim());        // ← ADD THIS (SOL_ID value e.g. "('9001','9002')")
                            ins.setString(6, accountId.trim());    // ← shift from 5 → 6
                            totalInserted += ins.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
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

        System.out.println("BRFBaseTable SUBMIT inserted: " + totalInserted + ", updated: " + totalUpdated + ", blocked: " + blockedList.size());
        response.put("inserted", totalInserted);
        response.put("updated",  totalUpdated);
        response.put("blocked",  blockedList);
        return response;
    }
}