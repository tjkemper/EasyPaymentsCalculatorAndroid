package com.easyPayments.services;

import java.util.List;

import com.easyPayments.TransferObjects.Payment;
import com.easyPayments.TransferObjects.PaymentCalculatorInput;
import com.easyPayments.TransferObjects.Situation;
import com.easyPayments.components.CalculatePaymentComponent;

public class EasyCalculateService {

	CalculatePaymentComponent calcPayment = new CalculatePaymentComponent();
	public List<Payment> retrieveCalculatedPayments(PaymentCalculatorInput input){
		//TODO logic and calls for calculation logic
		Situation bestSituation = calcPayment.CalculateBestSituation(input.getUsers());
		return bestSituation.getPayments();
	}
}
