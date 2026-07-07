package com.example.moviewapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.HomeMovieAdapter;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.MovieResponse;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.moviewapp.ui.DiaryLogsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRecentMovies;
    private RecyclerView rvWatchlist;
    private LinearLayout layoutEmptyWatchlist;
    private ImageButton btnAddWatchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // =========================
        // Bind View
        // =========================

        rvRecentMovies = findViewById(R.id.rvRecentMovies);
        rvWatchlist = findViewById(R.id.rvWatchlist);
        layoutEmptyWatchlist = findViewById(R.id.layoutEmptyWatchlist);
        btnAddWatchlist = findViewById(R.id.btnAddWatchlist);

        // =========================
        // RecyclerView
        // =========================

        rvRecentMovies.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        rvWatchlist.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        // =========================
        // Watchlist masih kosong
        // =========================

        rvWatchlist.setVisibility(View.GONE);
        layoutEmptyWatchlist.setVisibility(View.VISIBLE);

        // =========================
        // Tombol tambah watchlist
        // =========================

        btnAddWatchlist.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SearchActivity.class))
        );

        // =========================
        // Bottom Navigation
        // =========================

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

            if (id == R.id.menu_history) {

                startActivity(
                        new Intent(HomeActivity.this,
                                DiaryLogsActivity.class));

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

        // =========================
        // Load API TMDB
        // =========================

        loadPopularMovies();
    }

    private void loadPopularMovies() {

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        apiService.getPopularMovies("ce0282febe66aa78d512db45971aee56")
                .enqueue(new Callback<MovieResponse>() {

                    @Override
                    public void onResponse(Call<MovieResponse> call,
                                           Response<MovieResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            HomeMovieAdapter adapter =
                                    new HomeMovieAdapter(response.body().getResults());

                            rvRecentMovies.setAdapter(adapter);

                            Log.d("HOME",
                                    "Movie : " + response.body().getResults().size());

                        } else {

                            Toast.makeText(HomeActivity.this,
                                    "Gagal mengambil data",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call,
                                          Throwable t) {

                        Toast.makeText(HomeActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        Log.e("HOME", t.getMessage());
                    }
                });
    }
}