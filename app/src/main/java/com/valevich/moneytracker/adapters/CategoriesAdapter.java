package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.model.Category;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by NotePad.by on 20.04.2016.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<Category> mCategories;

    public CategoriesAdapter (List<Category> categories) {
        mCategories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.category_list_item,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bindCategory(mCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.category_label)
        TextView categoryLabel;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindCategory(Category category) {
            categoryLabel.setText(category.getName());
        }
    }
}
