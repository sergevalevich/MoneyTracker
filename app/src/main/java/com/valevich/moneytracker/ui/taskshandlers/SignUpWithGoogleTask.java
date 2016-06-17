package com.valevich.moneytracker.ui.taskshandlers;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.Preferences_;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

/**
 * Created by NotePad.by on 28.05.2016.
 */
@EBean
public class SignUpWithGoogleTask {
    @RootContext
    LoginActivity mLoginActivity;

    @Bean
    RestService mRestService;

    @Bean
    FetchUserDataTask mFetchUserDataTask;

    @Background
    public void logInWithGoogle(final String accountName) {
        String token = null;
        try {
            token = GoogleAuthUtil.getToken(mLoginActivity, accountName,
                    ConstantsManager.SCOPES);

        } catch(UserRecoverableAuthException userAuthEx){
            mLoginActivity.startActivityForResult(userAuthEx.getIntent(), LoginActivity.REQUEST_CODE);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (GoogleAuthException fatalAuthEx) {
            fatalAuthEx.printStackTrace();
            Log.e(LoginActivity.TAG, "Fatal Exception " + fatalAuthEx.getLocalizedMessage());
        }
        //Log.d(LoginActivity.TAG,token);
        if(token != null) {
            MoneyTrackerApplication_.saveGoogleToken(token);
            UserGoogleInfoModel userGoogleInfoModel = mRestService.getGoogleInfo(token);
            MoneyTrackerApplication_.saveUserInfo(
                    userGoogleInfoModel.getName(),
                    userGoogleInfoModel.getEmail(),
                    userGoogleInfoModel.getPicture(),
                    "");
            fetchUserData();
        }
    }

    private void fetchUserData() {
        mFetchUserDataTask.fetchUserData();
    }

}
