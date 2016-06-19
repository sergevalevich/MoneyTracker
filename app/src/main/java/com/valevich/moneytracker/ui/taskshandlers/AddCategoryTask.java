package com.valevich.moneytracker.ui.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by User on 20.06.2016.
 */
@EBean
public class AddCategoryTask {
    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Background
    public void addCategory(String title) {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.addCategory(title,getAuthToken(),getGoogleToken());
        }
        BusProvider.getInstance().post(new QueryFinishedEvent());
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

    private String getAuthToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }
}
