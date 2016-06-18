package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 18.06.2016.
 */
public abstract class SelectableAdapter <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    private SparseBooleanArray mSelectedItems;

    public SelectableAdapter() {
        mSelectedItems = new SparseBooleanArray();
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
