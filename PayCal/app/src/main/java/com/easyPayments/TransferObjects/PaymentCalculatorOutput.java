package com.easyPayments.TransferObjects;

import java.util.List;

public class PaymentCalculatorOutput {

	List<Payment> payments;
	
	public PaymentCalculatorOutput() {
		// TODO Auto-generated constructor stub
	}

	public PaymentCalculatorOutput(List<Payment> payments) {
		super();
		this.payments = payments;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
}
