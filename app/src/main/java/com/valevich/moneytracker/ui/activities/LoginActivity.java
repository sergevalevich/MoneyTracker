package com.valevich.moneytracker.ui.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Account;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.utils.ConstantsManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 100;
    private final static String TAG = LoginActivity.class.getSimpleName();

    @StringRes(R.string.google_account_picker_error_msg)
    String mGoogleAccountPickerErrorMessage;

    @ViewById(R.id.google_login_btn)
    SignInButton mLoginButton;

    @Click(R.id.google_login_btn)
    void pickAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, @OnActivityResult.Extra(value = AccountManager.KEY_ACCOUNT_NAME) String accountName) {
        if (resultCode == RESULT_OK) {
            logInWithGoogle(accountName);
        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, mGoogleAccountPickerErrorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Background
    void logInWithGoogle(final String accountName) {
        String token = null;
        try {
            token = GoogleAuthUtil.getToken(this, accountName,
                    ConstantsManager.SCOPES);

        } catch(UserRecoverableAuthException userAuthEx){
            startActivityForResult(userAuthEx.getIntent(), 10);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (GoogleAuthException fatalAuthEx) {
            fatalAuthEx.printStackTrace();
            Log.e(TAG, "Fatal Exception " + fatalAuthEx.getLocalizedMessage());
        }
        Log.d(TAG,token);
        if(token != null) {
            MoneyTrackerApplication_.saveGoogleToken(token);
        }
    }
}
