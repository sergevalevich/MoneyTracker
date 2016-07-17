package com.valevich.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.valevich.moneytracker.adapters.util.ViewBinder;
import com.valevich.moneytracker.adapters.wrappers.ViewWrapper;
import com.valevich.moneytracker.utils.ui.ClickListener;

import java.util.ArrayList;
import java.util.List;


public abstract class RecyclerViewAdapterBase<T, V extends View & ViewBinder<T>>
        extends RecyclerView.Adapter<ViewWrapper<V>> implements ClickListener {

    protected List<T> mItems = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<>(onCreateItemView(parent, viewType), this);
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public void add(int position, T item) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItemFromAdapter(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

}
