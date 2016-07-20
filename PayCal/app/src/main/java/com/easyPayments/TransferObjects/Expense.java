package com.easyPayments.TransferObjects;

public class Expense {

	double amount;
	String name;
	
	Expense() {}

	public Expense(double amount, String name) {
		super();
		this.amount = amount;
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	};
	
}
