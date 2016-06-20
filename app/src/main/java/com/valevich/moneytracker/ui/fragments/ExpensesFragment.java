package com.valevich.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;


import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.ui.activities.NewExpenseActivity_;
import com.valevich.moneytracker.utils.ClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.util.List;

@OptionsMenu(R.menu.search_menu)
@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment implements ClickListener {

    private static final String SEARCH_ID = "search_id";
    @ViewById(R.id.expenseList)
    RecyclerView mExpenseRecyclerView;
    @ViewById(R.id.fab)
    FloatingActionButton mFab;
    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;
    @OptionsMenuItem(R.id.action_search)
    MenuItem mSearchMenuItem;
    @StringRes(R.string.search_hint)
    String mSearchHint;
    @ColorRes(R.color.colorPrimary)
    int mPrimaryColor;

    private static final int EXPENSES_LOADER = 0;

    private ExpenseAdapter mExpenseAdapter;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionModeCallback();


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
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();

        //customize default searchview style for pre L devices because it looks ugly
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            customizeSearchViewOld(searchView);
        }


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

    private void customizeSearchViewOld(SearchView searchView) {
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);

        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(mPrimaryColor);
        }

        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView search = (ImageView) searchView.findViewById(searchImgId);

        if(search != null) {
            search.setImageResource(R.drawable.ic_action_search);
        }

        int closeImgId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView close = (ImageView) searchView.findViewById(closeImgId);

        if(close != null) {
            close.setImageResource(R.drawable.ic_clear);
            close.setAlpha(0.4f);
        }

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
                mExpenseAdapter = (ExpenseAdapter) mExpenseRecyclerView.getAdapter();
                if(mExpenseAdapter == null) {
                    mExpenseAdapter = new ExpenseAdapter(data,ExpensesFragment.this);
                    mExpenseRecyclerView.setAdapter(mExpenseAdapter);
                } else {
                    mExpenseAdapter.refresh(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<ExpenseEntry>> loader) {

            }
        });
    }

    private void toggleSection(int position) {
        mExpenseAdapter.toggleSelection(position);
        int selectedItemsCount = mExpenseAdapter.getSelectedItemCount();
        if(selectedItemsCount == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(selectedItemsCount));
            mActionMode.invalidate();
        }
    }

    @Override
    public boolean onItemClick(int position) {
        if(mActionMode != null) {
            toggleSection(position);
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemLongClick(int position) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSection(position);
        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextual_action_bar,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_remove:
                    mExpenseAdapter.removeItems(mExpenseAdapter.getSelectedItems());
                    mActionMode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mExpenseAdapter.clearSelection();
            mActionMode = null;
        }
    }

}
