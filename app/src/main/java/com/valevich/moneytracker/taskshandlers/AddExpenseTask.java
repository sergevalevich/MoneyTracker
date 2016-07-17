package com.valevich.moneytracker.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class AddExpenseTask {

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
    public void addExpense(final double sum, final String comment, final int categoryId,
                           final String trDate) {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.addExpense(
                    sum,
                    comment,
                    categoryId,
                    trDate,
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken(),
                    new Callback<AddedExpenseModel>() {
                        @Override
                        public void success(AddedExpenseModel expenseModel, Response response) {
                            String status = expenseModel.getStatus();
                            switch (status) {
                                case ConstantsManager.STATUS_SUCCESS:
                                    break;
                                default:
                                    mApiErrorTriesCounter.reduceTry();
                                    if (mApiErrorTriesCounter.areTriesLeft()) {
                                        mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                            @Override
                                            public void onHandle() {
                                                addExpense(sum, comment, categoryId, trDate);
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
                                addExpense(sum, comment, categoryId, trDate);
                            } else {
                                notifyAboutError(error.getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    private void notifyAboutError(String message) {
        mEventBus.post(new NetworkErrorEvent(message));
    }

}
