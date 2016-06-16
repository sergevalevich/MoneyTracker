package com.valevich.moneytracker.database.data;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;

import org.androidannotations.annotations.EBean;

import java.util.List;
import java.util.Map;

/**
 * Created by NotePad.by on 07.05.2016.
 */
@ModelContainer
@Table(database = MoneyTrackerDatabase.class)
public class CategoryEntry extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id;

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


    public static void updateCategoryIds(final Map<String,Integer> ids, Transaction.Success successCallback, Transaction.Error errorCallback) {

        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<CategoryEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {

                        String categoryName = category.getName();

                        for(Map.Entry<String,Integer> id: ids.entrySet()) {
                            if(categoryName.equals(id.getKey())) {
                                category.setId(id.getValue());
                                break;
                            }
                        }

                        category.update();

                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<CategoryEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, CategoryEntry category) {

                    }
                }).addAll(getAllCategories("")).build();//// FIXME: 16.06.2016


        Transaction transaction = database
                .beginTransactionAsync(processModelTransaction)
                .success(successCallback)
                .error(errorCallback)
                .build();

        transaction.execute();

    }


    @Override
    public String toString() {
        return name;
    }
}
