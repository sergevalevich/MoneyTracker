package com.valevich.moneytracker.adapters.util;

import com.valevich.moneytracker.database.data.CategoryEntry;

import java.util.List;

/**
 * Created by User on 05.07.2016.
 */
public interface CategoriesFinder {
    List<CategoryEntry> findAll(String filter);
}
