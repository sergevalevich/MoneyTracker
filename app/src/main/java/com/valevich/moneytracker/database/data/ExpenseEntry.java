package com.valevich.moneytracker.database.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;
import com.raizlabs.android.dbflow.structure.container.ModelContainerAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.network.rest.model.ExpenseData;

import java.util.List;
import java.util.Map;

/**
 * Created by NotePad.by on 07.05.2016.
 */
@Table(database = MoneyTrackerDatabase.class)
public class ExpenseEntry extends BaseModel {

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
        ModelContainerAdapter<CategoryEntry> adapter = FlowManager.getContainerAdapter(CategoryEntry.class);
        category = adapter.toForeignKeyContainer(categoryEntry);// convenience conversion
    }

    public static List<ExpenseEntry> getAllExpenses(String filter) {//query
        return SQLite.select()
                .from(ExpenseEntry.class)
                .where(ExpenseEntry_Table.description.like("%" + filter + "%"))
                .queryList();
    }

    public static void saveExpense(final String description,
                                   final String amount,
                                   final String date,
                                   final CategoryEntry category,
                                   Transaction.Success successCallback,
                                   Transaction.Error errorCallback) {
        ExpenseEntry expense = new ExpenseEntry();

        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<ExpenseEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<ExpenseEntry>() {
                    @Override
                    public void processModel(ExpenseEntry expense) {
                        expense.setDate(date);
                        expense.setDescription(description);
                        expense.setPrice(amount);

                        expense.associateCategory(category);
                        expense.save();
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<ExpenseEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, ExpenseEntry modifiedModel) {

                    }
                }).addAll(expense).build();

        Transaction transaction = database.beginTransactionAsync(processModelTransaction)
                .success(successCallback)
                .error(errorCallback)
                .build();

        transaction.execute();

    }
}

