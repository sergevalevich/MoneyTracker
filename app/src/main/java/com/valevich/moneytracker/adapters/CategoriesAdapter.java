package com.valevich.moneytracker.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.valevich.moneytracker.adapters.util.CategoriesFinder;
import com.valevich.moneytracker.adapters.views.CategoryItemView;
import com.valevich.moneytracker.adapters.views.CategoryItemView_;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.CategoryItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryItemLongClickedEvent;
import com.valevich.moneytracker.utils.ClickListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by NotePad.by on 20.04.2016.
 */
@EBean(scope = EBean.Scope.Singleton)
public class CategoriesAdapter
        extends SelectableAdapter<CategoryEntry, CategoryItemView>
        implements ClickListener {

    @RootContext
    Context mContext;

    @Bean(CategoryEntry.class)
    CategoriesFinder mCategoriesFinder;

    public void initAdapter(String filter) {
        mItems = mCategoriesFinder.findAll(filter);
    }

    @Override
    protected CategoryItemView onCreateItemView(ViewGroup parent, int viewType) {
        return CategoryItemView_.build(mContext);
    }

    public List<Integer> removeDbAndAdapterItems(List<Integer> positions) {

        List<Integer> removedCategoriesIds = new ArrayList<>();

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            int id = removeItemFromDbAndAdapter(positions.get(0));
                removedCategoriesIds.add(id);
                positions.remove(0);
        }
        return removedCategoriesIds;
    }

    public int removeItemFromDbAndAdapter(int position) {
        CategoryEntry category = mItems.get(position);
        int id = 0;
        if (category!= null) {
            id = (int) category.getId();
            CategoryEntry.removeCategory(category);
            mItems.remove(position);
            notifyItemRemoved(position);
        }
        return id;
    }

    //Using eventBus here because AndroidAnnotations does not provide good solution for RecyclerView
    //click events
    @Override
    public boolean onItemClick(int position) {
        BusProvider.getInstance().post(new CategoryItemClickedEvent(position));
        return true;
    }

    @Override
    public boolean onItemLongClick(int position) {
        BusProvider.getInstance().post(new CategoryItemLongClickedEvent(position));
        return true;
    }
}
