package com.valevich.moneytracker.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {

    @ViewById(R.id.chart)
    PieChart mChart;

    @ColorRes(R.color.colorText)
    int mTextColor;

    @ColorRes(R.color.colorBackground)
    int mBackGroundColor;

    @StringRes(R.string.chart_total_center_text)
    String mTotalDefaultText;

    @Bean
    PriceFormatter mPriceFormatter;

    @AfterViews
    void setupChart() {
        mChart.setUsePercentValues(true);
        mChart.animateXY(600,600, Easing.EasingOption.EaseInOutCubic, Easing.EasingOption.EaseInCubic);
        mChart.setCenterTextSize(ConstantsManager.CHART_LABEL_CENTER_SIZE);
        mChart.setCenterTextColor(mTextColor);
        mChart.setHoleColor(mBackGroundColor);
        mChart.getLegend().setEnabled(false);
        mChart.setDescription("");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories("");
    }

    private void loadCategories(final String filter) {
        getLoaderManager().restartLoader(ConstantsManager.CATEGORIES_LOADER_ID,
                null,
                new LoaderManager.LoaderCallbacks<List<CategoryEntry>>() {
            @Override
            public Loader<List<CategoryEntry>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<CategoryEntry>> loader = new AsyncTaskLoader<List<CategoryEntry>>(getActivity()) {
                    @Override
                    public List<CategoryEntry> loadInBackground() {
                        return CategoryEntry.getAllCategories(filter);
                    }
                };
                loader.forceLoad();
                return loader;
            }

            @Override
            public void onLoadFinished(Loader<List<CategoryEntry>> loader, List<CategoryEntry> data) {
                fillChart(data);
            }

            @Override
            public void onLoaderReset(Loader<List<CategoryEntry>> loader) {

            }
        });
    }

    private void fillChart(final List<CategoryEntry> categories) {
        List<Entry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        int index = 0;
        for(CategoryEntry category:categories) {
            float total = category.getCategoryTotal();
            if(total != 0) {
                Entry entry = new Entry(total, index);
                String label = category.getName();
                entries.add(entry);
                labels.add(label);
                index++;
            }
        }

        PieDataSet pieDataSet = new PieDataSet(entries,"");
        int[] colors = getActivity().getResources().getIntArray(R.array.chartColors);
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new PercentFormatter());

        final PieData pieData = new PieData(labels, pieDataSet);
        pieData.setValueTextColor(mTextColor);
        pieData.setValueTextSize(ConstantsManager.CHART_LABEL_SIZE);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                mChart.setCenterText(String.format(Locale.getDefault(),
                        "%s %n %s",
                        labels.get(e.getXIndex()),
                        mPriceFormatter.formatCategoryTotal(e.getVal())));
            }

            @Override
            public void onNothingSelected() {
                setDefaultCenterText(categories);
            }
        });
        setDefaultCenterText(categories);
        mChart.setData(pieData);
        mChart.invalidate();
    }

    private float getTotal(List<CategoryEntry> categories) {
        float total = 0.f;
        for (CategoryEntry category:categories) {
            total+=category.getCategoryTotal();
        }
        return total;
    }

    private void setDefaultCenterText(List<CategoryEntry> categories) {
        mChart.setCenterText(String.format(Locale.getDefault(),
                "%s %n %s",
                mTotalDefaultText,
                mPriceFormatter.formatCategoryTotal(getTotal(categories))));
    }
}
