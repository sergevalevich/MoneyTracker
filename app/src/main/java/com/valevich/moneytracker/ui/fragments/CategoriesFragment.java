package com.valevich.moneytracker.ui.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
import com.valevich.moneytracker.database.TransactionExecutor;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.ActionItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.ActionModeDestroyedEvent;
import com.valevich.moneytracker.eventbus.events.CategoriesRemovedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryAddedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryItemClickedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryItemLongClickedEvent;
import com.valevich.moneytracker.eventbus.events.CategorySaveButtonClickedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryUpdatedEvent;
import com.valevich.moneytracker.eventbus.events.SyncFinishedEvent;
import com.valevich.moneytracker.ui.fragments.dialogs.EditCategoryDialogFragment;
import com.valevich.moneytracker.ui.fragments.dialogs.EditCategoryDialogFragment_;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.InputFieldValidator;
import com.valevich.moneytracker.utils.ui.ActionModeHandler;
import com.valevich.moneytracker.utils.ui.ViewCustomizer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.List;

@OptionsMenu(R.menu.search_menu)
@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends Fragment
        implements Transaction.Error, Transaction.Success {

    private static final String SEARCH_ID = "search_id";
    private static final int CATEGORIES_LOADER = 1;
    private static final String CATEGORY_NAME_KEY = "CATEGORY_NAME";
    private static final String SELECTED_ITEMS_KEY = "SELECTED_ITEMS";

    @ViewById(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    @ViewById(R.id.swipeToRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @ViewById(R.id.fab)
    FloatingActionButton mFab;

    @OptionsMenuItem(R.id.action_search)
    MenuItem mSearchMenuItem;

    @StringRes(R.string.search_hint)
    String mSearchHint;

    @StringRes(R.string.new_expense_save_message)
    String mSaveMessage;

    @StringRes(R.string.new_expense_error_saving_message)
    String mSaveErrorMessage;

    @StringRes(R.string.dialog_title_new_category)
    String mNewCategoryDialogTitle;

    @StringRes(R.string.dialog_title_edit_category)
    String mEditCategoryDialogTitle;

    @ColorRes(R.color.colorPrimary)
    int mRefreshColor;

    @Bean
    CategoriesAdapter mCategoriesAdapter;

    @Bean
    ActionModeHandler mActionModeHandler;

    @Bean
    ViewCustomizer mViewCustomizer;

    @Bean
    OttoBus mEventBus;

    @Bean
    InputFieldValidator mInputFieldValidator;

    private EditCategoryDialogFragment mDialog;

    private ActionMode mActionMode;

    CategoryEntry mCategory;

    //needed to update category if sync happened when the dialog is shown
    @InstanceState
    int mCategoryPosition;

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
        loadCategories("");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCategory != null)
            outState.putString(CATEGORY_NAME_KEY, mCategory.getName());

        //save selected items when screen rotates
        outState.putIntegerArrayList(SELECTED_ITEMS_KEY
                , (ArrayList<Integer>) mCategoriesAdapter.getSelectedItems());
        onDestroyActionMode(null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCategory = CategoryEntry
                    .getCategory(savedInstanceState.getString(CATEGORY_NAME_KEY));
            List<Integer> selectedItems = savedInstanceState.getIntegerArrayList(SELECTED_ITEMS_KEY);
            if (selectedItems != null && selectedItems.size() != 0) {
                startActionMode();
                for (int position : selectedItems) toggleSelection(position);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();

        //customize default searchview style for pre L devices because it looks ugly
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
                BackgroundExecutor.cancelAll(SEARCH_ID, true);
                queryCategories(newText);
                return false;
            }
        });

    }

    @Background(delay = 700, id = SEARCH_ID)
    void queryCategories(String filter) {
        loadCategories(filter);
    }


    @AfterViews
    void setupViews() {
        setUpRecyclerView();
        setupSwipeToRefresh();
    }

    @Click(R.id.fab)
    void addCategory() {
        mCategory = null;
        showDialog(mNewCategoryDialogTitle);
    }

    @Subscribe
    public void onItemClick(CategoryItemClickedEvent event) {
        mCategoryPosition = event.getPosition();
        if (mActionMode != null) {
            toggleSelection(mCategoryPosition);
        } else {
            mCategory = mCategoriesAdapter.getItem(mCategoryPosition);
            showDialog(mEditCategoryDialogTitle);
        }
    }

    @Subscribe
    public void onItemLongClick(CategoryItemLongClickedEvent event) {
        startActionMode();
        toggleSelection(event.getPosition());
    }

    @Subscribe
    public void onSyncFinished(SyncFinishedEvent syncFinishedEvent) {
        if (!syncFinishedEvent.isSyncBeforeExit())
            loadCategories("");
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        loadCategories("");
        showToast(mSaveErrorMessage);
    }

    @Override
    public void onSuccess(Transaction transaction) {
        loadCategories("");
        showToast(mSaveMessage);
        String transactionType = transaction.name();
        if (transactionType.equals(TransactionExecutor.TRANSACTION_TYPE_UPDATE))
            notifyCategoryUpdated();
        else notifyCategoryAdded();
    }

    @Subscribe
    public void onActionItemClicked(ActionItemClickedEvent actionItemClickedEvent) {
        switch (actionItemClickedEvent.getMenuItem().getItemId()) {
            case R.id.menu_remove:
                List<Integer> ids = mCategoriesAdapter
                        .removeDbAndAdapterItems(mCategoriesAdapter.getSelectedItems());
                mActionMode.finish();
                if (!mFab.isShown()) mFab.show();//to prevent accidentally  hiding fab
                notifyCategoryRemoved(ids);
        }
    }

    @Subscribe
    public void onDestroyActionMode(ActionModeDestroyedEvent event) {
        mCategoriesAdapter.clearSelection();
        mActionMode = null;
    }

    @Subscribe
    public void onSaveClicked(CategorySaveButtonClickedEvent event) {
        String oldName = mCategory != null ? mCategory.getName() : "";
        String name = event.getInputText();
        if (mInputFieldValidator.isCategoryNameValid(name, oldName)) {
            List<CategoryEntry> categoriesToProcess = new ArrayList<>(1);
            if (mCategory != null) {
                mCategory = mCategoriesAdapter.getItem(mCategoryPosition);//if sync happened
                mCategory.setName(name);
                categoriesToProcess.add(mCategory);
                CategoryEntry.update(categoriesToProcess,
                        CategoriesFragment.this,
                        CategoriesFragment.this);
            } else {
                mCategory = new CategoryEntry();
                mCategory.setName(name);
                categoriesToProcess.add(mCategory);
                CategoryEntry.create(categoriesToProcess,
                        CategoriesFragment.this,
                        CategoriesFragment.this);
            }
        }
    }

    private void setUpRecyclerView() {
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(mRefreshColor);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCategories("");
            }
        });
    }

    private void startActionMode() {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity())
                    .startSupportActionMode(mActionModeHandler);
        }
    }

    private void loadCategories(final String filter) {
        getLoaderManager().restartLoader(CATEGORIES_LOADER, null, new LoaderManager.LoaderCallbacks() {
            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader loader = new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        mCategoriesAdapter.initAdapter(filter);
                        return null;
                    }
                };
                loader.forceLoad();
                return loader;
            }

            @Override
            public void onLoadFinished(Loader loader, Object data) {
                mSwipeRefreshLayout.setRefreshing(false);
                mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
            }

            @Override
            public void onLoaderReset(Loader loader) {

            }
        });
    }

    private void toggleSelection(int position) {
        mCategoriesAdapter.toggleSelection(position);
        int selectedItemsCount = mCategoriesAdapter.getSelectedItemCount();
        if (selectedItemsCount == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(selectedItemsCount));
            mActionMode.invalidate();
        }
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    private void showDialog(String title) {
        String inputText = mCategory != null ? mCategory.getName() : "";
        mDialog = EditCategoryDialogFragment_
                .builder()
                .title(title)
                .input(inputText)
                .build();
        if (mDialog != null)
            mDialog.show(getFragmentManager(), ConstantsManager.CATEGORY_DIALOG_TAG);
    }

    private void notifyCategoryAdded() {
        mEventBus.post(new CategoryAddedEvent(mCategory));
    }

    private void notifyCategoryUpdated() {
        mEventBus.post(new CategoryUpdatedEvent(mCategory));
    }

    private void notifyCategoryRemoved(List<Integer> ids) {
        mEventBus.post(new CategoriesRemovedEvent(ids));
    }

}
