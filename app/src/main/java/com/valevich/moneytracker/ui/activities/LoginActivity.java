package com.valevich.moneytracker.ui.activities;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.ui.taskshandlers.SignUpTask;
import com.valevich.moneytracker.ui.taskshandlers.SignUpWithGoogleTask;
import com.valevich.moneytracker.utils.InputFieldValidator;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity{

    public final static int REQUEST_CODE = 100;
    public final static String TAG = LoginActivity.class.getSimpleName();

    @StringRes(R.string.google_account_picker_error_msg)
    String mGoogleAccountPickerErrorMessage;

    @ViewById(R.id.google_login_btn)
    RelativeLayout mLogInWithGoogleButton;

    @ViewById(R.id.logInButton)
    Button mLogInButton;

    @ViewById(R.id.signUpButton)
    TextView mSignUpButton;

    @ViewById(R.id.root)
    RelativeLayout mRootLayout;

    @ViewById(R.id.userNameField)
    AppCompatEditText mUsernameField;

    @ViewById(R.id.passwordField)
    AppCompatEditText mPasswordField;

    @NonConfigurationInstance
    @Bean
    SignUpTask mSignUpTask;

    @Bean
    UserNotifier mUserNotifier;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @NonConfigurationInstance
    @Bean
    SignUpWithGoogleTask mSignUpWithGoogleTask;

    @StringRes(R.string.invalid_username_msg)
    String mInvalidUsernameMessage;

    @StringRes(R.string.invalid_password_msg)
    String mInvalidPasswordMessage;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @StringRes(R.string.auth_dialog_message)
    String mAuthMessage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unblockButtons();//when going back from SignUpActivity Buttons are blocked(preventing)
        BusProvider.getInstance().unregister(this);
    }

    @Click(R.id.google_login_btn)
    void pickAccount() {
        blockButtons();
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Click(R.id.logInButton)
    void logIn() {
        blockButtons();
        if(mNetworkStatusChecker.isNetworkAvailable()) {

            String username = mUsernameField.getText().toString();
            String password = mPasswordField.getText().toString();

            if (isInputValid(username, password)) {
                showProgressDialog();
                mSignUpTask.logIn(username, password);
            } else {
                unblockButtons();
            }
        } else {
            mUserNotifier.notifyUser(mRootLayout,mNetworkUnavailableMessage);
            unblockButtons();
        }
    }

    @Click(R.id.signUpButton)
    void signUp() {
        blockButtons();
        SignUpActivity_.intent(this).start();
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, @OnActivityResult.Extra(value = AccountManager.KEY_ACCOUNT_NAME) String accountName) {
        if (resultCode == RESULT_OK) {
            if(mNetworkStatusChecker.isNetworkAvailable()) {
                showProgressDialog();
                mSignUpWithGoogleTask.logInWithGoogle(accountName);
            }
            else {
                unblockButtons();
                mUserNotifier.notifyUser(mRootLayout,mNetworkUnavailableMessage);
            }
        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, mGoogleAccountPickerErrorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onLoginFinished(LoginFinishedEvent loginFinishedEvent) {
        Log.d(TAG, "onLoginFinished: ");
        closeProgressDialog();
        unblockButtons();
    }

    private boolean isInputValid(String username,String password) {
        if(!InputFieldValidator.isUsernameValid(username)) {
            mUserNotifier.notifyUser(mRootLayout,mInvalidUsernameMessage);
            return false;
        } else if(!InputFieldValidator.isPasswordValid(password)) {
            mUserNotifier.notifyUser(mRootLayout, mInvalidPasswordMessage);
            return false;
        }
        return true;
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(mAuthMessage);
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing())
        mProgressDialog.dismiss();
    }

    private void blockButtons() {
        mLogInWithGoogleButton.setClickable(false);
        mSignUpButton.setClickable(false);
        mLogInButton.setClickable(false);
    }

    private void unblockButtons() {
        mLogInWithGoogleButton.setClickable(true);
        mSignUpButton.setClickable(true);
        mLogInButton.setClickable(true);
    }
}
