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
            @RequestParam(required = false, defaultValue = "") String source,
            @RequestParam(required = false, defaultValue = "") String glHead,
            @RequestParam(required = false, defaultValue = "") String glSubHead) {

        List<Map<String, Object>> result = new ArrayList<>();

        String trimmedSource = source.trim();
        String trimmedGlHead = glHead.trim();
        String trimmedGlSubHead = glSubHead.trim();

        String dataTypeParam = null;
        if ("TREASURY".equalsIgnoreCase(trimmedSource)) {
            dataTypeParam = "TREASURY";
        } else if ("GL".equalsIgnoreCase(trimmedSource)) {           
            dataTypeParam = "GL"; 
        }

        String glHeadParam    = trimmedGlHead.isEmpty()    ? null : "%" + trimmedGlHead + "%";
        String glSubHeadParam = trimmedGlSubHead.isEmpty() ? null : "%" + trimmedGlSubHead + "%";

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
                row.put("DATA_TYPE",           r.getDataType());
                result.add(row);
            }
        } catch (Exception e) {
            System.err.println("BRFBaseTable ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("BRFBaseTable rows returned for Report " + reportCode + ": " + result.size());
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
                String asstCls       = row.get("assetClass");  
                String purposeOfAdvn = row.get("purposeOfAdvn"); 
                String currency      = row.get("currency");
                
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
                if (asstCls       == null) asstCls       = ""; 
                if (purposeOfAdvn == null) purposeOfAdvn = ""; 
                if (currency == null) currency = "";
                
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
                final String fAsstCls       = asstCls.trim();  
                final String fPurposeOfAdvn = purposeOfAdvn.trim(); 
                final String fCurrency      = currency.trim();
                
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
                BrfCommonMappingId submitPk = new BrfCommonMappingId(fAccountId, fReportCode, fRowId, fColumnId, fCurrency);

                boolean isNew = !commonMappingRepo.existsById(submitPk);

                BrfCommonMapping record = commonMappingRepo
                    .findById(submitPk)
                    .orElseGet(() -> {

                        BrfBaseMapping base = baseMappingRepo
                            .findByAccountIdBacidAndCurrency(fAccountId,fCurrency)
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
                record.setAsstCls(fAsstCls);      
                record.setPurposeOfAdvn(fPurposeOfAdvn);
                
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
            @RequestParam String rowId,@RequestParam String columnId,@RequestParam String currency) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            BrfCommonMappingId pk =
                new BrfCommonMappingId(
                    accountId.trim(), reportCode.trim(), rowId.trim(),columnId.trim(),currency.trim());

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
            String oldColumnId     = nvl(row.get("oldColumnId")).trim();
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
            String asstCls      = nvl(row.get("assetClass")).trim();  
            String purposeOfAdvn = nvl(row.get("purposeOfAdvn")).trim();
            String currency = nvl(row.get("currency")).trim();

            if (accountId.isEmpty() || reportCode.isEmpty() || oldRowId.isEmpty()) {
                response.put("status",  "ERROR");
                response.put("message", "accountId, reportCode and oldRowId are required.");
                return response;
            }

            // 1. Find the ORIGINAL record by the old composite key
            BrfCommonMappingId oldPk = new BrfCommonMappingId(accountId, reportCode, oldRowId,oldColumnId,currency);
            BrfCommonMapping existing = commonMappingRepo.findById(oldPk).orElse(null);

            if (existing == null) {
                response.put("status",  "NOT_FOUND");
                response.put("message", "No record found for accountId=" + accountId
                    + ", reportCode=" + reportCode + ", rowId=" + oldRowId);
                return response;
            }

            boolean rowIdChanged = !oldRowId.equals(newRowId)|| !oldColumnId.equals(columnId);

            if (rowIdChanged) {
                // ROW_ID is part of the PK — we must delete old + insert new
                // 2a. Check if the new composite key already exists (avoid duplicate PK)
                BrfCommonMappingId newPk = new BrfCommonMappingId(accountId, reportCode, newRowId,columnId,currency);
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
                newRecord.setAsstCls(asstCls);         
                newRecord.setPurposeOfAdvn(purposeOfAdvn);

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
                existing.setAsstCls(asstCls);          
                existing.setPurposeOfAdvn(purposeOfAdvn); 
                
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
    
    @PostMapping("/BRFCommonTable/submitOtherMapping")
    @ResponseBody
    @Transactional
    public Map<String, Object> submitOtherMapping(@RequestBody Map<String, Object> payload) {

        Map<String, Object> response = new LinkedHashMap<>();
        List<Map<String, String>> savedRecords   = new ArrayList<>();
        List<Map<String, String>> skippedRecords = new ArrayList<>();

        try {
            String reportCode    = nvl((String) payload.get("reportCode")).trim();
            String rowId         = nvl((String) payload.get("rowId")).trim();
            String columnId      = nvl((String) payload.get("columnId")).trim();
            if (columnId.isEmpty()) columnId = "None";              // ← no Column ID selected => "None", not null

            String glHead        = nvl((String) payload.get("glHead")).trim();
            String glSubheadCode = nvl((String) payload.get("glSubheadCode")).trim();
            String solId         = nvl((String) payload.get("solId")).trim();
            String constCode     = nvl((String) payload.get("constitutionCode")).trim();
            String legalEntity   = nvl((String) payload.get("legalEntityType")).trim();
            String hniNetworth   = nvl((String) payload.get("hniNetworth")).trim();
            String turnover      = nvl((String) payload.get("turnover")).trim();
            String schemeType    = nvl((String) payload.get("schemeType")).trim();
            String asstCls       = nvl((String) payload.get("assetClass")).trim();
            String purposeOfAdvn = nvl((String) payload.get("purposeOfAdvn")).trim();

            if (reportCode.isEmpty() || rowId.isEmpty()) {
                response.put("status", "ERROR");
                response.put("message", "reportCode and rowId are required.");
                return response;
            }

            String currency = nvl((String) payload.get("currencies")).trim();
            if (currency.isEmpty()) {
                response.put("status", "ERROR");
                response.put("message", "currencies is required.");
                return response;
            }

         // 1. Don't create a second mapping for the same reportCode+rowId+columnId
            Optional<BrfCommonMapping> already =
            	    commonMappingRepo.findExistingOtherMapping(reportCode, rowId, columnId, currency,
            	        glSubheadCode, solId, constCode, legalEntity, schemeType, asstCls, purposeOfAdvn);

            if (already.isPresent()) {
                Map<String, String> skipped = new LinkedHashMap<>();
                skipped.put("currencies", currency);
                skipped.put("existingAccountId", already.get().getAccountIdBacid());
                skippedRecords.add(skipped);
                System.out.println("OTHER-MAPPING SKIPPED (already exists): existingAccountId=" + already.get().getAccountIdBacid());
            } else {
                // 2. Generate the id, then CHECK before inserting.
                String generatedAccountId = generateNextOthMappAccountId(reportCode);
                BrfCommonMappingId pk = new BrfCommonMappingId(generatedAccountId, reportCode, rowId, columnId, currency);

                int guard = 0;
                while (commonMappingRepo.existsById(pk) && guard < 50) {
                    generatedAccountId = incrementOthMappId(generatedAccountId);
                    pk = new BrfCommonMappingId(generatedAccountId, reportCode, rowId, columnId, currency);
                    guard++;
                }

                if (commonMappingRepo.existsById(pk)) {
                    Map<String, String> skipped = new LinkedHashMap<>();
                    skipped.put("currencies", currency);
                    skipped.put("reason", "Could not allocate a free OTHMAPP id after " + guard + " attempts.");
                    skippedRecords.add(skipped);
                } else {
                    BrfCommonMapping record = new BrfCommonMapping();
                    record.setAccountIdBacid(generatedAccountId);
                    record.setReportCode(reportCode);
                    record.setRowId(rowId);
                    record.setColumnId(columnId);
                    record.setCurrency(currency);   // ← comma-joined, single row
                    record.setGlHead(glHead);
                    record.setGlSubheadCode(glSubheadCode);
                    record.setSolId(solId);
                    record.setConstitutionCode(constCode);
                    record.setLegalEntityType(legalEntity);
                    record.setHniNetworth(hniNetworth);
                    record.setTurnover(turnover);
                    record.setSchemeType(schemeType);
                    record.setAsstCls(asstCls);
                    record.setPurposeOfAdvn(purposeOfAdvn);

                commonMappingRepo.save(record);
                commonMappingRepo.flush();   // next loop iteration's existsById/generate must see this insert

                Map<String, String> saved = new LinkedHashMap<>();
                saved.put("accountId", generatedAccountId);
                saved.put("currency",  currency);
                savedRecords.add(saved);

                System.out.println("OTHER-MAPPING INSERT: accountId=" + generatedAccountId
                    + " reportCode=" + reportCode + " currency=" + currency);
            }
            }

            response.put("status",   "SUCCESS");
            response.put("inserted", savedRecords.size());
            response.put("records",  savedRecords);
            response.put("skipped",  skippedRecords);

        } catch (Exception e) {
            System.err.println("SUBMIT OTHER MAPPING ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("status",  "ERROR");
            response.put("message", "Error: " + e.getMessage());
        }
        

        return response;
    }

    /** OTHMAPP1, OTHMAPP2, ... — counter restarts at 1 for every new report code. */
    private String generateNextOthMappAccountId(String reportCode) {
        List<String> existingIds = commonMappingRepo.findAccountIdsByReportCodeAndPrefix(reportCode);
        int maxNum = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith("OTHMAPP")) {
                try {
                    int n = Integer.parseInt(id.substring("OTHMAPP".length()).trim());
                    if (n > maxNum) maxNum = n;
                } catch (NumberFormatException ignore) { }
            }
        }
        return "OTHMAPP" + (maxNum + 1);
    }

    private String incrementOthMappId(String accountId) {
        try {
            int n = Integer.parseInt(accountId.substring("OTHMAPP".length()).trim());
            return "OTHMAPP" + (n + 1);
        } catch (NumberFormatException e) {
            return "OTHMAPP1";
        }
    }
}