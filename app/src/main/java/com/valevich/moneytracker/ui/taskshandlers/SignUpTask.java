package com.valevich.moneytracker.ui.taskshandlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.ViewGroup;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.ui.activities.SignUpActivity;
import com.valevich.moneytracker.ui.activities.SignUpActivity_;
import com.valevich.moneytracker.utils.Preferences_;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by NotePad.by on 22.05.2016.
 */
@EBean
public class SignUpTask {

    @RootContext
    Activity mActivity;

    @Bean
    UserNotifier mUserNotifier;

    @StringRes(R.string.login_busy_message)
    String mLoginBusyMessage;

    @StringRes(R.string.general_error_message)
    String mGeneralErrorMessage;

    @StringRes(R.string.wrong_username_msg)
    String mWrongUsernameMessage;

    @StringRes(R.string.wrong_password_msg)
    String mWrongPasswordMessage;

    @Bean
    RestService mRestService;

    @Background
    public void signUp(String userName, String password, String email) {
        UserRegistrationModel userRegistrationModel = mRestService.register(userName, password);
        String status = userRegistrationModel.getStatus();
        switch (status) {
            case UserRegistrationModel.STATUS_SUCCESS:
                logIn(userName, password, email);
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
    public void logIn(String userName, String password, String email) {
        UserLoginModel userLoginModel = mRestService.logIn(userName, password);
        String status = userLoginModel.getStatus();
        switch (status) {
            case UserLoginModel.STATUS_SUCCESS:
                MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                MoneyTrackerApplication_.saveUserInfo(userName,email,"",password);
                navigateToMain();
                break;
            case UserLoginModel.STATUS_WRONG_USERNAME:
                notifyUser(mWrongUsernameMessage);
                break;
            case UserLoginModel.STATUS_WRONG_PASSWORD:
                notifyUser(mWrongPasswordMessage);
                break;
            default:
                notifyUser(mGeneralErrorMessage);
                break;
        }
    }

    @Background
    public void logIn(String userName, String password) {
        UserLoginModel userLoginModel = mRestService.logIn(userName, password);
        String status = userLoginModel.getStatus();
        switch (status) {
            case UserLoginModel.STATUS_SUCCESS:
                MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                MoneyTrackerApplication_.saveUserInfo(userName,"","",password);
                navigateToMain();
                break;
            case UserLoginModel.STATUS_WRONG_USERNAME:
                notifyUser(mWrongUsernameMessage);
                break;
            case UserLoginModel.STATUS_WRONG_PASSWORD:
                notifyUser(mWrongPasswordMessage);
                break;
            default:
                notifyUser(mGeneralErrorMessage);
                break;
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(mActivity,MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }

    @UiThread
    void notifyUser(String message) {
        mUserNotifier.notifyUser(mActivity.findViewById(R.id.root),message);
    }
}
