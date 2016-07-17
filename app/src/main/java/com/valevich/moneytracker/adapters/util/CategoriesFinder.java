package com.valevich.moneytracker.adapters.util;

import com.valevich.moneytracker.database.data.CategoryEntry;

import java.util.List;


public interface CategoriesFinder {
    List<CategoryEntry> findAll(String filter);
}
