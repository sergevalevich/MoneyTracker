package com.valevich.moneytracker.ui.activities;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.taskshandlers.AddExpenseTask;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.InputFieldValidator;
import com.valevich.moneytracker.utils.formatters.DateFormatter;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@EActivity(R.layout.activity_new_expense)
public class NewExpenseActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<CategoryEntry>>,
        Transaction.Success,
        Transaction.Error {

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

    @StringRes(R.string.empty_categories_picker)
    String mEmptyCategoriesText;

    @StringRes(R.string.add_categories_prompt)
    String mAddCategoriesPrompt;

    @StringRes(R.string.add_category_positive)
    String mAddCategoryActionText;

    @ColorRes(R.color.colorAccentDatePicker)
    int mDatePickerColor;

    @NonConfigurationInstance
    @Bean
    AddExpenseTask mAddExpenseTask;

    @Bean
    InputFieldValidator mInputFieldValidator;

    @Bean
    DateFormatter mDateFormatter;

    @Bean
    PriceFormatter mPriceFormatter;

    @Bean
    OttoBus mEventBus;

    private String mAmount;
    private String mDescription;
    private String mDate;
    private CategoryEntry mCategory;

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
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

    //not allowing to enter more than 2 digits after dot
    @TextChange(R.id.amountLabel)
    void setUpAmountEditText(CharSequence charSequence) {
        String text = charSequence.toString();
        int dotIndex = text.indexOf(PriceFormatter.SEPARATOR);
        if (dotIndex != -1) {
            if (text.substring(dotIndex + 1).length() > 2) {
                mAmountEditText.setText(text.substring(0, text.length() - 1));
            }
        }
    }

    @Click(R.id.date_picker)
    void pickDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = DatePickerDialog.newInstance(mDatePickerListener, year, month, day);
        dialog.setAccentColor(mDatePickerColor);
        dialog.setThemeDark(true);
        dialog.show(getFragmentManager(), ConstantsManager.DATE_PICKER_TAG);

    }

    @Click(R.id.saveExpenseButton)
    void setupSaveExpenseButton() {
        Object selectedItem = mCategoriesPicker.getSelectedItem();
        if (selectedItem instanceof String) {
            notifyUserWithSnackBar(mAddCategoriesPrompt);
        } else {
            mCategory = (CategoryEntry) selectedItem;
            mAmount = mAmountEditText.getText().toString();
            mDescription = mDescriptionEditText.getText().toString();
            mDate = mDateFormatter.formatDateForDb(mDatePicker.getText().toString());

            if (mCategory != null
                    && mInputFieldValidator.isAmountValid(mAmount)
                    && mInputFieldValidator.isDescriptionValid(mDescription)) {
                saveExpense();
            } else {
                notifyUserWithSnackBar(mEmptyFieldsWarning);
            }
        }
    }

    @Click(R.id.cancelButton)
    void setupCancelButton() {
        dropFields();
        notifyUserWithSnackBar(mCancelMessage);
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

    @Override
    public void onSuccess(Transaction transaction) {
        int serverId = mCategory.getServerId();
        if (serverId != 0)
            mAddExpenseTask.addExpense(Double.valueOf(mAmount),
                    mDescription,
                    serverId,
                    mDate);
        notifyUserWithToast(mSaveMessage);
        finish();
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        notifyUserWithToast(mSaveErrorMessage);
    }

    @Subscribe
    public void onNetworkError(NetworkErrorEvent event) {
        notifyUserWithToast(event.getMessage());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_push_out);
    }

    private void saveExpense() {
        List<ExpenseEntry> expensesToAdd = new ArrayList<>(1);
        ExpenseEntry expense = new ExpenseEntry();
        expense.setDate(mDate);
        expense.setDescription(mDescription);
        expense.setPrice(mPriceFormatter.formatPriceForDb(mAmount));
        expense.associateCategory(mCategory);
        expensesToAdd.add(expense);

        ExpenseEntry.create(expensesToAdd, this, this);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mActivityTitle);
        }
    }

    private void setupCategoriesPicker(List<CategoryEntry> data) {
        if (data.size() != 0) {
            ArrayAdapter<CategoryEntry> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_foreground, data);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            mCategoriesPicker.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                    , R.layout.spinner_item_foreground,
                    new String[]{mEmptyCategoriesText});
            adapter.setDropDownViewResource(R.layout.spinner_item);
            mCategoriesPicker.setAdapter(adapter);
        }
        mCategoriesPicker.setSelection(0);
    }

    private void setupDatePicker() {
        mDatePicker.setText(mDateFormatter.getStringFromDate(new Date()));
    }

    private DatePickerDialog.OnDateSetListener mDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePickerDialog view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            int month = selectedMonth + 1;
            String stringMonth = String.valueOf(month);
            if (month < 10) stringMonth = "0" + stringMonth;

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

    private void notifyUserWithToast(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void notifyUserWithSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void loadCategories() {
        getSupportLoaderManager().restartLoader(ConstantsManager.CATEGORIES_LOADER_ID, null, this);
    }

}
