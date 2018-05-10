package de.seidfred.accountservice.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.seidfred.accountservice.util.JsonPath;
import de.seidfred.accountservice.util.Serializer;

@JsonSerialize(using = Serializer.class)
public class AccountResponseBody {
	@JsonPath(path = "cstmrAccInfStsRpt.grpHdr.msgId")
	private String requestId;
	@JsonPath(path = "cstmrAccInfStsRpt.grpHdr.creDtTm")
	private Date requestCreationDate;
	@JsonPath(path = "cstmrAccInfStsRpt.accInf.id.iban")
	private String accountIban;

	// cstmrAccInfStsRpt.accInf.accTx
	// private List<AccountTransaction> accountTransactions = new
	// ArrayList<AccountTransaction>();

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Date getRequestCreationDate() {
		return requestCreationDate;
	}

	public void setRequestCreationDate(Date requestCreationDate) {
		this.requestCreationDate = requestCreationDate;
	}

	public String getAccountIban() {
		return accountIban;
	}

	public void setAccountIban(String accountIban) {
		this.accountIban = accountIban;
	}

	// public List<AccountTransaction> getAccountTransactions() {
	// return accountTransactions;
	// }
	//
	// public void setAccountTransactions(
	// List<AccountTransaction> accountTransactions) {
	// this.accountTransactions = accountTransactions;
	// }
}
