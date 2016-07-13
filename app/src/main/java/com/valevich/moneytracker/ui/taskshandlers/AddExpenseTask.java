package com.valevich.moneytracker.ui.taskshandlers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by User on 18.06.2016.
 */
@EBean
public class AddExpenseTask {

    @RootContext
    AppCompatActivity mActivity;

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
                                        notifyUser(ConstantsManager.STATUS_ERROR);
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
                                notifyUser(error.getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    @UiThread
    void notifyUser(String message) {
        Toast.makeText(mActivity,
                message,
                Toast.LENGTH_SHORT).show();
    }

}
