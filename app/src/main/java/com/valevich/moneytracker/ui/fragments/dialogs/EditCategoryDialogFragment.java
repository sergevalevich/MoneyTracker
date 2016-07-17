package com.valevich.moneytracker.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.CategorySaveButtonClickedEvent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.res.StringRes;

@EFragment
public class EditCategoryDialogFragment extends DialogFragment {

    @FragmentArg
    String title;

    @FragmentArg
    String input;

    @StringRes(R.string.add_category_positive)
    String mPositiveText;

    @Bean
    OttoBus mEventBus;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (title != null && input != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .title(title)
                    .theme(Theme.DARK)
                    .widgetColorRes(R.color.white)
                    .contentColorRes(R.color.colorText)
                    .titleColorRes(R.color.colorText)
                    .inputRangeRes(1, 15, R.color.colorAccent)
                    .positiveText(mPositiveText)
                    .input("", input, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            mEventBus.post(new CategorySaveButtonClickedEvent(input.toString()));
                        }
                    }).build();
            dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
            return dialog;
        }

        return super.onCreateDialog(savedInstanceState);

    }
}

