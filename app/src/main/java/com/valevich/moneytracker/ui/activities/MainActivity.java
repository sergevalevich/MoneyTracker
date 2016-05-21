package com.valevich.moneytracker.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.MoneyTrackerDatabase;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.ui.fragments.CategoriesFragment_;
import com.valevich.moneytracker.ui.fragments.ExpensesFragment_;
import com.valevich.moneytracker.ui.fragments.SettingsFragment_;
import com.valevich.moneytracker.ui.fragments.StatisticsFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private String[] mDefaultCategories = {"Одежда","Бизнес","Налоги","Еда","Дом","Образование","ToCheckSearchItem1","ToCheckSearchItem2","ToCheckSearchItem3"};

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOOLBAR_TITLE_KEY = "TOOLBAR_TITLE";

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;
    @ViewById(R.id.navigation_view)
    NavigationView mNavigationView;
    @StringRes(R.string.prefs_filename)
    String mPrefsFileName;

    private ActionBarDrawerToggle mToggle;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfUserRegistered();

        if(CategoryEntry.getAllCategories("").isEmpty()) {
            saveDefaultCategories();
        }

        if(savedInstanceState == null) {
            replaceFragment(new ExpensesFragment_());
        }

    }

    @AfterViews
    void setupViews() {
        setupActionBar();
        setupDrawerLayout();
        setupFragmentManager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TOOLBAR_TITLE_KEY, String.valueOf(getTitle()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String toolBarTitle = savedInstanceState.getString(TOOLBAR_TITLE_KEY,getString(R.string.app_name));
        setTitle(toolBarTitle);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }

    }

    private void checkIfUserRegistered() {

//        SharedPreferences sharedPreferences = getSharedPreferences(mPrefsFileName,
//                Context.MODE_PRIVATE);
//        String defaultValue = "";
//        String token = sharedPreferences.getString(getResources().getString(R.string.token_key),defaultValue);
//
//        if(token.equals(defaultValue)) {
//            signUp();
//        }
        signUp();

    }

    private void signUp() {
        SignUpActivity_.intent(this).start();
        finish();
    }

    private void setupNavigationContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.drawer_expenses:
                        replaceFragment(new ExpensesFragment_());
                        break;
                    case R.id.drawer_categories:
                        replaceFragment(new CategoriesFragment_());
                        break;
                    case R.id.drawer_statistics:
                        replaceFragment(new StatisticsFragment_());
                        break;
                    case R.id.drawer_settings:
                        replaceFragment(new SettingsFragment_());
                        break;
                }
                return true;
            }
        });
    }

    private void changeToolbarTitle(String backStackEntryName) {
        if(backStackEntryName.equals(ExpensesFragment_.class.getName())) {
            setTitle(getString(R.string.nav_drawer_expenses));
            mNavigationView.setCheckedItem(R.id.drawer_expenses);
        } else if(backStackEntryName.equals(CategoriesFragment_.class.getName())) {
            setTitle(getString(R.string.nav_drawer_categories));
            mNavigationView.setCheckedItem(R.id.drawer_categories);
        } else if(backStackEntryName.equals(SettingsFragment_.class.getName())) {
            setTitle(getString(R.string.nav_drawer_settings));
            mNavigationView.setCheckedItem(R.id.drawer_settings);
        } else {
            setTitle(getString(R.string.nav_drawer_statistics));
            mNavigationView.setCheckedItem(R.id.drawer_statistics);
        }
    }


    private void setupDrawerLayout() {
        setupNavigationContent(mNavigationView);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                ,mToolbar
                ,R.string.navigation_drawer_open
                ,R.string.navigation_drawer_close);
        mToggle.syncState();
        mDrawerLayout.addDrawerListener(mToggle);
        setTitle(R.string.app_name);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void replaceFragment(Fragment fragment) {
        String backStackName = fragment.getClass().getName();

        boolean isFragmentPopped = mFragmentManager.popBackStackImmediate(backStackName,0);

        if(!isFragmentPopped && mFragmentManager.findFragmentByTag(backStackName) == null) {

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.main_container,fragment,backStackName);
            transaction.addToBackStack(backStackName);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();

        }
    }

    private void setupFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {

        Fragment f = mFragmentManager
                .findFragmentById(R.id.main_container);

        if(f != null) {
            changeToolbarTitle(f.getClass().getName());
        }

    }

    private void saveDefaultCategories() {

       final CategoryEntry[] categories = new CategoryEntry[mDefaultCategories.length];


        DatabaseDefinition database = FlowManager.getDatabase(MoneyTrackerDatabase.class);

        ProcessModelTransaction<CategoryEntry> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<CategoryEntry>() {
                    @Override
                    public void processModel(CategoryEntry category) {
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<CategoryEntry>() {
                    @Override
                    public void onModelProcessed(long current, long total, CategoryEntry category) {
                        category = new CategoryEntry();
                        category.setName(mDefaultCategories[(int) current]);
                        category.save();
                    }
                }).addAll(categories).build();

        Transaction transaction = database.beginTransactionAsync(processModelTransaction).build();
        transaction.execute();
    }
}

