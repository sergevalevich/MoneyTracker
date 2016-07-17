package com.valevich.moneytracker.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class AuthProgressDialogFragment extends DialogFragment {

    @FragmentArg
    String message;

    @FragmentArg
    String content;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (message != null && content != null) {
            return new MaterialDialog.Builder(getContext())
                    .title(message)
                    .content(content)
                    .progress(true, 0)
                    .theme(Theme.DARK)
                    .contentColorRes(R.color.colorText)
                    .titleColorRes(R.color.colorText)
                    .build();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
