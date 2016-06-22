package com.valevich.moneytracker.database.data;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by NotePad.by on 07.05.2016.
 */
@ModelContainer
@Table(database = MoneyTrackerDatabase.class,
        uniqueColumnGroups = {@UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.IGNORE)})
public class CategoryEntry extends BaseModel {

    //----DEFAULT CATEGORY. Needed to allow sync when the user removed all items
    public static final String DEFAULT_CATEGORY_NAME = "DEFAULT_CATEGORY";
    public static final String TRANSACTION_TYPE_CREATE = "CREATE";
    public static final String TRANSACTION_TYPE_UPDATE = "UPDATE";

    private static String mTransactionType;


    @PrimaryKey(autoincrement = true)
    long id;

    @Unique(unique = false, uniqueGroups = 1)
    @Column
    private String name;

    List<ExpenseEntry> expenses;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "expenses")
    public List<ExpenseEntry> getExpenses() {
        if (expenses == null || expenses.isEmpty()) {
            expenses = SQLite.select()
                    .from(ExpenseEntry.class)
                    .where(ExpenseEntry_Table.category_id.eq(id))
                    .queryList();
        }
        return expenses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static List<CategoryEntry> getAllCategories(String filter) {//query
        return SQLite.select()
                .from(CategoryEntry.class)
                .where(CategoryEntry_Table.name.like("%" + filter + "%"))
                .queryList();
    }

    public static CategoryEntry getCategory(String name) {
        return SQLite.select()
                .from(CategoryEntry.class)
                .where(CategoryEntry_Table.name.eq(name))
                .querySingle();
    }

    public static List<CategoryEntry> updateIds(List<CategoryEntry> categories,int[] ids) {

        for(int i = 0; i < ids.length; i++) {
            CategoryEntry category = categories.get(i);
            int newId = ids[i];
            long oldId = category.getId();
            SQLite.update(CategoryEntry.class)
                    .set(CategoryEntry_Table.id.eq(newId))
                    .where(CategoryEntry_Table.id.eq(oldId))
                    .execute();
            SQLite.update(ExpenseEntry.class)
                    .set(ExpenseEntry_Table.category_id.eq(newId))
                    .where(ExpenseEntry_Table.category_id.eq(oldId))
                    .execute();
        }

        return getAllCategories("");

    }

    public static void saveCategories(final List<GlobalCategoriesDataModel> globalCategoriesData,
                                      Transaction.Success successCallback,
                                      Transaction.Error errorCallback) {

        final CategoryEntry[] categories = new CategoryEntry[globalCategoriesData.size()];

        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<CategoryEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<CategoryEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, CategoryEntry category) {
                        GlobalCategoriesDataModel fetchedCategory =
                                globalCategoriesData.get((int) current);

                        if(!isCategoryDefault(fetchedCategory)) {

                            category = new CategoryEntry();
                            category.setId(fetchedCategory.getId());
                            category.setName(fetchedCategory.getTitle());
                            category.save();

                            List<ExpenseData> fetchedExpenses = fetchedCategory.getTransactions();
                            for (ExpenseData fetchedExpense : fetchedExpenses) {
                                ExpenseEntry expense = new ExpenseEntry();
                                expense.setDate(fetchedExpense.getTrDate());
                                expense.setDescription(fetchedExpense.getComment());
                                expense.setPrice(String.valueOf(fetchedExpense.getSum()));
                                expense.associateCategory(category);
                                expense.save();
                            }
                        }
                    }
                }).addAll(categories).build();


        Transaction transaction = database
                .beginTransactionAsync(processModelTransaction)
                .success(successCallback)
                .error(errorCallback)
                .build();

        transaction.execute();

    }

    public static void removeCategory(CategoryEntry category) {

        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<CategoryEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {
                        category.delete();
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<CategoryEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, CategoryEntry modifiedModel) {
                    }
                }).addAll(category).build();

        Transaction transaction = database.beginTransactionAsync(processModelTransaction)
                .build();

        transaction.execute();

    }

    private static boolean isCategoryDefault(GlobalCategoriesDataModel fetchedCategory) {
        return fetchedCategory.getTitle().equals(DEFAULT_CATEGORY_NAME);
    }


    public static void saveCategory(CategoryEntry category,
                                    final String name,
                                    Transaction.Success successCallback,
                                    Transaction.Error errorCallback) {

        mTransactionType = TRANSACTION_TYPE_UPDATE;
        if(category == null) {
            category = new CategoryEntry();
            mTransactionType = TRANSACTION_TYPE_CREATE;
        }

        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<CategoryEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {
                        category.setName(name);
                        if (mTransactionType.equals(TRANSACTION_TYPE_CREATE))
                            category.insert();
                        if(mTransactionType.equals(TRANSACTION_TYPE_UPDATE))
                            category.update();
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<CategoryEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, CategoryEntry category) {

                    }
                }).addAll(category).build();

        Transaction transaction = database
                .beginTransactionAsync(processModelTransaction)
                .success(successCallback)
                .error(errorCallback)
                .name(mTransactionType)
                .build();
        transaction.execute();

    }


    public float getCategoryTotal() {
        float total = 0.f;
        for (ExpenseEntry expense:getExpenses()) {
            total += Float.valueOf(expense.getPrice());
        }
        return total;
    }

    @Override
    public String toString() {
        return name;
    }
}
