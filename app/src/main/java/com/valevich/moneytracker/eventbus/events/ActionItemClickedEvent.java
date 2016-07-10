package com.valevich.moneytracker.eventbus.events;

import android.view.MenuItem;

/**
 * Created by User on 07.07.2016.
 */
public class ActionItemClickedEvent {
    private MenuItem mMenuItem;

    public ActionItemClickedEvent(MenuItem menuItem) {
        mMenuItem = menuItem;
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }
}
