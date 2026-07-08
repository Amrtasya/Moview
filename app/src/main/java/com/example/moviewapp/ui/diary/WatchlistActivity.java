package com.example.moviewapp.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.moviewapp.adapter.WatchlistAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.WatchlistEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity; // Pastikan import ini benar
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchlistActivity extends AppCompatActivity {

    private RecyclerView rvWatchlist;
    private ImageView btnBack;
    private FloatingActionButton fabAdd;
    private EditText etSearch;
    private TextView txtEmpty;
    private Chip chipAll, chipGenre, chipYear;

    private WatchlistAdapter adapter;
    private List<WatchlistEntity> allWatchlistList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initView();
        setupSearchListener();
        setupDropdownListeners();
        setupBottomNav();

        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlist();
    }

    private void initView() {
        rvWatchlist = findViewById(R.id.rvWatchlist);
        btnBack = findViewById(R.id.btnBack);
        fabAdd = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearchWatchlist);
        txtEmpty = findViewById(R.id.txtEmpty);
        chipAll = findViewById(R.id.chipAll);
        chipGenre = findViewById(R.id.chipGenre);
        chipYear = findViewById(R.id.chipYear);

        rvWatchlist.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadWatchlist() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            allWatchlistList = DatabaseClient.getInstance(this).watchlistDao().getWatchlistByUser(currentUserId);
            runOnUiThread(() -> displayData(allWatchlistList));
        });
        executor.shutdown();
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<WatchlistEntity> filtered = new ArrayList<>();
                for (WatchlistEntity item : allWatchlistList) {
                    if (item.getTitle() != null && item.getTitle().toLowerCase().contains(s.toString().toLowerCase()))
                        filtered.add(item);
                }
                displayData(filtered);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDropdownListeners() {
        chipAll.setOnClickListener(v -> {
            changeChipSelectionStyle(chipAll);
            displayData(allWatchlistList);
        });

        chipGenre.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipGenre);
            String[] genres = {"Action", "Adventure", "Animation", "Comedy", "Drama", "Fantasy", "Horror", "Sci-Fi"};
            for (String g : genres) popup.getMenu().add(g);
            popup.setOnMenuItemClickListener(menuItem -> {
                String selectedGenre = String.valueOf(menuItem.getTitle());
                chipGenre.setText(selectedGenre + " ▼");
                changeChipSelectionStyle(chipGenre);
                List<WatchlistEntity> filtered = new ArrayList<>();
                for (WatchlistEntity item : allWatchlistList) {
                    if (item.getGenre() != null && item.getGenre().contains(selectedGenre))
                        filtered.add(item);
                }
                displayData(filtered);
                return true;
            });
            popup.show();
        });

        chipYear.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, chipYear);
            String[] years = {"2026", "2025", "2024", "2023", "2022"};
            for (String y : years) popup.getMenu().add(y);
            popup.setOnMenuItemClickListener(menuItem -> {
                String selectedYear = String.valueOf(menuItem.getTitle());
                chipYear.setText(selectedYear + " ▼");
                changeChipSelectionStyle(chipYear);
                List<WatchlistEntity> filtered = new ArrayList<>();
                for (WatchlistEntity item : allWatchlistList) {
                    if (item.getAddedDate() != null && item.getAddedDate().contains(selectedYear))
                        filtered.add(item);
                }
                displayData(filtered);
                return true;
            });
            popup.show();
        });
    }

    private void displayData(List<WatchlistEntity> list) {
        adapter = new WatchlistAdapter(this, list);
        rvWatchlist.setAdapter(adapter);
        txtEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void changeChipSelectionStyle(Chip selected) {
        chipAll.setChecked(selected == chipAll);
        chipGenre.setChecked(selected == chipGenre);
        chipYear.setChecked(selected == chipYear);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                // Ganti ProfileActivity.class dengan Activity Profil utamamu
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
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