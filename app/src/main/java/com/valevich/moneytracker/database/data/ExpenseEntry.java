package com.valevich.moneytracker.database.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;
import com.raizlabs.android.dbflow.structure.container.ModelContainerAdapter;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.model.Category;

import java.util.List;

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
        category = adapter.toForeignKeyContainer(categoryEntry) ;// convenience conversion
    }

    public static List<ExpenseEntry> getAllExpenses() {//query
        return SQLite.select()
                .from(ExpenseEntry.class)
                .queryList();
    }
}
