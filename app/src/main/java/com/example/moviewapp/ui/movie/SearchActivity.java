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


public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

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

                        if (response.isSuccessful() && response.body() != null) {

                            MovieAdapter adapter =
                                    new MovieAdapter(response.body().getResults());

                            rvMovies.setAdapter(adapter);

                            for (Movie movie : response.body().getResults()) {
                                Log.d("TMDB", movie.getTitle());
                            }

                        } else {
                            Toast.makeText(SearchActivity.this,
                                    "Data tidak ditemukan",
                                    Toast.LENGTH_SHORT).show();
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