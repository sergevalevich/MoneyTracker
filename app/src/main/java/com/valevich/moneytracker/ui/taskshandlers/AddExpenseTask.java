package com.valevich.moneytracker.ui.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by User on 18.06.2016.
 */
@EBean
public class AddExpenseTask {

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Background
    public void addExpense(double sum, String comment, int categoryId,
                           String trDate) {
        if (mNetworkStatusChecker.isNetworkAvailable())
            mRestService.addExpense(sum, comment, categoryId, trDate, getLoftToken(), getGoogleToken());
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

}
