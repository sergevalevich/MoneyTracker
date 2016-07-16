package com.valevich.moneytracker.taskshandlers;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class SignUpWithGoogleTask {

    @RootContext
    LoginActivity mLoginActivity;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @StringRes(R.string.general_error_message)
    String mGeneralErrorMessage;

    @StringRes(R.string.google_token_error_message)
    String mGoogleTokenErrorMessage;

    @Bean
    RestService mRestService;

    @Bean
    FetchUserDataTask mFetchUserDataTask;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

    @Bean
    TriesCounter mTriesCounter;

    @Background
    public void logInWithGoogle(final String accountName) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(mLoginActivity, accountName,
                        ConstantsManager.SCOPES);

            } catch (UserRecoverableAuthException userAuthEx) {
                mLoginActivity.startActivityForResult(userAuthEx.getIntent(), LoginActivity.REQUEST_CODE);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            } catch (GoogleAuthException fatalAuthEx) {
                fatalAuthEx.printStackTrace();
                Timber.e("Fatal Exception %s", fatalAuthEx.getLocalizedMessage());
            }

            if (token != null) {
                MoneyTrackerApplication_.saveGoogleToken(token);
                mRestService.getGoogleInfo(token, new Callback<UserGoogleInfoModel>() {
                    @Override
                    public void success(UserGoogleInfoModel userGoogleInfoModel, Response response) {
                        MoneyTrackerApplication_.saveUserInfo(
                                userGoogleInfoModel.getName(),
                                userGoogleInfoModel.getEmail(),
                                userGoogleInfoModel.getPicture(),
                                "");
                        fetchUserData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Timber.d(error.getLocalizedMessage());
                        mTriesCounter.reduceTry();
                        if (mTriesCounter.areTriesLeft()) {
                            logInWithGoogle(accountName);
                        } else {
                            notifyAboutNetworkError(mGeneralErrorMessage);
                        }
                    }
                });
            } else {
                notifyAboutNetworkError(mGoogleTokenErrorMessage);
            }
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    private void fetchUserData() {
        mFetchUserDataTask.fetchUserData();
    }

    private void notifyAboutNetworkError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }

}
