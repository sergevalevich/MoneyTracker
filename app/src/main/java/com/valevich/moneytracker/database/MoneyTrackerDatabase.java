package com.valevich.moneytracker.database;

import com.raizlabs.android.dbflow.annotation.Database;


@Database(name = MoneyTrackerDatabase.NAME, version = MoneyTrackerDatabase.VERSION)
public class MoneyTrackerDatabase {

    public static final String NAME = "moneytracker"; // dbflow will add the .db extension

    public static final int VERSION = 1;

}

