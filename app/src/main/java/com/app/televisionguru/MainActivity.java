package com.app.televisionguru;

import static java.util.Collections.reverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.ui.AppBarConfiguration;

import com.app.televisionguru.dao.Task;
import com.app.televisionguru.databinding.ActivityMainBinding;
import com.app.televisionguru.databinding.AppBarMainBinding;
import com.app.televisionguru.databinding.NavHeaderMainBinding;
import com.app.televisionguru.room.AppExecutors;
import com.app.televisionguru.room.DatabaseClient;
import com.app.televisionguru.ui.AboutActivity;
import com.app.televisionguru.ui.AddNameActivity;
import com.app.televisionguru.ui.AnimInterface;
import com.app.televisionguru.ui.MoviesInterface;
import com.app.televisionguru.ui.TelevisionInterface;
import com.app.televisionguru.ui.gallery.TelevisionFragment;
import com.app.televisionguru.ui.home.AnimsFragment;
import com.app.televisionguru.ui.slideshow.MoviesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    TextView tvName;
    int bottomBarSelectedIndex = 0;
    DrawerLayout drawer;
    Fragment selectedFragment = new AnimsFragment();
    AnimInterface animInterface;
    TelevisionInterface televisionInterface;
    MoviesInterface moviesInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                shuffleAndGetTask(view)
        );
        drawer = binding.drawerLayout;
        NavHeaderMainBinding navigationView = binding.navView;

        AppBarMainBinding materialToolbar = binding.appBarMain;
        materialToolbar.toolbar.setNavigationOnClickListener(view -> drawer.open());
        navigationView.addShuffle.setOnClickListener(view -> shuffleAndGetTask(view));
        navigationView.addSort.setOnClickListener(view -> {
            drawer.close();
            AppExecutors.getInstance().diskIO().execute(() -> {
                List<Task> tasks = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .getAllByType(bottomBarSelectedIndex == 0 ? "Animes" :
                                bottomBarSelectedIndex == 1 ? "Movies" : "Television");
                for (int a = 0; a< tasks.size(); a++){
                    tasks.get(a).setId(0);
                }
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao().deleteAll(bottomBarSelectedIndex == 0 ? "Animes" :
                        bottomBarSelectedIndex == 1 ? "Movies" : "Television");
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao().insertAll(tasks);
            });
        });

        navigationView.addClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.close();
                tvName.setText("");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .taskDao().deleteAll(bottomBarSelectedIndex == 0 ? "Animes" :
                                bottomBarSelectedIndex == 1 ? "Movies" : "Television");
                    }
                });
            }
        });
        navigationView.addName.setOnClickListener(view -> {
            drawer.close();
            startActivity(new Intent(MainActivity.this, AddNameActivity.class));
        });
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        tvName = findViewById(R.id.tvName);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, new AnimsFragment()).commit();

    }

    private void shuffleAndGetTask(View view) {
        drawer.close();
        tvName.setText("");
        AppExecutors.getInstance().diskIO().execute(() -> {
            Task tasks = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .taskDao().getRandomTask(bottomBarSelectedIndex == 0 ? "Animes" :
                            bottomBarSelectedIndex == 1 ? "Movies" : "Television");
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    if (tasks == null && tvName.getText().length() == 0) {
                        Snackbar.make(view, "No name is enabled", Snackbar.LENGTH_LONG)
                                .show();
                    } else if (tasks != null) {
                        setNameText(tasks.getName());
                    }
                }
            });
        });
    }

    private BottomNavigationView.OnItemSelectedListener navListener = item -> {
        switch (item.getItemId()) {
            case R.id.Animess:
                bottomBarSelectedIndex = 0;
                selectedFragment = new AnimsFragment();
                break;
            case R.id.movies:
                bottomBarSelectedIndex = 1;
                selectedFragment = new MoviesFragment();
                break;
            case R.id.television:
                bottomBarSelectedIndex = 2;
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

    public void setNameText(String name) {
        tvName.setText(name);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isOpen()) {
            drawer.close();
        } else {
            super.onBackPressed();
        }

    }

    public void passAnimVal(AnimInterface animInterface) {
        this.animInterface = animInterface;
    }

    public void passMoviesVal(MoviesInterface moviesInterface) {
        this.moviesInterface = moviesInterface;
    }

    public void passTelevisionVal(TelevisionInterface televisionInterface) {
        this.televisionInterface = televisionInterface;
    }
}