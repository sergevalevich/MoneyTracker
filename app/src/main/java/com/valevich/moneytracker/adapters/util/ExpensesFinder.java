package com.valevich.moneytracker.adapters.util;

import com.valevich.moneytracker.database.data.ExpenseEntry;

import java.util.List;

/**
 * Created by User on 05.07.2016.
 */
public interface ExpensesFinder {
    List<ExpenseEntry> findAll(String filter);
}
