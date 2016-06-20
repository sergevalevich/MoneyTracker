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
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.ThreadEnforcer;
import com.valevich.moneytracker.MoneyTrackerApplication;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.eventbus.events.QueryStartedEvent;
import com.valevich.moneytracker.eventbus.events.SyncFinishedEvent;
import com.valevich.moneytracker.network.rest.RestClient;
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


import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = TrackerSyncAdapter.class.getSimpleName();

    private RestService mRestService;

    private int[] mNewCategoryIds;

    private List<CategoryEntry> mCategoriesDb;

    private boolean mIsSyncStopped = false;

    private static boolean mStopAfterSync = false;

    private static Account mAccount;

    private NetworkStatusChecker mNetworkStatusChecker;

    public TrackerSyncAdapter(Context context) {
        super(context,true);
        mRestService = new RestService();
        mRestService.setRestClient(new RestClient());
        mNetworkStatusChecker = new NetworkStatusChecker();
        mNetworkStatusChecker.setConnectivityManager(getContext());
    }

    public static void syncImmediately(Context context,boolean stopAfterSync) {
        mStopAfterSync = stopAfterSync;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                ConstantsManager.CONTENT_AUTHORITY, bundle);
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        mAccount = new Account(context.getString(R.string.app_name),
                ConstantsManager.SYNC_ACCOUNT_TYPE);
        ContentResolver.setIsSyncable(mAccount,ConstantsManager.CONTENT_AUTHORITY,1);
        if ( null == accountManager.getPassword(mAccount) ) {
            if (!accountManager.addAccountExplicitly(mAccount, "", null)) {
                return null;
            }
            onAccountCreated(mAccount, context);
        }
        return mAccount;
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


        Log.d(TAG,"SYNC");

        if(!mIsSyncStopped) {

            notifyQueryStarted();

            if (!areExpensesEmpty()) {

                syncCategories(getCategoriesString(getPreparedCategories()));

                if (newIdsReceived()) {
                    syncExpenses(getExpensesString(getPreparedExpenses()));
                }
            } else {
                if(!areCategoriesEmpty()) {
                    //sync categories without expenses to remove expenses from the server
                    syncCategories(getCategoriesString(getPreparedCategories()));
                    updateDbEntriesIds();

                } else {
                    /*
                     Because the server doesn't allow sync of empty categories list this is
                     needed to perform sync if the user removed everything.
                     So we send default category to the server to remove all the other categories
                     Everything will be fine if the network connection exists when the user removes
                     all categories, because in this case delete category query will work.
                     But if the user removes all data without internet, we need to perform this
                     query when network connection is present or the user logs out.
                     */
                    syncCategories(getDefaultCategoryString());

                }
            }
            notifyQueryFinished();
        }

        if(mStopAfterSync) {
            disableSync();
            BusProvider.getInstance().post(new SyncFinishedEvent());
            mStopAfterSync = false;
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        mIsSyncStopped = true;
    }

    private boolean areExpensesEmpty() {
        return ExpenseEntry.getAllExpenses("").size() == 0;
    }

    private boolean areCategoriesEmpty() {
        return CategoryEntry.getAllCategories("").size() == 0;
    }

    private boolean newIdsReceived() {
        return mNewCategoryIds != null && mNewCategoryIds.length != 0;
    }

    private void notifyQueryStarted() {
        BusProvider.getInstance().post(new QueryStartedEvent());
    }

    private void notifyQueryFinished() {
        BusProvider.getInstance().post(new QueryFinishedEvent());
    }

    private void disableSync() {
        ContentResolver.setIsSyncable(mAccount, ConstantsManager.CONTENT_AUTHORITY, 0);
        ContentResolver.cancelSync(mAccount,ConstantsManager.CONTENT_AUTHORITY);
    }

    private void syncCategories(String categoriesString) {

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        if(mNetworkStatusChecker.isNetworkAvailable()) {
            CategoriesSyncModel apiCategories = mRestService
                    .syncCategories(categoriesString, loftToken, googleToken);

            String status = apiCategories.getStatus();

            switch (status) {
                case ConstantsManager.STATUS_SUCCESS:
                    setNewCategoryIds(apiCategories);
                    break;
                default:
                    reLogInAndTryAgain();
                    break;
            }
        }

    }

    private void reLogInAndTryAgain() {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            UserLogoutModel userLogoutModel = mRestService.logout();
            String status = userLogoutModel.getStatus();
            switch (status) {
                case ConstantsManager.STATUS_EMPTY:// TODO: 19.06.2016 Shorten
                    logIn();
                    break;
                case ConstantsManager.STATUS_SUCCESS:
                    logIn();
                    break;
                default:
                    break;
            }
        }
    }

    private void logIn() {
        if(mNetworkStatusChecker.isNetworkAvailable()) {
            if (MoneyTrackerApplication_.isGoogleTokenExist()) {
                loginWithGoogle();
            } else {
                regularLogin();
            }
        }
    }

    private void regularLogin() {
        UserLoginModel userLoginModel = mRestService.logIn(MoneyTrackerApplication_.getUserFullName(),
                MoneyTrackerApplication_.getUserPassword());
        String status = userLoginModel.getStatus();
        switch (status) {
            case ConstantsManager.STATUS_SUCCESS:
                syncImmediately(getContext(),false);
                break;
            default:
                break;
        }
    }

    private void loginWithGoogle() {
        String token = null;
        try {
            String accountName = MoneyTrackerApplication_.getUserEmail();
            token = GoogleAuthUtil.getToken(getContext(), accountName,
                    ConstantsManager.SCOPES);

        } catch(UserRecoverableAuthException | IOException userAuthEx){
            userAuthEx.printStackTrace();
        } catch (GoogleAuthException fatalAuthEx) {
            fatalAuthEx.printStackTrace();
            Log.e(LoginActivity.TAG, "Fatal Exception " + fatalAuthEx.getLocalizedMessage());
        }

        if(token != null) {
            MoneyTrackerApplication_.saveGoogleToken(token);
            UserGoogleInfoModel userGoogleInfoModel = mRestService.getGoogleInfo(token);
            MoneyTrackerApplication_.saveUserInfo(
                    userGoogleInfoModel.getName(),
                    userGoogleInfoModel.getEmail(),
                    userGoogleInfoModel.getPicture(),
                    "");
            syncImmediately(getContext(),false);
        }
    }

    private void setNewCategoryIds(CategoriesSyncModel apiCategories) {

        List<CategoryData> categoryData = apiCategories.getData();
        int dataSize = categoryData.size();
        mNewCategoryIds = new int[dataSize];

        for(int i = 0; i<dataSize; i++) {

            int categoryId = categoryData.get(i).getId();
            mNewCategoryIds[i] = categoryId;

        }
    }

    @NonNull
    private String getCategoriesString(List<CategoryData> categoriesToSync) {
        Gson gson = new Gson();

        return gson.toJson(categoriesToSync);
    }

    private List<CategoryData> getPreparedCategories() {

        mCategoriesDb = CategoryEntry.getAllCategories("");

        List<CategoryData> categoriesToSync = new ArrayList<>();

        for(int i = 0; i<mCategoriesDb.size(); i++) {
            CategoryData categoryToSync = new CategoryData();
            CategoryEntry categoryDb = mCategoriesDb.get(i);


            categoryToSync.setId(0);
            categoryToSync.setTitle(categoryDb.getName());

            categoriesToSync.add(categoryToSync);
        }
        return categoriesToSync;
    }

    private String getDefaultCategoryString() {
        List<CategoryData> categoriesToSync = new ArrayList<>();
        CategoryData categoryToSync = new CategoryData();
        categoryToSync.setId(0);
        categoryToSync.setTitle(CategoryEntry.DEFAULT_CATEGORY_NAME);
        categoriesToSync.add(categoryToSync);

        return getCategoriesString(categoriesToSync);
    }

    private void syncExpenses(String expensesString) {

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        if(mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.syncExpenses(expensesString, loftToken, googleToken);
            updateDbEntriesIds();
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

        for(int i = 0; i< mCategoriesDb.size(); i++) {
            CategoryEntry category = mCategoriesDb.get(i);
            for(ExpenseEntry expenseDb:category.getExpenses()) {
                ExpenseData expenseToSync = new ExpenseData();

                expenseToSync.setCategory_id(mNewCategoryIds[i]);
                expenseToSync.setComment(expenseDb.getDescription());
                expenseToSync.setId((int) expenseDb.getId());
                expenseToSync.setSum(Double.valueOf(expenseDb.getPrice()));
                expenseToSync.setTrDate(expenseDb.getDate());

                expensesToSync.add(expenseToSync);
            }
        }
        return expensesToSync;
    }

    private void updateDbEntriesIds() {
        // FIXME: 16.06.2016 не доставать категории. Достаю их, чтобы проверить обновление id
        List<CategoryEntry> categoryEntries = CategoryEntry.updateIds(mCategoriesDb,mNewCategoryIds);
        for (CategoryEntry category:categoryEntries) {
            Log.d(TAG,String.format(Locale.getDefault(),"%s = %d %n",category.getName(),category.getId()));
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        final int SYNC_INTERVAL = 60*5;
        final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
        TrackerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                ConstantsManager.CONTENT_AUTHORITY, true);
        ContentResolver.addPeriodicSync(newAccount, ConstantsManager.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);
        syncImmediately(context,false);
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }
}