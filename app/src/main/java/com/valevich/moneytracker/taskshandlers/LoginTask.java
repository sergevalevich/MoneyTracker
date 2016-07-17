package com.valevich.moneytracker.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class LoginTask {

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
    TriesCounter mNetworkErrorTriesCounter;

    @Bean
    OttoBus mEventBus;

    @Background
    public void logIn(final String userName, final String password) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.logIn(userName, password, new Callback<UserLoginModel>() {
                @Override
                public void success(UserLoginModel userLoginModel, Response response) {
                    String status = userLoginModel.getStatus();
                    switch (status) {
                        case ConstantsManager.STATUS_SUCCESS:
                            MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                            MoneyTrackerApplication_.saveUserInfo(userName, "", "", password);
                            fetchUserData();
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
                        logIn(userName, password);
                    } else {
                        notifyAboutNetworkError(mGeneralErrorMessage);
                    }
                }
            });
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    private void fetchUserData() {
        mFetchUserDataTask.fetchUserData();
    }

    private void notifyAboutNetworkError(String message) {
        MoneyTrackerApplication_.setIsNetworkError(true);
        MoneyTrackerApplication_.setErrorMessage(message);
        mEventBus.post(new NetworkErrorEvent(message));
    }
}
