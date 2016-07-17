package com.valevich.moneytracker.ui.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.eventbus.events.SignUpFinishedEvent;
import com.valevich.moneytracker.taskshandlers.SignUpTask;
import com.valevich.moneytracker.ui.fragments.dialogs.AuthProgressDialogFragment;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.InputFieldValidator;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity(R.layout.activity_sign_up)
public class SignUpActivity extends AppCompatActivity {

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

    @StringRes(R.string.wrong_auth_input)
    String mWrongInputMessage;

    @StringRes(R.string.invalid_username_msg)
    String mInvalidUsernameMessage;

    @StringRes(R.string.invalid_password_msg)
    String mInvalidPasswordMessage;

    @StringRes(R.string.invalid_email_msg)
    String mInvalidEmailMessage;

    @StringRes(R.string.auth_dialog_content)
    String mAuthDialogContent;

    @StringRes(R.string.auth_dialog_message)
    String mAuthMessage;

    @NonConfigurationInstance
    @Bean
    SignUpTask mSignUpTask;

    @Bean
    InputFieldValidator mInputFieldValidator;

    @Bean
    OttoBus mEventBus;

    private AuthProgressDialogFragment mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
        if (MoneyTrackerApplication_.isSignUpFinished()) {//if the user pressed the power button
            onSignUpFinished(null);
        } else if (MoneyTrackerApplication_.isNetworkError()) {
            onNetworkError(new NetworkErrorEvent(MoneyTrackerApplication_.getErrorMessage()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unblockButton();
        mEventBus.unregister(this);
    }

    @Click(R.id.signUpButton)
    void submitAccountInfo() {
        blockButton();

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();

        if (isInputValid(username, password, email)) {
            showProgressDialog();
            mSignUpTask.signUp(username, password, email);
        } else {
            unblockButton();
        }
    }

    @Subscribe
    public void onSignUpFinished(SignUpFinishedEvent signUpFinishedEvent) {
        MoneyTrackerApplication_.setIsSignUpFinished(false);
        closeProgressDialog();
        unblockButton();
        navigateToMain();
    }

    @Subscribe
    public void onNetworkError(NetworkErrorEvent event) {
        MoneyTrackerApplication_.setIsNetworkError(false);
        MoneyTrackerApplication_.setErrorMessage("");
        closeProgressDialog();
        unblockButton();
        notifyUserWithSnackBar(event.getMessage());
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isInputValid(String username, String password, String email) {
        if (!mInputFieldValidator.isUsernameValid(username)) {
            notifyUserWithSnackBar(mInvalidUsernameMessage);
            return false;
        } else if (!mInputFieldValidator.isPasswordValid(password)) {
            notifyUserWithSnackBar(mInvalidPasswordMessage);
            return false;
        } else if (!mInputFieldValidator.isEmailValid(email)) {
            notifyUserWithSnackBar(mInvalidEmailMessage);
            return false;
        }
        return true;
    }

    private void notifyUserWithSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void showProgressDialog() {
        mProgressDialog = AuthProgressDialogFragment.newInstance(mAuthMessage, mAuthDialogContent);
        mProgressDialog.show(getSupportFragmentManager(), ConstantsManager.PROGRESS_DIALOG_TAG);
        mProgressDialog.setCancelable(false);
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void blockButton() {
        mSignUpButton.setClickable(false);
    }

    private void unblockButton() {
        mSignUpButton.setClickable(true);
    }
}
