package com.valevich.moneytracker.ui.fragments;



import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.CategoriesAdapter;
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
public class CategoriesFragment extends Fragment {

    @ViewById(R.id.categories_list)
    RecyclerView mCategoriesRecyclerView;
    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;
    private List<Category> mCategoriesList = new ArrayList<>();


    public CategoriesFragment() {

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
