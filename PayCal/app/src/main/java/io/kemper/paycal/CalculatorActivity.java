package io.kemper.paycal;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.easyPayments.TransferObjects.Expense;
import com.easyPayments.TransferObjects.Payment;
import com.easyPayments.TransferObjects.PaymentCalculatorInput;
import com.easyPayments.TransferObjects.PaymentCalculatorOutput;
import com.easyPayments.TransferObjects.User;
import com.easyPayments.services.EasyCalculateService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends Activity {

    private class UserUi {
        private EditText name;
        private EditText expense;
        private LinearLayout linearWrapper;

        private final LinearLayout.LayoutParams editTextNameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 4.0f);
        private final LinearLayout.LayoutParams editTextExpenseLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        private final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        public UserUi(){
            name = new EditText(CalculatorActivity.this);
            name.setLayoutParams(editTextNameLayoutParams);

            name.setHint("John...");


            expense = new EditText(CalculatorActivity.this);
            expense.setLayoutParams(editTextExpenseLayoutParams);
            expense.setHint("12.34");
            expense.setGravity(Gravity.RIGHT);
            expense.setInputType(InputType.TYPE_CLASS_PHONE);

            linearWrapper = new LinearLayout(CalculatorActivity.this);
            linearWrapper.setOrientation(LinearLayout.HORIZONTAL);
            linearWrapper.setLayoutParams(linearLayoutParams);

            linearWrapper.addView(name);
            linearWrapper.addView(expense);

        }

        public EditText getName() {
            return name;
        }

        public EditText getExpense() {
            return expense;
        }

        public LinearLayout getLinearWrapper() {
            return linearWrapper;
        }
    }


    private List<UserUi> userUiList = new ArrayList<>();
    private LinearLayout userListLayout;
    private LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        userListLayout = (LinearLayout)findViewById(R.id.user_list_layout);
        Button myBtn = (Button)findViewById(R.id.my_btn);

        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUi newUser = new UserUi();
                userUiList.add(newUser);

                userListLayout.addView(newUser.getLinearWrapper());



            }
        });

    }

    public void onClickCalculate(View view) {

        PaymentCalculatorInput input = new PaymentCalculatorInput();
        List<User> inputUserList = new ArrayList<>();
        input.setUsers(inputUserList);

        for(UserUi user : userUiList){

            String name = user.getName().getText().toString();
            String expenseStr = user.getExpense().getText().toString();

            double expenseValue = 0;
            try {
                expenseValue = Double.valueOf(expenseStr);
            }catch(NumberFormatException e){

            }

            User newUser = new User(name);
            List<Expense> newUserExpenseList = new ArrayList<>();
            newUserExpenseList.add(new Expense(expenseValue, ""));
            newUser.setMyExpenses(newUserExpenseList);

            inputUserList.add(newUser);

        }
        EasyCalculateService calcService = new EasyCalculateService();
        PaymentCalculatorOutput output = new PaymentCalculatorOutput(calcService.retrieveCalculatedPayments(input));

        generateResultsLayout(output);

    }

    private void generateResultsLayout(PaymentCalculatorOutput output){
        resultsLayout = (LinearLayout) findViewById(R.id.results_layout);
        resultsLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(Payment payment : output.getPayments()){
            DecimalFormat df = new DecimalFormat("#.##");
            String amountStr = df.format(payment.getAmount());
            String text = String.format("%s pays %s $%s", payment.getFrom().getName(), payment.getTo().getName(), amountStr);

            TextView paymentView = new TextView(CalculatorActivity.this);
            paymentView.setLayoutParams(layoutParams);
            paymentView.setGravity(Gravity.LEFT);
            paymentView.setText(text);

            resultsLayout.addView(paymentView);
        }


    }

}
