package com.valevich.moneytracker.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.eventbus.buses.OttoBus;
import com.valevich.moneytracker.eventbus.events.CategoriesRemovedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryAddedEvent;
import com.valevich.moneytracker.eventbus.events.CategoryUpdatedEvent;
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
import com.valevich.moneytracker.utils.ui.ImageLoader;
import com.valevich.moneytracker.utils.ui.UserNotifier;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

import io.fabric.sdk.android.Fabric;


@EActivity
public class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener {

    private static final String TOOLBAR_TITLE_KEY = "TOOLBAR_TITLE";

    @ViewById(R.id.drawer_layout)
    DrawerLayout mRootLayout;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.navigation_view)
    NavigationView mNavigationView;

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
    UserNotifier mUserNotifier;

    @Bean
    OttoBus mEventBus;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this);
        TrackerSyncAdapter.initializeSyncAdapter(this);

        if(savedInstanceState == null) {
            replaceFragment(new ExpensesFragment_());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TOOLBAR_TITLE_KEY, String.valueOf(getTitle()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String toolBarTitle = savedInstanceState.getString(TOOLBAR_TITLE_KEY, getString(R.string.app_name));
        setTitle(toolBarTitle);
    }

    @AfterViews
    void setupViews() {
        setupActionBar();
        setupDrawerLayout();
        setupFragmentManager();
    }

    @Subscribe
    public void onSyncFinished(SyncFinishedEvent syncFinishedEvent) {
        if (syncFinishedEvent.isStopAfterSync())
            mLogoutTask.onSyncFinished();
    }

    @Subscribe
    public void onCategoriesRemoved(CategoriesRemovedEvent categoriesRemovedEvent) {
        mRemoveCategoriesTask.removeCategories(categoriesRemovedEvent.getIds());
    }

    @Subscribe
    public void onCategoryAdded(CategoryAddedEvent categoryAddedEvent) {
        mAddCategoryTask.addCategory(categoryAddedEvent.getTitle());
    }

    @Subscribe
    public void onCategoryUpdated(CategoryUpdatedEvent categoryUpdatedEvent) {
        mUpdateCategoryTask.updateCategory(categoryUpdatedEvent.getNewName(),categoryUpdatedEvent.getId());
    }

    @Override
    public void onBackPressed() {
        if (mRootLayout.isDrawerOpen(GravityCompat.START)) {
            mRootLayout.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onBackStackChanged() {

        Fragment f = mFragmentManager
                .findFragmentById(R.id.main_container);

        if (f != null) {
            changeToolbarTitle(f.getClass().getName());
        }

    }

    public View getRootView() {
        return mRootLayout;
    }

    private void logout() {
        mLogoutTask.requestSync();
    }

    private void setupNavigationContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (mRootLayout != null) {
                    mRootLayout.closeDrawer(GravityCompat.START);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mRootLayout
                , mToolbar
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        toggle.syncState();
        mRootLayout.addDrawerListener(toggle);
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
}

