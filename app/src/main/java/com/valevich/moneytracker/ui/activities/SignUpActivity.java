package com.valevich.moneytracker.ui.activities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity(R.layout.activity_sign_up)
public class SignUpActivity extends AppCompatActivity {

    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();
    @ViewById(R.id.root)
    RelativeLayout mRootLayout;

    @ViewById(R.id.backgroundImageView)
    ImageView mBackground;

    @ViewById(R.id.userNameField)
    AppCompatEditText mUsernameField;

    @ViewById(R.id.passwordField)
    AppCompatEditText mPasswordField;

    @ViewById(R.id.signUpButton)
    Button mSignUpButton;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @StringRes(R.string.wrong_auth_input)
    String mWrongInputMessage;

    @StringRes(R.string.login_busy_message)
    String mLoginBusyMessage;

    @StringRes(R.string.general_error_message)
    String mGeneralErrorMessage;

    @StringRes(R.string.prefs_filename)
    String mPrefsFileName;

    @AfterViews
    void setupViews() {
        Glide.with(this).load(R.drawable.gray_bg).into(mBackground);
    }

    @Click(R.id.signUpButton)
    void submitAccountInfo() {
        Log.d(LOG_TAG,"Click");
        if(NetworkStatusChecker.isNetworkAvailable(this)) {
            String userName = mUsernameField.getText().toString();
            String password = mPasswordField.getText().toString();
            if(userName.length() < 5 || password.length() < 5) {
                notifyUser(mWrongInputMessage);
            } else {
                signUp(userName,password);
            }
        } else {
            notifyUser(mNetworkUnavailableMessage);
        }
    }

    @Background
    void signUp(String userName, String password) {
        RestService restService = new RestService();
        UserRegistrationModel userRegistrationModel = restService.register(userName, password);
        String status = userRegistrationModel.getStatus();
        switch (status) {
            case UserRegistrationModel.STATUS_SUCCESS:
                logIn(userName,password,restService);
                break;
            case UserRegistrationModel.STATUS_LOGIN_BUSY:
                notifyUser(mLoginBusyMessage);
                break;
            default:
                notifyUser(mGeneralErrorMessage);
                break;
        }
    }

    @Background
    void logIn(String userName, String password,RestService restService) {
        UserLoginModel userLoginModel = restService.logIn(userName, password);
        String status = userLoginModel.getStatus();
        if(status.equals(UserRegistrationModel.STATUS_SUCCESS)) {
            saveToken(userLoginModel.getAuthToken());
            navigateToMain();
        } else {
            notifyUser(mGeneralErrorMessage);
        }
    }

    private void saveToken(String authToken) {
        getSharedPreferences(mPrefsFileName,Context.MODE_PRIVATE)
            .edit()
            .putString(getResources().getString(R.string.token_key),authToken)
            .commit();
    }

    private void navigateToMain() {
        MainActivity_.intent(this).start();
    }

    @UiThread
    void notifyUser(String message) {
        Snackbar.make(mRootLayout,message,Snackbar.LENGTH_LONG).show();
    }
}
