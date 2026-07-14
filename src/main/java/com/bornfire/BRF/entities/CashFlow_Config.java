package com.bornfire.BRF.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CASHFLOW_CONFIG")
public class CashFlow_Config {
	@Id
	@Column(name = "config_id")
	private Long configId;

	@Column(name = "condition_name")
	private String conditionName;

	@Column(name = "global_gshc")
	private String globalGshc;

	@Column(name = "global_bacid")
	private String globalBacid;

	@Column(name = "cls_list")
	private String clsList;

	@Column(name = "si_list")
	private String siList;

	@Column(name = "type_list")
	private String typeList;

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getGlobalGshc() {
		return globalGshc;
	}

	public void setGlobalGshc(String globalGshc) {
		this.globalGshc = globalGshc;
	}

	public String getGlobalBacid() {
		return globalBacid;
	}

	public void setGlobalBacid(String globalBacid) {
		this.globalBacid = globalBacid;
	}

	public String getClsList() {
		return clsList;
	}

	public void setClsList(String clsList) {
		this.clsList = clsList;
	}

	public String getSiList() {
		return siList;
	}

	public void setSiList(String siList) {
		this.siList = siList;
	}

	public String getTypeList() {
		return typeList;
	}

	public void setTypeList(String typeList) {
		this.typeList = typeList;
	}

}
