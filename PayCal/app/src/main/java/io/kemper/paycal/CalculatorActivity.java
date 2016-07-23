package io.kemper.paycal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
    private TableLayout resultsLayout;

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
            expense.setInputType(InputType.TYPE_CLASS_NUMBER);
            if(!TextUtils.isEmpty(expenseText)){
                expense.setText(expenseText);
            }

            expense.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    expense.setSelection(expense.getText().length());
                    return false;
                }
            });

            expense.addTextChangedListener(new TextWatcher() {

                boolean ignoreChange = false;

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (!ignoreChange) {
                        String cleanString = s.toString().replaceAll("[$,.]", "");

                        ignoreChange = true;

                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));
                        expense.setText(formatted);
                        expense.setSelection(expense.getText().length());


                        ignoreChange = false;
                    }
                }
            });

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

        // Obtain the shared Tracker instance.
        PayCalApplication application = (PayCalApplication) getApplication();
        mTracker = application.getDefaultTracker();

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

        int numUserDefault = 3;
        if(userUiList.size() == 0){
            for(int i = 0; i < numUserDefault; i++){
                UserUi newUser = new UserUi(null, null);
                userUiList.add(newUser);
                userListLayout.addView(newUser.getLinearWrapper());
            }
        }


        Button addUserBtn = (Button)findViewById(R.id.add_user_btn);

        addUserBtn.setOnClickListener(new View.OnClickListener() {
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

        //Hide soft keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Google Analytics
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
                String cleanString = expenseStr.toString().replaceAll("[$,.]", "");
                expenseValue = Double.parseDouble(cleanString)/100;
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
        resultsLayout = (TableLayout) findViewById(R.id.results_layout);
        resultsLayout.removeAllViews();

        //LayoutParams for each table row
        TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 1.0f);

        //LayoutParams for each table row child
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        //add recommendation text
        TableRow recommendationRow = new TableRow(CalculatorActivity.this);
        recommendationRow.setLayoutParams(tableRowLayoutParams);

        EditText recommendationView = new EditText(CalculatorActivity.this);
        layoutParams.span = 4;
        layoutParams.topMargin = 20;
        recommendationView.setLayoutParams(layoutParams);
        recommendationView.setGravity(Gravity.CENTER_HORIZONTAL);
        recommendationView.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        recommendationView.setTextColor(Color.parseColor("#222222"));
        recommendationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        recommendationView.setText("Recommendation");

        recommendationRow.addView(recommendationView);
        resultsLayout.addView(recommendationRow);


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
            String amountStr = NumberFormat.getCurrencyInstance().format(payment.getAmount());


            TextView fromView = new TextView(CalculatorActivity.this);
            fromView.setLayoutParams(layoutParams);
            fromView.setGravity(Gravity.CENTER_HORIZONTAL);
            fromView.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            fromView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            fromView.setText(from);


            TextView paysView = new TextView(CalculatorActivity.this);
            paysView.setLayoutParams(layoutParams);
            paysView.setGravity(Gravity.CENTER_HORIZONTAL);
            paysView.setTypeface(Typeface.create("sans-serif-light", Typeface.ITALIC));
            paysView.setText("pays");

            TextView toView = new TextView(CalculatorActivity.this);
            toView.setLayoutParams(layoutParams);
            toView.setGravity(Gravity.CENTER_HORIZONTAL);
            toView.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            toView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            toView.setText(to);

            TextView amountView = new TextView(CalculatorActivity.this);
            amountView.setLayoutParams(layoutParams);
            amountView.setGravity(Gravity.CENTER_HORIZONTAL);
            amountView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            amountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            amountView.setText(amountStr);

            //Add new TableRow to TableLayout
            TableRow newRow = new TableRow(CalculatorActivity.this);
            newRow.setLayoutParams(tableRowLayoutParams);
            newRow.addView(fromView);
            newRow.addView(paysView);
            newRow.addView(toView);
            newRow.addView(amountView);
            resultsLayout.addView(newRow);

        }

        if(output.getPayments().size() == 0){
            TableRow newRow = new TableRow(CalculatorActivity.this);
            newRow.setLayoutParams(tableRowLayoutParams);

            TextView noResultsView = new TextView(CalculatorActivity.this);
            noResultsView.setLayoutParams(layoutParams);
            noResultsView.setGravity(Gravity.CENTER_HORIZONTAL);
            noResultsView.setText("No recommendation found.");

            newRow.addView(noResultsView);

            resultsLayout.addView(newRow);


        }
    }



}
