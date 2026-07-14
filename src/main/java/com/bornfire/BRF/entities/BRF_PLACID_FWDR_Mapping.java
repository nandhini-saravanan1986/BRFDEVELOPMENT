package com.bornfire.BRF.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BRF_PLACID_FWDR_MAPPING_TABLE")
public class BRF_PLACID_FWDR_Mapping {

	@Id
	private Long id;

	@Column(name = "REPORT_CODE")
	private String reportCode;

	@Column(name = "ROW_LABEL")
	private String rowLabel;

	@Column(name = "COLUMN_LABEL")
	private String columnLabel;

	@Column(name = "SCREEN_NAME")
	private String screenName;

	@Column(name = "portefeuille_contrepartie")
	private String portefeuille_contrepartie;

	@Column(name = "portefeuille_contrepartie_EXCLUDE")
	private String portefeuille_contrepartie_exclude;

	@Column(name = "TITRE")
	private String titre;

	@Column(name = "TITRE_EXCLUDE")
	private String titre_exclude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getRowLabel() {
		return rowLabel;
	}

	public void setRowLabel(String rowLabel) {
		this.rowLabel = rowLabel;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getPortefeuille_contrepartie() {
		return portefeuille_contrepartie;
	}

	public void setPortefeuille_contrepartie(String portefeuille_contrepartie) {
		this.portefeuille_contrepartie = portefeuille_contrepartie;
	}

	public String getPortefeuille_contrepartie_exclude() {
		return portefeuille_contrepartie_exclude;
	}

	public void setPortefeuille_contrepartie_exclude(String portefeuille_contrepartie_exclude) {
		this.portefeuille_contrepartie_exclude = portefeuille_contrepartie_exclude;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getTitre_exclude() {
		return titre_exclude;
	}

	public void setTitre_exclude(String titre_exclude) {
		this.titre_exclude = titre_exclude;
	}

}
