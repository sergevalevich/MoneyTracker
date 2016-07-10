package com.valevich.moneytracker.ui.taskshandlers;

import android.app.Activity;
import android.content.Intent;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.database.TransactionExecutor;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;
import com.valevich.moneytracker.utils.ui.UserNotifier;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by User on 16.06.2016.
 */
@EBean
public class FetchUserDataTask implements Transaction.Error, Transaction.Success {

    @RootContext
    Activity mActivity;

    @Bean
    RestService mRestService;

    @Bean
    UserNotifier mUserNotifier;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    PriceFormatter mPriceFormatter;

    @Bean
    OttoBus mEventBus;

    List<GlobalCategoriesDataModel> mGlobalCategoriesData;

    @Background
    public void fetchUserData() {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mGlobalCategoriesData = mRestService
                    .fetchGlobalCategoriesData(getLoftToken(), getGoogleToken());
            if (mGlobalCategoriesData != null) {
                saveData(mGlobalCategoriesData);
            } else {
                notifyLoginFinished();
            }
        }
    }

    private void saveData(final List<GlobalCategoriesDataModel> globalCategoriesData) {

        final List<CategoryEntry> categoriesToSave = new ArrayList<>();

        for (GlobalCategoriesDataModel fetchedCategory : globalCategoriesData) {
            if (!isFetchedCategoryDefault(fetchedCategory))
                categoriesToSave.add(createCategory(fetchedCategory));
        }

        final int[] count = {0};
        TransactionExecutor<CategoryEntry> transactionExecutor = new TransactionExecutor<>();
        transactionExecutor.executeProcessModelTransaction(categoriesToSave
                , this
                , this
                , new TransactionExecutor.ProcessModelCallback<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {
                        category.save();
                        List<ExpenseData> fetchedExpenses = globalCategoriesData
                                .get(count[0])
                                .getTransactions();
                        for (ExpenseData fetchedExpense : fetchedExpenses) {
                            ExpenseEntry expense = createExpense(fetchedExpense);
                            expense.associateCategory(category);
                            expense.save();
                            count[0]++;
                        }
                    }
                }, TransactionExecutor.TRANSACTION_TYPE_CREATE);

    }

    private CategoryEntry createCategory(GlobalCategoriesDataModel fetchedCategory) {
        CategoryEntry category = new CategoryEntry();
        category.setId(fetchedCategory.getId());
        category.setName(fetchedCategory.getTitle());
        return category;
    }

    private ExpenseEntry createExpense(ExpenseData fetchedExpense) {
        ExpenseEntry expense = new ExpenseEntry();
        expense.setDescription(fetchedExpense.getComment());
        expense.setPrice(mPriceFormatter
                .formatPrice(String.valueOf(fetchedExpense.getSum())));
        expense.setDate(fetchedExpense.getTrDate());
        return expense;
    }

    private String getLoftToken() {
        return MoneyTrackerApplication_.getLoftApiToken();
    }

    private String getGoogleToken() {
        return MoneyTrackerApplication_.getGoogleToken();
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        notifyLoginFinished();
    }

    @Override
    public void onSuccess(Transaction transaction) {
        Timber.d("DATA SAVED SUCCESSFULLY");
        notifyLoginFinished();
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(mActivity,MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }

    private void notifyLoginFinished() {
        Timber.d("notifyLoginFinished: ");
        mEventBus.post(new LoginFinishedEvent());
    }

    private boolean isFetchedCategoryDefault(GlobalCategoriesDataModel category) {
        return category.getTitle().equals(CategoryEntry.DEFAULT_CATEGORY_NAME);
    }

}
