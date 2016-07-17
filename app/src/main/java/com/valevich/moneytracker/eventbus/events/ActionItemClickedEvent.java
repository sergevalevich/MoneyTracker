package com.valevich.moneytracker.eventbus.events;

import android.view.MenuItem;


public class ActionItemClickedEvent {
    private MenuItem mMenuItem;

    public ActionItemClickedEvent(MenuItem menuItem) {
        mMenuItem = menuItem;
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }
}
