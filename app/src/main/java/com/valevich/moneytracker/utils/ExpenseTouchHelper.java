package com.valevich.moneytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.database.data.ExpenseEntry;

/**
 * Created by User on 22.06.2016.
 */
public class ExpenseTouchHelper extends ItemTouchHelper.SimpleCallback {
    private ExpenseAdapter mExpenseAdapter;

    private Context mContext;

    private View mRootView;

    public ExpenseTouchHelper(ExpenseAdapter expenseAdapter, Context context, View rootView){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
        mExpenseAdapter = expenseAdapter;
        mContext = context;
        mRootView = rootView;
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
        final ExpenseEntry expenseToRemove = mExpenseAdapter.getExpense(itemPosition);
        mExpenseAdapter.removeItemFromAdapter(itemPosition);

        Snackbar snackbar = Snackbar.make(mRootView, mContext.getString(R.string.expense_removed_msg), Snackbar.LENGTH_LONG)
                .setAction(mContext.getString(R.string.undo_delete_action_msg), new View.OnClickListener() {
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
