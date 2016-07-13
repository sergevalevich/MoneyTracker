package com.valevich.moneytracker.ui.taskshandlers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 21.06.2016.
 */
@EBean
public class UpdateCategoryTask {

    @RootContext
    AppCompatActivity mActivity;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    OttoBus mEventBus;

    @Bean
    RestService mRestService;

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Background
    public void updateCategory(String newName,int id) {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            AddedCategoryModel addedCategoryModel = mRestService.updateCategory(
                    newName,
                    id,
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken());

            String status = addedCategoryModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    setServerId(newName, addedCategoryModel.getData().getId());
                    break;
                case ConstantsManager.STATUS_WRONG_ID:
                    break;
                default:
                    if (mApiErrorHandler.areTriesLeft()) {
                        mApiErrorHandler.handleError(status);
                        updateCategory(newName, id);
                    }
                    notifyAboutError();
                    break;
            }
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

    @UiThread
    void notifyAboutError() {
        Toast.makeText(mActivity,
                ConstantsManager.STATUS_ERROR,
                Toast.LENGTH_SHORT).show();
    }
}
