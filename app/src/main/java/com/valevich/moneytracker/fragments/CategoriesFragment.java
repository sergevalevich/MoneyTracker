package com.valevich.moneytracker.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.model.Category;
import com.valevich.moneytracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {

    @Bind(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;
    private List<Category> mCategoriesList = new ArrayList<>();


    public CategoriesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this,rootView);
        setUpRecyclerView();

        return rootView;
    }

    private void setUpRecyclerView() {
        addExampleData();
        CategoriesAdapter adapter = new CategoriesAdapter(mCategoriesList);
        mCategoriesRecyclerView.setAdapter(adapter);
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //default data
    private void addExampleData() {
        for(int i = 1; i<31; i++) {
            mCategoriesList.add(new Category("Category " + i));
        }
    }

}
