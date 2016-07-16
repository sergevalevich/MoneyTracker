package com.valevich.moneytracker.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.valevich.moneytracker.R;


public class AuthProgressDialogFragment extends DialogFragment {

    private static final String PROGRESS_DIALOG_MESSAGE_KEY = "MESSAGE";
    private static final String PROGRESS_DIALOG_CONTENT_KEY = "CONTENT";

    private MaterialDialog mProgressDialog;

    public static AuthProgressDialogFragment newInstance(String message, String content) {
        AuthProgressDialogFragment frag = new AuthProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(PROGRESS_DIALOG_MESSAGE_KEY, message);
        args.putString(PROGRESS_DIALOG_CONTENT_KEY, content);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String message = getArguments().getString(PROGRESS_DIALOG_MESSAGE_KEY);
        String content = getArguments().getString(PROGRESS_DIALOG_CONTENT_KEY);

        if (message != null && content != null)
            mProgressDialog = new MaterialDialog.Builder(getContext())
                    .title(message)
                    .content(content)
                    .progress(true, 0)
                    .theme(Theme.DARK)
                    .contentColorRes(R.color.colorText)
                    .titleColorRes(R.color.colorText)
                    .build();

        return mProgressDialog;
    }
}
