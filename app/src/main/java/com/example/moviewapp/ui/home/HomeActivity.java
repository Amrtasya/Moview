package com.example.moviewapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.HomeMovieAdapter;
import com.example.moviewapp.ui.diary.HistoryActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.example.moviewapp.adapter.HomeWatchlistAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.WatchlistEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.diary.WatchlistActivity;
import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRecentMovies;
    private RecyclerView rvWatchlist;
    private LinearLayout layoutEmptyWatchlist;
    private ImageButton btnAddWatchlist;
    private TextView tvSeeAllWatchlist;
    private int currentUserId;
    private DiaryDao diaryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences sharedPreferences =
                getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);

        currentUserId =
                sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);
        diaryDao = DatabaseClient.getInstance(this).diaryDao();

        // Bind View
        rvRecentMovies = findViewById(R.id.rvRecentMovies);

        rvWatchlist = findViewById(R.id.rvWatchlist);

        layoutEmptyWatchlist =
                findViewById(R.id.layoutEmptyWatchlist);

        btnAddWatchlist =
                findViewById(R.id.btnAddWatchlist);
        tvSeeAllWatchlist = findViewById(R.id.tvSeeAllWatchlist);

        // RecyclerView
        rvRecentMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        rvWatchlist.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // Tombol tambah watchlist
        btnAddWatchlist.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this,
                        WatchlistActivity.class))
        );

        tvSeeAllWatchlist.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this,
                        WatchlistActivity.class))
        );

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                return true;
            }

            if (id == R.id.menu_search) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            // SUDAH DIPERBAIKI: Langsung mengarah ke HistoryActivity
            if (id == R.id.menu_history) {
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            if (id == R.id.menu_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });

        // Load Recent Movies & Watchlist
        loadRecentMovies();
        loadWatchlist();
    }

    private void loadRecentMovies() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            List<DiaryEntity> watched =
                    diaryDao.getDiaryByStatus(currentUserId, "WATCHED");
            System.out.println("WATCHED SIZE = " + watched.size());

            for (DiaryEntity d : watched) {
                System.out.println(
                        d.getTitle() + " | " + d.getWatchStatus()
                );
            }

            runOnUiThread(() -> {

                HomeMovieAdapter adapter =
                        new HomeMovieAdapter(watched);

                rvRecentMovies.setAdapter(adapter);

            });

        });

        executor.shutdown();
    }
    @Override
    protected void onResume() {
        super.onResume();

        loadRecentMovies();
        loadWatchlist();
    }
    private void loadWatchlist() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            List<WatchlistEntity> watchlist =
                    DatabaseClient.getInstance(HomeActivity.this)
                            .watchlistDao()
                            .getWatchlistByUser(currentUserId);

            runOnUiThread(() -> {

                if (watchlist == null || watchlist.isEmpty()) {

                    rvWatchlist.setVisibility(View.GONE);
                    layoutEmptyWatchlist.setVisibility(View.VISIBLE);

                } else {

                    rvWatchlist.setVisibility(View.VISIBLE);
                    layoutEmptyWatchlist.setVisibility(View.GONE);

                    HomeWatchlistAdapter adapter =
                            new HomeWatchlistAdapter(
                                    HomeActivity.this,
                                    watchlist
                            );

                    rvWatchlist.setAdapter(adapter);

                }

            });

        });

        executor.shutdown();
    }
}