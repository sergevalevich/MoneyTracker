package com.valevich.moneytracker.ui.taskshandlers;

import android.util.Log;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 21.06.2016.
 */
@EBean
public class UpdateCategoryTask {
    private static final String TAG = UpdateCategoryTask.class.getSimpleName();
    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    AddCategoryTask mAddCategoryTask;

    @Bean
    RestService mRestService;

    @Background
    public void updateCategory(String newName,int id) {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            AddedCategoryModel addedCategoryModel = mRestService.updateCategory(newName,id,getAuthToken(),getGoogleToken());
            String status = addedCategoryModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    updateDbIds(newName,addedCategoryModel.getData().getId());
                    break;
                case ConstantsManager.STATUS_WRONG_ID:
                    mAddCategoryTask.addCategory(newName);
                    break;
                default:
                    break;
            }
        }
        BusProvider.getInstance().post(new QueryFinishedEvent());
    }

    private void updateDbIds(String title, int id) {
        CategoryEntry category = CategoryEntry.getCategory(title);
        List<CategoryEntry> categoryEntries = new ArrayList<>(1);
        categoryEntries.add(category);
        List<CategoryEntry> categories = CategoryEntry.updateIds(categoryEntries,new int[] {id});
        for (CategoryEntry categoryEntry:categories) {
            Log.d(TAG, "updateDbIdsUPDATE: " + categoryEntry.getId());
        }
    }

    private String getAuthToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }
}
