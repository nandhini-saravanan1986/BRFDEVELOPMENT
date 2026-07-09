package com.bornfire.BRF.entities;

import java.io.Serializable;
import java.util.Objects;

public class BrfCommonMappingId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountIdBacid;
    private String reportCode;
    private String rowId;
    private String columnId;
    private String currency;

    public BrfCommonMappingId() {}

    public BrfCommonMappingId(String accountIdBacid, String reportCode, String rowId,String columnId,String currency) {
        this.accountIdBacid = accountIdBacid;
        this.reportCode     = reportCode;
        this.rowId          = rowId;
        this.columnId          = columnId;
        this.currency          = currency;
    }

    public String getAccountIdBacid()             { return accountIdBacid; }
    public void   setAccountIdBacid(String v)     { this.accountIdBacid = v; }
    public String getReportCode()                 { return reportCode; }
    public void   setReportCode(String v)         { this.reportCode = v; }
    public String getRowId()                      { return rowId; }
    public void   setRowId(String v)              { this.rowId = v; }    

    public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	// REQUIRED by JPA for composite key equality checks
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrfCommonMappingId)) return false;
        BrfCommonMappingId that = (BrfCommonMappingId) o;
        return Objects.equals(accountIdBacid, that.accountIdBacid)
            && Objects.equals(reportCode,     that.reportCode)
            && Objects.equals(rowId,          that.rowId)
            && Objects.equals(columnId,          that.columnId)
            && Objects.equals(currency,          that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountIdBacid, reportCode, rowId,columnId,currency);
    }
}