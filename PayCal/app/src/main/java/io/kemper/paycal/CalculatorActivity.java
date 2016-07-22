package io.kemper.paycal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.kemper.paycal.app.PayCalApplication;

public class CalculatorActivity extends Activity {

    private static final String TAG = "CalculatorActivity";

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    /*
     * linear layoutParams
     */
    private final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    /*
     * layout references
     */
    private LinearLayout userListLayout;
    private LinearLayout resultsLayout;

    /*
     *
     */
    private List<UserUi> userUiList = new ArrayList<>();


    private class UserUi {
        private EditText name;
        private EditText expense;
        private Button removeBtn;
        private LinearLayout linearWrapper;

        private final LinearLayout.LayoutParams editTextNameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 8.0f);
        private final LinearLayout.LayoutParams editTextExpenseLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 4.0f);
        private final LinearLayout.LayoutParams removeBtnLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        //private final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        private View.OnClickListener onClickRemoveUser = new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                ViewParent userView = view.getParent();
                ViewGroup userListView = (ViewGroup) userView.getParent();
                userListView.removeView((View)userView);

                Iterator<UserUi> userUiIterator = userUiList.iterator();
                while(userUiIterator.hasNext()){
                    UserUi temp = userUiIterator.next();
                    if(temp.getLinearWrapper() == userView){
                        userUiIterator.remove();
                        break;
                    }
                }

            }
        };

        public UserUi(String nameText, String expenseText){
            //name
            name = new EditText(CalculatorActivity.this);
            name.setLayoutParams(editTextNameLayoutParams);
            name.setHint("name");
            if(!TextUtils.isEmpty(nameText)){
                name.setText(nameText);
            }

            //expense
            expense = new EditText(CalculatorActivity.this);
            expense.setLayoutParams(editTextExpenseLayoutParams);
            expense.setHint("0.00");
            expense.setGravity(Gravity.RIGHT);
            expense.setInputType(InputType.TYPE_CLASS_PHONE);
            if(!TextUtils.isEmpty(expenseText)){
                expense.setText(expenseText);
            }

            //removeBtn
            removeBtn = new Button(CalculatorActivity.this);
            removeBtn.setLayoutParams(removeBtnLayoutParams);
            removeBtn.setText("X");
            removeBtn.setOnClickListener(onClickRemoveUser);

            linearWrapper = new LinearLayout(CalculatorActivity.this);
            linearWrapper.setOrientation(LinearLayout.HORIZONTAL);
            linearWrapper.setLayoutParams(linearLayoutParams);

            linearWrapper.addView(name);
            linearWrapper.addView(expense);
            linearWrapper.addView(removeBtn);

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        userListLayout = (LinearLayout)findViewById(R.id.user_list_layout);

        if(savedInstanceState != null){
            //retrieve userUiList
            ArrayList<String> nameList = savedInstanceState.getStringArrayList("nameList");
            ArrayList<String> expenseList = savedInstanceState.getStringArrayList("expenseList");
            for(int i = 0; i < nameList.size(); i++){
                UserUi newUser = new UserUi(nameList.get(i), expenseList.get(i));
                userUiList.add(newUser);
                userListLayout.addView(newUser.getLinearWrapper());

            }

        }

        // Obtain the shared Tracker instance.
        PayCalApplication application = (PayCalApplication) getApplication();
        mTracker = application.getDefaultTracker();



        Button myBtn = (Button)findViewById(R.id.add_user_btn);

        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUi newUser = new UserUi(null, null);
                userUiList.add(newUser);

                userListLayout.addView(newUser.getLinearWrapper());



            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + "activity_calculator");
        mTracker.setScreenName("Image~" + "activity_calculator");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    //persist userUiList onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> expenseList = new ArrayList<>();
        for(UserUi userUi : userUiList){
            nameList.add(userUi.getName().getText().toString());
            expenseList.add(userUi.getExpense().getText().toString());
        }
        outState.putStringArrayList("nameList", nameList);
        outState.putStringArrayList("expenseList", expenseList);
    }

    public void onClickGoHome(View view) {
        Intent intent = new Intent(CalculatorActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickCalculate(View view) {

        int numUsers = userUiList.size();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Calculator")
                .setAction("Calculate")
                //.setLabel("")
                .setValue(numUsers)
                .build());


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

        generateResultsLayout(input, output);

    }

    private void generateResultsLayout(PaymentCalculatorInput input, PaymentCalculatorOutput output){
        resultsLayout = (LinearLayout) findViewById(R.id.results_layout);
        resultsLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        LinearLayout.LayoutParams noResultsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);


        for(Payment payment : output.getPayments()){

            //String for from
            String from = payment.getFrom().getName();
            if(TextUtils.isEmpty(payment.getFrom().getName())){
                //Empty from
                from = "User" + (input.getUsers().indexOf(payment.getFrom()) + 1); //User1 pays User2 (base 1)
            }

            //String for to
            String to = payment.getTo().getName();
            if(TextUtils.isEmpty(payment.getTo().getName())){
                //Empty to
                to = "User" + (input.getUsers().indexOf(payment.getTo()) + 1); //User1 pays User2 (base 1)
            }

            //String for amount
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String amountStr = formatter.format(payment.getAmount());

            //DecimalFormat df = new DecimalFormat("#.##");
            //String amountStr = df.format(payment.getAmount());

            //String text = String.format("%s   pays   %s   %s", from, to, amountStr);



            TextView fromView = new TextView(CalculatorActivity.this);
            fromView.setLayoutParams(layoutParams);
            fromView.setGravity(Gravity.LEFT);
            fromView.setText(from);

            TextView paysView = new TextView(CalculatorActivity.this);
            paysView.setLayoutParams(layoutParams);
            paysView.setGravity(Gravity.LEFT);
            paysView.setText("pays");

            TextView toView = new TextView(CalculatorActivity.this);
            toView.setLayoutParams(layoutParams);
            toView.setGravity(Gravity.LEFT);
            toView.setText(to);

            TextView amountView = new TextView(CalculatorActivity.this);
            amountView.setLayoutParams(layoutParams);
            amountView.setGravity(Gravity.RIGHT);
            amountView.setText(amountStr);


            LinearLayout linearWrapper = new LinearLayout(CalculatorActivity.this);
            linearWrapper.setOrientation(LinearLayout.HORIZONTAL);
            linearWrapper.setLayoutParams(linearLayoutParams);

            linearWrapper.addView(fromView);
            linearWrapper.addView(paysView);
            linearWrapper.addView(toView);
            linearWrapper.addView(amountView);

            resultsLayout.addView(linearWrapper);

        }

        if(output.getPayments().size() == 0){

            TextView noResultsView = new TextView(CalculatorActivity.this);
            noResultsView.setLayoutParams(noResultsLayoutParams);
            noResultsView.setGravity(Gravity.CENTER_HORIZONTAL);
            noResultsView.setText("No recommendation found.");

            resultsLayout.addView(noResultsView);


        }


    }

}
