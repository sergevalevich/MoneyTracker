package com.valevich.moneytracker.ui.taskshandlers;

import android.app.Activity;
import android.util.Log;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;
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
        mGlobalCategoriesData = mRestService
                .fetchGlobalCategoriesData(getLoftToken(),getGoogleToken());
        if(mGlobalCategoriesData != null)
            saveData(mGlobalCategoriesData);
    }

    private void saveData(List<GlobalCategoriesDataModel> globalCategoriesData) {
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
        Log.d(TAG,"ERROR SAVING DATA");
    }

    @Override
    public void onSuccess(Transaction transaction) {
        Log.d(TAG,"DATA SAVED SUCCESSFULLY");
    }
}
