package com.valevich.moneytracker.ui.activities;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.taskshandlers.LoginTask;
import com.valevich.moneytracker.taskshandlers.SignUpWithGoogleTask;
import com.valevich.moneytracker.ui.fragments.dialogs.AuthProgressDialogFragment;
import com.valevich.moneytracker.ui.fragments.dialogs.AuthProgressDialogFragment_;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.InputFieldValidator;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import timber.log.Timber;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

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

    @StringRes(R.string.google_account_picker_error_msg)
    String mGoogleAccountPickerErrorMessage;

    @StringRes(R.string.auth_dialog_content)
    String mAuthDialogContent;

    @StringRes(R.string.invalid_username_msg)
    String mInvalidUsernameMessage;

    @StringRes(R.string.invalid_password_msg)
    String mInvalidPasswordMessage;

    @StringRes(R.string.auth_dialog_message)
    String mAuthMessage;

    @NonConfigurationInstance
    @Bean
    LoginTask mLoginTask;

    @NonConfigurationInstance
    @Bean
    SignUpWithGoogleTask mSignUpWithGoogleTask;

    @Bean
    InputFieldValidator mInputFieldValidator;

    @Bean
    OttoBus mEventBus;

    private AuthProgressDialogFragment mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
        //if the user pressed the power button during authorization
        if (MoneyTrackerApplication_.isLoginFinished()) {
            onLoginFinished(null);
        } else if (MoneyTrackerApplication_.isNetworkError()) {
            onNetworkError(new NetworkErrorEvent(MoneyTrackerApplication_.getErrorMessage()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unblockButtons();//when going back from SignUpActivity Buttons are blocked(preventing)
        mEventBus.unregister(this);
    }

    @Click(R.id.google_login_btn)
    void pickAccount() {
        blockButtons();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, ConstantsManager.PICK_ACCOUNT_REQUEST_CODE);
        } else {
            showErrorDialog(status);
        }
    }

    @Click(R.id.logInButton)
    void logIn() {
        blockButtons();

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (isInputValid(username, password)) {
            showProgressDialog();
            mLoginTask.logIn(username, password);
        } else {
            unblockButtons();
        }

    }

    @Click(R.id.signUpButton)
    void signUp() {
        blockButtons();
        SignUpActivity_.intent(this).start();
    }

    @OnActivityResult(ConstantsManager.PICK_ACCOUNT_REQUEST_CODE)
    void onAccountPicked(int resultCode, @OnActivityResult.Extra(value = AccountManager.KEY_ACCOUNT_NAME) String accountName) {
        if (resultCode == RESULT_OK) {
            showProgressDialog();
            mSignUpWithGoogleTask.logInWithGoogle(accountName);
        } else if (resultCode == RESULT_CANCELED) {
            unblockButtons();
        } else {
            Toast.makeText(this, mGoogleAccountPickerErrorMessage, Toast.LENGTH_LONG).show();
            unblockButtons();
        }
    }

    @OnActivityResult((ConstantsManager.GOOGLE_PLAY_SERVICES_ERROR_REQUEST_CODE))
    void onGooglePlayServicesVerified(int resultCode) {
        unblockButtons();
    }

    @Subscribe
    public void onNetworkError(NetworkErrorEvent event) {
        MoneyTrackerApplication_.setIsNetworkError(false);
        MoneyTrackerApplication_.setErrorMessage("");
        closeProgressDialog();
        unblockButtons();
        notifyUserWithSnackBar(event.getMessage());
    }

    @Subscribe
    public void onLoginFinished(LoginFinishedEvent loginFinishedEvent) {
        Timber.d("onLoginFinished: ");
        MoneyTrackerApplication_.setIsLoginFinished(false);
        closeProgressDialog();
        unblockButtons();
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isInputValid(String username, String password) {
        if (!mInputFieldValidator.isUsernameValid(username)) {
            notifyUserWithSnackBar(mInvalidUsernameMessage);
            return false;
        } else if (!mInputFieldValidator.isPasswordValid(password)) {
            notifyUserWithSnackBar(mInvalidPasswordMessage);
            return false;
        }
        return true;
    }

    private void notifyUserWithSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void showProgressDialog() {
        mProgressDialog = AuthProgressDialogFragment_.builder()
                .message(mAuthMessage)
                .content(mAuthDialogContent)
                .build();
        mProgressDialog.show(getSupportFragmentManager(), ConstantsManager.PROGRESS_DIALOG_TAG);
        mProgressDialog.setCancelable(false);
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void showErrorDialog(int errorCode) {
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                this,
                ConstantsManager.GOOGLE_PLAY_SERVICES_ERROR_REQUEST_CODE,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        unblockButtons();
                    }
                });
        if (errorDialog != null)
            errorDialog.show();
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
