package com.valevich.moneytracker.taskshandlers;

import android.support.v7.app.AppCompatActivity;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
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
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class UpdateCategoryTask {

    @RootContext
    AppCompatActivity mActivity;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    RestService mRestService;

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Bean
    TriesCounter mApiErrorTriesCounter;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    @Bean
    OttoBus mEventBus;

    @Background
    public void updateCategory(final String newName, final int id) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.updateCategory(
                    newName,
                    id,
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken(),
                    new Callback<AddedCategoryModel>() {
                        @Override
                        public void success(AddedCategoryModel addedCategoryModel, Response response) {
                            String status = addedCategoryModel.getStatus();
                            switch (status) {
                                case ConstantsManager.STATUS_SUCCESS:
                                    setServerId(newName, addedCategoryModel.getData().getId());
                                    break;
                                case ConstantsManager.STATUS_WRONG_ID:
                                    break;
                                default:
                                    mApiErrorTriesCounter.reduceTry();
                                    if (mApiErrorTriesCounter.areTriesLeft()) {
                                        mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                            @Override
                                            public void onHandle() {
                                                updateCategory(newName, id);
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
                                updateCategory(newName, id);
                            } else {
                                notifyAboutNetworkError(error.getLocalizedMessage());
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
            CategoryEntry.update(categoriesToProcess, null, null);
        }
    }

    private void notifyAboutNetworkError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }

}
