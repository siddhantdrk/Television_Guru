package com.app.televisionguru;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.app.televisionguru.databinding.ActivityMainBinding;
import com.app.televisionguru.databinding.AppBarMainBinding;
import com.app.televisionguru.databinding.NavHeaderMainBinding;
import com.app.televisionguru.ui.AboutActivity;
import com.app.televisionguru.ui.AddNameActivity;
import com.app.televisionguru.ui.gallery.TelevisionFragment;
import com.app.televisionguru.ui.home.AnimsFragment;
import com.app.televisionguru.ui.slideshow.MoviesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
        DrawerLayout drawer = binding.drawerLayout;
        NavHeaderMainBinding navigationView = binding.navView;

        AppBarMainBinding materialToolbar = binding.appBarMain;
        materialToolbar.toolbar.setNavigationOnClickListener(view -> {
            drawer.open();
        });
        navigationView.addName.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AddNameActivity.class)));
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new AnimsFragment()).commit();

    }

    private BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.animes:
                selectedFragment = new AnimsFragment();
                break;
            case R.id.movies:
                selectedFragment = new MoviesFragment();
                break;
            case R.id.television:
                selectedFragment = new TelevisionFragment();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, selectedFragment)
                .commit();
        return true;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_about: {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}