package com.valevich.moneytracker.fragments;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.coordinator)
    CoordinatorLayout mRootLayout;
    private List<Category> mCategoriesList = new ArrayList<>();


    public CategoriesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this,rootView);

        setUpRecyclerView();
        setupFab();

        return rootView;
    }

    private void setupFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar();
            }
        });
    }

    private void showSnackBar() {
        Snackbar.make(mRootLayout, "Hello. I am Snackbar!", Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
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
