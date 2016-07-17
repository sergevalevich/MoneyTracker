package com.valevich.moneytracker.adapters.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.util.ViewBinder;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


@EViewGroup(R.layout.category_list_item)
public class CategoryItemView extends FrameLayout implements ViewBinder<CategoryEntry> {

    @ViewById(R.id.category_label)
    TextView categoryNameLabel;

    @ViewById(R.id.category_total)
    TextView categoryTotalLabel;

    @ViewById(R.id.selected_overlay)
    View selectedView;

    @Bean
    PriceFormatter mPriceFormatter;

    public CategoryItemView(Context context) {
        super(context);
    }

    @Override
    public void bind(CategoryEntry item, boolean isSelected) {
        categoryNameLabel.setText(item.getName());
        categoryTotalLabel.setText(mPriceFormatter
                .formatCategoryTotal(item.getCategoryTotal()));

        selectedView.setVisibility(isSelected
                ? View.VISIBLE
                : View.INVISIBLE);
    }
}
