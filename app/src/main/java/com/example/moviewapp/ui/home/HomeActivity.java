package com.example.moviewapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.HomeMovieAdapter;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.MovieResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.content.Intent;
import android.widget.ImageButton;
import com.example.moviewapp.ui.movie.SearchActivity;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRecentMovies;
    private RecyclerView rvWatchlist;
    private LinearLayout layoutEmptyWatchlist;
    private ImageButton btnAddWatchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton btnAddWatchlist = findViewById(R.id.btnAddWatchlist);
        ImageButton btnExploreMovie = findViewById(R.id.btnExploreMovie);

        btnAddWatchlist.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        btnExploreMovie.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        rvRecentMovies = findViewById(R.id.rvRecentMovies);
        rvWatchlist = findViewById(R.id.rvWatchlist);
        layoutEmptyWatchlist = findViewById(R.id.layoutEmptyWatchlist);
        btnAddWatchlist = findViewById(R.id.btnAddWatchlist);

        rvRecentMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvWatchlist.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

// Karena watchlist masih kosong
        rvWatchlist.setVisibility(View.GONE);
        layoutEmptyWatchlist.setVisibility(View.VISIBLE);

        btnAddWatchlist.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    com.example.moviewapp.ui.movie.SearchActivity.class
            );

            startActivity(intent);

        });
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

                            Log.d("HOME", "Movie : "
                                    + response.body().getResults().size());

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