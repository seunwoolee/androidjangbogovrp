package com.example.jangbogovrp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.adapter.CustomerListAdapter;
import com.example.jangbogovrp.fragment.MainFragment;
import com.example.jangbogovrp.fragment.MapsFragment;
import com.example.jangbogovrp.model.User;
import com.example.jangbogovrp.utils.Tools;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private TabLayout tab_layout;
//    private NestedScrollView nested_scroll_view;
    private Realm mRealm;
    private long mPressedTime;

    @Override
    public void onBackPressed() {
        if (mPressedTime == 0) {
            Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
            mPressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - mPressedTime);

            if (seconds > 2000) {
                Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                mPressedTime = 0;
            } else {
                super.onBackPressed();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initComponent();
        mRealm = Tools.initRealm(this);
        if (isLogin()) {
            initMapFragment();
        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isLogin() {
        User user = mRealm.where(User.class).findFirst();
        return user != null;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.light_blue_500), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
//        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_equalizer), 0);
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_credit_card), 1);

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.light_blue_100), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.light_blue_700), PorterDuff.Mode.SRC_IN);
//
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchFragment(position);
                tab.getIcon().setColorFilter(getResources().getColor(R.color.light_blue_100), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.light_blue_700), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.light_blue_500));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        CharSequence title = item.getTitle();
        if ("logout".equals(title)) {
            mRealm.beginTransaction();
            mRealm.where(User.class).findAll().deleteAllFromRealm();
            mRealm.commitTransaction();
            goToLogin();
        } else if ("start".equals(title)) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment = new MapsFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(MapsFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MapsFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mapsFragment, MapsFragment.class.getName()).addToBackStack(null).commit();
    }

    private void initMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment mainFragment = new MainFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(MainFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MainFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mainFragment, MainFragment.class.getName()).addToBackStack(null).commit();
    }

    private void switchFragment(int position) {
        switch (position) {
            case 0:
                initMapFragment();
                break;
            case 1:
                initMainFragment();
                break;
        }
    }


}