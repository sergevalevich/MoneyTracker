package com.valevich.moneytracker.utils;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.ui.activities.MainActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by User on 22.06.2016.
 */
@EBean
public class ExpenseTouchHelper extends ItemTouchHelper.SimpleCallback {

    @Bean
    ExpenseAdapter mExpenseAdapter;

    @RootContext
    MainActivity mActivity;

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
        //Remove item
        final int itemPosition = viewHolder.getAdapterPosition();
        final ExpenseEntry expenseToRemove = mExpenseAdapter.getItem(itemPosition);
        mExpenseAdapter.removeItemFromAdapter(itemPosition);

        Snackbar snackbar = Snackbar.make(mActivity.getRootView(), mActivity.getString(R.string.expense_removed_msg), Snackbar.LENGTH_LONG)
                .setAction(mActivity.getString(R.string.undo_delete_action_msg), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpenseAdapter.add(itemPosition, expenseToRemove);
                    }
                });

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    ExpenseEntry.removeExpense(expenseToRemove);
                }
            }
        });

        snackbar.show();

    }

}
