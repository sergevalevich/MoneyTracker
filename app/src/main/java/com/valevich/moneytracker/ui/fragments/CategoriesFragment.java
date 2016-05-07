package com.valevich.moneytracker.ui.fragments;



import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.model.Category;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CategoryEntry>> {

    @ViewById(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;

    public CategoriesFragment() {}

    private static final int CATEGORIES_LOADER = 1;

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
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

    private void loadCategories() {
        getLoaderManager().restartLoader(CATEGORIES_LOADER,null,this);
    }

    @Override
    public Loader<List<CategoryEntry>> onCreateLoader(int id, Bundle args) {
        final AsyncTaskLoader<List<CategoryEntry>> loader = new AsyncTaskLoader<List<CategoryEntry>>(getActivity()) {
            @Override
            public List<CategoryEntry> loadInBackground() {
                return CategoryEntry.getAllCategories();
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
}
