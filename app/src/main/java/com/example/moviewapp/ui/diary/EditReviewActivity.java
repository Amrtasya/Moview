package com.example.moviewapp.ui.diary;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditReviewActivity extends AppCompatActivity {

    private EditText etReview, edtWatchDate;
    private RatingBar ratingBar;
    private RadioGroup rgWatchStatus;
    private Button btnEditEntry, btnDelete;
    private ImageView imgBackdrop;
    private LinearLayout btnBack; // Sudah diubah ke LinearLayout sesuai XML
    private TextView txtTitle, txtDirector;

    private int diaryId;
    private DiaryEntity diaryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        initView();
        setupDatePicker();

        diaryId = getIntent().getIntExtra("DIARY_ID", -1);
        if (diaryId != -1) {
            loadDiaryData(diaryId);
        } else {
            Toast.makeText(this, "Error: Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEditEntry.setOnClickListener(v -> updateReview());
        btnDelete.setOnClickListener(v -> deleteReview());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initView() {
        etReview = findViewById(R.id.etReview);
        ratingBar = findViewById(R.id.ratingBar);
        rgWatchStatus = findViewById(R.id.rgWatchStatus);
        edtWatchDate = findViewById(R.id.edtWatchDate);
        btnEditEntry = findViewById(R.id.btnEditEntry);
        btnDelete = findViewById(R.id.btnDelete);
        imgBackdrop = findViewById(R.id.imgBackdrop);
        txtTitle = findViewById(R.id.txtTitle);
        txtDirector = findViewById(R.id.txtDirector);
        // btnBack sekarang adalah LinearLayout
        btnBack = findViewById(R.id.btnBack);
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

    private void loadDiaryData(int id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                diaryData = DatabaseClient.getInstance(this).diaryDao().getDiaryById(id);
                if (diaryData != null && !isFinishing()) {
                    runOnUiThread(() -> {
                        etReview.setText(diaryData.getReview());
                        ratingBar.setRating(diaryData.getRating());
                        edtWatchDate.setText(diaryData.getWatchDate());

                        if ("WATCHLIST".equals(diaryData.getWatchStatus())) {
                            rgWatchStatus.check(R.id.rbWatchlist);
                        } else {
                            rgWatchStatus.check(R.id.rbWatched);
                        }

                        txtTitle.setText(diaryData.getTitle());
                        txtDirector.setText("Director: " + (diaryData.getDirector() != null ? diaryData.getDirector() : "-"));

                        if (diaryData.getPosterPath() != null && !diaryData.getPosterPath().isEmpty()) {
                            String fullUrl = "https://image.tmdb.org/t/p/w780" + diaryData.getPosterPath();
                            Glide.with(this).load(fullUrl).into(imgBackdrop);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    private void updateReview() {
        if (diaryData == null) return;

        int statusId = rgWatchStatus.getCheckedRadioButtonId();
        String newStatus = (statusId == R.id.rbWatchlist) ? "WATCHLIST" : "WATCHED";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            diaryData.setReview(etReview.getText().toString());
            diaryData.setRating(ratingBar.getRating());
            diaryData.setWatchDate(edtWatchDate.getText().toString());
            diaryData.setWatchStatus(newStatus);
            diaryData.setUpdatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            DatabaseClient.getInstance(this).diaryDao().update(diaryData);

            if (newStatus.equals("WATCHED")) {
                DatabaseClient.getInstance(this).watchlistDao().deleteByMovieId(
                        diaryData.getUserId(),
                        diaryData.getTmdbId()
                );
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Review updated!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
        executor.shutdown();
    }

    private void deleteReview() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            DatabaseClient.getInstance(this).diaryDao().deleteById(diaryId);
            runOnUiThread(() -> {
                Toast.makeText(this, "Review deleted.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
        executor.shutdown();
    }
}