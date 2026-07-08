package com.example.moviewapp.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.HistoryAdapter;
import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView txtSort;
    private ImageView btnBack;
    private Chip chipAll, chipRating, chipGenre;

    private DiaryDao diaryDao;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        diaryDao = DatabaseClient.getInstance(this).diaryDao();
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            finish();
            return;
        }

        initView();
        setupFilterListeners();
        setupBottomNav();
        loadHistoryData();
    }

    private void initView() {
        rvHistory = findViewById(R.id.rvHistory);
        txtSort = findViewById(R.id.txtSort);
        btnBack = findViewById(R.id.btnBack);
        chipAll = findViewById(R.id.chipAll);
        chipRating = findViewById(R.id.chipRating);
        chipGenre = findViewById(R.id.chipGenre);

        btnBack.setOnClickListener(v -> finish());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFilterListeners() {
        chipAll.setOnClickListener(v -> {
            chipRating.setText("Rating ▼");
            chipGenre.setText("Genre ▼");
            loadHistoryData();
        });

        chipRating.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipRating);
            popup.getMenu().add("⭐ 1+");
            popup.getMenu().add("⭐ 2+");
            popup.getMenu().add("⭐ 3+");
            popup.getMenu().add("⭐ 4+");
            popup.getMenu().add("⭐ 5");

            popup.setOnMenuItemClickListener(item -> {
                String ratingText = item.getTitle().toString();
                chipRating.setText(ratingText + " ▼");
                String val = ratingText.replaceAll("[^0-9]", "");
                filterData("rating", val);
                return true;
            });
            popup.show();
        });

        chipGenre.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipGenre);
            String[] genres = {"Action", "Adventure", "Animation", "Comedy", "Drama", "Fantasy", "Horror", "Sci-Fi"};
            for (String g : genres) popup.getMenu().add(g);

            popup.setOnMenuItemClickListener(item -> {
                chipGenre.setText(item.getTitle() + " ▼");
                filterData("genre", item.getTitle().toString());
                return true;
            });
            popup.show();
        });

        SearchView svHistory = findViewById(R.id.svHistory);

        EditText searchEditText =
                svHistory.findViewById(androidx.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.GRAY);
    }

    private void filterData(String type, String value) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.execute(() -> {
                List<DiaryEntity> all =
                        diaryDao.getDiaryByStatus(currentUserId, "WATCHED");
                List<DiaryEntity> filtered = new ArrayList<>();
                for (DiaryEntity d : all) {
                    if (type.equals("rating") && d.getRating() >= Float.parseFloat(value)) filtered.add(d);
                    else if (type.equals("genre") && d.getGenre() != null && d.getGenre().contains(value)) filtered.add(d);
                }
                runOnUiThread(() -> displayData(filtered));
            });
        }
    }

    private void loadHistoryData() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.execute(() -> {
                List<DiaryEntity> list =
                        diaryDao.getDiaryByStatus(currentUserId, "WATCHED");
                runOnUiThread(() -> displayData(list));
            });
        }
    }

    private void displayData(List<DiaryEntity> list) {
        HistoryAdapter adapter = new HistoryAdapter(list, diary -> {
            Intent intent = new Intent(this, EditReviewActivity.class);
            intent.putExtra("DIARY_ID", diary.getId());
            startActivity(intent);
        });
        rvHistory.setAdapter(adapter);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_history);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (item.isChecked()) return false;

            Intent intent = null;
            if (id == R.id.menu_home) intent = new Intent(this, HomeActivity.class);
            else if (id == R.id.menu_search) intent = new Intent(this, SearchActivity.class);
            else if (id == R.id.menu_history) return true;
            else if (id == R.id.menu_profile) intent = new Intent(this, ProfileActivity.class);

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
    }
}