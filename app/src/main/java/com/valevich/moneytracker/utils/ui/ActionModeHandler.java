package com.valevich.moneytracker.utils.ui;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.ActionItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ActionModeDestroyedEvent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class ActionModeHandler implements ActionMode.Callback {

    @Bean
    OttoBus mEventBus;

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.contextual_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        mEventBus.post(new ActionItemClickedEvent(menuItem));
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mEventBus.post(new ActionModeDestroyedEvent());
    }
}