package com.bornfire.BRF.services;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
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

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
@ConfigurationProperties("output")
public class ReportServices {

	private static final Logger logger = LoggerFactory.getLogger(ReportServices.class);

	private static final Object TRANMASTERDETAILPage = null;
	
	@Autowired
	BRFReportsMasterRep brfReportsMasterRep;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${output.exportpathtemp}")
	private String templateFolder;
	
	 // ── Regex: B/C cell value starts with a digit (e.g. "1.0", "14.1", "2.3.4") ──
    private static final Pattern STARTS_WITH_DIGIT = Pattern.compile("^\\d+.*");
 
    // ── Header labels to skip (B column) ──────────────────────────────────────────
    private static final java.util.Set<String> HEADER_LABELS = new java.util.HashSet<>(
            java.util.Arrays.asList("no", "s.n.", "s.no", "s.no.", "sn"));
    
    private final org.apache.poi.ss.usermodel.DataFormatter DATA_FORMATTER =
            new org.apache.poi.ss.usermodel.DataFormatter();
	
	@Autowired
	SessionFactory sessionFactory1;

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
	
	public File findTemplate(String reportCode) {

	    String pattern = convertCode(reportCode);
	    System.out.println("Report Pattern : "+pattern);

	    File folder = new File(templateFolder);

	    File[] files = folder.listFiles();

	    if(files == null) return null;
	    /*
	    for(File file : files){
	    	System.out.println(file.getName());	      
	    }*/
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
 
        switch (type) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: {
                // DataFormatter respects the cell's display format (e.g. "1.10" stays "1.10")
                String formatted = DATA_FORMATTER.formatCellValue(cell).trim();
                if (!formatted.isEmpty()) return formatted;
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
    
    private List<String> getAvailableCols(Row row, int structure) {
        List<String> cols = new ArrayList<>();
        int startCol = (structure == 0) ? 2 : 3;; // Column D or like BRF65 starts from C

        int lastCol = row.getLastCellNum();
        for (int col = startCol; col <= lastCol; col++) {
            Cell ac = row.getCell(col);

            if (ac == null) continue;

            CellType type = ac.getCellTypeEnum();

            // Skip blank cells
            if (type == CellType.BLANK) continue;

            // Skip ALL string/text cells — they are labels, not data columns
            if (type == CellType.STRING) continue;

            // For formula cells: skip only non-trivial computed formulas
            // (simple =0 or =0.00 are treated as blank data slots and kept)
            if (type == CellType.FORMULA) {
                String formula = ac.getCellFormula().trim();
                if (!formula.matches("^\\d+(\\.\\d+)?$")) {
                    continue; // skip SUM / reference formulas
                }
            }

            // Position-based label: D→A, E→B, F→C …
            cols.add(String.valueOf((char) ('A' + (col - startCol))));
        }

        return cols;
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
        	
        	int lastCol = nameRow.getLastCellNum();
        	System.out.println("Last Column : "+lastCol);
            for (int col = 1; col <= lastCol; col++) {
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
        int     firstDataRow    = -1;
        int     lastBCNumRow    = -1;
        int     firstStopRow    = totalRows;
        boolean fileHasNumB     = false;
        int     lastAllStringRow = -1; // for structure=0: tracks last all-STRING column-header row

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

            if (firstDataRow == -1) {
                if (bNum) {
                    firstDataRow = i;
                } else if (!fileHasNumB) {
                    boolean hasAnyCol = false;
                    boolean allString = true;
                    int lastCol = row.getLastCellNum();
                    for (int col = 2; col < lastCol; col++) {
                        Cell dc = row.getCell(col);
                        if (dc == null || dc.getCellTypeEnum() == CellType.BLANK) continue;
                        hasAnyCol = true;
                        if (dc.getCellTypeEnum() != CellType.STRING) { allString = false; break; }
                    }
                    if (hasAnyCol && allString) lastAllStringRow = i;
                }
            }

            if (bNum || cNum) lastBCNumRow = i;
        }

        // structure=0 (BRF-65 type): firstDataRow = row immediately after the column-header row
        if (!fileHasNumB && firstDataRow == -1 && lastAllStringRow >= 0) {
            firstDataRow = lastAllStringRow + 1;
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
     
     /* ── Read column headers (rows before firstDataRow, cols E–M = index 4–12) ── */
     /*
     List<Map<String,String>> columns = new ArrayList<>();

     if (firstDataRow >= 0) {

         int startCol = (structure == 0) ? 2 : 3; // D or C;
         int endCol   = 12;

         int totalCols = endCol - startCol + 1;

         String[] colNames = new String[totalCols];
         Arrays.fill(colNames, "");

         int headerStart = (structure == 0 && lastAllStringRow >= 0) 
                 ? lastAllStringRow 
                 : Math.max(0, firstDataRow - 6);

      // CHANGE 1: <= instead of <  (include firstDataRow in scan)
         for (int r = headerStart; r <= firstDataRow; r++) {

             Row hr = sheet.getRow(r);
             if (hr == null) continue;

             for (int col = startCol; col <= endCol; col++) {

                 // CHANGE 2: only read STRING cells — skips numeric 0s in data rows
                 Cell hc = hr.getCell(col);
                 if (hc == null || hc.getCellTypeEnum() != CellType.STRING) continue;
                 String val = hc.getStringCellValue().trim();

                 if (!val.isEmpty()) {
                     if (!colNames[col - startCol].isEmpty())
                         colNames[col - startCol] += " - ";
                     colNames[col - startCol] += val;
                 }
             }
         }

         for (int i = 0; i < colNames.length; i++) {

             if (!colNames[i].isEmpty()) {

                 Map<String, String> cm = new HashMap<>();
                 cm.put("colCode", String.valueOf((char)('A' + i)));
                 cm.put("colName", colNames[i]);

                 columns.add(cm);
             }
         }
     }
     */
     List<Map<String,String>> columns = new ArrayList<>();

     if (firstDataRow >= 0) {

         int startCol = (structure == 0) ? 2 : 3; 

         int headerStart = (structure == 0 && lastAllStringRow >= 0) 
                 ? lastAllStringRow 
                 : Math.max(0, firstDataRow - 6);

         int endCol = 0;
         for (int r = headerStart; r <= firstDataRow; r++) {
             Row hr = sheet.getRow(r);
             if (hr != null && hr.getLastCellNum() - 1 > endCol) {
                 endCol = hr.getLastCellNum() - 1; 
             }
         }
         
         if (endCol < startCol) endCol = startCol;

         int totalCols = endCol - startCol + 1;
         String[] colNames = new String[totalCols];
         Arrays.fill(colNames, "");

         for (int r = headerStart; r <= firstDataRow; r++) {
             Row hr = sheet.getRow(r);
             if (hr == null) continue;

             for (int col = startCol; col <= endCol; col++) {

                 Cell hc = hr.getCell(col);
                 if (hc == null || hc.getCellTypeEnum() != CellType.STRING) continue;
                 String val = hc.getStringCellValue().trim();

                 if (!val.isEmpty()) {
                     if (!colNames[col - startCol].isEmpty())
                         colNames[col - startCol] += " - ";
                     colNames[col - startCol] += val;
                 }
             }
         }

         for (int i = 0; i < colNames.length; i++) {
             if (!colNames[i].isEmpty()) {
                 Map<String, String> cm = new HashMap<>();
                 cm.put("colCode", String.valueOf((char)('A' + i)));
                 cm.put("colName", colNames[i]);
                 columns.add(cm);
             }
         }
     }
     result.put("columns", columns);
//     System.out.println("COLUMNS: " + columns);
 
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
 
            /* ── Hard stop: Notes or Form-number line ── */
            if (isStopRow(level1, level2, description)) break;
 
            /* ── Wait until the first data row ── */
            if (!inDataZone) {
                if (i == firstDataRow) inDataZone = true;
                else continue;
            }
 
            /* ── After the last numbered B/C row: look for a text Total row ── */
            if (structure != 0 && lastBCNumRow >= 0 && i > lastBCNumRow) {
            	String combined = (rawB + " " + rawC + " " + level1 + " " + level2 + " " + description).toUpperCase();
                if (combined.contains("TOTAL")) {

                    String header = "";

                    int realFormulaCount = 0;
                    int headerCheckStart = (structure == 0) ? 2 : 4;

                    int lastCol = row.getLastCellNum();
                    for (int col = headerCheckStart; col < lastCol; col++) {

                        Cell c = row.getCell(col);

                        if (c == null || c.getCellTypeEnum() == CellType.BLANK) continue;
                        if (c.getCellTypeEnum() == CellType.STRING) continue;

                        if (c.getCellTypeEnum() == CellType.FORMULA) {

                            String formula = c.getCellFormula().trim();

                            if (!formula.matches("^\\d+(\\.\\d+)?$")) {
                                realFormulaCount++;
                            }
                        }
                    }

                    if (realFormulaCount > 0) {
                        header = "Y";
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("level1", level1);
                    map.put("level2", level2);
                    String totalDesc = !description.isEmpty() ? description
                            : (!rawC.isEmpty() ? rawC : rawB);
                    map.put("description", totalDesc);

                    map.put("label", "ROW" + rowLabel++);
                    map.put("header", header);
                    map.put("availableCols", getAvailableCols(row, structure));
                    map.put("remarks", "");
                    rows.add(map);
                }
                break;   // nothing more to read after the numbered zone
            }
            
            
            /* ── Skip fully blank rows inside the data zone ── */
            if (level1.isEmpty() && level2.isEmpty() && description.isEmpty()) continue;

            /* ── NEW LOGIC: skip fake "1" rows with only text ── */
            boolean hasOnlyText = true;

            int checkStart = (structure == 0) ? 2 : 4;

            for (int col = checkStart; col <= 12; col++) {

                Cell c = row.getCell(col);

                if (c == null || c.getCellTypeEnum() == CellType.BLANK)
                    continue;

                // If numeric or formula exists → it's real data row
                if (c.getCellTypeEnum() == CellType.NUMERIC || 
                    c.getCellTypeEnum() == CellType.FORMULA) {
                    hasOnlyText = false;
                    break;
                }
            }

            if (level1.trim().equals("1") && hasOnlyText) {
                continue;
            }

           
 
            /* ── Detect formula cell in column D (marks computed/total rows) ── */ 
            
            String header = "";

            int formulaCount = 0;
            int nonEmptyCount = 0;

         // FIX — start from correct column based on structure:
            int headerCheckStart = (structure == 0) ? 2 : 4;
            int lastCol = row.getLastCellNum();
            for (int col = headerCheckStart; col < lastCol; col++) {

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
//            if (level1.trim().equals("1") && !header.equals("Y")) {
//                continue;
//            }
 
            Map<String, Object> map = new HashMap<>();
            map.put("level1",      level1);
            map.put("level2",      level2);
            map.put("description", description);
            map.put("label",       "ROW" + rowLabel++);
            map.put("header",      header);
            map.put("availableCols", getAvailableCols(row, structure));
            map.put("remarks",     "");
            rows.add(map); 
        }
 
        workbook.close();
 
        result.put("reportName", reportName);
        result.put("rows",       rows);
        System.out.println("ROWS: " + rows.size());
        return result;
        
    }
    
    public List<String> getGLHeads(String dataType) {
        String sql = "SELECT DISTINCT GL_HEAD FROM BRF_BASE_MAPPING_TABLE " +
                     "WHERE UPPER(DATA_TYPE) = UPPER(?) ORDER BY GL_HEAD";
        return jdbcTemplate.queryForList(sql, String.class, dataType);
    }

    public List<Map<String, String>> getGLSubHeads(String dataType, String glHead) {
        String sql = "SELECT DISTINCT GL_SUBHEAD_CODE " +
                     "FROM BRF_BASE_MAPPING_TABLE " +
                     "WHERE UPPER(DATA_TYPE) = UPPER(?) " +
                     "AND UPPER(GL_HEAD) = UPPER(?) " +
                     "ORDER BY GL_SUBHEAD_CODE";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("subHeadCode", rs.getString("GL_SUBHEAD_CODE"));
            return map;
        }, dataType, glHead);
    }
	@SuppressWarnings("unchecked")
	public List<ReportTitle> getReportName(String reportid) {

		logger.info("Getting Report Name :" + reportid);

		Session hs = sessionFactory1.getCurrentSession();
		List<Object[]> reportName = hs.createNativeQuery(
				"select distinct a.report_id, a.report_name from report_master_tb a where a.parent_report_id=?1 order by a.report_id")
				.setParameter(1, reportid).getResultList();

		List<ReportTitle> title = new ArrayList<ReportTitle>();

		for (Object[] a : reportName) {

			String repId = (String) a[0];
			String repName = (String) a[1];

			title.add(new ReportTitle(repName, repId));

		}

		return title;

	}

}
