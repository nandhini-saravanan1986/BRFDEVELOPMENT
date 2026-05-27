package com.bornfire.BRF.services;

import com.bornfire.BRF.entities.DataMapping_Entity;
import com.bornfire.BRF.entities.DataMapping_Repo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataComparisonService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataMapping_Repo DataMapping_Repo;

	@Autowired
	private ObjectMapper objectMapper;

	public String getAmountColumn(String tableName) {
		switch (tableName.toUpperCase()) {
		case "BRF_TREASURY_MASTER_TB":
			return "AMOUNT_AC";
		case "BRF_TREASURY_PLACEMENT_ID":
			return "NOMINAL_1";		
		default:
			return "AMOUNT";
		}
	}

	public String getDateColumn(String tableName) {
		switch (tableName.toUpperCase()) {
		case "BRF_TREASURY_MASTER_TB":
			return "REPORT_DATE";
		case "BRF_TREASURY_PLACEMENT_ID":
			return "REPORT_DATE";
		default:
			return "REPORT_DATE";
		}
	}

	public List<String> getAllDistinctvalues(String columnname, String tablename) {
		String sql = "SELECT DISTINCT(" + columnname + ") FROM " + tablename + " WHERE " + columnname + " IS NOT NULL";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public boolean compareAllMappingsForDate(Date targetDate) {
		List<DataMapping_Entity> allMappings = DataMapping_Repo.findAll();
		if (allMappings.isEmpty())
			return true;

		boolean allTablesMatch = true;
		for (DataMapping_Entity mapping : allMappings) {
			boolean isMatch = compareTableAmountsForDate(mapping.getSOURCE_TABLE(), mapping.getDEST_TABLE(), targetDate,
					mapping.getSOURCE_RULES(), mapping.getDEST_RULES());

			if (!isMatch) {
				allTablesMatch = false;
				System.out.println("Logic Validation Failed for pair: " + mapping.getSOURCE_TABLE() + " -> "
						+ mapping.getDEST_TABLE());
			}
		}
		return allTablesMatch;
	}

	public boolean compareMappingsForSpecificTable(String targetTableName, Date targetDate) {
		List<DataMapping_Entity> allMappings = DataMapping_Repo.findAll();
		List<DataMapping_Entity> relevantMappings = new ArrayList<>();

		for (DataMapping_Entity mapping : allMappings) {
			if (targetTableName.equalsIgnoreCase(mapping.getSOURCE_TABLE())
					|| targetTableName.equalsIgnoreCase(mapping.getDEST_TABLE())) {
				relevantMappings.add(mapping);
			}
		}

		if (relevantMappings.isEmpty())
			return true;

		boolean allTablesMatch = true;
		for (DataMapping_Entity mapping : relevantMappings) {
			boolean isMatch = compareTableAmountsForDate(mapping.getSOURCE_TABLE(), mapping.getDEST_TABLE(), targetDate,
					mapping.getSOURCE_RULES(), mapping.getDEST_RULES());
			if (!isMatch)
				allTablesMatch = false;
		}
		return allTablesMatch;
	}

	public boolean compareTableAmountsForDate(String sourceTable, String destTable, Date targetDate, String sourceRules,
			String destRules) {
		BigDecimal srcAmt = calculateTableAmount(sourceTable, targetDate, sourceRules);
		BigDecimal destAmt = calculateTableAmount(destTable, targetDate, destRules);

		System.out.println("Source value : " + srcAmt + " | Destination value : " + destAmt);
		return srcAmt.compareTo(destAmt) == 0;
	}

	public List<Map<String, Object>> getDetailedMappingReport(String targetTableName, Date targetDate) {
		List<DataMapping_Entity> allMappings = DataMapping_Repo.findAll();
		List<Map<String, Object>> reportList = new ArrayList<>();

		for (DataMapping_Entity mapping : allMappings) {
			if (targetTableName.equalsIgnoreCase(mapping.getSOURCE_TABLE())
					|| targetTableName.equalsIgnoreCase(mapping.getDEST_TABLE())) {

				Map<String, Object> details = new HashMap<>();
				details.put("sourceTable", mapping.getSOURCE_TABLE());
				details.put("destTable", mapping.getDEST_TABLE());
				details.put("sourceRules", mapping.getSOURCE_RULES());
				details.put("destRules", mapping.getDEST_RULES());

				BigDecimal srcAmt = calculateTableAmount(mapping.getSOURCE_TABLE(), targetDate,
						mapping.getSOURCE_RULES());
				BigDecimal destAmt = calculateTableAmount(mapping.getDEST_TABLE(), targetDate, mapping.getDEST_RULES());

				details.put("sourceAmount", srcAmt);
				details.put("destAmount", destAmt);
				details.put("isMatch", srcAmt.compareTo(destAmt) == 0);

				reportList.add(details);
			}
		}
		return reportList;
	}

	public Map<String, Object> getSingleTableValidationReport(String tableName, Date targetDate) {
		Map<String, Object> details = new HashMap<>();

		String col1 = "CREDIT_AMOUNT";
		String col2 = "DEBIT_AMOUNT";
		String dateCol = getDateColumn(tableName);

		String sql = "SELECT SUM(" + col1 + ") AS AMT1, SUM(" + col2 + ") AS AMT2 FROM " + tableName + " WHERE "
				+ dateCol + " = ?";

		try {
			Map<String, Object> row = jdbcTemplate.queryForMap(sql, new java.sql.Date(targetDate.getTime()));
			BigDecimal amt1 = row.get("AMT1") != null ? new BigDecimal(row.get("AMT1").toString()).abs()
					: BigDecimal.ZERO;
			BigDecimal amt2 = row.get("AMT2") != null ? new BigDecimal(row.get("AMT2").toString()).abs()
					: BigDecimal.ZERO;

			details.put("tableName", tableName);
			details.put("col1Name", col1);
			details.put("col2Name", col2);
			details.put("col1Amount", amt1);
			details.put("col2Amount", amt2);
			details.put("isMatch", amt1.compareTo(amt2) == 0);
		} catch (Exception e) {
			details.put("col1Amount", BigDecimal.ZERO);
			details.put("col2Amount", BigDecimal.ZERO);
			details.put("isMatch", false);
		}
		return details;
	}

	private BigDecimal calculateTableAmount(String tableName, Date targetDate, String rulesJson) {
		String amountCol = getAmountColumn(tableName);
		String dateCol = getDateColumn(tableName);

		StringBuilder sql = new StringBuilder(
				"SELECT SUM(" + amountCol + ") AS AMOUNT FROM " + tableName + " WHERE " + dateCol + " = ?");
		List<Object> params = new ArrayList<>();
		params.add(new java.sql.Date(targetDate.getTime()));

		try {
			if (rulesJson != null && !rulesJson.isEmpty() && !rulesJson.equals("null")) {
				List<Map<String, String>> rulesList = objectMapper.readValue(rulesJson,
						new TypeReference<List<Map<String, String>>>() {
						});

				for (Map<String, String> rule : rulesList) {
					String dbName = rule.get("dbName");
					String action = rule.get("action");

					if (action != null && !action.equalsIgnoreCase("Unmapped") && !action.equalsIgnoreCase("Ignore")) {
						if (action.contains(",")) {
							String[] multipleActions = action.split(",");
							sql.append(" AND ").append(dbName).append(" IN (");
							for (int i = 0; i < multipleActions.length; i++) {
								sql.append("?");
								if (i < multipleActions.length - 1)
									sql.append(",");
								params.add(multipleActions[i].trim());
							}
							sql.append(")");
						} else {
							sql.append(" AND ").append(dbName).append(" = ?");
							params.add(action.trim());
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error parsing rules for " + tableName);
		}

		List<Map<String, Object>> data = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Map<String, Object> row : data) {
			if (row.get("AMOUNT") != null) {
				totalAmount = totalAmount.add(new BigDecimal(row.get("AMOUNT").toString()).abs());
			}
		}
		return totalAmount;
	}
}
