package com.valevich.moneytracker.ui.activities;


import android.support.v4.app.LoaderManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.ui.taskshandlers.AddExpenseTask;
import com.valevich.moneytracker.utils.DateFormatter;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@EActivity
public class NewExpenseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<CategoryEntry>>,
        Transaction.Success,
        Transaction.Error {

    private static final int CATEGORIES_LOADER = 2;
    private static final String TAG = NewExpenseActivity.class.getSimpleName();

    @NonConfigurationInstance
    @Bean
    AddExpenseTask mAddExpenseTask;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @ViewById(R.id.amountLabel)
    AppCompatEditText mAmountEditText;

    @ViewById(R.id.descriptionLabel)
    AppCompatEditText mDescriptionEditText;

    @ViewById(R.id.categories_picker)
    AppCompatSpinner mCategoriesPicker;

    @ViewById(R.id.date_picker)
    EditText mDatePicker;

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

    @StringRes(R.string.new_expense_error_saving_message)
    String mSaveErrorMessage;

    @StringRes(R.string.new_expense_empty_fields_warning)
    String mEmptyFieldsWarning;

    @ColorRes(R.color.colorPrimary)
    int mDatePickerColor;

    private String mAmount;
    private String mDescription;
    private String mDate;
    private CategoryEntry mCategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    @AfterViews
    void setupViews() {
        setupActionBar();
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

    private void setupCategoriesPicker(List<CategoryEntry> data) {
        ArrayAdapter<CategoryEntry> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_foreground, data);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        mCategoriesPicker.setAdapter(adapter);
        mCategoriesPicker.setPrompt(mCategoriesPickerTitle);
        mCategoriesPicker.setSelection(0);
    }

    private void setupDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date date = new Date();
        mDatePicker.setText(sdf.format(date));
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
            int month = selectedMonth + 1;
            String stringMonth = String.valueOf(month);
            if(month < 10) stringMonth = "0" + stringMonth;

            mDatePicker.setText(String.format(Locale.getDefault(),
                    "%d-%s-%d",
                    selectedDay,
                    stringMonth,
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

    private void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    @Click(R.id.saveExpenseButton) //if fields are empty show warning
    void setupSaveExenseButton() {

        mCategory = (CategoryEntry) mCategoriesPicker.getSelectedItem();
        mAmount = mAmountEditText.getText().toString();
        mDescription = mDescriptionEditText.getText().toString();
        mDate = DateFormatter.formatDateForDb(mDatePicker.getText().toString());

        if(mCategory != null && mAmount.trim().length() != 0 && mDescription.trim().length() != 0) {
            saveExpense();
        } else {
            showSnackBar(mEmptyFieldsWarning);
        }
    }

    @Click(R.id.cancelButton)
    void setupCancelButton() {
        dropFields();
        showSnackBar(mCancelMessage);
    }

    private void loadCategories() {
        getSupportLoaderManager().restartLoader(CATEGORIES_LOADER,null,this);
    }

    @Override
    public Loader<List<CategoryEntry>> onCreateLoader(int id, Bundle args) {
        final AsyncTaskLoader<List<CategoryEntry>> loader = new AsyncTaskLoader<List<CategoryEntry>>(this) {
            @Override
            public List<CategoryEntry> loadInBackground() {
                return CategoryEntry.getAllCategories("");
            }
        };
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<CategoryEntry>> loader, List<CategoryEntry> data) {
        setupCategoriesPicker(data);
    }

    @Override
    public void onLoaderReset(Loader<List<CategoryEntry>> loader) {

    }

    private void saveExpense() {
        ExpenseEntry.saveExpense(mDescription, mAmount, mDate, mCategory, this, this);
    }

    @Override
    public void onSuccess(Transaction transaction) {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            mAddExpenseTask.addExpense(Double.valueOf(mAmount),
                    mDescription,
                    (int) mCategory.getId(),
                    mDate);
        }
        showToast(mSaveMessage);
        finish();
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        showSnackBar(mSaveErrorMessage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_fade_in,R.anim.exit_push_out);
    }
}
