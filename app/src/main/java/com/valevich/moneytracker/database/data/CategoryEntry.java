package com.valevich.moneytracker.database.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.adapters.util.CategoriesFinder;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.database.TransactionExecutor;

import org.androidannotations.annotations.EBean;

import java.util.List;


@EBean
@ModelContainer
@Table(database = MoneyTrackerDatabase.class)
public class CategoryEntry extends BaseModel implements CategoriesFinder {

    //----DEFAULT CATEGORY. Needed to allow sync when the user removed all Items
    public static final String DEFAULT_CATEGORY_NAME = "DEFAULT_CATEGORY";

    private static final TransactionExecutor<CategoryEntry> mTransactionExecutor
            = new TransactionExecutor<>();

    //Columns//
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    private int serverId;

    @Unique(unique = true)
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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public static void create(List<CategoryEntry> categoriesToProcess,
                              Transaction.Success successCallback,
                              Transaction.Error errorCallback) {
        mTransactionExecutor.create(categoriesToProcess, successCallback, errorCallback);
    }

    public static void update(List<CategoryEntry> categoriesToProcess,
                              Transaction.Success successCallback,
                              Transaction.Error errorCallback) {
        mTransactionExecutor.update(categoriesToProcess, successCallback, errorCallback);
    }

    public static void delete(List<CategoryEntry> categoriesToProcess,
                              Transaction.Success successCallback,
                              Transaction.Error errorCallback) {
        mTransactionExecutor.delete(categoriesToProcess, successCallback, errorCallback);
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

    @Override
    public List<CategoryEntry> findAll(String filter) {
        return getAllCategories(filter);
    }

}
