package com.valevich.moneytracker.ui.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

/**
 * Created by User on 19.06.2016.
 */
@EBean
public class RemoveCategoriesTask {
    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Background
    public void removeCategories(List<Integer> categoriesIds) {
        for (int id: categoriesIds) {
            removeCategory(id);
        }
        BusProvider.getInstance().post(new QueryFinishedEvent());
    }

    private void removeCategory(int id) {
        if(mNetworkStatusChecker.isNetworkAvailable())
            mRestService.removeCategory(id,getAuthToken(),getGoogleToken());
    }

    private String getAuthToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }
}
