package com.valevich.moneytracker.utils.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.ItemSwipedEvent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by User on 22.06.2016.
 */
@EBean
public class ExpenseTouchHelper extends ItemTouchHelper.SimpleCallback {

    @Bean
    OttoBus mEventBus;

    public ExpenseTouchHelper() {
        super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mEventBus.post(new ItemSwipedEvent(viewHolder.getAdapterPosition()));
    }

}
