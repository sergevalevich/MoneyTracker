package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.model.Expense;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseEntry> mExpenses;

    public ExpenseAdapter (List<ExpenseEntry> expenses) {
        mExpenses = expenses;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.expense_list_item,parent,false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        holder.bindExpense(mExpenses.get(position));
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public void refresh(List<ExpenseEntry> expenses) {
        mExpenses.clear();
        mExpenses.addAll(expenses);
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.description)
        TextView description;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindExpense(ExpenseEntry expense) {
            price.setText(expense.getPrice());
            description.setText(expense.getDescription());
        }
    }
}
