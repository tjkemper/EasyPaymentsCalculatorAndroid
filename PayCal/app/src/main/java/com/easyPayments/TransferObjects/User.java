package com.easyPayments.TransferObjects;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String name;
	private List<Expense> myExpenses = new ArrayList<>();
	private double owedToExpenses;
	private double owedToRoommates;
	private boolean iOweMoney;
	
	private double amountIOwe;
	private double amountImOwed;
	

	public double getAmountIOwe() {
		return amountIOwe;
	}

	public void setAmountIOwe(double amountIOwe) {
		this.amountIOwe = amountIOwe;
	}

	public double getAmountImOwed() {
		return amountImOwed;
	}

	public void setAmountImOwed(double amountImOwed) {
		this.amountImOwed = amountImOwed;
	}

	public void setOwedToExpenses(double owedToExpenses) {
		this.owedToExpenses = owedToExpenses;
	}

	public void setOwedToRoommates(double owedToRoommates) {
		this.owedToRoommates = owedToRoommates;
	}

	public void setiOweMoney(boolean iOweMoney) {
		this.iOweMoney = iOweMoney;
	}

	public User(){}
	
	public User(String name) {
		super();
		this.name = name;
	}
	
	public User(String name, List<Expense> myExpenses) {
		super();
		this.name = name;
		this.myExpenses = myExpenses;
	}
	
	public void calculateOwedToExpenses(){
		for(Expense expense : myExpenses){
			owedToExpenses += expense.getAmount();
		}
	}

	//TODO: account for owedToRoommates == 0
	public void calculateOwedToRoommates(double totalIShouldPay) {
		this.owedToRoommates = totalIShouldPay - owedToExpenses;
		if(this.owedToRoommates > 0){
			this.iOweMoney = true;
		}else {
			this.iOweMoney = false;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isiOweMoney() {
		return iOweMoney;
	}

	public List<Expense> getMyExpenses() {
		return myExpenses;
	}

	public void setMyExpenses(List<Expense> myExpenses) {
		this.myExpenses = myExpenses;
	}

	public double getOwedToExpenses() {
		return owedToExpenses;
	}

	public double getOwedToRoommates() {
		return owedToRoommates;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", myExpenses=" + myExpenses + ", owedToExpenses=" + owedToExpenses
				+ ", owedToRoommates=" + owedToRoommates + ", iOweMoney=" + iOweMoney + "]";
	}

	
	
	
	
}
