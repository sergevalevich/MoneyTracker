package com.valevich.moneytracker.ui.activities;


import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.marvinlabs.widget.floatinglabel.instantpicker.DatePickerFragment;
import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelDatePicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.Instant;
import com.marvinlabs.widget.floatinglabel.instantpicker.InstantPickerListener;
import com.marvinlabs.widget.floatinglabel.instantpicker.JavaDateInstant;
import com.marvinlabs.widget.floatinglabel.itempicker.FloatingLabelItemPicker;
import com.marvinlabs.widget.floatinglabel.itempicker.ItemPickerListener;
import com.marvinlabs.widget.floatinglabel.itempicker.StringPickerDialogFragment;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


@EActivity
public class NewExpenseActivity extends AppCompatActivity implements
        FloatingLabelItemPicker.OnItemPickerEventListener<String>,
        ItemPickerListener<String>,
        InstantPickerListener,
        FloatingLabelInstantPicker.OnInstantPickerEventListener,
        FloatingLabelEditText.EditTextListener{

    private String[] mExampleCategories = {
            "Одежда",
            "Еда",
            "Дом",
            "Налоги",
            "Бизнес"
    };

    @ViewById(R.id.amountLabel)
    FloatingLabelEditText mAmountEditText;

    @ViewById(R.id.descriptionLabel)
    FloatingLabelEditText mDescriptionEditText;

    @ViewById(R.id.categories_picker)
    FloatingLabelItemPicker<String> mCategoriesPicker;

    @ViewById(R.id.datePicker)
    FloatingLabelDatePicker<JavaDateInstant> mDatePicker;

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
        setupTextFields();
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
        mCategoriesPicker.setItemPickerListener(this);
        mCategoriesPicker.setAvailableItems(new ArrayList<String>(Arrays.asList(mExampleCategories)));
        mCategoriesPicker.setWidgetListener(new FloatingLabelItemPicker.OnWidgetEventListener<String>() {
            @Override
            public void onShowItemPickerDialog(FloatingLabelItemPicker source) {
                StringPickerDialogFragment categoriesPicker = StringPickerDialogFragment.newInstance(
                        source.getId(),
                        mCategoriesPickerTitle,
                        mCategoriesPickerPositive,
                        mCategoriesPickerNegative,
                        false,
                        source.getSelectedIndices(),
                        new ArrayList<String>(source.getAvailableItems()));
                categoriesPicker.show(getSupportFragmentManager(), "CategoriesPicker");
            }
        });
    }

    private void setupDatePicker() {
        mDatePicker.setSelectedInstant(new JavaDateInstant());
        mDatePicker.setInstantPickerListener(this);
        mDatePicker.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<JavaDateInstant>() {
            @Override
            public void onShowInstantPickerDialog(FloatingLabelInstantPicker<JavaDateInstant> source) {
                DatePickerFragment<JavaDateInstant> pickerFragment =
                        DatePickerFragment.<JavaDateInstant>newInstance(source.getId(), source.getSelectedInstant());
                pickerFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });
    }

    private void setupTextFields() {
        mAmountEditText.setEditTextListener(this);
        mDescriptionEditText.setEditTextListener(this);

        setInputFieldMaxLength(mAmountEditText.getInputWidget(),20);
        setInputFieldMaxLength(mDescriptionEditText.getInputWidget(),140);

        mDescriptionEditText.getInputWidget().setSingleLine(false);

    }

    private void setInputFieldMaxLength(EditText editText, int maxLength) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(filterArray);
    }

    private void dropFields() {
        mAmountEditText.getInputWidget().setText("");
        mDescriptionEditText.getInputWidget().setText("");
        mCategoriesPicker.setSelectedIndices(null);
    }

    private void showSnackBar(String text) {
        Snackbar.make(mRootLayout, text, Snackbar.LENGTH_SHORT).show();
    }

    @Click(R.id.saveExpenseButton) //if fields are empty show warning
    void setupSaveExenseButton() {
        String amountText = mAmountEditText
                .getInputWidget()
                .getText()
                .toString()
                .trim();
        String descriptionText = mDescriptionEditText
                .getInputWidget()
                .getText()
                .toString()
                .trim();
        Collection<String> selectedItems = mCategoriesPicker.getSelectedItems();
        if(amountText.length() != 0 && descriptionText.length() != 0 && selectedItems.size() != 0) {
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

    @Override
    public void onSelectionChanged(FloatingLabelItemPicker<String> source, Collection<String> selectedItems) {
        Toast.makeText(this, source.getItemPrinter().printCollection(selectedItems), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelled(int pickerId) {

    }

    @Override
    public void onInstantSelected(int pickerId, Instant instant) {
        mDatePicker.setSelectedInstant((JavaDateInstant) instant);
    }

    @Override
    public void onItemsSelected(int pickerId, int[] selectedIndices) {
        mCategoriesPicker.setSelectedIndices(selectedIndices);
    }

    @Override
    public void onInstantChanged(FloatingLabelInstantPicker source, Instant instant) {
        Toast.makeText(this, source.getInstantPrinter().print(instant), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextChanged(FloatingLabelEditText source, String text) {
    }

}
