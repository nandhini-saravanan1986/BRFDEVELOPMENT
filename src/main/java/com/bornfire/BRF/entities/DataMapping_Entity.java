package com.bornfire.BRF.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BRF_DATA_MAPPINGS")
public class DataMapping_Entity {

	@Id
	@Column(name = "MAPPING_ID")
	@JsonProperty("MAPPING_ID")
	private BigDecimal MAPPING_ID;

	@Column(name = "SOURCE_TABLE")
	@JsonProperty("SOURCE_TABLE")
	private String SOURCE_TABLE;

	@Column(name = "DEST_TABLE")
	@JsonProperty("DEST_TABLE")
	private String DEST_TABLE;

	@Column(name = "SOURCE_RULES", length = 4000)
	@JsonProperty("SOURCE_RULES")
	private String SOURCE_RULES;

	@Column(name = "DEST_RULES", length = 4000)
	@JsonProperty("DEST_RULES")
	private String DEST_RULES;

	public BigDecimal getMAPPING_ID() {
		return MAPPING_ID;
	}

	public void setMAPPING_ID(BigDecimal MAPPING_ID) {
		this.MAPPING_ID = MAPPING_ID;
	}

	public String getSOURCE_TABLE() {
		return SOURCE_TABLE;
	}

	public void setSOURCE_TABLE(String SOURCE_TABLE) {
		this.SOURCE_TABLE = SOURCE_TABLE;
	}

	public String getDEST_TABLE() {
		return DEST_TABLE;
	}

	public void setDEST_TABLE(String DEST_TABLE) {
		this.DEST_TABLE = DEST_TABLE;
	}

	public String getSOURCE_RULES() {
		return SOURCE_RULES;
	}

	public void setSOURCE_RULES(String SOURCE_RULES) {
		this.SOURCE_RULES = SOURCE_RULES;
	}

	public String getDEST_RULES() {
		return DEST_RULES;
	}

	public void setDEST_RULES(String DEST_RULES) {
		this.DEST_RULES = DEST_RULES;
	}
}