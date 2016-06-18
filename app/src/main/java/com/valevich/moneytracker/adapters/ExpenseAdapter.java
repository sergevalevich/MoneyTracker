package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.utils.ClickListener;
import com.valevich.moneytracker.utils.DateFormatter;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpenseAdapter extends SelectableAdapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseEntry> mExpenses;

    private ClickListener mClickListener;

    public ExpenseAdapter (List<ExpenseEntry> expenses, ClickListener clickListener) {
        mExpenses = expenses;
        mClickListener = clickListener;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.expense_list_item,parent,false);
        return new ExpenseViewHolder(view,mClickListener);
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

    class ExpenseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.description)
        TextView description;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.category)
        TextView category;

        @Bind(R.id.selected_overlay)
        View selectedView;
        private ClickListener clickListener;

        public ExpenseViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.clickListener = clickListener;
        }

        public void bindExpense(ExpenseEntry expense) {
            price.setText(String.format(Locale.getDefault(),"%s%s",expense.getPrice(),"$"));
            description.setText(expense.getDescription());
            date.setText(DateFormatter.formatDateFromDb(expense.getDate()));
            CategoryEntry categoryDb = expense.getCategory();
            if(categoryDb != null)
            category.setText(expense.getCategory().getName());

            selectedView.setVisibility(isSelected(getAdapterPosition())
                    ? View.VISIBLE
                    : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (clickListener != null) {
                clickListener.onItemLongClick(getAdapterPosition());
                return true;
            } else {
                return false;
            }
        }

    }

    public void removeItems(List<Integer> positions) {

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            removeItem(positions.get(0));
            positions.remove(0);
        }
    }

    public void removeItem(int position) {
        ExpenseEntry expense = mExpenses.get(position);
        if (expense != null) {
            expense.delete();
            mExpenses.remove(position);
            notifyItemRemoved(position);
        }
    }

}
