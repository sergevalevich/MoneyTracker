package com.valevich.moneytracker.ui.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.AccountPicker;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.ui.taskshandlers.FetchUserDataTask;
import com.valevich.moneytracker.ui.taskshandlers.SignUpTask;
import com.valevich.moneytracker.ui.taskshandlers.SignUpWithGoogleTask;
import com.valevich.moneytracker.utils.InputFieldValidator;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.AfterViews;
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

    @ViewById(R.id.backgroundImageView)
    ImageView mBackground;

    @ViewById(R.id.signUpText)
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

    @AfterViews
    void loadBackground() {
        Glide.with(this)
                .load(R.drawable.gray_bg)
                .placeholder(R.drawable.gray_bg_placeholder)
                .crossFade()
                .into(mBackground);
    }
    @Click(R.id.google_login_btn)
    void pickAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Click(R.id.logInButton)
    void logIn() {
        if(mNetworkStatusChecker.isNetworkAvailable()) {

            String username = mUsernameField.getText().toString();
            String password = mPasswordField.getText().toString();

            if (isInputValid(username, password))
                mSignUpTask.logIn(username, password);

        } else {
            mUserNotifier.notifyUser(mRootLayout,mNetworkUnavailableMessage);
        }

    }

    @Click(R.id.signUpText)
    void signUp() {
        SignUpActivity_.intent(this).start();
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, @OnActivityResult.Extra(value = AccountManager.KEY_ACCOUNT_NAME) String accountName) {
        if (resultCode == RESULT_OK) {
            if(mNetworkStatusChecker.isNetworkAvailable())
                mSignUpWithGoogleTask.logInWithGoogle(accountName);
            else mUserNotifier.notifyUser(mRootLayout,mNetworkUnavailableMessage);
        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, mGoogleAccountPickerErrorMessage, Toast.LENGTH_LONG).show();
        }
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
}
