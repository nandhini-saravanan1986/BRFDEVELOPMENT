package com.bornfire.BRF.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ETL_MONITORING_DETAILS_TABLE")
public class ETL_MONITORING_DETAILS_ENTITY {

	@Id
	public BigDecimal error_id;
	public String module_id;
	public String module_name;
	public String procedure_name;
	public Date report_date;
	public String error_details;
	public Date recr_time;
	public String status;
	public String del_flg;

	public BigDecimal getError_id() {
		return error_id;
	}

	public void setError_id(BigDecimal error_id) {
		this.error_id = error_id;
	}

	public String getModule_id() {
		return module_id;
	}

	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getProcedure_name() {
		return procedure_name;
	}

	public void setProcedure_name(String procedure_name) {
		this.procedure_name = procedure_name;
	}

	public Date getReport_date() {
		return report_date;
	}

	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}

	public String getError_details() {
		return error_details;
	}

	public void setError_details(String error_details) {
		this.error_details = error_details;
	}

	public Date getRecr_time() {
		return recr_time;
	}

	public void setRecr_time(Date recr_time) {
		this.recr_time = recr_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDel_flg() {
		return del_flg;
	}

	public void setDel_flg(String del_flg) {
		this.del_flg = del_flg;
	}

}
