package com.valevich.moneytracker.ui.taskshandlers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.RemovedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.List;

/**
 * Created by User on 19.06.2016.
 */
@EBean
public class RemoveCategoriesTask {

    @RootContext
    AppCompatActivity mActivity;

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Background
    public void removeCategories(List<Integer> categoriesIds) {
        for (int id: categoriesIds) {
            removeCategory(id);
        }
    }

    private void removeCategory(int id) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            RemovedCategoryModel removedCategoryModel = mRestService.removeCategory(
                    id,
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken());

            String status = removedCategoryModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                case ConstantsManager.STATUS_WRONG_ID:
                    break;
                default:
                    if (mApiErrorHandler.areTriesLeft()) {
                        mApiErrorHandler.handleError(status);
                        removeCategory(id);
                    }
                    notifyAboutError();
                    break;
            }
        }
    }

    @UiThread
    void notifyAboutError() {
        Toast.makeText(mActivity,
                ConstantsManager.STATUS_ERROR,
                Toast.LENGTH_SHORT).show();
    }

}
