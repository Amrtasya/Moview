package com.example.moviewapp.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.api.ApiService;
import com.example.moviewapp.api.RetrofitClient;
import com.example.moviewapp.model.Movie;
import com.example.moviewapp.ui.diary.ReviewMovieActivity; // Import halaman review Rahma

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView imgBackdrop, imgPoster;

    private TextView tvTitle;
    private TextView tvGenreYear;
    private TextView tvRuntime;
    private TextView tvRelease;
    private TextView tvLanguage;
    private TextView tvOverview;

    private Button btnReview;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        imgBackdrop = findViewById(R.id.imgBackdrop);
        imgPoster = findViewById(R.id.imgPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenreYear = findViewById(R.id.tvGenreYear);
        tvRuntime = findViewById(R.id.tvRuntime);
        tvOverview = findViewById(R.id.tvOverview);
        btnReview = findViewById(R.id.btnReview);
        tvRelease = findViewById(R.id.tvRelease);
        tvLanguage = findViewById(R.id.tvLanguage);
        movieId = getIntent().getIntExtra("movie_id", 0);

        if (movieId != 0) {
            loadMovieDetail();
        }
    }

    private void loadMovieDetail() {

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        apiService.getMovieDetail(
                movieId,
                "ce0282febe66aa78d512db45971aee56"
        ).enqueue(new Callback<Movie>() {

            @Override
            public void onResponse(Call<Movie> call,
                                   Response<Movie> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Movie movie = response.body();
                    tvRuntime.setText("Runtime : " + movie.getRuntime() + " min");

                    tvRelease.setText("Release : " + movie.getRelease_date());

                    tvLanguage.setText(
                            "Language : " +
                                    movie.getOriginal_language().toUpperCase()
                    );

                    if(movie.getOverview()==null || movie.getOverview().isEmpty()){
                        tvOverview.setText("No synopsis available.");
                    }else{
                        tvOverview.setText(movie.getOverview());
                    }

                    tvTitle.setText(movie.getTitle());

                    tvGenreYear.setText(
                            movie.getGenreName() +
                                    " • " +
                                    movie.getYear()
                    );

                    Glide.with(MovieDetailActivity.this)
                            .load(movie.getPosterUrl())
                            .into(imgPoster);

                    Glide.with(MovieDetailActivity.this)
                            .load(movie.getBackdropUrl())
                            .into(imgBackdrop);

                    // ===============================================================
                    // LOGIKA KLIK: Hubungkan tombol review ke halaman milik Rahma
                    // ===============================================================
                    btnReview.setOnClickListener(v -> {
                        Intent intent = new Intent(MovieDetailActivity.this, ReviewMovieActivity.class);

                        // Kirim data film yang sukses diambil dari API TMDB ke halaman Rahma
                        intent.putExtra("TMDB_ID", movie.getId());
                        intent.putExtra("MOVIE_TITLE", movie.getTitle());
                        intent.putExtra("POSTER_PATH", movie.getPoster_path());
                        intent.putExtra("GENRE", movie.getGenreName());
                        intent.putExtra("RELEASE_YEAR", movie.getYear());
                        intent.putExtra("DIRECTOR", "TMDB Movie"); // Fallback default text jika model tidak menampung nama director

                        startActivity(intent);
                    });

                } else {

                    Toast.makeText(
                            MovieDetailActivity.this,
                            "Gagal mengambil detail film",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

                Toast.makeText(
                        MovieDetailActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();

            }
        });
    }
}