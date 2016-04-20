package com.valevich.moneytracker;

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
import android.util.Log;
import android.view.MenuItem;

import com.valevich.moneytracker.fragments.CategoriesFragment;
import com.valevich.moneytracker.fragments.ExpensesFragment;
import com.valevich.moneytracker.fragments.SettingsFragment;
import com.valevich.moneytracker.fragments.StatisticsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            replaceFragment(new ExpensesFragment());
        }

        setupActionBar();
        setupDrawerLayout();

    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }

    }

    private void setupNavigationContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(mDrawerLayout != null) {
                    item.setChecked(true);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.drawer_expenses:
                        replaceFragment(new ExpensesFragment());
                        break;
                    case R.id.drawer_categories:
                        replaceFragment(new CategoriesFragment());
                        break;
                    case R.id.drawer_statistics:
                        replaceFragment(new StatisticsFragment());
                        break;
                    case R.id.drawer_settings:
                        replaceFragment(new SettingsFragment());
                }
                return true;
            }
        });
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
        FragmentManager manager = getSupportFragmentManager();

        boolean isFragmentPopped = manager.popBackStackImmediate(backStackName,0);

        if(!isFragmentPopped && manager.findFragmentByTag(backStackName) == null) {

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_container,fragment,backStackName);
            transaction.addToBackStack(backStackName);
            transaction.commit();

        }
    }

}

