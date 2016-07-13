package com.valevich.moneytracker.adapters.views;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.util.ViewBinder;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.utils.formatters.DateFormatter;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


/**
 * Created by User on 05.07.2016.
 */
@EViewGroup(R.layout.expense_list_item)
public class ExpenseItemView extends RelativeLayout implements ViewBinder<ExpenseEntry> {

    @ViewById(R.id.price)
    TextView price;

    @ViewById(R.id.description)
    TextView description;

    @ViewById(R.id.date)
    TextView date;

    @ViewById(R.id.category)
    TextView category;

    @ViewById(R.id.selected_overlay)
    View selectedView;

    @Bean
    DateFormatter mDateFormatter;

    @Bean
    PriceFormatter mPriceFormatter;

    public ExpenseItemView(Context context) {
        super(context);
    }

    @Override
    public void bind(ExpenseEntry expense, boolean isSelected) {
        price.setText(mPriceFormatter.formatPriceFromDb(expense.getPrice()));
        description.setText(expense.getDescription());
        date.setText(mDateFormatter.formatDateFromDb(expense.getDate()));
        CategoryEntry categoryDb = expense.getCategory();
        if (categoryDb != null)
            category.setText(categoryDb.getName());

        selectedView.setVisibility(isSelected
                ? View.VISIBLE
                : View.INVISIBLE);
    }

}
