package com.app.televisionguru;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.app.televisionguru.databinding.ActivityMainBinding;
import com.app.televisionguru.databinding.AppBarMainBinding;
import com.app.televisionguru.databinding.NavHeaderMainBinding;
import com.app.televisionguru.ui.AddNameActivity;
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}