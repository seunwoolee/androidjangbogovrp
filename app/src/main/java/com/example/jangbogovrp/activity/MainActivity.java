package com.example.jangbogovrp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jangbogovrp.BuildConfig;
import com.example.jangbogovrp.R;
import com.example.jangbogovrp.adapter.CustomerListAdapter;
import com.example.jangbogovrp.fragment.MainFragment;
import com.example.jangbogovrp.fragment.MapsFragment;
import com.example.jangbogovrp.fragment.MapsFragment.IsAmButtonClicked;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.RouteD;
import com.example.jangbogovrp.model.User;
import com.example.jangbogovrp.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private static final String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSION = 200;

    private TabLayout tab_layout;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private boolean isAm = true;

    private Realm mRealm;
    private long mPressedTime;
    private ArrayList<RouteD> mRouteDS;
    private HttpService mHttpService;
    private final Callback<List<RouteD>> callback = new Callback<List<RouteD>>() {
        @Override
        public void onResponse(Call<List<RouteD>> call, Response<List<RouteD>> response) {
            if (response.isSuccessful()) {
                mRouteDS = (ArrayList<RouteD>) response.body();
                initMainFragment();
            }
        }

        @Override
        public void onFailure(Call<List<RouteD>> call, Throwable t) {

        }
    };
    private final Callback<List<RouteD>> reLoadCallback = new Callback<List<RouteD>>() {
        @Override
        public void onResponse(Call<List<RouteD>> call, Response<List<RouteD>> response) {
            if (response.isSuccessful()) {
                mRouteDS = (ArrayList) response.body();
                reloadMapFragment();

            }
        }

        @Override
        public void onFailure(Call<List<RouteD>> call, Throwable t) {

        }
    };

    public void requestPermission() {
        for (String permission : needPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        needPermissions,
                        REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = true;

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionToRecordAccepted = false;
                    break;
                }
            }
        }

        if (!permissionToRecordAccepted) {
            Toast.makeText(MainActivity.this, "권한이 거부되었습니다. 권한을 승인해주세요.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
    }

    private final IsAmButtonClicked isAmButtonClicked = new IsAmButtonClicked() {
        @Override
        public void buttonClicked() {
            isAm = !isAm;
            Call<List<RouteD>> call = mHttpService.getRouteDs(isAm);
            call.enqueue(reLoadCallback);
        }
    };

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
        requestPermission();
        initToolbar();
        initComponent();
        initDrawerMenu();
        mRealm = Tools.initRealm(this);
        User user = mRealm.where(User.class).findFirst();

        if (!isLogin()) {
            goToLogin();
            return;
        }

        mHttpService = RetrofitClient.getHttpService(user.key);
        Call<List<RouteD>> call = mHttpService.getRouteDs(isAm);
        call.enqueue(callback);
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
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.light_blue_500), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initDrawerMenu() {
        final NavigationView nav_view = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                int item_id = item.getItemId();

                if (item_id == R.id.logout) {
                    mRealm.beginTransaction();
                    mRealm.where(User.class).findAll().deleteAllFromRealm();
                    mRealm.commitTransaction();
                    goToLogin();
                } else if (item_id == R.id.exit) {
                    finish();
                }

                return true;
            }


        });
    }

    private void initComponent() {
        tab_layout = findViewById(R.id.tab_layout);

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_equalizer), 0);
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_credit_card), 1);

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.quantum_white_100), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.light_blue_700), PorterDuff.Mode.SRC_IN);
//
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchFragment(position);
                tab.getIcon().setColorFilter(getResources().getColor(R.color.quantum_white_100), PorterDuff.Mode.SRC_IN);
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
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_90));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        CharSequence title = item.getTitle();
        if ("start".contentEquals(title)) {
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
        MapsFragment mapsFragment = getMapsFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(MapsFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MapsFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mapsFragment, MapsFragment.class.getName()).addToBackStack(MapsFragment.class.getName()).commit();
    }

    private void reloadMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(MapsFragment.class.getName(), POP_BACK_STACK_INCLUSIVE);
        MapsFragment mapsFragment = getMapsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.mainFragment, mapsFragment, MapsFragment.class.getName()).addToBackStack(MapsFragment.class.getName()).commit();
    }


    private MapsFragment getMapsFragment() {
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setIsAmButtonClicked(isAmButtonClicked);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAm", isAm);
        bundle.putParcelableArrayList("routeDs", mRouteDS);
        mapsFragment.setArguments(bundle);
        return mapsFragment;
    }


    private void initMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment mainFragment = new MainFragment(mHttpService);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("routeDs", mRouteDS);
        mainFragment.setArguments(bundle);
        Fragment fragment = fragmentManager.findFragmentByTag(MainFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MainFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mainFragment, MainFragment.class.getName()).addToBackStack(MainFragment.class.getName()).commit();
    }

    private void switchFragment(int position) {
        switch (position) {
            case 0:
                initMainFragment();
                break;
            case 1:
                initMapFragment();
                break;
        }
    }
}