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
import com.bornfire.BRF.entities.BrfCommonMappingId;
import com.bornfire.BRF.entities.BrfCommonMappingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    @PostMapping("/BRFCommonTable/submit")
    @ResponseBody
    public Map<String, Object> submitAccounts(@RequestBody List<Map<String, String>> selectedRows) {
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

                // 1. Extract fields
                String accountId     = row.get("accountId");
                String balanceLc     = row.get("balanceLc");
                String reportCode    = row.get("reportCode");
                String rowId         = row.get("rowId");
                String columnId      = row.get("columnId");
                String solId         = row.get("solId");
                String constCode     = row.get("constitutionCode");
                String legalEntity   = row.get("legalEntityType");
                String hniNetworth   = row.get("hniNetworth");
                String turnover      = row.get("turnover");
                String filterColumns = row.get("filterColumns");
                String schemeType    = row.get("schemeType");
                
                if (accountId == null || accountId.trim().isEmpty()) continue;

                // 2. Handle nulls
                if (reportCode   == null) reportCode   = "";
                if (rowId        == null) rowId        = "";
                if (columnId     == null) columnId     = "";
                if (solId        == null) solId        = "";
                if (constCode    == null) constCode    = "";
                if (legalEntity  == null) legalEntity  = "";
                if (hniNetworth  == null) hniNetworth  = "";
                if (turnover     == null) turnover     = "";
                if (filterColumns == null) filterColumns = "";
                if (schemeType   == null) schemeType   = "";
                
                // 3. Trim and finalize
                final String fAccountId    = accountId.trim();
                final String fReportCode   = reportCode.trim();
                final String fRowId        = rowId.trim();
                final String fColumnId     = columnId.trim();
                final String fSolId        = solId.trim();
                final String fBalanceLc    = balanceLc;
                final String fConstCode    = constCode.trim();
                final String fLegalEntity  = legalEntity.trim();
                final String fHniNetworth  = hniNetworth.trim();
                final String fTurnover     = turnover.trim();
                final String fFilterColumns = filterColumns.trim();
                final String fSchemeType    = schemeType.trim();
                
                // 4. Block cross-report duplicates
                Optional<BrfCommonMapping> conflicting =
                    commonMappingRepo.findConflictingMapping(
                        fAccountId, fRowId, fColumnId, fReportCode);

                if (conflicting.isPresent()) {
                    Map<String, String> blocked = new LinkedHashMap<>();
                    blocked.put("accountId", fAccountId);
                    blocked.put("savedIn",   conflicting.get().getReportCode());
                    blockedList.add(blocked);
                    System.out.println("BLOCKED: accountId=" + fAccountId
                        + " already in " + conflicting.get().getReportCode());
                    continue;
                }

                // 5. Find existing record by composite key (ACCOUNT_ID_BACID + ROW_ID + REPORT_CODE)
                //    If not found, build a fresh record from BRF_BASE_MAPPING_TABLE
                boolean isNew = !commonMappingRepo
                    .findByAccountIdBacidAndRowIdAndReportCode(fAccountId, fRowId, fReportCode)
                    .isPresent();

                BrfCommonMapping record = commonMappingRepo
                    .findByAccountIdBacidAndRowIdAndReportCode(fAccountId, fRowId, fReportCode)
                    .orElseGet(() -> {

                        BrfBaseMapping base = baseMappingRepo
                            .findByAccountIdBacid(fAccountId)
                            .orElse(null);

                        if (base == null) {
                            System.out.println("WARN: No base record for accountId=" 
                                + fAccountId + " — skipped.");
                            return null;
                        }

                        BrfCommonMapping newRecord = new BrfCommonMapping();

                        // Composite key
                        newRecord.setAccountIdBacid(fAccountId);
                        newRecord.setReportCode(fReportCode);
                        newRecord.setRowId(fRowId);

                        // Copy from base table
                        newRecord.setGlHead(base.getGlHead());
                        newRecord.setGlSubheadCode(base.getGlSubheadCode());
                        newRecord.setAccountDescription(base.getAccountDescription());
                        newRecord.setCurrency(base.getCurrency());
                        newRecord.setDataType(base.getDataType());
                        newRecord.setEntityFlg(base.getEntityFlg());
                        newRecord.setAuthFlg(base.getAuthFlg());
                        newRecord.setModifyFlg(base.getModifyFlg());
                        newRecord.setDelFlg(base.getDelFlg());
                        newRecord.setEntryUser(base.getEntryUser());
                        newRecord.setModifyUser(base.getModifyUser());
                        newRecord.setAuthUser(base.getAuthUser());
                        newRecord.setEntryTime(base.getEntryTime());
                        newRecord.setModifyTime(base.getModifyTime());
                        newRecord.setAuthTime(base.getAuthTime());
                        newRecord.setReportDate(base.getReportDate());
                        newRecord.setReportVersion(base.getReportVersion());
                        newRecord.setReportFrequency(base.getReportFrequency());
                        newRecord.setReportDesc(base.getReportDesc());
                        newRecord.setReportAddlCriteria1(base.getReportAddlCriteria1());
                        newRecord.setReportAddlCriteria2(base.getReportAddlCriteria2());
                        newRecord.setReportAddlCriteria3(base.getReportAddlCriteria3());

                        return newRecord;
                    });

                // 6. Skip if base record was missing
                if (record == null) continue;

                // 7. Apply override fields — same for both INSERT and UPDATE
                record.setColumnId(fColumnId);
                record.setAccountBalanceLc(fBalanceLc);
                record.setSolId(fSolId);
                record.setConstitutionCode(fConstCode);
                record.setLegalEntityType(fLegalEntity);
                record.setHniNetworth(fHniNetworth);
                record.setTurnover(fTurnover);
                record.setFilterColumns(fFilterColumns);
                record.setSchemeType(fSchemeType);
                
                // 8. Single save — JPA decides INSERT or UPDATE via composite key
                commonMappingRepo.save(record);

                if (isNew) {
                    totalInserted++;
                    System.out.println("INSERT: accountId=" + fAccountId
                        + " reportCode=" + fReportCode + " rowId=" + fRowId);
                } else {
                    totalUpdated++;
                    System.out.println("UPDATE: accountId=" + fAccountId
                        + " reportCode=" + fReportCode + " rowId=" + fRowId);
                }
            }

        } catch (Exception e) {
            System.err.println("SUBMIT ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("inserted", 0);
            response.put("updated",  0);
            response.put("blocked",  new ArrayList<>());
            response.put("message",  "Error: " + e.getMessage());
            return response;
        }

        System.out.println("SUBMIT DONE → inserted: " + totalInserted
            + ", updated: " + totalUpdated
            + ", blocked: " + blockedList.size());

        response.put("inserted", totalInserted);
        response.put("updated",  totalUpdated);
        response.put("blocked",  blockedList);
        return response;
    }
    
 // Removes a single row from BRF_COMMON_MAPPING_TABLE by composite key.
    @GetMapping("/BRFCommonTable/deleteMapping")
    @ResponseBody
    public Map<String, Object> deleteMapping(
            @RequestParam String accountId,
            @RequestParam String reportCode,
            @RequestParam String rowId) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            BrfCommonMappingId pk =
                new BrfCommonMappingId(
                    accountId.trim(), reportCode.trim(), rowId.trim());

            if (!commonMappingRepo.existsById(pk)) {
                response.put("status",  "NOT_FOUND");
                response.put("message", "No mapping found for accountId=" + accountId
                    + ", reportCode=" + reportCode + ", rowId=" + rowId);
                return response;
            }

            commonMappingRepo.deleteById(pk);

            System.out.println("DELETE MAPPING: accountId=" + accountId
                + " reportCode=" + reportCode + " rowId=" + rowId);

            response.put("status",  "SUCCESS");
            response.put("message", "Mapping deleted successfully.");

        } catch (Exception e) {
            System.err.println("DELETE MAPPING ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("status",  "ERROR");
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }
    
    @PostMapping("/BRFCommonTable/updateMapping")
    @ResponseBody
    @Transactional
    public Map<String, Object> updateMapping(@RequestBody Map<String, String> row) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            String accountId    = nvl(row.get("accountId")).trim();
            String reportCode   = nvl(row.get("reportCode")).trim();
            String oldRowId     = nvl(row.get("oldRowId")).trim();   // original key
            String newRowId     = nvl(row.get("rowId")).trim();      // possibly changed
            String columnId     = nvl(row.get("columnId")).trim();
            String balanceLc    = nvl(row.get("balanceLc"));
            String solId        = nvl(row.get("solId")).trim();
            String constCode    = nvl(row.get("constitutionCode")).trim();
            String legalEntity  = nvl(row.get("legalEntityType")).trim();
            String hniNetworth  = nvl(row.get("hniNetworth")).trim();
            String turnover     = nvl(row.get("turnover")).trim();
            String filterCols   = nvl(row.get("filterColumns")).trim();
            String schemeType   = nvl(row.get("schemeType")).trim(); 

            if (accountId.isEmpty() || reportCode.isEmpty() || oldRowId.isEmpty()) {
                response.put("status",  "ERROR");
                response.put("message", "accountId, reportCode and oldRowId are required.");
                return response;
            }

            // 1. Find the ORIGINAL record by the old composite key
            BrfCommonMappingId oldPk = new BrfCommonMappingId(accountId, reportCode, oldRowId);
            BrfCommonMapping existing = commonMappingRepo.findById(oldPk).orElse(null);

            if (existing == null) {
                response.put("status",  "NOT_FOUND");
                response.put("message", "No record found for accountId=" + accountId
                    + ", reportCode=" + reportCode + ", rowId=" + oldRowId);
                return response;
            }

            boolean rowIdChanged = !oldRowId.equals(newRowId);

            if (rowIdChanged) {
                // ROW_ID is part of the PK — we must delete old + insert new
                // 2a. Check if the new composite key already exists (avoid duplicate PK)
                BrfCommonMappingId newPk = new BrfCommonMappingId(accountId, reportCode, newRowId);
                if (commonMappingRepo.existsById(newPk)) {
                    response.put("status",  "DUPLICATE");
                    response.put("message", "A record already exists for rowId=" + newRowId
                        + ". Cannot overwrite another row's data.");
                    return response;
                }

                // 2b. Delete the old record
                commonMappingRepo.deleteById(oldPk);
                commonMappingRepo.flush(); // ensure delete hits DB before insert

                // 2c. Build new record — copy all fields from existing, apply new key + overrides
                BrfCommonMapping newRecord = new BrfCommonMapping();
                newRecord.setAccountIdBacid(accountId);
                newRecord.setReportCode(reportCode);
                newRecord.setRowId(newRowId);                    // ← new PK value

                // Copy non-key fields from old record
                newRecord.setGlHead(existing.getGlHead());
                newRecord.setGlSubheadCode(existing.getGlSubheadCode());
                newRecord.setAccountDescription(existing.getAccountDescription());
                newRecord.setCurrency(existing.getCurrency());
                newRecord.setDataType(existing.getDataType());
                newRecord.setReportDesc(existing.getReportDesc());
                newRecord.setReportVersion(existing.getReportVersion());
                newRecord.setReportFrequency(existing.getReportFrequency());
                newRecord.setReportAddlCriteria1(existing.getReportAddlCriteria1());
                newRecord.setReportAddlCriteria2(existing.getReportAddlCriteria2());
                newRecord.setReportAddlCriteria3(existing.getReportAddlCriteria3());
                newRecord.setEntityFlg(existing.getEntityFlg());
                newRecord.setAuthFlg(existing.getAuthFlg());
                newRecord.setModifyFlg(existing.getModifyFlg());
                newRecord.setDelFlg(existing.getDelFlg());
                newRecord.setEntryUser(existing.getEntryUser());
                newRecord.setModifyUser(existing.getModifyUser());
                newRecord.setAuthUser(existing.getAuthUser());
                newRecord.setEntryTime(existing.getEntryTime());
                newRecord.setModifyTime(existing.getModifyTime());
                newRecord.setAuthTime(existing.getAuthTime());
                newRecord.setReportDate(existing.getReportDate());

                // Apply user-edited override fields
                newRecord.setColumnId(columnId);
                newRecord.setAccountBalanceLc(balanceLc);
                newRecord.setSolId(solId);
                newRecord.setConstitutionCode(constCode);
                newRecord.setLegalEntityType(legalEntity);
                newRecord.setHniNetworth(hniNetworth);
                newRecord.setTurnover(turnover);
                newRecord.setFilterColumns(filterCols);
                newRecord.setSchemeType(schemeType);

                commonMappingRepo.save(newRecord);

                System.out.println("UPDATE (key change): " + accountId
                    + " rowId " + oldRowId + " → " + newRowId);

            } else {
                // ROW_ID unchanged — simple field update on the existing record
                existing.setColumnId(columnId);
                existing.setAccountBalanceLc(balanceLc);
                existing.setSolId(solId);
                existing.setConstitutionCode(constCode);
                existing.setLegalEntityType(legalEntity);
                existing.setHniNetworth(hniNetworth);
                existing.setTurnover(turnover);
                existing.setFilterColumns(filterCols);
                existing.setSchemeType(schemeType);
                commonMappingRepo.save(existing);

                System.out.println("UPDATE (fields only): " + accountId
                    + " reportCode=" + reportCode + " rowId=" + oldRowId);
            }

            response.put("status",  "SUCCESS");
            response.put("updated", 1);

        } catch (Exception e) {
            System.err.println("UPDATE MAPPING ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("status",  "ERROR");
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }
}