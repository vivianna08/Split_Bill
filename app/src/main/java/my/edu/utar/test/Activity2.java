package my.edu.utar.test;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;



import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Activity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Activity2";
    private TextView mDisplayDate;
    private Spinner spinner;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText amountEdt, peopleEdt;
    private TextView amtTV;
    private TextView amtTA;
    private LinearLayout ly;
    private Button showPercentagesBtn, calculateBtn;
    private String equalMethodContent;
    private String percentageMethodContent;
    private String amountMethodContent;
    private String displayResultContent;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);



        mDisplayDate = (TextView) findViewById(R.id.tvDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Activity2.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDataSet: mm/dd/yy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        // Find the Spinner by its ID
        spinner = findViewById(R.id.spnGroup);


        // Create an ArrayAdapter using the string array from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.method, R.layout.color_spinner
        );
        // Set the layout for dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set the OnItemSelectedListener to handle spinner item selection
        spinner.setOnItemSelectedListener(this);

        // Find views by their IDs
        amountEdt = findViewById(R.id.etvAmount);
        peopleEdt = findViewById(R.id.etvPeople);
        amtTV = findViewById(R.id.tvIdv);
        amtTA = findViewById(R.id.tvTotal);
        ly = findViewById(R.id.llIndividualPercentages);
        showPercentagesBtn = findViewById(R.id.btnShowPercentages);
        calculateBtn = findViewById(R.id.btnCalculate);


        // Adding click listener for "Show Percentages" button.
        showPercentagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMethod = spinner.getSelectedItem().toString();

                if (selectedMethod.equals("Equal")) {
                    // Display a message or perform an action to inform the user
                    Toast.makeText(Activity2.this, "Cannot show percentages for 'Equal' method. Directly click the calculate button.", Toast.LENGTH_SHORT).show();
                } else {
                    showPercentageInputs();
                }
            }
        });

        // Adding click listener for "Calculate" button.
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = mDisplayDate.getText().toString();

                if (selectedDate.isEmpty()) {
                    Toast.makeText(Activity2.this, "Please select a date before calculating.", Toast.LENGTH_SHORT).show();
                    return; // Stop the calculation if date is not selected
                }
                calculateResult();
            }
        });

        Button resetBtn = findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share_menu) {
            // Calculate the result based on the selected method and entered data
            calculateResult();


            String selectedMethod = spinner.getSelectedItem().toString();
            String shareMessage = "";

            // Determine the share message based on the selected spinner item
            if (selectedMethod.equals("Equal")) {
                shareMessage = equalMethodContent;
            } else if (selectedMethod.equals("Percentage")) {
                shareMessage = percentageMethodContent;
            } else if (selectedMethod.equals("Amount")) {
                shareMessage = amountMethodContent;
            }


            // Create a sharing intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } catch (ActivityNotFoundException e) {
                // No app is available to handle the intent, handle this case if needed
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Method to show the percentage input fields dynamically
    private void showPercentageInputs() {
        String peopleStr = peopleEdt.getText().toString();
        if (!peopleStr.isEmpty()) {
            int numPeople = Integer.parseInt(peopleStr);
            if (numPeople > 0) {
                // Clear the layout before adding individual percentage inputs
                ly.removeAllViews();

                // Create individual percentage inputs dynamically
                for (int i = 1; i <= numPeople; i++) {
                    EditText individualNameEdt = new EditText(Activity2.this);
                    EditText individualPercentageEdt = new EditText(Activity2.this);

                    individualNameEdt.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                    );
                    individualNameEdt.setHint("User " + i + " Name");
                    individualNameEdt.setInputType(InputType.TYPE_CLASS_TEXT );
                    individualNameEdt.setTextColor(ContextCompat.getColor(Activity2.this, R.color.grey));
                    ly.addView(individualNameEdt);

                    individualPercentageEdt.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                    );
                    individualPercentageEdt.setHint("User " + i + " Percentage (%)");
                    individualPercentageEdt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    individualPercentageEdt.setTextColor(ContextCompat.getColor(Activity2.this, R.color.grey));
                    ly.addView(individualPercentageEdt);
                }
            } else {
                Toast.makeText(Activity2.this, "Number of people must be greater than 0.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Activity2.this, "Please enter the number of people.", Toast.LENGTH_SHORT).show();
        }
    }

     //Method to calculate the result based on the selected method and entered data
    private void calculateResult() {
        String selectedMethod = spinner.getSelectedItem().toString();

        if (selectedMethod.equals("Equal")) {
            try {
                String amountStr = amountEdt.getText().toString();
                String peopleStr = peopleEdt.getText().toString();

                if (!amountStr.isEmpty() && !peopleStr.isEmpty()  ) {
                    double amount = Double.parseDouble(amountStr);
                    int numPeople = Integer.parseInt(peopleStr);

                    if (numPeople > 0) {
                        // Calculate individual amount and display it.
                        double individualAmt = amount / numPeople;
                        StringBuilder equalMethodResult = new StringBuilder();

                        equalMethodResult.append("====Split the Bill=====" )
                                .append("\n")
                                .append("Total Amount: RM " )
                                .append(String.format("%.2f", amount))
                                .append("\n")
                                .append("Individual Amount(per): RM " )
                                .append(String.format("%.2f", individualAmt))
                                .append("\n");

                        amtTV.setText("Individual Amount: RM " + String.format("%.2f", individualAmt));
                        amtTA.setText("Total Amount: RM " + String.format("%.2f", amount));

                        equalMethodContent= equalMethodResult.toString();

                    } else {
                        Toast.makeText(Activity2.this, "Number of people must be greater than 0.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity2.this, "Please selected a date and enter valid values for Amount and Number of People ", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(Activity2.this, "Invalid input. Please enter valid numeric values.", Toast.LENGTH_SHORT).show();
            }
    } else if (selectedMethod.equals("Percentage")) {
            try {
                String amountStr = amountEdt.getText().toString();
                String peopleStr = peopleEdt.getText().toString();

                if (!amountStr.isEmpty() && !peopleStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    int numPeople = Integer.parseInt(peopleStr);

                    if (numPeople > 0) {
                        // Calculate individual amounts based on percentages and display them.
                        double totalPercentage = 0.0;
                        StringBuilder percentageMethodResult = new StringBuilder();
                        StringBuilder displayResult = new StringBuilder();


                        for (int i = 0; i < numPeople; i++) {
                            EditText individualPercentageEdt = (EditText) ly.getChildAt(i*2 +1);
                            String percentageStr = individualPercentageEdt.getText().toString();

                            if (!percentageStr.isEmpty()) {
                                double percentage = Double.parseDouble(percentageStr);
                                totalPercentage += percentage;
                            }
                        }

                        if (totalPercentage != 100.0) {
                            Toast.makeText(Activity2.this, "Total percentage must be 100%.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        percentageMethodResult.append("====Split the Bill ====\n");
                        displayResult.append("=== Display Result ===\n");

                        // Calculate individual amounts based on percentages and display them.
                        for (int i = 0; i < numPeople; i++) {
                            EditText individualPercentageEdt = (EditText) ly.getChildAt(i*2+1);
                            String percentageStr = individualPercentageEdt.getText().toString();

                            EditText individualNameEdt = (EditText) ly.getChildAt(i*2);
                            String name = individualNameEdt.getText().toString();


                            if (!percentageStr.isEmpty()) {
                                double percentage = Double.parseDouble(percentageStr);
                                double individualAmount = (amount * percentage) / 100.0;

                                percentageMethodResult.append(name)
                                        .append(": RM ")
                                        .append(String.format("%.2f", individualAmount))
                                        .append("\n");

                                displayResult.append(name)
                                        .append(": ")
                                        .append(String.format("%.2f", individualAmount))
                                        .append("\n");


                                // Create TextViews to display individual amounts
                                TextView amtPercentTV = new TextView(Activity2.this);
                                amtPercentTV.setLayoutParams(
                                        new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                );

//                                amtPercentTV.setText(name + ": RM " + String.format("%.2f", individualAmount));
//                                amtPercentTV.setTextColor(ContextCompat.getColor(Activity2.this, R.color.yellow));
//                                ly.addView(amtPercentTV);

                            }
                        }

                        percentageMethodContent = percentageMethodResult.toString();
                        displayResultContent= displayResult.toString();

                        amtTV.setVisibility(View.GONE);
                        amtTA.setVisibility(View.GONE);

                        TextView displayResultTV = new TextView(Activity2.this);
                        displayResultTV.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                        );

                        displayResultTV.setText(displayResultContent);
                        displayResultTV.setTextColor(ContextCompat.getColor(Activity2.this, R.color.grey)); // Adjust color as needed
                        ly.addView(displayResultTV);



                    } else {
                        Toast.makeText(Activity2.this, "Number of people must be greater than 0.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity2.this, "Please enter valid values for Amount and Number of People.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Log.e("PercentageCalculation", "Error parsing numeric input: " + e.getMessage());
                Toast.makeText(Activity2.this, "Invalid input. Please enter valid numeric values.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (selectedMethod.equals("Amount")) {
            try {
                String amountStr = amountEdt.getText().toString();
                String peopleStr = peopleEdt.getText().toString();

                if (!amountStr.isEmpty() && !peopleStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    int numPeople = Integer.parseInt(peopleStr);

                    if (numPeople > 0) {
                        // Calculate individual amounts based on percentages and display them.
                        double totalAmount = 0.0;
                        StringBuilder amountMethodResult = new StringBuilder();
                        StringBuilder displayResult2 = new StringBuilder();



                        for (int i = 0; i < numPeople; i++) {
                            EditText individualAmountEdt = (EditText) ly.getChildAt(i*2+1);
                            String AmountStr = individualAmountEdt.getText().toString();

                            if (!AmountStr.isEmpty()) {
                                double percentage = Double.parseDouble(AmountStr);
                                totalAmount += percentage;
                            }
                        }

                        if (totalAmount != amount) {
                            Toast.makeText(Activity2.this, "Total amount must be equal to input amount.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        amountMethodResult.append("====Split the Bill ====\n");
                        displayResult2.append("=== Display Result ===\n");

                        // Calculate individual amounts based on percentages and display them.
                        for (int i = 0; i < numPeople; i++) {
                            EditText individualAmountEdt = (EditText) ly.getChildAt(i*2+1);
                            String AmountStr = individualAmountEdt.getText().toString();

                            EditText individualNameEd = (EditText) ly.getChildAt(i*2);
                            String nameI = individualNameEd.getText().toString();


                            if (!AmountStr.isEmpty()) {
                                double percentage = Double.parseDouble(AmountStr);
                                double individualAmount = percentage;

                                amountMethodResult
                                        .append(nameI)
                                        .append(": RM ")
                                        .append(String.format("%.2f", individualAmount))
                                        .append("\n");

                                displayResult2.append(nameI)
                                        .append(": ")
                                        .append(String.format("%.2f", individualAmount))
                                        .append("\n");




                                // Create TextViews to display individual amounts
                                TextView amtPercentTV = new TextView(Activity2.this);
                                amtPercentTV.setLayoutParams(
                                        new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                );

//                                amtPercentTV.setText(nameI + ": RM " + String.format("%.2f", individualAmount));
//                                amtPercentTV.setTextColor(ContextCompat.getColor(Activity2.this, R.color.yellow));
//                                ly.addView(amtPercentTV);

                            }
                        }

                        amountMethodContent = amountMethodResult.toString();
                        displayResultContent= displayResult2.toString();

                        amtTV.setVisibility(View.GONE);
                        amtTA.setVisibility(View.GONE);

                        TextView displayResultTV = new TextView(Activity2.this);
                        displayResultTV.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                        );
                        displayResultTV.setText(displayResultContent);
                        displayResultTV.setTextColor(ContextCompat.getColor(Activity2.this, R.color.grey)); // Adjust color as needed
                        ly.addView(displayResultTV);




                    } else {
                        Toast.makeText(Activity2.this, "Number of people must be greater than 0.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity2.this, "Please enter valid values for Amount and Number of People.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(Activity2.this, "Invalid input.The amount not equal to the total amount.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetFields() {
        // Clear input fields
        mDisplayDate.setText("");  // Reset selected date
        amountEdt.setText("");
        peopleEdt.setText("");

        // Clear result displays
        amtTV.setText("");
        amtTA.setText("");

        // Clear dynamically added views
        ly.removeAllViews();

        // Show the "Total Amount" and "Individual Amount" TextViews
        amtTV.setVisibility(View.VISIBLE);
        amtTA.setVisibility(View.VISIBLE);
    }

    // Spinner item selection callback
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
