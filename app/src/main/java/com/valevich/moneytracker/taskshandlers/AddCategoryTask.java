package com.valevich.moneytracker.taskshandlers;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.CategorySubmittedEvent;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class AddCategoryTask {

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

    @Background
    public void addCategory(final String title) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.addCategory(
                    title
                    , MoneyTrackerApplication_.getLoftApiToken()
                    , MoneyTrackerApplication_.getGoogleToken(),
                    new Callback<AddedCategoryModel>() {
                        @Override
                        public void success(AddedCategoryModel addedCategoryModel, Response response) {
                            Timber.d("Response status %s", response.getStatus());
                            String status = addedCategoryModel.getStatus();
                            switch (status) {
                                case ConstantsManager.STATUS_SUCCESS:
                                    setServerId(title, addedCategoryModel.getData().getId());
                                    break;
                                default:
                                    mApiErrorTriesCounter.reduceTry();
                                    if (mApiErrorTriesCounter.areTriesLeft()) {
                                        mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                            @Override
                                            public void onHandle() {
                                                addCategory(title);
                                            }
                                        });
                                    } else {
                                        notifyAboutError(ConstantsManager.STATUS_ERROR);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Timber.d(error.getLocalizedMessage());
                            mNetworkErrorTriesCounter.reduceTry();
                            if (mNetworkErrorTriesCounter.areTriesLeft()) {
                                addCategory(title);
                            } else {
                                notifyAboutError(error.getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    private void setServerId(String title, int id) {
        CategoryEntry category = CategoryEntry.getCategory(title);
        if (category != null) {
            List<CategoryEntry> categoriesToProcess = new ArrayList<>(1);
            category.setServerId(id);
            categoriesToProcess.add(category);
            CategoryEntry.update(categoriesToProcess, new Transaction.Success() {
                @Override
                public void onSuccess(Transaction transaction) {
                    notifyCategorySubmitted();
                }
            }, null);
        }
    }

    private void notifyAboutError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }

    private void notifyCategorySubmitted() {
        mEventBus.post(new CategorySubmittedEvent());
    }

}
