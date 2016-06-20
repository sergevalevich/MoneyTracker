package com.valevich.moneytracker.ui.taskshandlers;

import android.app.Activity;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by User on 18.06.2016.
 */
@EBean
public class AddExpenseTask {

    @Bean
    RestService mRestService;

    @Background
    public void addExpense(double sum, String comment, int categoryId,
                           String trDate) {
        mRestService.addExpense(sum,comment,categoryId,trDate,getLoftToken(),getGoogleToken());
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

}
