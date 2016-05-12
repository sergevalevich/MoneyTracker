package com.valevich.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.model.Expense;
import com.valevich.moneytracker.ui.activities.NewExpenseActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;


@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ExpenseEntry>> {

    @ViewById(R.id.expenseList)
    RecyclerView mExpenseRecyclerView;
    @ViewById(R.id.fab)
    FloatingActionButton mFab;
    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    private static final int EXPENSES_LOADER = 0;


    public ExpensesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }

    @AfterViews
    void setupViews() {
        setUpRecyclerView();
    }

    @Click(R.id.fab)
    void setupFab() {
        Intent intent = new Intent(getActivity(), NewExpenseActivity_.class);
        startActivity(intent);
    }

    private void setUpRecyclerView() {
        mExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadExpenses() {
        getLoaderManager().restartLoader(EXPENSES_LOADER,null,this);
    }

    @Override
    public Loader<List<ExpenseEntry>> onCreateLoader(int id, Bundle args) {
        final AsyncTaskLoader<List<ExpenseEntry>> loader = new AsyncTaskLoader<List<ExpenseEntry>>(getActivity()) {
            @Override
            public List<ExpenseEntry> loadInBackground() {
                return ExpenseEntry.getAllExpenses();
            }
        };
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<ExpenseEntry>> loader, List<ExpenseEntry> data) {
        ExpenseAdapter adapter = (ExpenseAdapter) mExpenseRecyclerView.getAdapter();
        if(adapter == null) {
            mExpenseRecyclerView.setAdapter(new ExpenseAdapter(data));
        } else {
            adapter.refresh(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ExpenseEntry>> loader) {

    }
}
