package com.example.kaskine.tipcalculator;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String sUserInput;
    private int iSeekBarValue;
    private boolean bFieldsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();

        final EditText inputField = (EditText)findViewById(R.id.inputField);
        final TextView percentText = (TextView)findViewById(R.id.percentDisplay);
        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final Button clearButton = (Button)findViewById(R.id.clearButton);
        final TextView outputField = (TextView)findViewById(R.id.outputField);

        if(savedInstanceState != null) {
            sUserInput = savedInstanceState.getString(res.getString(R.string.sUserInput_StoreID));
            iSeekBarValue = savedInstanceState.getInt(res.getString(R.string.iSeekBarValue_StoreID));
            bFieldsSet = savedInstanceState.getBoolean(res.getString(R.string.bFieldsSet_StoreID));

            String sPercent = iSeekBarValue + res.getString(R.string.percent_symbol);

            inputField.setTextKeepState(sUserInput);
            percentText.setText(sPercent);
            seekBar.setProgress(iSeekBarValue);

            if(bFieldsSet)
                setOutputFieldValues(outputField);

        }
        else
            setDefaultValues(inputField, seekBar, percentText, outputField);


        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String sInput = inputField.getText().toString();

                if (!isValidEntryInput(sInput)) {
                    String sOutput = reformatEntryInput(sInput);
                    inputField.setTextKeepState(sOutput);
                    sUserInput = sOutput;
                }
                else
                    sUserInput = sInput;

                setOutputFieldValues(outputField);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                iSeekBarValue = seekBar.getProgress();

                String sOutput = iSeekBarValue + getResources().getString(R.string.percent_symbol);
                percentText.setText(sOutput);

                if(sUserInput.isEmpty()) {
//                    makeErrorToast();
                    inputField.requestFocus();
                }
                else
                    setOutputFieldValues(outputField);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultValues(inputField, seekBar, percentText, outputField);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Resources res = getResources();

        outState.putString(res.getString(R.string.sUserInput_StoreID), sUserInput);
        outState.putInt(res.getString(R.string.iSeekBarValue_StoreID), iSeekBarValue);
        outState.putBoolean(res.getString(R.string.bFieldsSet_StoreID), bFieldsSet);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Resources res = getResources();

        sUserInput = savedInstanceState.getString(res.getString(R.string.sUserInput_StoreID));
        iSeekBarValue = savedInstanceState.getInt(res.getString(R.string.iSeekBarValue_StoreID));
        bFieldsSet = savedInstanceState.getBoolean(res.getString(R.string.bFieldsSet_StoreID));
    }

    /**
     * Sets the defualt values for all of the fields. Called during initialization or when the clear button is clicked.
     * @param inputField - The user input field
     * @param seekBar - The percentage selection SeekBar
     * @param percentText - The label which displays the SeekBar percentage readout.
     * @param outputField - The field in which all output is displayed.
     */
    public void setDefaultValues(EditText inputField, SeekBar seekBar, TextView percentText, TextView outputField) {
        Resources res = getResources();

        sUserInput = new String();
        iSeekBarValue = 15;
        bFieldsSet = false;

        inputField.setText(sUserInput);
        seekBar.setProgress(iSeekBarValue);

        String sPercent = seekBar.getProgress() + res.getString(R.string.percent_symbol);
        percentText.setText(sPercent);

        outputField.setText(new String());
    }

    /**
     * Determines if the input String is valid for use during entry input.
     * Considerably more relaxed than the final input String pattern due to input process being incomplete.
     * @param sInput - The String to be tested for validity.
     * @return True if the String is valid. See @string/regex_entry for pattern.
     */
    public boolean isValidEntryInput(String sInput) {
        if(sInput.isEmpty())
            return true;

        Pattern patternRegexEntry = Pattern.compile(getResources().getString(R.string.regex_entry));
        Matcher matcherRegexEntry = patternRegexEntry.matcher(sInput);

        return matcherRegexEntry.matches();
    }

    /**
     * Determines if the input String is valid for use in the final output calculations.
     * @param sInput - The String to be tested for validity.
     * @return True if the String is a valid format. See @string/regex_final for pattern.
     */
    public boolean isValidFinalInput(String sInput) {
        Pattern patternRegexFinal = Pattern.compile(getResources().getString(R.string.regex_final));
        Matcher matcherRegexFinal = patternRegexFinal.matcher(sInput);

        return matcherRegexFinal.matches();
    }

    /**
     * Reformats a user input String while the input String is still being modified.
     * Truncates the last value from the String, prevents users from inputting a String with more than two decimal places.
     * @param sInput - The String of user input to truncate
     * @return The truncated String
     */
    public String reformatEntryInput(String sInput) {
        return sInput.substring(0, sInput.length() - 1);
    }

    /**
     * Reformats a user input String to prepare it for the output calculations
     * @param sInput - The user input String to reformat
     * @return A reformatted String (Returns a default value if the function was called with no input given)
     */
    public String reformatFinalInput(String sInput) {
        DecimalFormat df = new DecimalFormat(getResources().getString(R.string.decimal_format));

        return sInput.isEmpty() || sInput.equals(getResources().getString(R.string.decimal_symbol)) ?
                String.valueOf(Double.parseDouble(getResources().getString(R.string.bill_start))) :
                df.format(Double.parseDouble(sInput));
    }

    /**
     * Calculates the tip amount and sets the output labels.
     * @param outputField - The TextView to contain the function output.
     */
    public void setOutputFieldValues(TextView outputField) {
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance();
        Resources res = getResources();

        Double dPercent = (double)iSeekBarValue / 100;
        String sBillAmountInput = sUserInput;

        if(!isValidFinalInput(sBillAmountInput))
            sBillAmountInput = reformatFinalInput(sBillAmountInput);

        Double dBillAmount = Double.parseDouble(sBillAmountInput);
        Double dTipAmount = dBillAmount * dPercent;
        Double dTotalAmount = dBillAmount + dTipAmount;

        String sOutputBill = nf.format(dBillAmount);
        String sOutputTip = nf.format(dTipAmount);
        String sOutputTotal = nf.format(dTotalAmount);

        outputField.setText(
                res.getString(R.string.output_bill_prefix) + sOutputBill + res.getString(R.string.output_bill_suffix) +
                res.getString(R.string.output_tip_prefix) + sOutputTip + res.getString(R.string.output_tip_suffix) +
                res.getString(R.string.output_total_prefix) + sOutputTotal);
    }

    /**
     * Creates an error message on the screen when the user tries to enter a percentage without a bill amount.
     */
    public void makeErrorToast() {
        Toast.makeText(this, getResources().getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
    }
}
