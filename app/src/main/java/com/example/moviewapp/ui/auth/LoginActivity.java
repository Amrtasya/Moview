package com.example.moviewapp.ui.auth;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviewapp.R;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.Movie;
import com.example.moviewapp.model.MovieResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        apiService.getPopularMovies("ce0282febe66aa78d512db45971aee56")
                .enqueue(new Callback<MovieResponse>() {

                    @Override
                    public void onResponse(
                            Call<MovieResponse> call,
                            Response<MovieResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            for (Movie movie :
                                    response.body().getResults()) {

                                Log.d("TMDB", movie.getTitle());
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<MovieResponse> call,
                            Throwable t) {

                        Log.e("TMDB", t.getMessage());
                    }
                });
    }
}