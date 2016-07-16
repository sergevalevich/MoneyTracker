package com.valevich.moneytracker.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.eventbus.events.SignUpFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class SignUpTask {

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

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    @Background
    public void signUp(final String userName, final String password, final String email) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.register(userName, password, new Callback<UserRegistrationModel>() {
                @Override
                public void success(UserRegistrationModel userRegistrationModel, Response response) {
                    mNetworkErrorTriesCounter.resetTries();
                    String status = userRegistrationModel.getStatus();
                    switch (status) {
                        case ConstantsManager.STATUS_SUCCESS:
                            logIn(userName, password, email);
                            break;
                        case ConstantsManager.STATUS_LOGIN_BUSY:
                            notifyAboutNetworkError(mLoginBusyMessage);
                            break;
                        default:
                            notifyAboutNetworkError(mGeneralErrorMessage);
                            break;
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.d(error.getLocalizedMessage());
                    mNetworkErrorTriesCounter.reduceTry();
                    if (mNetworkErrorTriesCounter.areTriesLeft()) {
                        signUp(userName, password, email);
                    } else {
                        notifyAboutNetworkError(error.getLocalizedMessage());
                    }
                }
            });

        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    @Background
    void logIn(final String userName, final String password, final String email) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.logIn(userName, password, new Callback<UserLoginModel>() {
                @Override
                public void success(UserLoginModel userLoginModel, Response response) {
                    String status = userLoginModel.getStatus();
                    switch (status) {
                        case ConstantsManager.STATUS_SUCCESS:
                            MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                            MoneyTrackerApplication_.saveUserInfo(userName, email, "", password);
                            notifySignUpFinished();
                            break;
                        case ConstantsManager.STATUS_WRONG_USERNAME:
                            notifyAboutNetworkError(mWrongUsernameMessage);
                            break;
                        case ConstantsManager.STATUS_WRONG_PASSWORD:
                            notifyAboutNetworkError(mWrongPasswordMessage);
                            break;
                        default:
                            notifyAboutNetworkError(mGeneralErrorMessage);
                            break;
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.d(error.getLocalizedMessage());
                    mNetworkErrorTriesCounter.reduceTry();
                    if (mNetworkErrorTriesCounter.areTriesLeft()) {
                        logIn(userName, password, email);
                    } else {
                        notifyAboutNetworkError(error.getLocalizedMessage());
                    }
                }
            });

        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }

    }

    private void notifySignUpFinished() {
        mEventBus.post(new SignUpFinishedEvent());
    }

    private void notifyAboutNetworkError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }
}
