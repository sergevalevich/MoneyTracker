package com.valevich.moneytracker.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.valevich.moneytracker.R;

public class TrackerPreferenceCategory extends PreferenceCategory {

    private Context mContext;

    public TrackerPreferenceCategory(Context context) {
        super(context);
        mContext = context;
    }

    public TrackerPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TrackerPreferenceCategory(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        titleView.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        View root = holder.itemView;
        int padding_in_dp = 15;
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        root.setPadding(padding_in_px, padding_in_px, padding_in_px, 0);

    }
}
