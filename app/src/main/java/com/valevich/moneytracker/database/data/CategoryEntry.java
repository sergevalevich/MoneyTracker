package com.valevich.moneytracker.database.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;

import java.util.List;

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

    public static List<CategoryEntry> getAllCategories(String filter) {//query
        return SQLite.select()
                .from(CategoryEntry.class)
                .where(CategoryEntry_Table.name.like("%" + filter + "%"))
                .queryList();
    }

    @Override
    public String toString() {
        return name;
    }
}
