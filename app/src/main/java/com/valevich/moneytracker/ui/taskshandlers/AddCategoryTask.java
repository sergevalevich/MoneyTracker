package com.valevich.moneytracker.ui.taskshandlers;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by User on 20.06.2016.
 */
@EBean
public class AddCategoryTask {

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

    @Background
    public void addCategory(String title) {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            AddedCategoryModel addedCategoryModel = mRestService.addCategory(title,getAuthToken(),getGoogleToken());
            String status = addedCategoryModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    updateDbIds(title,addedCategoryModel.getData().getId());
                    break;
                default:
                    break;
            }
        }
    }

    private void updateDbIds(String title, int id) {
        CategoryEntry category = CategoryEntry.getCategory(title);
        List<CategoryEntry> categoryEntries = new ArrayList<>(1);
        categoryEntries.add(category);
        List<CategoryEntry> categories = CategoryEntry.updateIds(categoryEntries,new int[] {id});
        for (CategoryEntry categoryEntry:categories) {
            Timber.d("updateDbIdsCREATE: %d", categoryEntry.getId());
        }
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

    private String getAuthToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }
}
