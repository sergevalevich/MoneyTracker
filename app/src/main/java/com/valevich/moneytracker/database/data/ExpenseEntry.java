package com.valevich.moneytracker.database.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;
import com.raizlabs.android.dbflow.structure.container.ModelContainerAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.adapters.util.ExpensesFinder;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.database.TransactionExecutor;

import org.androidannotations.annotations.EBean;

import java.util.List;

/**
 * Created by NotePad.by on 07.05.2016.
 */
@EBean
@Table(database = MoneyTrackerDatabase.class)
public class ExpenseEntry extends BaseModel implements ExpensesFinder {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    private String description;

    @Column
    private String price;

    @Column
    private String date;

    @ForeignKey
    ForeignKeyContainer<CategoryEntry> category;

    private static final TransactionExecutor<ExpenseEntry> mTransactionExecutor
            = new TransactionExecutor<>();

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public CategoryEntry getCategory() {
        return category.load();
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void associateCategory(CategoryEntry categoryEntry) {
        ModelContainerAdapter<CategoryEntry> adapter = FlowManager
                .getContainerAdapter(CategoryEntry.class);
        category = adapter.toForeignKeyContainer(categoryEntry);// convenience conversion
    }

    public static List<ExpenseEntry> getAllExpenses(String filter) {//query
        return SQLite.select()
                .from(ExpenseEntry.class)
                .where(ExpenseEntry_Table.description.like("%" + filter + "%"))
                .orderBy(ExpenseEntry_Table.date, true)
                .queryList();
    }

    public static ExpenseEntry getLastInserted() {
        return SQLite.select()
                .from(ExpenseEntry.class)
                .orderBy(ExpenseEntry_Table.id, false)
                .limit(1)
                .querySingle();
    }

    public static void create(List<ExpenseEntry> expensesToProcess,
                              Transaction.Success successCallback,
                              Transaction.Error errorCallback) {
        mTransactionExecutor.create(expensesToProcess, successCallback, errorCallback);
    }

    public static void delete(List<ExpenseEntry> expensesToProcess,
                              Transaction.Success successCallback,
                              Transaction.Error errorCallback) {
        mTransactionExecutor.delete(expensesToProcess, successCallback, errorCallback);
    }

    @Override
    public List<ExpenseEntry> findAll(String filter) {
        return getAllExpenses(filter);
    }
}

