package com.valevich.moneytracker.ui.activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.database.data.ExpenseEntry;
import com.valevich.moneytracker.eventbus.buses.BusProvider;
import com.valevich.moneytracker.eventbus.events.CategoriesRemovedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryAddedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryUpdatedEvent;
import com.valevich.moneytracker.eventbus.events.QueryFinishedEvent;
import com.valevich.moneytracker.eventbus.events.QueryStartedEvent;
import com.valevich.moneytracker.eventbus.events.SyncFinishedEvent;
import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;
import com.valevich.moneytracker.ui.fragments.CategoriesFragment_;
import com.valevich.moneytracker.ui.fragments.ExpensesFragment_;
import com.valevich.moneytracker.ui.fragments.SettingsFragment_;
import com.valevich.moneytracker.ui.fragments.StatisticsFragment_;
import com.valevich.moneytracker.ui.taskshandlers.AddCategoryTask;
import com.valevich.moneytracker.ui.taskshandlers.LogoutTask;
import com.valevich.moneytracker.ui.taskshandlers.RemoveCategoriesTask;
import com.valevich.moneytracker.ui.taskshandlers.UpdateCategoryTask;
import com.valevich.moneytracker.utils.ImageLoader;
import com.valevich.moneytracker.utils.NetworkStatusChecker;
import com.valevich.moneytracker.utils.UserNotifier;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOOLBAR_TITLE_KEY = "TOOLBAR_TITLE";

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;
    @ViewById(R.id.navigation_view)
    NavigationView mNavigationView;
    @ViewById(R.id.progress_spinner)
    ProgressBar mProgressBar;

    @Bean
    ImageLoader mImageLoader;

    @Bean
    @NonConfigurationInstance
    LogoutTask mLogoutTask;

    @NonConfigurationInstance
    @Bean
    RemoveCategoriesTask mRemoveCategoriesTask;

    @NonConfigurationInstance
    @Bean
    AddCategoryTask mAddCategoryTask;

    @NonConfigurationInstance
    @Bean
    UpdateCategoryTask mUpdateCategoryTask;

    @Bean
    NetworkStatusChecker mNetworkStatusChecker;

    @Bean
    UserNotifier mUserNotifier;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    private ActionBarDrawerToggle mToggle;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TrackerSyncAdapter.initializeSyncAdapter(this);

        if(savedInstanceState == null) {
            replaceFragment(new ExpensesFragment_());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @AfterViews
    void setupViews() {
        setupActionBar();
        setupDrawerLayout();
        setupFragmentManager();
    }

    private void logout() {
        if (mNetworkStatusChecker.isNetworkAvailable()) {
            mLogoutTask.requestSync();
        } else {
            mUserNotifier.notifyUser(mDrawerLayout, mNetworkUnavailableMessage);
        }
    }

    @Subscribe
    public void onLastSyncFinished(SyncFinishedEvent syncFinishedEvent) {
        mLogoutTask.onSyncFinished();
    }

    @Subscribe
    public void onCategoriesRemoved(CategoriesRemovedEvent categoriesRemovedEvent) {
        startProgressBar();
        mRemoveCategoriesTask.removeCategories(categoriesRemovedEvent.getIds());
    }

    @Subscribe
    public void onCategoryAdded(CategoryAddedEvent categoryAddedEvent) {
        startProgressBar();
        mAddCategoryTask.addCategory(categoryAddedEvent.getTitle());
    }

    @Subscribe
    public void onCategoryUpdated(CategoryUpdatedEvent categoryUpdatedEvent) {
        startProgressBar();
        mUpdateCategoryTask.updateCategory(categoryUpdatedEvent.getNewName(),categoryUpdatedEvent.getId());
    }

    @Subscribe
    public void onQueryStartedEvent(QueryStartedEvent queryStartedEvent) {
        startProgressBar();
    }

    @Subscribe
    public void onQueryFinished(QueryFinishedEvent queryFinishedEvent) {
        stopProgressBar();
    }

    private void stopProgressBar() {
        if(mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void startProgressBar() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
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
                    case R.id.drawer_exit:
                        logout();
                        break;
                }
                return true;
            }
        });
        View headerView = navigationView.getHeaderView(0);
        final ImageView profileImage = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView nameField = (TextView) headerView.findViewById(R.id.name);
        TextView emailField = (TextView) headerView.findViewById(R.id.email);

        String imageUrl = MoneyTrackerApplication_.getUserPhoto();
        String userFullName = MoneyTrackerApplication_.getUserFullName();
        String userEmail = MoneyTrackerApplication_.getUserEmail();

        nameField.setText(userFullName);
        emailField.setText(userEmail);

        if(MoneyTrackerApplication_.isGoogleTokenExist()) {
            mImageLoader.loadRoundedUserImage(profileImage, imageUrl);
        } else {
            mImageLoader.loadRoundedUserImage(profileImage,R.drawable.dummy_profile);
        }
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
}

