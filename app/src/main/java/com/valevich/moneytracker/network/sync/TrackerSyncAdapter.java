package com.valevich.moneytracker.network.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.gson.Gson;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.eventbus.events.QueryStartedEvent;
import com.valevich.moneytracker.eventbus.events.SyncBeforeExitFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.CategoryData;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
import com.valevich.moneytracker.ui.activities.LoginActivity;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.NotificationUtil;
import com.valevich.moneytracker.utils.Preferences_;


import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@EBean
public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = TrackerSyncAdapter.class.getSimpleName();

    private List<CategoryEntry> mCategoriesDb;

    private List<ExpenseEntry> mExpensesDb;

    private int[] mNewCategoryIds;

    private static boolean mStopAfterSync = false;

    private static Account mAccount;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    RestService mRestService;

    @Bean
    NotificationUtil mNotificationUtil;

    @Pref
    Preferences_ mPreferences;

    public TrackerSyncAdapter(Context context) {
        super(context, true);
    }

    public static void syncImmediately(Context context, boolean stopAfterSync) {
        mStopAfterSync = stopAfterSync;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                ConstantsManager.CONTENT_AUTHORITY, bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = ConstantsManager.CONTENT_AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "SYNC STARTED");
        notifyQueryStarted();

        mCategoriesDb = CategoryEntry.getAllCategories("");
        mExpensesDb = ExpenseEntry.getAllExpenses("");

        try {
            syncCategories();
        } catch (Exception e) {
            handleExceptions(e);
        } finally {
            notifyQueryFinished();
            if (mStopAfterSync) {
                disableSync();
                notifySyncBeforeExitFinished();
                mStopAfterSync = false;
            }
        }

        //sync categories without expenses to remove expenses from the server
        /*
        Because the server doesn't allow sync of empty categories list this is
        needed to perform sync if the user removed everything.
        So we send default category to the server to remove all the other categories
        Everything will be fine if the network connection exists when the user removes
        all categories, because in this case delete category query will work.
        But if the user removes all data without internet, we need to perform this
        query when network connection is present or the user logs out.
        */

    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        mAccount = new Account(context.getString(R.string.app_name),
                ConstantsManager.SYNC_ACCOUNT_TYPE);
        ContentResolver.setIsSyncable(mAccount, ConstantsManager.CONTENT_AUTHORITY, 1);
        if (null == accountManager.getPassword(mAccount)) {
            if (!accountManager.addAccountExplicitly(mAccount, "", null)) {
                return null;
            }
            onAccountCreated(mAccount, context);
        }
        return mAccount;
    }

    private void handleExceptions(Exception e) {
        if (e instanceof SocketTimeoutException) {
            int count = mPreferences.exceptionsCount().get();
            mPreferences.exceptionsCount().put(++count);
        } else {
            e.printStackTrace();
        }
    }

    private boolean areExpensesEmpty() {
        return mExpensesDb.size() == 0;
    }

    private boolean areCategoriesEmpty() {
        return mCategoriesDb.size() == 0;
    }

    private void notifyQueryStarted() {
        BusProvider.getInstance().post(new QueryStartedEvent());
    }

    private void notifyQueryFinished() {
        BusProvider.getInstance().post(new QueryFinishedEvent());
    }

    private void sendUserNotification() {
        mNotificationUtil.updateNotification();
    }

    private void notifySyncBeforeExitFinished() {
        BusProvider.getInstance().post(new SyncBeforeExitFinishedEvent());
    }

    private void disableSync() {
        ContentResolver.setIsSyncable(mAccount, ConstantsManager.CONTENT_AUTHORITY, 0);
        ContentResolver.cancelSync(mAccount, ConstantsManager.CONTENT_AUTHORITY);
    }

    private void syncCategories() {

        String categoriesJsonString = areCategoriesEmpty()
                ? getCategoriesString(getPreparedDefaultCategory())
                : getCategoriesString(getPreparedCategories());

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        if (mNetworkStatusChecker.isNetworkAvailable()) {
            CategoriesSyncModel apiCategories = mRestService
                    .syncCategories(categoriesJsonString, loftToken, googleToken);

            String status = apiCategories.getStatus();

            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    setNewCategoryIds(apiCategories);
                    if(!areExpensesEmpty()) {
                        syncExpenses();
                    }
                    break;
            }
        }

    }

    private void setNewCategoryIds(CategoriesSyncModel apiCategories) {

        List<CategoryData> categoryData = apiCategories.getData();
        String firstCategoryName = categoryData.get(0).getTitle();

        if(!firstCategoryName.equals(CategoryEntry.DEFAULT_CATEGORY_NAME)) {//not updating db if we get default category

            int dataSize = categoryData.size();
            mNewCategoryIds = new int[dataSize];

            for (int i = 0; i < dataSize; i++) {

                int categoryId = categoryData.get(i).getId();
                mNewCategoryIds[i] = categoryId;

            }
            // FIXME: 16.06.2016 не доставать категории. Достаю их, чтобы проверить обновление id
            List<CategoryEntry> categoryEntries = CategoryEntry.updateIds(mCategoriesDb, mNewCategoryIds);
            for (CategoryEntry category : categoryEntries) {
                Log.d(TAG, String.format(Locale.getDefault(), "%s = %d %n", category.getName(), category.getId()));
            }
        }
    }

    @NonNull
    private String getCategoriesString(List<CategoryData> categoriesToSync) {
        Gson gson = new Gson();

        return gson.toJson(categoriesToSync);
    }

    private List<CategoryData> getPreparedCategories() {

        List<CategoryData> categoriesToSync = new ArrayList<>();

        for (int i = 0; i < mCategoriesDb.size(); i++) {
            CategoryData categoryToSync = new CategoryData();
            CategoryEntry categoryDb = mCategoriesDb.get(i);


            categoryToSync.setId(0);
            categoryToSync.setTitle(categoryDb.getName());

            categoriesToSync.add(categoryToSync);
        }
        return categoriesToSync;
    }

    private List<CategoryData> getPreparedDefaultCategory() {
        List<CategoryData> categoriesToSync = new ArrayList<>();
        CategoryData categoryToSync = new CategoryData();
        categoryToSync.setId(0);
        categoryToSync.setTitle(CategoryEntry.DEFAULT_CATEGORY_NAME);
        categoriesToSync.add(categoryToSync);
        return categoriesToSync;
    }

    private void syncExpenses() {

        List<ExpenseData> expenses = getPreparedExpenses();
        String expensesString = getExpensesString(expenses);

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        if (mNetworkStatusChecker.isNetworkAvailable()) {
            ExpensesSyncModel expensesSyncModel = mRestService
                    .syncExpenses(expensesString, loftToken, googleToken);
            String status = expensesSyncModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    if (!mStopAfterSync) sendUserNotification();
                    break;
            }
        }
    }

    @NonNull
    private String getExpensesString(List<ExpenseData> expensesToSync) {

        Gson gson = new Gson();

        return gson.toJson(expensesToSync);
    }

    @NonNull
    private List<ExpenseData> getPreparedExpenses() {

        List<ExpenseData> expensesToSync = new ArrayList<>();

        for (int i = 0; i < mCategoriesDb.size(); i++) {
            CategoryEntry category = mCategoriesDb.get(i);
            for (ExpenseEntry expenseDb : category.getExpenses()) {
                ExpenseData expenseToSync = new ExpenseData();

                expenseToSync.setCategoryId(mNewCategoryIds[i]);
                expenseToSync.setComment(expenseDb.getDescription());
                expenseToSync.setId((int) expenseDb.getId());
                expenseToSync.setSum(Double.valueOf(expenseDb.getPrice()));
                expenseToSync.setTrDate(expenseDb.getDate());

                expensesToSync.add(expenseToSync);
            }
        }
        return expensesToSync;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        final int SYNC_INTERVAL = 60*60;
        final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
        TrackerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                ConstantsManager.CONTENT_AUTHORITY, true);
        ContentResolver.addPeriodicSync(newAccount, ConstantsManager.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);
        syncImmediately(context, false);
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }
}