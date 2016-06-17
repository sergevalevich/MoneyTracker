package com.valevich.moneytracker.ui.taskshandlers;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry_Table;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;
import com.valevich.moneytracker.ui.activities.LoginActivity_;
import com.valevich.moneytracker.ui.activities.MainActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.UserNotifier;

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
    UserNotifier mUserNotifier;

    @StringRes(R.string.logout_error_message)
    String mLogoutErrorMessage;

    @Background
    public void logout() {
        UserLogoutModel userLogoutModel = mRestService.logout();
        String status = userLogoutModel.getStatus();

        switch (status) {
            case ConstantsManager.STATUS_SUCCESS:
                clearUserData();
                navigateToLogIn();
                break;
            case ConstantsManager.STATUS_EMPTY:
                clearUserData();
                navigateToLogIn();
                break;
            default:
                notifyUser(mLogoutErrorMessage);
                break;
        }
    }

    @UiThread
    void notifyUser(String message) {
        mUserNotifier.notifyUser(mActivity.findViewById(R.id.drawer_layout),message);
    }

    private void clearUserData() {
        stopSync();
        clearDatabase();
        MoneyTrackerApplication_.saveUserInfo("","","","");
        MoneyTrackerApplication_.saveGoogleToken("");
        MoneyTrackerApplication_.saveLoftApiToken("");
    }

    private void stopSync() {
        TrackerSyncAdapter.disableSync();
    }

    private void clearDatabase() {
        Delete.tables(ExpenseEntry.class, CategoryEntry.class);
    }

    private void navigateToLogIn() {//check back press after logout
        Intent intent = new Intent(mActivity,LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }
}
