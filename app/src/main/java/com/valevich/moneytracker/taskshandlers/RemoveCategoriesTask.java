package com.valevich.moneytracker.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.RemovedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class RemoveCategoriesTask {

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Bean
    TriesCounter mApiErrorTriesCounter;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    @Bean
    OttoBus mEventBus;

    public void removeCategories(List<Integer> categoriesIds) {
        mApiErrorTriesCounter.setTriesCount(2);
        mNetworkErrorTriesCounter.setTriesCount(2);
        for (int id : categoriesIds) {
            removeCategory(id);
        }
    }

    @Background
    void removeCategory(final int id) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.removeCategory(
                    id,
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken(),
                    new Callback<RemovedCategoryModel>() {
                        @Override
                        public void success(RemovedCategoryModel removedCategoryModel, Response response) {

                            mNetworkErrorTriesCounter.resetTries();

                            String status = removedCategoryModel.getStatus();
                            switch (status) {
                                case ConstantsManager.STATUS_SUCCESS:
                                case ConstantsManager.STATUS_WRONG_ID:
                                    mApiErrorTriesCounter.resetTries();
                                    break;
                                default:
                                    mApiErrorTriesCounter.reduceTry();
                                    if (mApiErrorTriesCounter.areTriesLeft()) {
                                        mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                            @Override
                                            public void onHandle() {
                                                removeCategory(id);
                                            }
                                        });
                                    } else {
                                        notifyAboutNetworkError(ConstantsManager.STATUS_ERROR);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Timber.d(error.getLocalizedMessage());
                            mNetworkErrorTriesCounter.reduceTry();
                            if (mNetworkErrorTriesCounter.areTriesLeft()) {
                                removeCategory(id);
                            } else {
                                notifyAboutNetworkError(error.getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    private void notifyAboutNetworkError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }

}
