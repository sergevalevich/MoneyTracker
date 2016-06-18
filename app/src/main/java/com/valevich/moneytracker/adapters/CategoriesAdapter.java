package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.utils.ClickListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by NotePad.by on 20.04.2016.
 */
public class CategoriesAdapter extends SelectableAdapter<CategoriesAdapter.CategoryViewHolder>  {

    private List<CategoryEntry> mCategories;

    private ClickListener mClickListener;

    public CategoriesAdapter (List<CategoryEntry> categories, ClickListener clickListener) {
        mCategories = categories;
        mClickListener = clickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.category_list_item,parent,false);
        return new CategoryViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bindCategory(mCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public void refresh(List<CategoryEntry> data) {
        mCategories.clear();
        mCategories.addAll(data);
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        @Bind(R.id.category_label)
        TextView categoryLabel;

        @Bind(R.id.selected_overlay)// TODO: 18.06.2016
        View selectedView;
        private ClickListener clickListener;

        public CategoryViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.clickListener = clickListener;
        }

        public void bindCategory(CategoryEntry category) {
            categoryLabel.setText(category.getName());
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
        if (mCategories.get(position) != null) {
            mCategories.get(position).delete();
            mCategories.remove(position);
            notifyItemRemoved(position);
        }
    }
}
