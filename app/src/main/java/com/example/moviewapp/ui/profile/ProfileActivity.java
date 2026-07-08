package com.example.moviewapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.example.moviewapp.ui.diary.DiaryLogsActivity;
import com.example.moviewapp.ui.diary.WatchlistActivity;
import com.example.moviewapp.ui.diary.FavoriteActivity; // Import halaman Favorite kamu
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePhoto;
    private TextView tvProfileName, tvUsername;
    private TextView tvMovieCount, tvThisYear, tvAvgRating;
    private Button btnEditProfile, btnShare;
    private Button btnDiary, btnWatchlist, btnFavorite;
    private LinearLayout rowAppSettings, rowAbout, rowLogOut;
    private ImageButton btnBack;

    private UserDao userDao;
    private DiaryDao diaryDao;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goToHome();
                    }
                });

        AppDatabase db    = DatabaseClient.getInstance(this);
        userDao           = db.userDao();
        diaryDao          = db.diaryDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId     = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            goToLogin();
            return;
        }

        bindViews();
        loadUserData();
        loadUserStats();
        setupClickListeners();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        loadUserStats();
    }

    private void bindViews() {
        btnBack        = findViewById(R.id.btnBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvProfileName  = findViewById(R.id.tvProfileName);
        tvUsername     = findViewById(R.id.tvUsername);
        tvMovieCount   = findViewById(R.id.tvMovieCount);
        tvThisYear     = findViewById(R.id.tvThisYear);
        tvAvgRating    = findViewById(R.id.tvAvgRating);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnShare       = findViewById(R.id.btnShare);
        btnDiary       = findViewById(R.id.btnDiary);
        btnWatchlist   = findViewById(R.id.btnWatchlist);
        btnFavorite    = findViewById(R.id.btnFavorite);
        rowAppSettings = findViewById(R.id.rowAppSettings);
        rowAbout       = findViewById(R.id.rowAbout);
        rowLogOut      = findViewById(R.id.rowLogOut);
    }

    private void loadUserData() {
        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;

        tvProfileName.setText(user.getBio() != null ? user.getBio() : "No Name");
        tvUsername.setText("@" + user.getUsername());
    }

    /**
     * Ambil 3 stats dari DiaryDao:
     * - tvMovieCount -> total film berstatus WATCHED
     * - tvThisYear   -> total film WATCHED yang watchDate-nya di tahun berjalan
     * - tvAvgRating  -> rata-rata rating dari semua diary yang punya rating > 0
     */
    private void loadUserStats() {
        int totalWatched   = diaryDao.getTotalWatchedMovies(currentUserId);
        int watchedThisYear = diaryDao.getWatchedThisYear(currentUserId);
        float avgRating     = diaryDao.getAvgRating(currentUserId);

        tvMovieCount.setText(String.valueOf(totalWatched));
        tvThisYear.setText(String.valueOf(watchedThisYear));
        tvAvgRating.setText(String.format(Locale.getDefault(), "%.1f", avgRating));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> goToHome());

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        btnShare.setOnClickListener(v -> {
            String name = tvProfileName.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Cek profil film " + name + " di Moview!");
            startActivity(Intent.createChooser(shareIntent, "Bagikan profil via"));
        });

        // Menuju ke halaman DiaryLogsActivity milik Rahma
        btnDiary.setOnClickListener(v -> {
            Intent intent = new Intent(this, DiaryLogsActivity.class);
            startActivity(intent);
        });

        // Menuju ke halaman WatchlistActivity milik Rahma
        btnWatchlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, WatchlistActivity.class);
            startActivity(intent);
        });

        // Menuju ke halaman FavoriteActivity milik Rahma
        btnFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        });

        rowAppSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings — coming soon", Toast.LENGTH_SHORT).show());

        rowAbout.setOnClickListener(v ->
                Toast.makeText(this, "About — coming soon", Toast.LENGTH_SHORT).show());

        rowLogOut.setOnClickListener(v -> showLogoutDialog());
    }


    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) return true;

            if (id == R.id.menu_home) {
                goToHome();
                return true;
            }
            if (id == R.id.menu_search) {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            if (id == R.id.menu_history) {
                Toast.makeText(this, "History — coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void logout() {
        sharedPreferences.edit()
                .remove(LoginActivity.KEY_IS_LOGGED_IN)
                .remove(LoginActivity.KEY_USER_ID)
                .apply();
        Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}