package com.valevich.moneytracker.ui.taskshandlers;

import android.support.design.widget.Snackbar;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by User on 07.07.2016.
 */
@EBean
public class LoginTask {

    @RootContext
    LoginActivity mActivity;

    @StringRes(R.string.general_error_message)
    String mGeneralErrorMessage;

    @StringRes(R.string.wrong_username_msg)
    String mWrongUsernameMessage;

    @StringRes(R.string.wrong_password_msg)
    String mWrongPasswordMessage;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    RestService mRestService;

    @Bean
    FetchUserDataTask mFetchUserDataTask;

    @Bean
    OttoBus mEventBus;

    @Background
    public void logIn(String userName, String password) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            UserLoginModel userLoginModel = mRestService.logIn(userName, password);
            String status = userLoginModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                    MoneyTrackerApplication_.saveUserInfo(userName, "", "", password);
                    fetchUserData();
                    break;
                case ConstantsManager.STATUS_WRONG_USERNAME:
                    notifyUser(mWrongUsernameMessage);
                    notifyLoginFinished();
                    break;
                case ConstantsManager.STATUS_WRONG_PASSWORD:
                    notifyUser(mWrongPasswordMessage);
                    notifyLoginFinished();
                    break;
                default:
                    notifyUser(mGeneralErrorMessage);
                    notifyLoginFinished();
                    break;
            }
        } else {
            notifyUser(mNetworkUnavailableMessage);
            notifyLoginFinished();
        }
    }

    @UiThread
    void notifyUser(String message) {
        Snackbar.make(mActivity.getRootView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void fetchUserData() {
        mFetchUserDataTask.fetchUserData();
    }

    private void notifyLoginFinished() {
        mEventBus.post(new LoginFinishedEvent());
    }
}
