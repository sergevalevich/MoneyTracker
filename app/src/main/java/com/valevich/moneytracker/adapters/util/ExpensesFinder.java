package com.valevich.moneytracker.adapters.util;

import com.valevich.moneytracker.database.data.ExpenseEntry;

import java.util.List;

public interface ExpensesFinder {
    List<ExpenseEntry> findAll(String filter);
}
