package com.valevich.moneytracker.app.test;

import android.test.AndroidTestCase;

import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;

import timber.log.Timber;

/**
 * Created by User on 08.07.2016.
 */
public class TestDb extends AndroidTestCase {


    public void testUpdatingIds() {

        CategoryEntry categoryEntry = new CategoryEntry();
        categoryEntry.setName("test");
        categoryEntry.save();

        ExpenseEntry expenseEntry = new ExpenseEntry();
        expenseEntry.setDate("");
        expenseEntry.setPrice("");
        expenseEntry.setDescription("");
        expenseEntry.associateCategory(categoryEntry);
        expenseEntry.save();


        Timber.d("category's id is %l %n from expense id is %l",
                categoryEntry.getId(),
                expenseEntry.getCategory().getId());
        assertEquals(categoryEntry.getId(), expenseEntry.getCategory().getId());


    }

}
