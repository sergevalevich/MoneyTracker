package com.valevich.moneytracker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.util.ExpensesFinder;
import com.valevich.moneytracker.adapters.views.ExpenseItemView;
import com.valevich.moneytracker.adapters.views.ExpenseItemView_;
import com.valevich.moneytracker.adapters.wrappers.ViewWrapper;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.ExpenseItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ExpenseItemLongClickedEvent;
import com.valevich.moneytracker.utils.ui.ClickListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class ExpenseAdapter
        extends SelectableAdapter<ExpenseEntry, ExpenseItemView>
        implements ClickListener {

    @RootContext
    Context mContext;

    @Bean(ExpenseEntry.class)
    ExpensesFinder mExpensesFinder;

    @Bean
    OttoBus mEventBus;

    private long mLastInsertedId;

    private boolean mWasAnimated;

    public void initAdapter(String filter) {
        mItems = mExpensesFinder.findAll(filter);
        ExpenseEntry expense = ExpenseEntry.getLastInserted();
        if (expense != null) {
            long id = expense.getId();
            mWasAnimated = mLastInsertedId == id;
            if (!mWasAnimated) mLastInsertedId = id;
        }
        //animating only last inserted item
    }

    @Override
    protected ExpenseItemView onCreateItemView(ViewGroup parent, int viewType) {
        return ExpenseItemView_.build(mContext);
    }


    @Override
    public void onBindViewHolder(ViewWrapper<ExpenseItemView> holder, int position) {
        super.onBindViewHolder(holder, position);
        setAnimation(holder.getView().getRootView(), position);
    }

    public void removeDbAndAdapterItems(List<Integer> positions) {

        List<ExpenseEntry> expensesToRemove = new ArrayList<>();

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            int position = positions.get(0);
            ExpenseEntry expense = mItems.get(position);
            if (expense != null) {
                expensesToRemove.add(expense);
                mItems.remove(position);
                notifyItemRemoved(position);
            }
            positions.remove(0);
        }
        ExpenseEntry.delete(expensesToRemove, null, null);
    }

    //Using eventBus here because AndroidAnnotations does not provide good solution for RecyclerView
    //click events
    @Override
    public boolean onItemClick(int position) {
        mEventBus.post(new ExpenseItemClickedEvent(position));
        return true;
    }

    @Override
    public boolean onItemLongClick(int position) {
        mEventBus.post(new ExpenseItemLongClickedEvent(position));
        return true;
    }

    private void setAnimation(View view, int position) {
        ExpenseEntry expense = mItems.get(position);
        if (expense != null && !mWasAnimated && expense.getId() == mLastInsertedId) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
            view.startAnimation(animation);
            mWasAnimated = true;
        }
    }
}
