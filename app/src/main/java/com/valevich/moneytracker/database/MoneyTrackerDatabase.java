package com.valevich.moneytracker.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by NotePad.by on 07.05.2016.
 */

@Database(name = MoneyTrackerDatabase.NAME, version = MoneyTrackerDatabase.VERSION)
public class MoneyTrackerDatabase {

    public static final String NAME = "moneytracker"; // we will add the .db extension

    public static final int VERSION = 1;

}

