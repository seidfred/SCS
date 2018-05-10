package de.seidfred.accountservice.entity;

public enum PaymentStatus {

	EXECUTED("EXEC"), REJECTED("RJCT");

	private String value;

	PaymentStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
