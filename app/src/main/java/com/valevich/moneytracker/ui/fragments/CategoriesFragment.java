package com.valevich.moneytracker.ui.fragments;



import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
import com.valevich.moneytracker.database.data.CategoryEntry;

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
@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends Fragment {

    private static final String SEARCH_ID = "search_id";
    @ViewById(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    @OptionsMenuItem(R.id.action_search)
    MenuItem mSearchMenuItem;

    @StringRes(R.string.search_hint)
    String mSearchHint;

    @ColorRes(R.color.colorPrimary)
    int mPrimaryColor;

    public CategoriesFragment() {}

    private static final int CATEGORIES_LOADER = 1;

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
                BackgroundExecutor.cancelAll(SEARCH_ID,true);
                queryCategories(newText);
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

    @Background(delay = 700, id = SEARCH_ID)
    void queryCategories(String filter) {
        loadCategories(filter);
    }


    @AfterViews
    void setupViews() {
        setUpRecyclerView();
    }

    @Click(R.id.fab)
    void setupFab() {
        showSnackBar();
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
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                CategoriesAdapter adapter = (CategoriesAdapter) mCategoriesRecyclerView.getAdapter();
                if (adapter == null) {
                    mCategoriesRecyclerView.setAdapter(new CategoriesAdapter(data));
                } else {
                    adapter.refresh(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<CategoryEntry>> loader) {

            }
        });
    }

}
