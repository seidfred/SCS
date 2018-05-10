package de.seidfred.accountservice.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.seidfred.accountservice.util.Deserializer;
import de.seidfred.accountservice.util.JsonPath;

@JsonDeserialize(using = Deserializer.class)
public class AccountRequestBody {
	// cstmrCdtTrfInitn.grpHdr.msgId
	@JsonPath(path = "cstmrCdtTrfInitn.grpHdr.msgId")
	private String requestId;
	// cstmrCdtTrfInitn.grpHdr.creDtTm
	@JsonPath(path = "cstmrCdtTrfInitn.grpHdr.creDtTm")
	private Date requestCreationDate;
	// cstmrCdtTrfInitn.accInf.id.iban
	@JsonPath(path = "cstmrCdtTrfInitn.accInf.id.iban")
	private String accountIban;

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

}
