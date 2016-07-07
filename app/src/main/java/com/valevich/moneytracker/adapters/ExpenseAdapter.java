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
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.ExpenseItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ExpenseItemLongClickedEvent;
import com.valevich.moneytracker.utils.ClickListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

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

    public void initAdapter(String filter) {
        mItems = mExpensesFinder.findAll(filter);
    }

    private int mLastPosition = -1;//animate last position

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

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            removeItemFromDbAndAdapter(positions.get(0));
            positions.remove(0);
        }
    }

    public void removeItemFromDbAndAdapter(int position) {
        ExpenseEntry expense = mItems.get(position);
        if (expense != null) {
            ExpenseEntry.removeExpense(expense);
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    //Using eventBus here because AndroidAnnotations does not provide good solution for RecyclerView
    //click events
    @Override
    public boolean onItemClick(int position) {
        BusProvider.getInstance().post(new ExpenseItemClickedEvent(position));
        return true;
    }

    @Override
    public boolean onItemLongClick(int position) {
        BusProvider.getInstance().post(new ExpenseItemLongClickedEvent(position));
        return true;
    }

    private void setAnimation(View view, int position) {
        if(position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }
}
