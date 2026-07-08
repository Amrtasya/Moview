package com.example.moviewapp.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.FavoriteGridAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.FavoriteEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity; // Sesuaikan dengan lokasi Profilmu
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorite;
    private TextView txtEmpty;
    private ImageView btnBack;
    private EditText etSearchFavorites;
    private Chip chipAll, chipRating, chipGenre, chipYear;

    private List<FavoriteEntity> allFavoritesList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initView();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        setupDropdownListeners();
        setupBottomNav();
        loadFavoriteData();
    }

    private void initView() {
        rvFavorite = findViewById(R.id.rvFavorite);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnBack = findViewById(R.id.btnBack);
        etSearchFavorites = findViewById(R.id.etSearchFavorites);
        chipAll = findViewById(R.id.chipAll);
        chipYear = findViewById(R.id.chipYear);
        chipRating = findViewById(R.id.chipRating);
        chipGenre = findViewById(R.id.chipGenre);

        rvFavorite.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadFavoriteData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            allFavoritesList = DatabaseClient.getInstance(this).favoriteDao().getFavoritesByUser(currentUserId);
            runOnUiThread(() -> displayData(allFavoritesList));
        });
        executor.shutdown();
    }

    private void setupDropdownListeners() {
        chipAll.setOnClickListener(v -> {
            resetChipTexts();
            displayData(allFavoritesList);
        });

        chipRating.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipRating);
            popup.getMenu().add("⭐ 1+"); popup.getMenu().add("⭐ 2+");
            popup.getMenu().add("⭐ 3+"); popup.getMenu().add("⭐ 4+");
            popup.getMenu().add("⭐ 5");

            popup.setOnMenuItemClickListener(item -> {
                chipRating.setText(item.getTitle() + " ▼");
                filterList("rating", item.getTitle().toString().replaceAll("[^0-9]", ""));
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
                filterList("genre", item.getTitle().toString());
                return true;
            });
            popup.show();
        });

        chipYear.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipYear);
            String[] years = {"2026", "2025", "2024", "2023", "2022"};
            for (String y : years) popup.getMenu().add(y);

            popup.setOnMenuItemClickListener(item -> {
                chipYear.setText(item.getTitle() + " ▼");
                filterList("year", item.getTitle().toString());
                return true;
            });
            popup.show();
        });
    }

    private void resetChipTexts() {
        chipRating.setText("Rating ▼");
        chipGenre.setText("Genre ▼");
        chipYear.setText("Year ▼");
    }

    private void filterList(String type, String value) {
        List<FavoriteEntity> filtered = new ArrayList<>();
        for (FavoriteEntity f : allFavoritesList) {
            if (type.equals("year") && f.getAddedDate() != null && f.getAddedDate().contains(value)) filtered.add(f);
            else if (type.equals("genre") && f.getGenre() != null && f.getGenre().contains(value)) filtered.add(f);
            else if (type.equals("rating")) {
                if (f.getRating() >= Float.parseFloat(value)) filtered.add(f);
            }
        }
        displayData(filtered);
    }

    private void displayData(List<FavoriteEntity> list) {
        FavoriteGridAdapter adapter = new FavoriteGridAdapter(list);
        rvFavorite.setAdapter(adapter);
        txtEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        rvFavorite.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            if (id == R.id.menu_home) { startActivity(new Intent(this, HomeActivity.class)); finish(); return true; }
            if (id == R.id.menu_search) { startActivity(new Intent(this, SearchActivity.class)); finish(); return true; }
            return false;
        });
    }
}