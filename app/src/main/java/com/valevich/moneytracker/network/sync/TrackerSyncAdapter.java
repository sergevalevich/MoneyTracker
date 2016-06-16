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

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.model.Category;
import com.valevich.moneytracker.network.rest.RestClient;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.CategoryData;
import com.valevich.moneytracker.network.rest.model.ExpenseData;


import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;




public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter implements Transaction.Success, Transaction.Error {

    private static final String TAG = TrackerSyncAdapter.class.getSimpleName();

    private RestService mRestService;

    private Map<String,Integer> mNewCategoryIds;

    public TrackerSyncAdapter(Context context) {
        super(context,true);
        mRestService = new RestService();
        mRestService.setRestClient(new RestClient());
        mNewCategoryIds = new TreeMap<>();
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
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

        List<CategoryEntry> categoriesDb = CategoryEntry.getAllCategories("");

        if(categoriesDb.size() != 0) {

            syncCategories(categoriesDb);

            List<ExpenseEntry> expensesDb = ExpenseEntry.getAllExpenses("");

            if (expensesDb.size() != 0) {

                syncExpenses(expensesDb);
                updateDbEntriesIds(expensesDb);

            }

        }


    }

    private void syncCategories(List<CategoryEntry> categoriesDb) {

        String categoriesString = getCategoriesString(categoriesDb);

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        CategoriesSyncModel apiCategories = mRestService
                .syncCategories(categoriesString,loftToken,googleToken);


        setNewCategoryIds(apiCategories);
    }

    private void setNewCategoryIds(CategoriesSyncModel apiCategories) {

        List<CategoryData> categoryData = apiCategories.getData();

        for(int i = 0; i<categoryData.size(); i++) {

            String categoryName = categoryData.get(i).getTitle();
            Integer categoryId = categoryData.get(i).getId();
            mNewCategoryIds.put(categoryName,categoryId);

        }
    }

    @NonNull
    private String getCategoriesString(List<CategoryEntry> categoriesDb) {
        List<CategoryData> categoriesToSync = getPreparedCategories(categoriesDb);

        Gson gson = new Gson();

        return gson.toJson(categoriesToSync);
    }

    private List<CategoryData> getPreparedCategories(List<CategoryEntry> categoriesDb) {

        List<CategoryData> categoriesToSync = new ArrayList<>();

        for(int i = 0; i<categoriesDb.size(); i++) {
            CategoryData categoryToSync = new CategoryData();
            CategoryEntry categoryDb = categoriesDb.get(i);


            categoryToSync.setId(0);
            categoryToSync.setTitle(categoryDb.getName());

            categoriesToSync.add(categoryToSync);
        }
        return categoriesToSync;
    }

    private void syncExpenses(List<ExpenseEntry> expensesDb) {

        String expensesString = getExpensesString(expensesDb);

        String loftToken = getLoftToken();
        String googleToken = getGoogleToken();

        mRestService.syncExpenses(expensesString,loftToken,googleToken);
    }

    @NonNull
    private String getExpensesString(List<ExpenseEntry> expensesDb) {
        List<ExpenseData> expensesToSync = getPreparedExpenses(expensesDb);

        Gson gson = new Gson();

        return gson.toJson(expensesToSync);
    }

    @NonNull
    private List<ExpenseData> getPreparedExpenses(List<ExpenseEntry> expensesDb) {

        List<ExpenseData> expensesToSync = new ArrayList<>();

        for(int i = 0; i<expensesDb.size(); i++) {
            ExpenseData expenseToSync = new ExpenseData();
            ExpenseEntry expenseDb = expensesDb.get(i);

            expenseToSync.setCategory_id(getNewCategoryId(expenseDb.getCategory().getName()));
            expenseToSync.setComment(expenseDb.getDescription());
            expenseToSync.setId((int) expenseDb.getId());
            expenseToSync.setSum(Double.valueOf(expenseDb.getPrice()));
            expenseToSync.setTrDate(expenseDb.getDate());

            expensesToSync.add(expenseToSync);
        }
        return expensesToSync;
    }

    private int getNewCategoryId(String categoryName) {
        for(Map.Entry<String,Integer> newId: mNewCategoryIds.entrySet()) {
            if(categoryName.equals(newId.getKey())) return newId.getValue();
        }
        return 0;
    }

    private void updateDbEntriesIds(List<ExpenseEntry> expensesDb) {
       ExpenseEntry.updateCategoryIds(expensesDb,mNewCategoryIds,this,this);
        //CategoryEntry.updateCategoryIds(mNewCategoryIds,this,this);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        final int SYNC_INTERVAL = 12;
        final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
        TrackerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);
        ContentResolver.addPeriodicSync(newAccount, context.getString(R.string.content_authority),
                Bundle.EMPTY,
                SYNC_INTERVAL);
        syncImmediately(context);
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

    @Override
    public void onSuccess(Transaction transaction) {
        Log.d(TAG,"updated ids successfully");
        List<CategoryEntry> categoryEntries = CategoryEntry.getAllCategories("");
        List<ExpenseEntry> expenseEntries = ExpenseEntry.getAllExpenses("");
        for (CategoryEntry category: categoryEntries) {
            Log.d(TAG,String.format(Locale.getDefault(),"%s --- %d %n",category.getName(),category.getId()));
        }
        for (ExpenseEntry expense: expenseEntries) {
            Log.d(TAG,String.format(Locale.getDefault(),"%s --- %d %n",expense.getCategory().getName(),expense.getCategory().getId()));
        }
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        Log.d(TAG,"error updating ids");
    }

}