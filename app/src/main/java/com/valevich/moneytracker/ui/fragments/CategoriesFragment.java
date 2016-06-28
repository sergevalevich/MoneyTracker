package com.valevich.moneytracker.ui.fragments;


import android.animation.Animator;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.Feature;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.CategoriesRemovedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryAddedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryUpdatedEvent;
import com.valevich.moneytracker.ui.taskshandlers.RemoveCategoriesTask;
import com.valevich.moneytracker.utils.ClickListener;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.util.List;

@OptionsMenu(R.menu.search_menu)
@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends Fragment implements ClickListener, Transaction.Error, Transaction.Success {

    private static final String SEARCH_ID = "search_id";
    private static final String TAG = CategoriesFragment.class.getSimpleName();
    @ViewById(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    @ViewById(R.id.swipeToRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @OptionsMenuItem(R.id.action_search)
    MenuItem mSearchMenuItem;

    @Bean
    UserNotifier mUserNotifier;

    @StringRes(R.string.wrong_category_message)
    String mWrongCategoryNameMessage;

    @StringRes(R.string.search_hint)
    String mSearchHint;

    @StringRes(R.string.new_expense_cancel_warning)
    String mCancelMessage;

    @StringRes(R.string.new_expense_save_message)
    String mSaveMessage;

    @StringRes(R.string.new_expense_error_saving_message)
    String mSaveErrorMessage;

    @StringRes(R.string.dialog_title_new_category)
    String mNewCategoryDialogTitle;

    @StringRes(R.string.dialog_title_edit_category)
    String mEditCategoryDialogTitle;

    @ColorRes(R.color.colorPrimary)
    int mColorPrimary;

    @ColorRes(R.color.colorPrimaryDark)
    int mColorPrimaryDark;

    public CategoriesFragment() {
    }

    private static final int CATEGORIES_LOADER = 1;

    private CategoriesAdapter mCategoriesAdapter;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionModeCallback();

    private Dialog mDialog;

    private String mCategoryName;

    private int mCategoryId;

    @Override
    public void onResume() {
        super.onResume();
        loadCategories("");
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
        showDialog(mNewCategoryDialogTitle,"",null);
    }

    @Override
    public boolean onItemClick(int position) {
        if (mActionMode != null) {
            toggleSection(position);
        } else {
            CategoryEntry category = mCategoriesAdapter.getItem(position);
            showDialog(mEditCategoryDialogTitle,category.getName(),category);
        }
        return true;
    }

    @Override
    public boolean onItemLongClick(int position) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSection(position);
        return true;
    }

    @Override
    public void onError(Transaction transaction, Throwable error) {
        mDialog.dismiss();
        showToast(mSaveErrorMessage);
    }

    @Override
    public void onSuccess(Transaction transaction) {
        mDialog.dismiss();
        loadCategories("");
        showToast(mSaveMessage);
        String transactionType = transaction.name();
        if(transactionType.equals(CategoryEntry.TRANSACTION_TYPE_UPDATE))
            BusProvider.getInstance().post(new CategoryUpdatedEvent(mCategoryName,mCategoryId));
        else BusProvider.getInstance().post(new CategoryAddedEvent(mCategoryName));
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextual_action_bar, menu);
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
                    List<Integer> ids = mCategoriesAdapter.removeItems(mCategoriesAdapter.getSelectedItems());
                    mActionMode.finish();

                    BusProvider.getInstance().post(new CategoriesRemovedEvent(ids));

                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mCategoriesAdapter.clearSelection();
            mActionMode = null;
        }
    }

    private void setUpRecyclerView() {
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(mColorPrimary,mColorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCategories("");
            }
        });
    }

    private void loadCategories(final String filter) {
        getLoaderManager().restartLoader(CATEGORIES_LOADER, null, new LoaderManager.LoaderCallbacks<List<CategoryEntry>>() {
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
                mSwipeRefreshLayout.setRefreshing(false);
                mCategoriesAdapter = (CategoriesAdapter) mCategoriesRecyclerView.getAdapter();
                if (mCategoriesAdapter == null) {
                    mCategoriesAdapter = new CategoriesAdapter(data, CategoriesFragment.this);
                    mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
                } else {
                    mCategoriesAdapter.refresh(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<CategoryEntry>> loader) {

            }
        });
    }

    private void toggleSection(int position) {
        mCategoriesAdapter.toggleSelection(position);
        int selectedItemsCount = mCategoriesAdapter.getSelectedItemCount();
        if (selectedItemsCount == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(selectedItemsCount));
            mActionMode.invalidate();
        }
    }

    private void customizeSearchViewOld(SearchView searchView) {
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);

        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(mColorPrimary);
        }

        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView search = (ImageView) searchView.findViewById(searchImgId);

        if (search != null) {
            search.setImageResource(R.drawable.ic_action_search);
        }

        int closeImgId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView close = (ImageView) searchView.findViewById(closeImgId);

        if (close != null) {
            close.setImageResource(R.drawable.ic_clear);
            close.setAlpha(0.4f);
        }

    }

    private void showToast(String text) {
        Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();
    }

    private void showDialog(String title, final String text, final CategoryEntry category) {
        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_add_category);


        TextView saveCategoryButton = (TextView) mDialog.findViewById(R.id.saveCategoryButton);
        TextView cancelButton = (TextView) mDialog.findViewById(R.id.cancelButton);
        final AppCompatEditText categoryNameField = (AppCompatEditText) mDialog.findViewById(R.id.category_name_field);
        TextView titleView = (TextView) mDialog.findViewById(R.id.dialog_title);
        titleView.setText(title);
        categoryNameField.setText(text);

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable editable = categoryNameField.getText();
                if (!TextUtils.isEmpty(editable) && !TextUtils.equals(editable,text)) {
                    mCategoryName = editable.toString();
                    if(!mCategoryName.equals(CategoryEntry.DEFAULT_CATEGORY_NAME)) {
                        if(category != null) mCategoryId = (int) category.getId();
                        CategoryEntry.saveCategory(category
                                , mCategoryName
                                , CategoriesFragment.this
                                , CategoriesFragment.this);
                    } else {
                        mUserNotifier.notifyUser(mRootLayout,mWrongCategoryNameMessage);
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                showToast(mCancelMessage);
            }
        });
        mDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        mDialog.show();
    }

}
