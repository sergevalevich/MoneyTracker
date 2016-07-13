package com.valevich.moneytracker.ui.taskshandlers;

import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.SignUpFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.ui.activities.SignUpActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

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

    @StringRes(R.string.wrong_username_msg)
    String mWrongUsernameMessage;

    @StringRes(R.string.wrong_password_msg)
    String mWrongPasswordMessage;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

    @Bean
    RestService mRestService;

    @Background
    public void signUp(final String userName, final String password, final String email) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.register(userName, password, new Callback<UserRegistrationModel>() {
                @Override
                public void success(UserRegistrationModel userRegistrationModel, Response response) {
                    String status = userRegistrationModel.getStatus();
                    switch (status) {
                        case ConstantsManager.STATUS_SUCCESS:
                            logIn(userName, password, email);
                            break;
                        case ConstantsManager.STATUS_LOGIN_BUSY:
                            notifyUser(mLoginBusyMessage);
                            notifySignUpFinished();
                            break;
                        default:
                            notifyUser(mGeneralErrorMessage);
                            notifySignUpFinished();
                            break;
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    String message = error.getLocalizedMessage();
                    Timber.d(message);
                    notifyUser(message);
                }
            });

        } else {
            notifyUser(mNetworkUnavailableMessage);
            notifySignUpFinished();
        }
    }

    private void logIn(String userName, String password, String email) {
        UserLoginModel userLoginModel = mRestService.logIn(userName, password);
        String status = userLoginModel.getStatus();
        switch (status) {
            case ConstantsManager.STATUS_SUCCESS:
                MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                MoneyTrackerApplication_.saveUserInfo(userName,email,"",password);
                notifySignUpFinished();
                navigateToMain();
                break;
            case ConstantsManager.STATUS_WRONG_USERNAME:
                notifyUser(mWrongUsernameMessage);
                notifySignUpFinished();
                break;
            case ConstantsManager.STATUS_WRONG_PASSWORD:
                notifyUser(mWrongPasswordMessage);
                notifySignUpFinished();
                break;
            default:
                notifyUser(mGeneralErrorMessage);
                notifySignUpFinished();
                break;
        }
    }

    @UiThread
    void notifyUser(String message) {
        Snackbar.make(mActivity.getRootView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void notifySignUpFinished() {
        mEventBus.post(new SignUpFinishedEvent());
    }

    private void navigateToMain() {
        Intent intent = new Intent(mActivity,MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }
}
