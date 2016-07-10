package com.valevich.moneytracker.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.CategorySaveButtonClickedEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

/**
 * Created by User on 09.07.2016.
 */
@EFragment
public class EditCategoryDialogFragment extends DialogFragment {

    @ViewById(R.id.saveCategoryButton)
    TextView mSaveCategoryButton;

    @ViewById(R.id.cancelButton)
    TextView mCancelButton;

    @ViewById(R.id.dialog_title)
    TextView mTitleView;

    @ViewById(R.id.category_name_field)
    AppCompatEditText mCategoryNameField;

    @FragmentArg
    String title;

    @Bean
    OttoBus mEventBus;

    private String mInputText;

    @AfterViews
    void setUpViews() {

        setUpDialogTitle();

        setUpInputField();

        setUpSaveButton();

        setUpCancelButton();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_category, container, false);
    }

    public void setInputFieldText(String text) {
        mInputText = text;
    }

    private void setUpDialogTitle() {
        if (title != null)
            mTitleView.setText(title);
    }

    private void setUpInputField() {
        if (mInputText != null)
            mCategoryNameField.setText(mInputText);
    }

    private void setUpSaveButton() {
        mSaveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventBus.post(new CategorySaveButtonClickedEvent(mCategoryNameField
                        .getText()
                        .toString()));
            }
        });
    }

    private void setUpCancelButton() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mEventBus.post(new CategoryCancelButtonClickedEvent());
                dismiss();
            }
        });
    }
}

