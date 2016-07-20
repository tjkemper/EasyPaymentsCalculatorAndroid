package com.easyPayments.TransferObjects;

import java.util.ArrayList;
import java.util.List;

public class Situation implements Comparable<Situation> {
	
	private List<Payment> payments = new ArrayList<>();
	
	public Situation(){}

	public Situation(List<Payment> payments) {
		super();
		this.payments = payments;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "Situation [payments=" + payments + "]";
	}

	@Override
	public int compareTo(Situation o) {
		return this.payments.size() - o.payments.size();
	}
	
	
	
}
