package com.valevich.moneytracker.ui.taskshandlers;

import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;
import com.valevich.moneytracker.ui.activities.LoginActivity_;
import com.valevich.moneytracker.ui.activities.MainActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by User on 16.06.2016.
 */
@EBean
public class LogoutTask {

    @RootContext
    MainActivity mActivity;

    @Bean
    RestService mRestService;

    @Bean
    TrackerSyncAdapter mTrackerSyncAdapter;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @StringRes(R.string.logout_error_message)
    String mLogoutErrorMessage;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @UiThread
    void notifyUser(String message) {
        Snackbar.make(mActivity.getRootView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Background
    public void requestSync() {
        if (mNetworkStatusChecker.isNetworkAvailable())
            mTrackerSyncAdapter.syncImmediately(mActivity, true);
        else notifyUser(mNetworkUnavailableMessage);
    }

    @Background
    void logOut() {
        UserLogoutModel userLogoutModel = mRestService.logout();
        String status = userLogoutModel.getStatus();

        switch (status) {
            case ConstantsManager.STATUS_SUCCESS:
                clearUserData();
                break;
            case ConstantsManager.STATUS_EMPTY:
                clearUserData();
                break;
            default:
                notifyUser(mLogoutErrorMessage);
                break;
        }
    }

    public void onSyncFinished(boolean isSuccessful) {
        if (isSuccessful) {
            mTrackerSyncAdapter.disableSync();
            if (mNetworkStatusChecker.isNetworkAvailable())
                logOut();
        } else {
            notifyUser(mLogoutErrorMessage);
        }
    }

    private void clearUserData() {
        clearDatabase();
        MoneyTrackerApplication_.saveUserInfo("","","","");
        MoneyTrackerApplication_.saveGoogleToken("");
        MoneyTrackerApplication_.saveLoftApiToken("");
        navigateToLogIn();
    }

    private void clearDatabase() {
        Delete.tables(ExpenseEntry.class, CategoryEntry.class);
    }

    private void navigateToLogIn() {
        Intent intent = new Intent(mActivity,LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }
}
