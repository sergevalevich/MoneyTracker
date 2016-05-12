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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.model.Expense;
import com.valevich.moneytracker.ui.activities.NewExpenseActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

@OptionsMenu(R.menu.search_menu)
@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment {

    private static final String SEARCH_ID = "search_id";
    @ViewById(R.id.expenseList)
    RecyclerView mExpenseRecyclerView;
    @ViewById(R.id.fab)
    FloatingActionButton mFab;
    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;
    @OptionsMenuItem(R.id.action_search)
    MenuItem mMenuItem;
    @StringRes(R.string.search_hint)
    String mSearchHint;

    private static final int EXPENSES_LOADER = 0;


    public ExpensesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses("");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) mMenuItem.getActionView();
        searchView.setQueryHint(mSearchHint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BackgroundExecutor.cancelAll(SEARCH_ID,true);
                queryExpenses(newText);
                return false;
            }
        });
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

    @Background(delay = 700, id = SEARCH_ID)
    void queryExpenses(String filter) {
        loadExpenses(filter);
    }

    private void loadExpenses(final String filter) {
        getLoaderManager().restartLoader(EXPENSES_LOADER, null, new LoaderManager.LoaderCallbacks<List<ExpenseEntry>>() {

            @Override
            public Loader<List<ExpenseEntry>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<ExpenseEntry>> loader = new AsyncTaskLoader<List<ExpenseEntry>>(getActivity()) {
                    @Override
                    public List<ExpenseEntry> loadInBackground() {
                        return ExpenseEntry.getAllExpenses(filter);
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
        });
    }
}
