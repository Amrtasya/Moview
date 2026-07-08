package com.example.moviewapp.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.HistoryAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEntryCount;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupBottomNav();
        loadHistoryData();
    }

    private void initView() {
        rvHistory = findViewById(R.id.rvHistory);
        tvEntryCount = findViewById(R.id.tvEntryCount);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadHistoryData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.execute(() -> {
                // Mengambil data history dari database
                List<DiaryEntity> historyList = DatabaseClient.getInstance(this).diaryDao().getDiaryByUser(currentUserId);

                runOnUiThread(() -> {
                    HistoryAdapter adapter = new HistoryAdapter(historyList);
                    rvHistory.setAdapter(adapter);
                    // Menampilkan jumlah entri
                    tvEntryCount.setText("ENTRY COUNT: " + historyList.size());
                });
            });
        } finally {
            executor.shutdown();
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        // Pastikan di menu XML, ID history adalah R.id.menu_history
        bottomNav.setSelectedItemId(R.id.menu_history);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_history) return true;
            if (id == R.id.menu_home) { startActivity(new Intent(this, HomeActivity.class)); finish(); return true; }
            if (id == R.id.menu_search) { startActivity(new Intent(this, SearchActivity.class)); finish(); return true; }
            if (id == R.id.menu_profile) { startActivity(new Intent(this, ProfileActivity.class)); finish(); return true; }
            return false;
        });
    }
}