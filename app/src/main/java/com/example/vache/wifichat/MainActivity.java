package com.example.vache.wifichat;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.MenuItem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void init() {
        mManager = (WifiP2pManager) Objects.requireNonNull(this).getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        Utils.getInstance().setManager(mManager);
        Utils.getInstance().setChannel(mChannel);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        if (id == R.id.nav_chat && navController.getCurrentDestination().getId() != R.id.thirdFragment) {
            if (navController.getCurrentDestination().getId() == R.id.firstFragment) {
                int n = navController.getCurrentDestination().getId() == R.id.firstFragment ? R.id.action_firstFragment_to_thirdFragment : R.id.action_secondFragment_to_thirdFragment;
                navController.navigate(n, new Bundle());
            }
        } else if (id == R.id.nav_history && (navController.getCurrentDestination().getId() != R.id.firstFragment)) {
            if (navController.getCurrentDestination().getId() == R.id.thirdFragment) {
                navController.navigate(R.id.action_thirdFragment_to_firstFragment);
            } else if (navController.getCurrentDestination().getId() == R.id.secondFragment) {
                navController.navigate(R.id.action_secondFragment_to_firstFragment);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
