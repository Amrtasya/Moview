package com.example.moviewapp.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.MovieAdapter;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.MovieResponse;
import com.example.moviewapp.ui.diary.HistoryActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvMovies;
    private LinearLayout layoutEmpty;
    private Button btnGenre;
    private Button btnYear;
    private Button btnRating;
    private Button btnAll;
    private String selectedGenre = "";
    private String selectedYear = "";
    private String selectedRating = "";

    private String getGenreId(String genre) {
        switch (genre) {
            case "Action": return "28";
            case "Adventure": return "12";
            case "Animation": return "16";
            case "Comedy": return "35";
            case "Drama": return "18";
            case "Fantasy": return "14";
            case "Horror": return "27";
            case "Sci-Fi": return "878";
            default: return "";
        }
    }

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_search);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_search) {
                return true;
            }

            if (id == R.id.menu_home) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.menu_history) {
                Intent intent = new Intent(SearchActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.menu_profile) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        // ... (sisanya tidak saya ubah sama sekali agar tidak error)
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
                selectedGenre = item.getTitle().toString();
                btnGenre.setText(selectedGenre);
                loadFilteredMovies();
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
                selectedYear = item.getTitle().toString();
                btnYear.setText(selectedYear);
                loadFilteredMovies();
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
                String ratingText = item.getTitle().toString();
                btnRating.setText(ratingText);

                switch (ratingText) {
                    case "⭐ 1+": selectedRating = "2"; break;
                    case "⭐ 2+": selectedRating = "4"; break;
                    case "⭐ 3+": selectedRating = "6"; break;
                    case "⭐ 4+": selectedRating = "8"; break;
                    case "⭐ 5": selectedRating = "9"; break;
                }

                loadFilteredMovies();
                return true;
            });
            popup.show();
        });

        btnAll.setOnClickListener(v -> {
            selectedGenre = "";
            selectedYear = "";
            selectedRating = "";

            btnGenre.setText("Genre ▼");
            btnYear.setText("Year ▼");
            btnRating.setText("Rating ▼");

            loadFilteredMovies();
        });

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();

            if (!keyword.isEmpty()) {
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

                apiService.searchMovies("ce0282febe66aa78d512db45971aee56", keyword)
                        .enqueue(new Callback<MovieResponse>() {
                            @Override
                            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                                if (response.body() != null && response.body().getResults().isEmpty()) {
                                    layoutEmpty.setVisibility(View.VISIBLE);
                                    rvMovies.setVisibility(View.GONE);
                                } else if (response.body() != null) {
                                    layoutEmpty.setVisibility(View.GONE);
                                    rvMovies.setVisibility(View.VISIBLE);

                                    MovieAdapter adapter = new MovieAdapter(response.body().getResults());
                                    rvMovies.setAdapter(adapter);

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieResponse> call, Throwable t) {
                                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("TMDB", t.getMessage());
                            }
                        });
            }
        });
    }

    private void loadFilteredMovies() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.discoverMovies(
                "ce0282febe66aa78d512db45971aee56",
                getGenreId(selectedGenre),
                selectedYear,
                selectedRating
        ).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieAdapter adapter = new MovieAdapter(response.body().getResults());
                    rvMovies.setAdapter(adapter);

                    layoutEmpty.setVisibility(View.GONE);
                    rvMovies.setVisibility(View.VISIBLE);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}