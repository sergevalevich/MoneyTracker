package com.valevich.moneytracker.ui.taskshandlers;

import android.support.design.widget.Snackbar;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by NotePad.by on 28.05.2016.
 */
@EBean
public class SignUpWithGoogleTask {

    @RootContext
    LoginActivity mLoginActivity;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Bean
    RestService mRestService;

    @Bean
    FetchUserDataTask mFetchUserDataTask;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

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
                UserGoogleInfoModel userGoogleInfoModel = mRestService.getGoogleInfo(token);
                MoneyTrackerApplication_.saveUserInfo(
                        userGoogleInfoModel.getName(),
                        userGoogleInfoModel.getEmail(),
                        userGoogleInfoModel.getPicture(),
                        "");
                fetchUserData();
            } else {
                notifyLoginFinished();
            }
        } else {
            notifyLoginFinished();
            notifyUser(mNetworkUnavailableMessage);
        }
    }

    @UiThread
    void notifyUser(String message) {
        Snackbar.make(mLoginActivity.getRootView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void fetchUserData() {
        mFetchUserDataTask.fetchUserData();
    }

    private void notifyLoginFinished() {
        mEventBus.post(new LoginFinishedEvent());
    }

}
