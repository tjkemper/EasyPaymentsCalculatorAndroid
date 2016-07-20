package com.easyPayments.TransferObjects;

import java.util.List;

public class PaymentCalculatorInput {

	List<User> users;
	
	public PaymentCalculatorInput() {
		// TODO Auto-generated constructor stub
	}

	public PaymentCalculatorInput(List<User> users) {
		super();
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
