package com.easyPayments.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.easyPayments.TransferObjects.Combo;
import com.easyPayments.TransferObjects.Payment;
import com.easyPayments.TransferObjects.Situation;
import com.easyPayments.TransferObjects.User;

public class CalculatePaymentComponent {

	public Situation CalculateBestSituation(List<User> users){
		setup(users);
		Situation bestSituation = findBestSituation(users);

		for(User user : users){
			System.out.println(user.getName() + "\t" + user.getOwedToRoommates() + "\t" + user.isiOweMoney());
		}
		System.out.println("\n");
		for(Payment temp : bestSituation.getPayments()){
			System.out.println(temp.getFrom().getName() + " Pays $" + temp.getAmount() +"  to\t" + temp.getTo().getName() + "\t");
		}
		return bestSituation;
	}

	public static void setup(List<User> roommates){
		/*
		 * Get total
		 */
		double total = 0;
		for(User roommate : roommates) {
			roommate.calculateOwedToExpenses();
			total += roommate.getOwedToExpenses();
		}
		
		System.out.println(total);
		
		/*
		 * Calculate what each person still owes
		 */
		double totalForEachRoommate = total / roommates.size();
		System.out.println("Total for each roommate: " + totalForEachRoommate);
		for(User roommate : roommates){
			roommate.calculateOwedToRoommates(totalForEachRoommate);
		}
	}
	
	public static void setAmountsOwedAndOwes(List<User> roommates){
		/*
		 * Set amountIOwe and amountImOwed
		 */
		for(User temp : roommates){
			if(temp.isiOweMoney()){
				temp.setAmountIOwe(temp.getOwedToRoommates());
			}else {
				temp.setAmountImOwed(temp.getOwedToRoommates() * -1);
			}
		}
	}
	
	public static Situation findBestSituation(List<User> roommates){
		
		List<Situation> situationList = new ArrayList<>();
		List<User> roommatesWhoOwe = new ArrayList<>();
		List<User> roommatesWhoAreOwed = new ArrayList<>();
		
		/* Split owed vs whoOwes */
		for(User temp : roommates){
			if(temp.isiOweMoney()){
				roommatesWhoOwe.add(temp);
			}else {
				if(temp.getOwedToRoommates() == 0){
					continue;
				}
				roommatesWhoAreOwed.add(temp);
			}
		}
		
		/*
		 * ASSERT: we have 2 lists
		 * 
		 * Need to get all permutations of those two lists
		 */
		List<Combo> allPerms = PermutationComponent.findPermTwoLists(roommatesWhoAreOwed, roommatesWhoOwe);
		
		for(Combo combo : allPerms){
			
			setAmountsOwedAndOwes(roommates); 
			Situation situation = new Situation();
			List<User> owedList = combo.getUserOne();
			List<User> whoOwesList = combo.getUserTwo();
			Iterator<User> owedIter = owedList.iterator();
			
			outer:
			while(owedIter.hasNext()){
				Iterator<User> whoOwesIter = whoOwesList.iterator();
				User owed = owedIter.next();
				double owedAmount = owed.getAmountImOwed();
				while(whoOwesIter.hasNext()){
					User whoOwes = whoOwesIter.next();
					double whoOwesAmount = whoOwes.getAmountIOwe();
					if(owedAmount < whoOwesAmount){
						whoOwes.setAmountIOwe(whoOwes.getAmountIOwe() - owedAmount);
						owed.setAmountImOwed(0);
						situation.getPayments().add(new Payment(whoOwes, owed, owedAmount));
						continue outer;
					}else if(owedAmount > whoOwesAmount){
						whoOwes.setAmountIOwe(0);
						owed.setAmountImOwed(owed.getAmountImOwed() - whoOwesAmount);
						situation.getPayments().add(new Payment(whoOwes, owed, whoOwesAmount));
						continue;
					}else {
						whoOwes.setAmountIOwe(0);
						owed.setAmountImOwed(0);
						situation.getPayments().add(new Payment(whoOwes, owed, owedAmount));
						continue outer;
					}
				}
			}
			situationList.add(situation);
		}

		Collections.sort(situationList);
		System.out.println("size: " + situationList.size());
		if(situationList.size() > 0){
			return situationList.get(0);
		}else {
			return null;
		}
	}
}