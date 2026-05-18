package com.bornfire.BRF.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ETL_MONITORING_MAIN_TABLE")
public class ETL_MONITORING_MAIN_ENTITY {

	@Id
	public BigDecimal process_id;
	public String module_id;
	public String module_name;
	public Date report_date;
	public Date start_time;
	public Date end_time;
	public BigDecimal no_of_rows_processed;
	public String status_flg;
	public String data_frequency;

	public BigDecimal getProcess_id() {
		return process_id;
	}

	public void setProcess_id(BigDecimal process_id) {
		this.process_id = process_id;
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

	public Date getReport_date() {
		return report_date;
	}

	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public BigDecimal getNo_of_rows_processed() {
		return no_of_rows_processed;
	}

	public void setNo_of_rows_processed(BigDecimal no_of_rows_processed) {
		this.no_of_rows_processed = no_of_rows_processed;
	}

	public String getStatus_flg() {
		return status_flg;
	}

	public void setStatus_flg(String status_flg) {
		this.status_flg = status_flg;
	}

	public String getData_frequency() {
		return data_frequency;
	}

	public void setData_frequency(String data_frequency) {
		this.data_frequency = data_frequency;
	}

}
