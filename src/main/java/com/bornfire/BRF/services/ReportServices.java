package com.bornfire.BRF.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.BRF.entities.BRFReportsMasterRep;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
import java.util.regex.Pattern;

@Service
@Transactional
@ConfigurationProperties("output")
public class ReportServices {

	@Autowired
	BRFReportsMasterRep brfReportsMasterRep;
	
	@Value("${output.exportpathtemp}")
	private String templateFolder;
	
	 // ── Regex: B/C cell value starts with a digit (e.g. "1.0", "14.1", "2.3.4") ──
    private static final Pattern STARTS_WITH_DIGIT = Pattern.compile("^\\d+.*");
 
    // ── Header labels to skip (B column) ──────────────────────────────────────────
    private static final java.util.Set<String> HEADER_LABELS = new java.util.HashSet<>(
            java.util.Arrays.asList("no", "s.n.", "s.no", "s.no.", "sn"));

	public class ReportTitle {

		String reportName;
		String reportId;
		Date report_date;
		String domain;
		Character completedFlg;
		String frequency;

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}

		public String getReportId() {
			return reportId;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}

		public Date getReport_date() {
			return report_date;
		}

		public void setReport_date(Date report_date) {
			this.report_date = report_date;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Character getCompletedFlg() {
			return completedFlg;
		}

		public void setCompletedFlg(Character completedFlg) {
			this.completedFlg = completedFlg;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public ReportTitle(String reportName, String reportId) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
		}

		public ReportTitle(String reportName, String reportId, Date reportDate, String domain, Character completedFlg,
				String frequency) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
			this.report_date = reportDate;
			this.domain = domain;
			this.completedFlg = completedFlg;
			this.frequency = frequency;
		}

	}

	public List<String> getDomainList() {

		return brfReportsMasterRep.getDomainList();
	}
	
	private String convertCode(String code) {

//	    int num = Integer.parseInt(code.replace("BRF",""));
//
//	    return String.format("BRF-%03d", num);
		String numPart = code.replace("BRF", "");

	    // if already contains leading zeros, keep it
	    if(numPart.startsWith("0")) {
	        return "BRF-" + numPart;
	    }

	    // otherwise pad to 3 digits
	    int num = Integer.parseInt(numPart);
	    return String.format("BRF-%03d", num);

	}
	
	private File findTemplate(String reportCode) {

	    String pattern = convertCode(reportCode);

	    File folder = new File(templateFolder);

	    File[] files = folder.listFiles();

	    if(files == null) return null;

	    for(File file : files){

	        if(file.getName().contains(pattern)){
	            return file;
	        }
	    }

	    return null;
	}
	
	// ── Evaluate cell to its actual value (handles FORMULA cells properly) ────────
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
 
        CellType type = cell.getCellTypeEnum();
// 
//        if (type == CellType.FORMULA) {
//            try {
//                CellValue cv = evaluator.evaluate(cell);
//                switch (cv.getCellTypeEnum()) {
//                    case NUMERIC: {
//                        double n = cv.getNumberValue();
//                        return (n == (long) n) ? String.valueOf((long) n) : String.valueOf(n);
//                    }
//                    case STRING:  return cv.getStringValue().trim();
//                    case BOOLEAN: return String.valueOf(cv.getBooleanValue());
//                    default:      return "";
//                }
//            } catch (Exception e) {
//                // Fallback: return formula string so the cell is never silently blank
//                return cell.getCellFormula();
//            }
//        }
 
        switch (type) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: {
                double n = cell.getNumericCellValue();
                return (n == (long) n) ? String.valueOf((long) n) : String.valueOf(n);
            }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return "";
            default:      return "";
        }
    }
 
    // ── True when a cell value looks like a numbered item: "1", "1.0", "14.1" ────
    private boolean isNumericLabel(String s) {
        return s != null && STARTS_WITH_DIGIT.matcher(s.trim()).matches();
    }
 
    // ── True when combined text signals a stop condition ──────────────────────────
    private boolean isStopRow(String b, String c, String d) {
        String bU = b.toUpperCase();
        String cU = c.toUpperCase();
        String dU = d.toUpperCase();
        // Notes section
        if (bU.startsWith("NOTE") || cU.startsWith("NOTE") || dU.startsWith("NOTE")) return true;
        // Form number line
        String all = bU + " " + cU + " " + dU;
        return all.contains("FORM NO") || all.contains("BSD/BRF");
    }
 
    // ── True when the row is a header row that must be skipped ────────────────────
    private boolean isHeaderLabel(String level1) {
        return HEADER_LABELS.contains(level1.toLowerCase().trim());
    }
    
 // True ONLY for pure integers: "1", "2", "14" — NOT "1.1"
    private boolean isIntegerLabel(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        return s.trim().matches("^\\d+$");
    }

    // True for sub-codes: "1.1", "14.3", "2.3.4 (a)" — NOT plain "0"
    private boolean isDecimalLabel(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        return s.trim().matches("^\\d+\\.\\d+.*");
    }
 
    // ─────────────────────────────────────────────────────────────────────────────
    public Map<String, Object> readTemplate(String reportCode) throws Exception {
 
        Map<String, Object>       result = new HashMap<>();
        List<Map<String, Object>> rows   = new ArrayList<>();
 
        File     templateFile = findTemplate(reportCode);
        Workbook workbook     = WorkbookFactory.create(templateFile);
        Sheet    sheet        = workbook.getSheetAt(0);
//        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
 
        /* ── Read report name from row 2 (index 1)── */
        String reportName = "";
        Row nameRow = sheet.getRow(1);
        if (nameRow != null) {
            // Row 2 uses a merged cell — POI stores the value in the first cell
            // of the merge. Scan columns B→M (index 1–13) to find it.
            for (int col = 1; col <= 13; col++) {
                String val = getCellValue(nameRow.getCell(col));
                if (val != null && !val.isEmpty()) {
                    reportName = val;
                    break;
                }
            }
        }
 
        int totalRows = sheet.getLastRowNum() + 1;
 
        /* ═══════════════════════════════════════════════════════════════
           PRE-SCAN  – determine:
             firstDataRow  : first row index where B is a numbered label
                             (or, for text-only-B files, first row with
                              a numeric value in column C or D)
             lastBCNumRow  : last row index (before any stop-row) where
                             B or C carries a numbered label
             firstStopRow  : first row index that is a Note/FormNo line
           ═══════════════════════════════════════════════════════════════ */
        int     firstDataRow  = -1;
        int     lastBCNumRow  = -1;
        int     firstStopRow  = totalRows;   // process up to here
        boolean fileHasNumB   = false;
 
        for (int i = 0; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
 
            String b = getCellValue(row.getCell(1));
            String c = getCellValue(row.getCell(2));
            String d = getCellValue(row.getCell(3));
 
            if (isStopRow(b, c, d)) { firstStopRow = i; break; }
 
            boolean bNum = isNumericLabel(b);
            boolean cNum = isNumericLabel(c);
            if (bNum) fileHasNumB = true;
 
            // ── Determine firstDataRow ──
            if (firstDataRow == -1) {
                if (bNum) {
                    firstDataRow = i;
                } else if (!fileHasNumB) {
                    // Text-only B files (e.g. BRF-065): start at first row
                    // that has a numeric value in column C or D
                    Cell cCell = row.getCell(2);
                    Cell dCell = row.getCell(3);
                    boolean cIsNumeric = (cCell != null && cCell.getCellTypeEnum() == CellType.NUMERIC);
                    boolean dIsNumeric = (dCell != null && dCell.getCellTypeEnum() == CellType.NUMERIC);
                    if (cIsNumeric || dIsNumeric) firstDataRow = i;
                }
            }
 
            if (bNum || cNum) lastBCNumRow = i;
        }
        
        /* ── Detect file column structure ──────────────────────────────────
        structure = 3  →  B=level1-int, C=level2-decimal, D=description
        structure = 2  →  B=int-or-decimal, C=description, D=ignore
        structure = 0  →  B=description-text, no numeric codes at all
        ────────────────────────────────────────────────────────────────── */
     int structure;
     if (!fileHasNumB) {
         structure = 0;  // text-only: BRF-65 type
     } else {
         boolean cHasDecimal = false;
         for (int i = (firstDataRow == -1 ? 0 : firstDataRow);
                  i <= Math.min(lastBCNumRow, firstStopRow - 1); i++) {
             Row r = sheet.getRow(i);
             if (r == null) continue;
             if (isDecimalLabel(getCellValue(r.getCell(2)))) {
                 cHasDecimal = true;
                 break;
             }
         }
         structure = cHasDecimal ? 3 : 2;
     }
 
        /* ═══════════════════════════════════════════════════════════════
           MAIN SCAN  – collect data rows
           ═══════════════════════════════════════════════════════════════ */
        int     rowLabel    = 101;
        boolean inDataZone  = (firstDataRow == -1); // if no data found, skip all
 
        for (int i = 0; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
 
            Cell colB = row.getCell(1);
            Cell colC = row.getCell(2);
            Cell colD = row.getCell(3);
 
//            String level1      = getCellValue(colB);
//            String level2      = getCellValue(colC);
//            String description = getCellValue(colD);
            
            /* ── Map columns → level1 / level2 / description by structure ── */
            String rawB = getCellValue(colB);
            String rawC = getCellValue(colC);
            String rawD = getCellValue(colD);

            String level1, level2, description;

            if (structure == 3) {
                // B=integer group code, C=decimal sub-code, D=description text
                level1      = isIntegerLabel(rawB) ? rawB : "";
                level2      = isDecimalLabel(rawC) ? rawC : "";
                description = rawD;

            } else if (structure == 2) {
                // B carries both integer and decimal codes; C is description text
                if (isIntegerLabel(rawB)) {
                    level1 = rawB;  level2 = "";     description = rawC;
                } else if (isDecimalLabel(rawB)) {
                    level1 = "";    level2 = rawB;   description = rawC;
                } else {
                    // B is empty or plain text (e.g. "All Individual Deposits", "Sub Total")
                    level1 = "";    level2 = "";
                    description = rawC.isEmpty() ? rawB : rawC;
                }

            } else {
                // structure == 0 : text-only files like BRF-65
                // B holds the row label/description; no numeric codes at all
                level1 = "";  level2 = "";  description = rawB;
            }
 
            /* ── Skip "No" / "S.N." / "S.No" header rows ── */
            if (isHeaderLabel(level1)) continue;
 
            /* ── Skip column-header rows containing "FIELD DESCRIPTION" ── */
//            if (description.toUpperCase().contains("FIELD DESCRIPTION")) continue;
 
            /* ── Hard stop: Notes or Form-number line ── */
            if (isStopRow(level1, level2, description)) break;
 
            /* ── Wait until the first data row ── */
            if (!inDataZone) {
                if (i == firstDataRow) inDataZone = true;
                else continue;
            }
 
            /* ── After the last numbered B/C row: look for a text Total row ── */
            if (lastBCNumRow >= 0 && i > lastBCNumRow) {
                String combined = (level1 + " " + level2 + " " + description).toUpperCase();
                if (combined.contains("TOTAL")) {
                    // Include this summary/total row, then stop
                    Map<String, Object> map = new HashMap<>();
                    map.put("level1",      level1);
                    map.put("level2",      level2);
                    map.put("description", description);
                    map.put("label",       "ROW" + rowLabel++);
                    map.put("header",      "");
                    map.put("remarks",     "");
                    rows.add(map);
                }
                break;   // nothing more to read after the numbered zone
            }
 
            /* ── Skip fully blank rows inside the data zone ── */
            if (level1.isEmpty() && level2.isEmpty() && description.isEmpty()) continue;
 
            /* ── Detect formula cell in column D (marks computed/total rows) ── */
//            String header = "";
//            int formulaCount = 0;
//            int nonEmptyCount = 0;
//            for (int col = 4; col <= 12; col++) {          // columns E(4) → M(12)
//                Cell c = row.getCell(col);
//                if (c != null && c.getCellTypeEnum() != CellType.BLANK) {
//                    nonEmptyCount++;
//                    if (c.getCellTypeEnum() == CellType.FORMULA) {
//                        formulaCount++;
//                    }
//                }
//            }
//            // "Y" only when every non-empty value cell in the row is a formula
//            if (nonEmptyCount > 0 && formulaCount == nonEmptyCount) {
//                header = "Y";
//            }
            
            String header = "";

            int formulaCount = 0;
            int nonEmptyCount = 0;

            for (int col = 4; col <= 12; col++) {   // E → M

                Cell c = row.getCell(col);

                if (c == null || c.getCellTypeEnum() == CellType.BLANK)
                    continue;

                nonEmptyCount++;

                if (c.getCellTypeEnum() == CellType.FORMULA) {

                    String formula = c.getCellFormula().trim();

                    // ignore formulas like =0 or =0.11
                    if (!formula.matches("^\\d+(\\.\\d+)?$")) {
                        formulaCount++;
                    }
                }
            }

            if (nonEmptyCount > 0 && formulaCount == nonEmptyCount) {
                header = "Y";
            }
            if (level1.trim().equals("1") && !header.equals("Y")) {
                continue;
            }
 
            Map<String, Object> map = new HashMap<>();
            map.put("level1",      level1);
            map.put("level2",      level2);
            map.put("description", description);
            map.put("label",       "ROW" + rowLabel++);
            map.put("header",      header);
            map.put("remarks",     "");
            rows.add(map);
        }
 
        workbook.close();
 
        result.put("reportName", reportName);
        result.put("rows",       rows);
        return result;
    }
}
