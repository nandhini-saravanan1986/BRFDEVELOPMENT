package com.bornfire.BRF.entities;

import java.io.Serializable;

public class BrfBaseMappingId implements Serializable {

	private String accountIdBacid;
	private String currency;

	public String getAccountIdBacid() {
		return accountIdBacid;
	}

	public void setAccountIdBacid(String accountIdBacid) {
		this.accountIdBacid = accountIdBacid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
