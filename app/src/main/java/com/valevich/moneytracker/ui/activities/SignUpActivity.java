package com.valevich.moneytracker.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.SignUpFinishedEvent;
import com.valevich.moneytracker.ui.taskshandlers.SignUpTask;
import com.valevich.moneytracker.utils.InputFieldValidator;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import io.fabric.sdk.android.Fabric;


@EActivity(R.layout.activity_sign_up)
public class SignUpActivity extends AppCompatActivity{

    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();
    @ViewById(R.id.root)
    RelativeLayout mRootLayout;

    @ViewById(R.id.userNameField)
    AppCompatEditText mUsernameField;

    @ViewById(R.id.passwordField)
    AppCompatEditText mPasswordField;

    @ViewById(R.id.emailField)
    AppCompatEditText mEmailField;

    @ViewById(R.id.signUpButton)
    Button mSignUpButton;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @StringRes(R.string.wrong_auth_input)
    String mWrongInputMessage;

    @StringRes(R.string.invalid_username_msg)
    String mInvalidUsernameMessage;

    @StringRes(R.string.invalid_password_msg)
    String mInvalidPasswordMessage;

    @StringRes(R.string.invalid_email_msg)
    String mInvalidEmailMessage;

    @NonConfigurationInstance
    @Bean
    SignUpTask mSignUpTask;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    UserNotifier mUserNotifier;

    @Bean
    InputFieldValidator mInputFieldValidator;

    @StringRes(R.string.auth_dialog_message)
    String mAuthMessage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unblockButton();
        BusProvider.getInstance().unregister(this);
    }

    @Click(R.id.signUpButton)
    void submitAccountInfo() {
        Log.d(LOG_TAG,"Click");
        blockButton();
        if(mNetworkStatusChecker.isNetworkAvailable()) {

            String username = mUsernameField.getText().toString();
            String password = mPasswordField.getText().toString();
            String email = mEmailField.getText().toString();

            if(isInputValid(username,password,email)) {
                showProgressDialog();
                mSignUpTask.signUp(username, password, email);
            } else {
                unblockButton();
            }

        } else {
            mUserNotifier.notifyUser(mRootLayout,mNetworkUnavailableMessage);
            unblockButton();
        }
    }

    @Subscribe
    public void onSignUpFinished(SignUpFinishedEvent signUpFinishedEvent) {
        Log.d(LOG_TAG, "onSignUpFinished: ");
        closeProgressDialog();
        unblockButton();
    }

    private boolean isInputValid(String username,String password,String email) {
        if (!mInputFieldValidator.isUsernameValid(username)) {
            mUserNotifier.notifyUser(mRootLayout,mInvalidUsernameMessage);
            return false;
        } else if (!mInputFieldValidator.isPasswordValid(password)) {
            mUserNotifier.notifyUser(mRootLayout,mInvalidPasswordMessage);
            return false;
        } else if (!mInputFieldValidator.isEmailValid(email)) {
            mUserNotifier.notifyUser(mRootLayout,mInvalidEmailMessage);
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

    private void blockButton() {
        mSignUpButton.setClickable(false);
    }

    private void unblockButton() {
        mSignUpButton.setClickable(true);
    }
}
