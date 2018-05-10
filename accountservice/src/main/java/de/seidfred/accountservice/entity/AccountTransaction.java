package de.seidfred.accountservice.entity;

import java.util.Date;

public class AccountTransaction {

	// cstmrAccInfStsRpt.accInf.accTx[].pmtInfSts
	private PaymentStatus paymentStatus;
	// cstmrAccInfStsRpt.accInf.accTx[].pmtAmt
	private Double amount;
	// cstmrAccInfStsRpt.accInf.accTx[].pmtTxDt
	private Date transactionDate;

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
}
