package com.valevich.moneytracker.utils.errorHandlers;

import android.content.Context;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


@EBean
public class ApiErrorHandler {

    public interface HandleCallback {
        void onHandle();
    }

    @RootContext
    Context mContext;

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    TriesCounter mTriesCounter;

    private HandleCallback mHandleCallback;

    public void handleError(String status, HandleCallback callback) {
        mHandleCallback = callback;
        Timber.d("HANDLING");
        switch (status) {
            case ConstantsManager.STATUS_ERROR:
                if (mNetworkStatusChecker.isNetworkAvailable()) {
                    if (MoneyTrackerApplication_.isGoogleTokenExist())
                        reLoginWithGoogle();
                    else reLogInWithLoft();
                }
                break;
        }
    }

    @Background
    void reLogInWithLoft() {
        mRestService.logIn(
                MoneyTrackerApplication_.getUserFullName(),
                MoneyTrackerApplication_.getUserPassword(),
                new Callback<UserLoginModel>() {
                    @Override
                    public void success(UserLoginModel userLoginModel, Response response) {
                        String status = userLoginModel.getStatus();
                        switch (status) {
                            case ConstantsManager.STATUS_SUCCESS:
                                MoneyTrackerApplication_.saveLoftApiToken(userLoginModel.getAuthToken());
                                mHandleCallback.onHandle();
                                break;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Timber.d(error.getLocalizedMessage());
                        mTriesCounter.reduceTry();
                        if (mTriesCounter.areTriesLeft()) {
                            reLogInWithLoft();
                        }
                    }
                });

    }


    @Background
    void reLoginWithGoogle() {

        String token = null;
        try {
            token = GoogleAuthUtil.getToken(mContext, MoneyTrackerApplication_.getUserEmail(),
                    ConstantsManager.SCOPES);

        } catch (UserRecoverableAuthException | IllegalArgumentException | IOException ioEx) {
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
                    mHandleCallback.onHandle();
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.d(error.getLocalizedMessage());
                    mTriesCounter.reduceTry();
                    if (mTriesCounter.areTriesLeft()) {
                        reLoginWithGoogle();
                    }
                }
            });
        }
    }

}
