package com.valevich.moneytracker.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.valevich.moneytracker.adapters.util.CategoriesFinder;
import com.valevich.moneytracker.adapters.views.CategoryItemView;
import com.valevich.moneytracker.adapters.views.CategoryItemView_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.CategoryItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryItemLongClickedEvent;
import com.valevich.moneytracker.utils.ui.ClickListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class CategoriesAdapter
        extends SelectableAdapter<CategoryEntry, CategoryItemView>
        implements ClickListener {

    @RootContext
    Context mContext;

    @Bean(CategoryEntry.class)
    CategoriesFinder mCategoriesFinder;

    @Bean
    OttoBus mEventBus;

    public void initAdapter(String filter) {
        mItems = mCategoriesFinder.findAll(filter);
    }

    public List<Integer> removeDbAndAdapterItems(List<Integer> positions) {

        List<Integer> removedCategoriesIds = new ArrayList<>();
        List<CategoryEntry> categoriesToRemove = new ArrayList<>();

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            int position = positions.get(0);
            int id = 0;
            CategoryEntry category = mItems.get(position);
            if (category != null) {
                id = category.getServerId();
                categoriesToRemove.add(category);
                mItems.remove(position);
                notifyItemRemoved(position);
            }
            if (id != 0)
            removedCategoriesIds.add(id);
            positions.remove(0);
        }
        CategoryEntry.delete(categoriesToRemove, null, null);
        return removedCategoriesIds;
    }

    //Using eventBus here because AndroidAnnotations does not provide good solution for RecyclerView
    //click events
    @Override
    public boolean onItemClick(int position) {
        mEventBus.post(new CategoryItemClickedEvent(position));
        return true;
    }

    @Override
    public boolean onItemLongClick(int position) {
        mEventBus.post(new CategoryItemLongClickedEvent(position));
        return true;
    }

    @Override
    protected CategoryItemView onCreateItemView(ViewGroup parent, int viewType) {
        return CategoryItemView_.build(mContext);
    }
}
