package com.valevich.moneytracker.ui.activities;


import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.valevich.moneytracker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@EActivity
public class NewExpenseActivity extends AppCompatActivity {

    private String[] mExampleCategories = {
            "Одежда",
            "Еда",
            "Дом",
            "Налоги",
            "Бизнес"
    };

    @ViewById(R.id.amountLabel)
    AppCompatEditText mAmountEditText;

    @ViewById(R.id.descriptionLabel)
    AppCompatEditText mDescriptionEditText;

    @ViewById(R.id.categories_picker)
    AppCompatSpinner mCategoriesPicker;

    @ViewById(R.id.date_picker)
    TextView mDatePicker;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.saveExpenseButton)
    TextView mSaveButton;

    @ViewById(R.id.cancelButton)
    TextView mCancelButton;

    @ViewById(R.id.root)
    RelativeLayout mRootLayout;

    @StringRes(R.string.new_expense_activity_title)
    String mActivityTitle;

    @StringRes(R.string.spinner_categories_title)
    String mCategoriesPickerTitle;

    @StringRes(R.string.categories_picker_positive)
    String mCategoriesPickerPositive;

    @StringRes(R.string.categories_picker_negative)
    String mCategoriesPickerNegative;

    @StringRes(R.string.new_expense_cancel_warning)
    String mCancelMessage;

    @StringRes(R.string.new_expense_save_message)
    String mSaveMessage;

    @StringRes(R.string.new_expense_empty_fields_warning)
    String mEmptyFieldsWarning;

    @ColorRes(R.color.colorPrimary)
    int mDatePickerColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
    }

    @AfterViews
    void setupViews() {
        setupActionBar();
        setupCategoriesPicker();
        setupDatePicker();
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mActivityTitle);
        }
    }

    private void setupCategoriesPicker() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_foreground, mExampleCategories);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        mCategoriesPicker.setAdapter(adapter);
        mCategoriesPicker.setPrompt(mCategoriesPickerTitle);
        mCategoriesPicker.setSelection(0);
        mCategoriesPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getBaseContext(), mExampleCategories[position], Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setupDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy",Locale.getDefault());
        mDatePicker.setText(sdf.format(new Date()));
    }

    @Click(R.id.date_picker)
    void pickDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = DatePickerDialog.newInstance(mDatePickerListener,year,month,day);
        dialog.setAccentColor(mDatePickerColor);
        dialog.show(getFragmentManager(),"DatePicker");

    }

    private DatePickerDialog.OnDateSetListener mDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePickerDialog view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            mDatePicker.setText(String.format(Locale.getDefault(),
                    "%d/%d/%d",
                    selectedDay,
                    selectedMonth+1,
                    selectedYear));
        }
    };

    private void dropFields() {
        mAmountEditText.setText("");
        mDescriptionEditText.setText("");
    }

    private void showSnackBar(String text) {
        Snackbar.make(mRootLayout, text, Snackbar.LENGTH_SHORT).show();
    }

    @Click(R.id.saveExpenseButton) //if fields are empty show warning
    void setupSaveExenseButton() {
        String amountText = mAmountEditText
                .getText()
                .toString()
                .trim();
        String descriptionText = mDescriptionEditText
                .getText()
                .toString()
                .trim();
        if(amountText.length() != 0 && descriptionText.length() != 0) {
            showSnackBar(mSaveMessage);
        } else {
            showSnackBar(mEmptyFieldsWarning);
        }
    }

    @Click(R.id.cancelButton)
    void setupCancelButton() {
        dropFields();
        showSnackBar(mCancelMessage);
    }
}
