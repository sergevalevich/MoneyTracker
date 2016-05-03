package com.valevich.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.valevich.moneytracker.R;
import com.valevich.moneytracker.adapters.ExpenseAdapter;
import com.valevich.moneytracker.model.Expense;
import com.valevich.moneytracker.ui.activities.NewExpenseActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment {

    @ViewById(R.id.expenseList)
    RecyclerView mExpenseRecyclerView;
    @ViewById(R.id.fab)
    FloatingActionButton mFab;
    @ViewById(R.id.coordinator)
    CoordinatorLayout mRootLayout;
    private List<Expense> mExpenseList = new ArrayList<>();


    public ExpensesFragment() {
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
        addExampleData();
        ExpenseAdapter adapter = new ExpenseAdapter(mExpenseList);
        mExpenseRecyclerView.setAdapter(adapter);
        mExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //default data
    private void addExampleData() {
        for(int i = 1; i<31; i++) {
            mExpenseList.add(new Expense("Item " + i," " + i*1000));
        }
    }

}
