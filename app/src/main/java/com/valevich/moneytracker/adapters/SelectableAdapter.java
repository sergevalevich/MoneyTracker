package com.valevich.moneytracker.adapters;

import android.util.SparseBooleanArray;
import android.view.View;

import com.valevich.moneytracker.adapters.util.ViewBinder;
import com.valevich.moneytracker.adapters.wrappers.ViewWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 18.06.2016.
 */
public abstract class SelectableAdapter<T, V extends View & ViewBinder<T>>
        extends RecyclerViewAdapterBase<T, V> {

    private SparseBooleanArray mSelectedItems;

    public SelectableAdapter() {
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public void onBindViewHolder(ViewWrapper<V> holder, int position) {
        V view = holder.getView();
        T item = mItems.get(position);
        view.bind(item, isSelected(position));
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }

        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();

        for (Integer i: selection) {
            notifyItemChanged(i);
        }
    }
}
