package com.valevich.moneytracker.ui.taskshandlers;

import android.content.Context;
import android.support.design.widget.Snackbar;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.ui.activities.SignUpActivity;
import com.valevich.moneytracker.ui.activities.SignUpActivity_;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by NotePad.by on 22.05.2016.
 */
@EBean
public class SignUpTask {
    @RootContext
    SignUpActivity mActivity;

    @StringRes(R.string.login_busy_message)
    String mLoginBusyMessage;

    @StringRes(R.string.general_error_message)
    String mGeneralErrorMessage;

    @StringRes(R.string.prefs_filename)
    String mPrefsFileName;

    @Bean
    RestService mRestService;

    @Background
    public void signUp(String userName, String password) {
        UserRegistrationModel userRegistrationModel = mRestService.register(userName, password);
        String status = userRegistrationModel.getStatus();
        switch (status) {
            case UserRegistrationModel.STATUS_SUCCESS:
                logIn(userName, password);
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
    void logIn(String userName, String password) {
        UserLoginModel userLoginModel = mRestService.logIn(userName, password);
        String status = userLoginModel.getStatus();
        if (status.equals(UserRegistrationModel.STATUS_SUCCESS)) {
            saveToken(userLoginModel.getAuthToken());
            navigateToMain();
        } else {
            notifyUser(mGeneralErrorMessage);
        }
    }

    private void saveToken(String authToken) {
        mActivity.getSharedPreferences(mPrefsFileName, Context.MODE_PRIVATE)
                .edit()
                .putString(mActivity.getResources().getString(R.string.token_key), authToken)
                .commit();
    }

    private void navigateToMain() {
        MainActivity_.intent(mActivity).start();
        mActivity.finish();
    }

    @UiThread
    void notifyUser(String message) {
        mActivity.notifyUser(message);
    }
}
