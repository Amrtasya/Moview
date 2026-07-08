package com.example.moviewapp.ui.diary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.dao.FavoriteDao;
import com.example.moviewapp.data.dao.WatchlistDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.data.entity.FavoriteEntity;
import com.example.moviewapp.data.entity.WatchlistEntity;
import com.example.moviewapp.ui.auth.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewMovieActivity extends AppCompatActivity {

    private TextView btnBack, txtMovieTitle, txtMovieDirector, txtCharacterCount, btnDiscardChanges;
    private ImageView imgMovieBackdrop, btnFavorite;
    private RadioGroup rgWatchStatus;
    private EditText edtWatchDate, edtReview;
    private RatingBar ratingBar;
    private Button btnSaveExperience;

    private DiaryDao diaryDao;
    private WatchlistDao watchlistDao;
    private FavoriteDao favoriteDao;
    private int currentUserId;
    private boolean isMovieFavorite = false;

    private int tmdbId;
    private String movieTitle, posterPath, movieDirector, genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_movie);

        AppDatabase db = DatabaseClient.getInstance(this);
        diaryDao = db.diaryDao();
        watchlistDao = db.watchlistDao();
        favoriteDao = db.favoriteDao();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi habis, silakan login kembali", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        getIntentData();
        setupDatePicker();
        setupTextWatcher();
        setupClickListeners();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        imgMovieBackdrop = findViewById(R.id.imgMovieBackdrop);
        btnFavorite = findViewById(R.id.btnFavorite);
        txtMovieTitle = findViewById(R.id.txtMovieTitle);
        txtMovieDirector = findViewById(R.id.txtMovieDirector);
        txtCharacterCount = findViewById(R.id.txtCharacterCount);
        rgWatchStatus = findViewById(R.id.rgWatchStatus);
        edtWatchDate = findViewById(R.id.edtWatchDate);
        edtReview = findViewById(R.id.edtReview);
        ratingBar = findViewById(R.id.ratingBar);
        btnSaveExperience = findViewById(R.id.btnSaveExperience);
        btnDiscardChanges = findViewById(R.id.btnDiscardChanges);

        rgWatchStatus.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isWatchlist = (checkedId == R.id.rbWatchlist);
            edtWatchDate.setEnabled(!isWatchlist);
            ratingBar.setIsIndicator(isWatchlist);
            if (isWatchlist) ratingBar.setRating(0);
        });
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        edtWatchDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                edtWatchDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void getIntentData() {
        tmdbId = getIntent().getIntExtra("TMDB_ID", 0);
        movieTitle = getIntent().getStringExtra("MOVIE_TITLE");
        posterPath = getIntent().getStringExtra("POSTER_PATH");
        movieDirector = getIntent().getStringExtra("DIRECTOR");
        genre = getIntent().getStringExtra("GENRE");

        txtMovieTitle.setText(movieTitle != null ? movieTitle : "Unknown Movie");
        txtMovieDirector.setText(movieDirector != null ? movieDirector : "Unknown Director");

        if (posterPath != null) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w780" + posterPath).into(imgMovieBackdrop);
        }
    }

    private void setupTextWatcher() {
        edtReview.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtCharacterCount.setText(String.format(Locale.getDefault(), "%d/500", s.length()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnDiscardChanges.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            isMovieFavorite = !isMovieFavorite;
            btnFavorite.setImageResource(isMovieFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            btnFavorite.setColorFilter(isMovieFavorite ? Color.parseColor("#FFD700") : Color.WHITE);
            Toast.makeText(this, isMovieFavorite ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
        });

        btnSaveExperience.setOnClickListener(v -> saveExperienceToRoom());
    }

    private void saveExperienceToRoom() {
        int statusId = rgWatchStatus.getCheckedRadioButtonId();
        if (statusId == -1) {
            Toast.makeText(this, "Pilih status!", Toast.LENGTH_SHORT).show();
            return;
        }

        String watchStatus = (statusId == R.id.rbWatchlist) ? "WATCHLIST" : "WATCHED";
        String watchDate = edtWatchDate.getText().toString().trim();
        String reviewText = edtReview.getText().toString().trim();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            DiaryEntity existingDiary = diaryDao.getDiaryByTmdbId(currentUserId, tmdbId);

            if (existingDiary != null) {
                existingDiary.setWatchStatus(watchStatus);
                existingDiary.setReview(reviewText);
                existingDiary.setRating(ratingBar.getRating());
                existingDiary.setWatchDate(watchDate);
                existingDiary.setUpdatedAt(currentTime);
                existingDiary.setFavorite(isMovieFavorite);
                diaryDao.update(existingDiary);
            } else {
                diaryDao.insert(new DiaryEntity(currentUserId, tmdbId, movieTitle, movieDirector, genre, posterPath,
                        ratingBar.getRating(), reviewText, watchStatus, watchDate, isMovieFavorite, currentTime, currentTime));
            }

            if (watchStatus.equals("WATCHED")) {
                watchlistDao.deleteByMovieId(currentUserId, tmdbId);
            } else if (watchStatus.equals("WATCHLIST")) {
                watchlistDao.insert(new WatchlistEntity(currentUserId, tmdbId, movieTitle, posterPath, genre, watchDate));
            }

            if (isMovieFavorite) {
                favoriteDao.insert(new FavoriteEntity(currentUserId, tmdbId, movieTitle, posterPath, watchDate, genre, (double) ratingBar.getRating()));
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show();
                // Selalu arahkan ke HistoryActivity
                Intent intent = new Intent(this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
        executor.shutdown();
    }
}