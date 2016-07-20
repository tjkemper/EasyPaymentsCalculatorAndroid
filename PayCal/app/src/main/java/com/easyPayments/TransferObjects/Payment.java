package com.easyPayments.TransferObjects;

public class Payment {

	User from;
	User to;
	double amount;

	Payment() {}

	public Payment(User from, User to, double amount) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	};
	
}
