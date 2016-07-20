package com.easyPayments.components;

import java.util.ArrayList;
import java.util.List;

import com.easyPayments.TransferObjects.Combo;
import com.easyPayments.TransferObjects.User;

public class PermutationComponent {

	public static List<Combo> findPermTwoLists(List<User> one, List<User> two){

		PermutationUtil<User> permUtil = new PermutationUtil<>();
		List<List<User>> onePerms = permUtil.generatePerm(one);
		List<List<User>> twoPerms = permUtil.generatePerm(two);
		List<Combo> comboList = new ArrayList<>();
		
		for(List<User> oneTemp : onePerms){
			for(List<User> twoTemp : twoPerms){
				comboList.add(new Combo(oneTemp, twoTemp));
			}
		}
		return comboList;
	}
}
