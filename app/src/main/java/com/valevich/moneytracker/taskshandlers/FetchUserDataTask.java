package com.valevich.moneytracker.taskshandlers;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.LoginFinishedEvent;
import com.valevich.moneytracker.eventbus.events.NetworkErrorEvent;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.TriesCounter;
import com.valevich.moneytracker.utils.formatters.DateFormatter;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EBean
public class FetchUserDataTask implements Transaction.Error, Transaction.Success {

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @Bean
    RestService mRestService;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    PriceFormatter mPriceFormatter;

    @Bean
    DateFormatter mDateFormatter;

    @Bean
    TriesCounter mNetworkErrorTriesCounter;

    @Bean
    OttoBus mEventBus;

    @Background
    public void fetchUserData() {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mRestService.fetchGlobalCategoriesData(
                    MoneyTrackerApplication_.getLoftApiToken(),
                    MoneyTrackerApplication_.getGoogleToken(),
                    new Callback<List<GlobalCategoriesDataModel>>() {
                        @Override
                        public void success(List<GlobalCategoriesDataModel> globalCategoriesData, Response response) {

                            if (globalCategoriesData != null) {
                                saveData(globalCategoriesData);
                            } else {
                                notifyAboutNetworkError(response.getReason());
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Timber.d(error.getLocalizedMessage());
                            mNetworkErrorTriesCounter.reduceTry();
                            if (mNetworkErrorTriesCounter.areTriesLeft()) {
                                fetchUserData();
                            } else {
                                notifyAboutNetworkError(error.getLocalizedMessage());
                            }
                        }
                    });
        } else {
            notifyAboutNetworkError(mNetworkUnavailableMessage);
        }
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        notifyLoginFinished();
    }

    @Override
    public void onSuccess(Transaction transaction) {
        Timber.d("DATA SAVED SUCCESSFULLY");
        notifyLoginFinished();
    }

    private void saveData(final List<GlobalCategoriesDataModel> globalCategoriesData) {

        final List<CategoryEntry> categoriesToSave = new ArrayList<>();

        for (GlobalCategoriesDataModel fetchedCategory : globalCategoriesData) {
            if (!isFetchedCategoryDefault(fetchedCategory))
                categoriesToSave.add(createCategory(fetchedCategory));
        }

        CategoryEntry.create(categoriesToSave, new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                List<CategoryEntry> categoriesDb = CategoryEntry.getAllCategories("");
                List<ExpenseEntry> expensesToSave = new ArrayList<>();
                for (int i = 0; i < categoriesDb.size(); i++) {
                    CategoryEntry category = categoriesDb.get(i);
                    List<ExpenseData> fetchedExpenses = globalCategoriesData
                            .get(i)
                            .getTransactions();
                    for (ExpenseData fetchedExpense : fetchedExpenses) {
                        ExpenseEntry expense = createExpense(fetchedExpense);
                        expense.associateCategory(category);
                        expensesToSave.add(expense);
                    }
                }
                Collections.sort(expensesToSave, new Comparator<ExpenseEntry>() {
                    @Override
                    public int compare(ExpenseEntry expense1, ExpenseEntry expense2) {
                        return mDateFormatter
                                .getDateFromString(expense1.getDate())
                                .compareTo(mDateFormatter.getDateFromString(expense2.getDate()));
                    }
                });
                ExpenseEntry.create(expensesToSave, FetchUserDataTask.this, FetchUserDataTask.this);
            }
        }, FetchUserDataTask.this);

    }

    private CategoryEntry createCategory(GlobalCategoriesDataModel fetchedCategory) {
        CategoryEntry category = new CategoryEntry();
        category.setServerId(fetchedCategory.getId());
        category.setName(fetchedCategory.getTitle());
        return category;
    }

    private ExpenseEntry createExpense(ExpenseData fetchedExpense) {
        ExpenseEntry expense = new ExpenseEntry();
        expense.setDescription(fetchedExpense.getComment());
        expense.setPrice(mPriceFormatter
                .formatPriceForDb(String.valueOf(fetchedExpense.getSum())));
        expense.setDate(fetchedExpense.getDate());
        return expense;
    }

    private void notifyLoginFinished() {
        Timber.d("notifyLoginFinished: ");
        MoneyTrackerApplication_.setIsLoginFinished(true);
        mEventBus.post(new LoginFinishedEvent());
    }

    private void notifyAboutNetworkError(String message) {
        MoneyTrackerApplication_.setIsNetworkError(true);
        MoneyTrackerApplication_.setErrorMessage(message);
        mEventBus.post(new NetworkErrorEvent(message));
    }

    private boolean isFetchedCategoryDefault(GlobalCategoriesDataModel category) {
        return category.getTitle().equals(CategoryEntry.DEFAULT_CATEGORY_NAME);
    }

}
