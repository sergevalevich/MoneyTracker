package com.valevich.moneytracker.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LogoutFinishedEvent;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
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
public class LogoutTask {

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    @Bean
    OttoBus mEventBus;

    @StringRes(R.string.logout_error_message)
    String mLogoutErrorMessage;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Background
    public void logOut() {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.logout(new Callback<UserLogoutModel>() {
                @Override
                public void success(UserLogoutModel userLogoutModel, Response response) {
                    String status = userLogoutModel.getStatus();

                    switch (status) {
                        case ConstantsManager.STATUS_SUCCESS:
                            notifyLogoutFinished();
                            break;
                        case ConstantsManager.STATUS_EMPTY:
                            notifyLogoutFinished();
                            break;
                        default:
                            notifyAboutNetworkError(mLogoutErrorMessage);
                            break;
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.d(error.getLocalizedMessage());
                    mNetworkErrorTriesCounter.reduceTry();
                    if (mNetworkErrorTriesCounter.areTriesLeft()) {
                        logOut();
                    } else {
                        notifyAboutNetworkError(mLogoutErrorMessage);
                    }
                }
            });
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    private void notifyLogoutFinished() {
        MoneyTrackerApplication_.setIsLogoutFinished(true);
        mEventBus.post(new LogoutFinishedEvent());
    }

    private void notifyAboutNetworkError(String message) {
        MoneyTrackerApplication_.setIsNetworkError(true);
        MoneyTrackerApplication_.setErrorMessage(message);
        mEventBus.post(new NetworkErrorEvent(message));
    }

}
