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

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.eventbus.events.SyncFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.CategoryData;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.Preferences_;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.errorHandlers.ApiErrorHandler;
import com.valevich.moneytracker.utils.ui.NotificationUtil;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private List<CategoryEntry> mCategoriesDb;

    private List<ExpenseEntry> mExpensesDb;

    private static boolean mIsSyncBeforeExit = false;

    private static Account mAccount;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Pref
    Preferences_ mPreferences;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    RestService mRestService;

    @Bean
    NotificationUtil mNotificationUtil;

    @Bean
    ApiErrorHandler mApiErrorHandler;

    @Bean
    OttoBus mEventBus;

    @Bean
    TriesCounter mApiErrorTriesCounter;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    public TrackerSyncAdapter(Context context) {
        super(context, true);
    }

    public void syncImmediately(Context context, boolean stopAfterSync) {
        mIsSyncBeforeExit = stopAfterSync;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                ConstantsManager.CONTENT_AUTHORITY, bundle);
    }

    public void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
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

    public void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Timber.d("SYNC STARTED");

        mCategoriesDb = CategoryEntry.getAllCategories("");
        mExpensesDb = ExpenseEntry.getAllExpenses("");

        syncCategories();

        /*
        we also sync categories without expenses to remove expenses from the server
        Because the server doesn't allow sync of empty categories list this is
        needed to perform sync if the user removed everything.
        So we send default category to the server to remove all the other categories.
        Everything will be fine if the network connection exists when the user removes
        all categories, because in this case delete category query will work.
        But if the user removes all data without internet, we need to perform this
        query when network connection is present or the user logs out.
        */

    }

    private Account getSyncAccount(Context context) {
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

    private void onAccountCreated(Account newAccount, Context context) {
        final int SYNC_INTERVAL = 60 * 60;
        final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                ConstantsManager.CONTENT_AUTHORITY, true);
        ContentResolver.addPeriodicSync(newAccount, ConstantsManager.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);
        syncImmediately(context, false);
    }

    private boolean areExpensesEmpty() {
        return mExpensesDb.size() == 0;
    }

    private boolean areCategoriesEmpty() {
        return mCategoriesDb.size() == 0;
    }

    private void sendUserNotification() {
        mNotificationUtil.updateNotification();
    }

    private void notifySyncFinished() {
        if (mIsSyncBeforeExit) MoneyTrackerApplication_.setIsSyncFinished(true);
        mEventBus.post(new SyncFinishedEvent(mIsSyncBeforeExit));
    }

    private void notifyAboutNetworkError(String message) {
        if (mIsSyncBeforeExit) {
            MoneyTrackerApplication_.setIsNetworkError(true);
            MoneyTrackerApplication_.setErrorMessage(message);
        }
        mEventBus.post(new NetworkErrorEvent(message));
    }

    public void disableSync() {
        ContentResolver.setIsSyncable(mAccount, ConstantsManager.CONTENT_AUTHORITY, 0);
        ContentResolver.cancelSync(mAccount, ConstantsManager.CONTENT_AUTHORITY);
        mIsSyncBeforeExit = false;
    }

    private void syncCategories() {

        String categoriesJsonString = areCategoriesEmpty()
                ? getCategoriesString(getPreparedDefaultCategory())
                : getCategoriesString(getPreparedCategories());

        if (mNetworkStatusChecker.isNetworkAvailable()) {
            if (MoneyTrackerApplication_.isGoogleTokenExist() || MoneyTrackerApplication_.isLoftTokenExist()) {
                mRestService.syncCategories(
                        categoriesJsonString,
                        MoneyTrackerApplication_.getLoftApiToken(),
                        MoneyTrackerApplication_.getGoogleToken(),
                        new Callback<CategoriesSyncModel>() {
                            @Override
                            public void success(CategoriesSyncModel apiCategories, Response response) {

                                mNetworkErrorTriesCounter.resetTries();
                                String status = apiCategories.getStatus();

                                switch (status) {
                                    case ConstantsManager.STATUS_SUCCESS:
                                        mApiErrorTriesCounter.resetTries();
                                        setServerIds(apiCategories);
                                        break;
                                    default:
                                        mApiErrorTriesCounter.reduceTry();
                                        if (mApiErrorTriesCounter.areTriesLeft()) {
                                            mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                                @Override
                                                public void onHandle() {
                                                    syncCategories();
                                                }
                                            });
                                        } else {
                                            notifyAboutNetworkError(ConstantsManager.STATUS_ERROR);
                                        }
                                        break;
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Timber.d(error.getLocalizedMessage());
                                mNetworkErrorTriesCounter.reduceTry();
                                if (mNetworkErrorTriesCounter.areTriesLeft()) {
                                    syncCategories();
                                } else {
                                    notifyAboutNetworkError(error.getLocalizedMessage());
                                }
                            }
                        });
            } else {
                disableSync();
            }
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }

    }

    private void syncExpenses() {

        List<ExpenseData> expenses = getPreparedExpenses();
        String expensesString = getExpensesString(expenses);

        if (mNetworkStatusChecker.isNetworkAvailable()) {
            if (MoneyTrackerApplication_.isGoogleTokenExist() || MoneyTrackerApplication_.isLoftTokenExist()) {
                mRestService.syncExpenses(
                        expensesString,
                        MoneyTrackerApplication_.getLoftApiToken(),
                        MoneyTrackerApplication_.getGoogleToken(),
                        new Callback<ExpensesSyncModel>() {
                            @Override
                            public void success(ExpensesSyncModel apiExpenses, Response response) {

                                String status = apiExpenses.getStatus();

                                switch (status) {
                                    case ConstantsManager.STATUS_SUCCESS:
                                        notifySyncFinished();
                                        sendUserNotification();
                                        break;
                                    default:
                                        mApiErrorTriesCounter.reduceTry();
                                        if (mApiErrorTriesCounter.areTriesLeft()) {
                                            mApiErrorHandler.handleError(status, new ApiErrorHandler.HandleCallback() {
                                                @Override
                                                public void onHandle() {
                                                    syncExpenses();
                                                }
                                            });
                                        } else {
                                            notifyAboutNetworkError(ConstantsManager.STATUS_ERROR);
                                        }
                                        break;
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Timber.d(error.getLocalizedMessage());
                                mNetworkErrorTriesCounter.reduceTry();
                                if (mNetworkErrorTriesCounter.areTriesLeft()) {
                                    syncExpenses();
                                } else {
                                    notifyAboutNetworkError(error.getLocalizedMessage());
                                }
                            }
                        });
            } else {
                disableSync();
            }
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    private void setServerIds(CategoriesSyncModel apiCategories) {

        List<CategoryData> categoryData = apiCategories.getData();
        String firstCategoryName = categoryData.get(0).getTitle();

        if (!firstCategoryName.equals(CategoryEntry.DEFAULT_CATEGORY_NAME)) {
            //not updating db if we get default category
            for (int i = 0; i < mCategoriesDb.size(); i++) {

                int categoryId = categoryData.get(i).getId();
                CategoryEntry category = mCategoriesDb.get(i);
                category.setServerId(categoryId);

            }
            CategoryEntry.update(mCategoriesDb, new Transaction.Success() {
                @Override
                public void onSuccess(Transaction transaction) {
                    if (!areExpensesEmpty())
                        syncExpenses();
                    else {
                        notifySyncFinished();
                        sendUserNotification();
                    }
                }
            }, null);
        } else {
            notifySyncFinished();
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

                expenseToSync.setCategoryId(category.getServerId());
                expenseToSync.setComment(expenseDb.getDescription());
                expenseToSync.setId((int) expenseDb.getId());
                expenseToSync.setSum(Double.valueOf(expenseDb.getPrice()));
                expenseToSync.setDate(expenseDb.getDate());

                expensesToSync.add(expenseToSync);
            }
        }
        return expensesToSync;
    }

}