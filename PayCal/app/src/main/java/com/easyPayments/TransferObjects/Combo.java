package com.easyPayments.TransferObjects;

import java.util.List;

public class Combo {
	private List<User> userOne;
	private List<User> userTwo;
	
	public Combo(){}

	public Combo(List<User> userOne, List<User> userTwo) {
		super();
		this.userOne = userOne;
		this.userTwo = userTwo;
	}

	public List<User> getUserOne() {
		return userOne;
	}

	public void setUserOne(List<User> userOne) {
		this.userOne = userOne;
	}

	public List<User> getUserTwo() {
		return userTwo;
	}

	public void setUserTwo(List<User> userTwo) {
		this.userTwo = userTwo;
	}

	@Override
	public String toString() {
		return "Combo [one=" + userOne + ", two=" + userTwo + "]";
	}
	
	
	
}
