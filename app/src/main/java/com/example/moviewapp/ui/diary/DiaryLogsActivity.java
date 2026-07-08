package com.example.moviewapp.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.DiaryAdapter;
import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class DiaryLogsActivity extends AppCompatActivity {

    private RecyclerView rvDiary;
    private TextView txtEmpty;
    private ImageView btnBack;
    private Chip chipAll, chipRating, chipGenre, chipYear;

    private DiaryDao diaryDao;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_logs);

        rvDiary = findViewById(R.id.rvDiary);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnBack = findViewById(R.id.btnBack);

        chipAll = findViewById(R.id.chipAll);
        chipRating = findViewById(R.id.chipRating);
        chipGenre = findViewById(R.id.chipGenre);
        chipYear = findViewById(R.id.chipYear);

        rvDiary.setLayoutManager(new LinearLayoutManager(this));

        diaryDao = DatabaseClient.getInstance(this).diaryDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi habis, silakan login kembali", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chipRating.setText("Rating ▼");
        chipGenre.setText("Watchlist");
        chipYear.setText("Favorite");

        loadDataAll();

        btnBack.setOnClickListener(v -> finish());
        setupFilterListeners();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataAll();
        changeChipSelectionStyle(chipAll);
        chipRating.setText("Rating ▼");
    }

    private void setupFilterListeners() {
        chipAll.setOnClickListener(v -> {
            chipRating.setText("Rating ▼");
            changeChipSelectionStyle(chipAll);
            loadDataAll();
        });

        chipRating.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(DiaryLogsActivity.this, chipRating);
            popup.getMenu().add("⭐ 1+");
            popup.getMenu().add("⭐ 2+");
            popup.getMenu().add("⭐ 3+");
            popup.getMenu().add("⭐ 4+");
            popup.getMenu().add("⭐ 5");

            popup.setOnMenuItemClickListener(item -> {
                String ratingText = item.getTitle().toString();
                chipRating.setText(ratingText + " ▼");
                changeChipSelectionStyle(chipRating);

                String val = ratingText.replaceAll("[^0-9]", "");
                float minRating = Float.parseFloat(val);

                List<DiaryEntity> allList = diaryDao.getDiaryByUser(currentUserId);
                List<DiaryEntity> filteredList = new ArrayList<>();
                for (DiaryEntity diary : allList) {
                    if (diary.getRating() >= minRating) {
                        filteredList.add(diary);
                    }
                }
                displayData(filteredList);
                return true;
            });
            popup.show();
        });

        chipGenre.setOnClickListener(v -> {
            chipRating.setText("Rating ▼");
            changeChipSelectionStyle(chipGenre);
            List<DiaryEntity> list = diaryDao.getWatchlist(currentUserId);
            displayData(list);
        });

        chipYear.setOnClickListener(v -> {
            chipRating.setText("Rating ▼");
            changeChipSelectionStyle(chipYear);
            List<DiaryEntity> list = diaryDao.getFavoriteMovies(currentUserId);
            displayData(list);
        });
    }

    private void loadDataAll() {
        List<DiaryEntity> list = diaryDao.getDiaryByUser(currentUserId);
        displayData(list);
    }

    private void displayData(List<DiaryEntity> list) {
        DiaryAdapter adapter = new DiaryAdapter(list);
        rvDiary.setAdapter(adapter);

        if (list == null || list.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            rvDiary.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvDiary.setVisibility(View.VISIBLE);
        }
    }

    private void changeChipSelectionStyle(Chip selectedChip) {
        chipAll.setChecked(false);
        chipRating.setChecked(false);
        chipGenre.setChecked(false);
        chipYear.setChecked(false);
        selectedChip.setChecked(true);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // Jika diklik Profile, tidak melakukan apa-apa (tidak menutup activity)
            if (id == R.id.menu_profile) { return true; }
            if (id == R.id.menu_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            }
            if (id == R.id.menu_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}