package com.example.moviewapp.ui.movie;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.moviewapp.R;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.Movie;
import com.example.moviewapp.model.MovieResponse;
import com.example.moviewapp.adapter.MovieAdapter;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import android.content.Intent;

import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvMovies;
    private LinearLayout layoutEmpty;
    private Button btnGenre;
    private Button btnYear;
    private Button btnRating;
    private Button btnAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAll = findViewById(R.id.btnAll);
        btnGenre = findViewById(R.id.btnGenre);
        btnYear = findViewById(R.id.btnYear);
        btnRating = findViewById(R.id.btnRating);
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        layoutEmpty = findViewById(R.id.layoutEmpty);
        layoutEmpty.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.GONE);

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        bottomNav.setSelectedItemId(R.id.menu_search);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_search) {
                return true;
            }

            if (id == R.id.menu_home) {

                startActivity(
                        new Intent(SearchActivity.this,
                                HomeActivity.class));

                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            if (id == R.id.menu_profile) {

                startActivity(
                        new Intent(SearchActivity.this,
                                ProfileActivity.class));

                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });

        btnGenre.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(this, btnGenre);

            popup.getMenu().add("Action");
            popup.getMenu().add("Adventure");
            popup.getMenu().add("Animation");
            popup.getMenu().add("Comedy");
            popup.getMenu().add("Drama");
            popup.getMenu().add("Fantasy");
            popup.getMenu().add("Horror");
            popup.getMenu().add("Sci-Fi");

            popup.setOnMenuItemClickListener(item -> {

                btnGenre.setText(item.getTitle());

                return true;
            });

            popup.show();
        });

        btnYear.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(this, btnYear);

            popup.getMenu().add("2026");
            popup.getMenu().add("2025");
            popup.getMenu().add("2024");
            popup.getMenu().add("2023");
            popup.getMenu().add("2022");

            popup.setOnMenuItemClickListener(item -> {

                btnYear.setText(item.getTitle());

                return true;
            });

            popup.show();
        });

        btnRating.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(this, btnRating);

            popup.getMenu().add("⭐ 1+");
            popup.getMenu().add("⭐ 2+");
            popup.getMenu().add("⭐ 3+");
            popup.getMenu().add("⭐ 4+");
            popup.getMenu().add("⭐ 5");

            popup.setOnMenuItemClickListener(item -> {

                btnRating.setText(item.getTitle());

                return true;
            });

            popup.show();
        });

        btnAll.setOnClickListener(v -> {

            btnGenre.setText("Genre ▼");
            btnYear.setText("Year ▼");
            btnRating.setText("Rating ▼");

        });

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();

            if (!keyword.isEmpty()) {

                ApiService apiService =
                        RetrofitClient.getClient().create(ApiService.class);

                apiService.searchMovies(
                        "ce0282febe66aa78d512db45971aee56",
                        keyword
                ).enqueue(new Callback<MovieResponse>() {

                    @Override
                    public void onResponse(Call<MovieResponse> call,
                                           Response<MovieResponse> response) {

                        if (response.body().getResults().isEmpty()) {

                            layoutEmpty.setVisibility(View.VISIBLE);
                            rvMovies.setVisibility(View.GONE);

                        } else {

                            layoutEmpty.setVisibility(View.GONE);
                            rvMovies.setVisibility(View.VISIBLE);

                            MovieAdapter adapter =
                                    new MovieAdapter(response.body().getResults());

                            rvMovies.setAdapter(adapter);

                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call,
                                          Throwable t) {

                        Toast.makeText(SearchActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        Log.e("TMDB", t.getMessage());
                    }
                });
            }
        });
    }
}