package com.valevich.moneytracker.ui.taskshandlers;

import android.content.Intent;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.SyncFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;
import com.valevich.moneytracker.ui.activities.LoginActivity_;
import com.valevich.moneytracker.ui.activities.MainActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.AfterInject;
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

    @UiThread
    void notifyUser(String message) {
        mUserNotifier.notifyUser(mActivity.findViewById(R.id.drawer_layout),message);
    }

    public void requestSync() {
        TrackerSyncAdapter.syncImmediately(mActivity,true);
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

    public void onSyncFinished() {
        logOut();
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

    private void navigateToLogIn() {//check back press after logout
        Intent intent = new Intent(mActivity,LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }
}
