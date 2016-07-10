package com.valevich.moneytracker.adapters.wrappers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.valevich.moneytracker.utils.ui.ClickListener;

/**
 * Created by User on 05.07.2016.
 */
public class ViewWrapper<V extends View>
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    private V view;

    private ClickListener mClickListener;

    public ViewWrapper(V itemView, ClickListener clickListener) {
        super(itemView);
        view = itemView;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        mClickListener = clickListener;
    }

    public V getView() {
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemLongClick(getAdapterPosition());
            return true;
        } else {
            return false;
        }
    }
}
