package com.valevich.moneytracker.ui.taskshandlers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by User on 16.06.2016.
 */
@EBean
public class FetchUserDataTask implements Transaction.Error, Transaction.Success {
    private static final String TAG = FetchUserDataTask.class.getSimpleName();//restartLoaderWhenComplete
    @RootContext
    Activity mActivity;

    @Bean
    RestService mRestService;

    @Bean
    UserNotifier mUserNotifier;

    List<GlobalCategoriesDataModel> mGlobalCategoriesData;

    @Background
    public void fetchUserData() {
        Crashlytics.log("FetchUserData");
        mGlobalCategoriesData = mRestService
                .fetchGlobalCategoriesData(getLoftToken(),getGoogleToken());
        if(mGlobalCategoriesData != null) {
            Crashlytics.log(mGlobalCategoriesData.toString());
            saveData(mGlobalCategoriesData);
        }
        else {
            Crashlytics.log("fetched data is null");
            notifyLoginFinished();
        }
    }

    private void saveData(List<GlobalCategoriesDataModel> globalCategoriesData) {
        Crashlytics.log("saveData");
        Crashlytics.log(globalCategoriesData.toString());
        CategoryEntry.saveCategories(globalCategoriesData,this,this);
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        Crashlytics.log("ERROR SAVING DATA");
        notifyLoginFinished();
    }

    @Override
    public void onSuccess(Transaction transaction) {
        Log.d(TAG,"DATA SAVED SUCCESSFULLY");
        notifyLoginFinished();
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(mActivity,MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }

    private void notifyLoginFinished() {
        Log.d(TAG, "notifyLoginFinished: ");
        BusProvider.getInstance().post(new LoginFinishedEvent());
    }

}
