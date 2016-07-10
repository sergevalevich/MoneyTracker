package com.valevich.moneytracker.ui.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.ActionItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ActionModeDestroyedEvent;
import com.valevich.moneytracker.eventbus.events.ExpenseItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ExpenseItemLongClickedEvent;
import com.valevich.moneytracker.ui.activities.NewExpenseActivity_;
import com.valevich.moneytracker.utils.ui.ActionModeHandler;
import com.valevich.moneytracker.utils.ui.ExpenseTouchHelper;
import com.valevich.moneytracker.utils.ui.ViewCustomizer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

@OptionsMenu(R.menu.search_menu)
@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment {

    private static final String SEARCH_ID = "search_id";
    private static final int EXPENSES_LOADER = 0;

    @ViewById(R.id.expenseList)
    RecyclerView mExpenseRecyclerView;

    @ViewById(R.id.fab)
    FloatingActionButton mFab;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    @ViewById(R.id.swipeToRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @OptionsMenuItem(R.id.action_search)
    MenuItem mSearchMenuItem;

    @StringRes(R.string.search_hint)
    String mSearchHint;

    @ColorRes(R.color.colorPrimary)
    int mRefreshColor;

    @Bean
    ViewCustomizer mViewCustomizer;

    @Bean
    ExpenseAdapter mExpenseAdapter;

    @Bean
    ActionModeHandler mActionModeHandler;

    @Bean
    ExpenseTouchHelper mExpenseTouchHelper;

    @Bean
    OttoBus mEventBus;

    private ActionMode mActionMode;

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
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

        //customize default searchView style for pre L devices because it looks ugly
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mViewCustomizer.customizeSearchView(searchView);
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

    @AfterViews
    void setupViews() {
        setUpRecyclerView();
        setupSwipeToRefresh();
    }

    @Subscribe
    public void onItemClick(ExpenseItemClickedEvent event) {
        if (mActionMode != null) {
            toggleSelection(event.getPosition());
        }
    }

    @Subscribe
    public void onItemLongClick(ExpenseItemLongClickedEvent event) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity())
                    .startSupportActionMode(mActionModeHandler);
        }

        toggleSelection(event.getPosition());
    }

    @Subscribe
    public void onActionItemClicked(ActionItemClickedEvent actionItemClickedEvent) {
        switch (actionItemClickedEvent.getMenuItem().getItemId()) {
            case R.id.menu_remove:
                mExpenseAdapter.removeDbAndAdapterItems(mExpenseAdapter.getSelectedItems());
                mActionMode.finish();
        }
    }

    @Subscribe
    public void onDestroyActionMode(ActionModeDestroyedEvent event) {
        mExpenseAdapter.clearSelection();
        mActionMode = null;
    }

    @Click(R.id.fab)
    void setupFab() {
        NewExpenseActivity_.intent(this).start().withAnimation(R.anim.enter_pull_in,R.anim.exit_fade_out);
    }

    @Background(delay = 700, id = SEARCH_ID)
    void queryExpenses(String filter) {
        loadExpenses(filter);
    }

    private void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(mRefreshColor);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadExpenses("");
            }
        });
    }

    private void setUpRecyclerView() {
        mExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadExpenses(final String filter) {
        getLoaderManager().restartLoader(EXPENSES_LOADER, null, new LoaderManager.LoaderCallbacks() {

            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader loader = new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        mExpenseAdapter.initAdapter(filter);
                        return null;
                    }
                };
                loader.forceLoad();
                return loader;
            }

            @Override
            public void onLoadFinished(Loader loader, Object data) {
                mSwipeRefreshLayout.setRefreshing(false);
                mExpenseRecyclerView.setAdapter(mExpenseAdapter);
                ItemTouchHelper.Callback callback = mExpenseTouchHelper;
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(mExpenseRecyclerView);
            }

            @Override
            public void onLoaderReset(Loader loader) {

            }
        });
    }

    private void toggleSelection(int position) {
        mExpenseAdapter.toggleSelection(position);
        int selectedItemsCount = mExpenseAdapter.getSelectedItemCount();
        if(selectedItemsCount == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(selectedItemsCount));
            mActionMode.invalidate();
        }
    }

}
