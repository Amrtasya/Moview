package com.example.moviewapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.ui.auth.LoginActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePhoto;
    private TextView tvProfileName, tvUsername;
    private TextView tvMovieCount, tvThisYear, tvAvgRating;
    private Button btnEditProfile, btnShare;
    private Button btnMovies, btnDiary, btnWatchlist, btnFavorite;
    private Switch switchDarkMode;
    private LinearLayout rowAppSettings, rowAbout, rowLogOut;
    private ImageButton btnBack;

    private UserDao userDao;
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
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId     = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            goToLogin();
            return;
        }

        bindViews();
        loadUserData();
        setupClickListeners();
        setupDarkModeSwitch();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
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
        btnMovies      = findViewById(R.id.btnMovies);
        btnDiary       = findViewById(R.id.btnDiary);
        btnWatchlist   = findViewById(R.id.btnWatchlist);
        btnFavorite    = findViewById(R.id.btnFavorite);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        rowAppSettings = findViewById(R.id.rowAppSettings);
        rowAbout       = findViewById(R.id.rowAbout);
        rowLogOut      = findViewById(R.id.rowLogOut);
    }

    private void loadUserData() {
        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;

        tvProfileName.setText(user.getBio() != null ? user.getBio() : "No Name");
        tvUsername.setText("@" + user.getUsername());

        // Stats — sambung ke DiaryDao milik Rahma kalau sudah jadi
        // tvMovieCount.setText(...);
        // tvThisYear.setText(...);
        // tvAvgRating.setText(...);
    }

    private void setupClickListeners() {

        // Tombol back → HomeActivity
        btnBack.setOnClickListener(v -> goToHome());

        // Edit Profile
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // Share
        btnShare.setOnClickListener(v -> {
            String name = tvProfileName.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Cek profil film " + name + " di Moview!");
            startActivity(Intent.createChooser(shareIntent, "Bagikan profil via"));
        });

        btnMovies.setOnClickListener(v ->
                Toast.makeText(this, "Movies — coming soon", Toast.LENGTH_SHORT).show());

        btnDiary.setOnClickListener(v ->
                Toast.makeText(this, "Diary — coming soon", Toast.LENGTH_SHORT).show());

        btnWatchlist.setOnClickListener(v ->
                Toast.makeText(this, "Watchlist — coming soon", Toast.LENGTH_SHORT).show());

        btnFavorite.setOnClickListener(v ->
                Toast.makeText(this, "Favorite — coming soon", Toast.LENGTH_SHORT).show());

        rowAppSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings — coming soon", Toast.LENGTH_SHORT).show());

        rowAbout.setOnClickListener(v ->
                Toast.makeText(this, "About — coming soon", Toast.LENGTH_SHORT).show());

        rowLogOut.setOnClickListener(v -> showLogoutDialog());
    }

    // ---------------------------------------------------------------
    // DARK MODE — fix: simpan dulu, baru recreate activity
    // ---------------------------------------------------------------
    private void setupDarkModeSwitch() {
        // Baca state dark mode yang tersimpan
        boolean isDark = sharedPreferences.getBoolean("dark_mode", true);
        switchDarkMode.setChecked(isDark);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 1. Simpan preferensi
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();

            // 2. Terapkan tema baru
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            // 3. recreate() supaya activity ini langsung apply tema baru
            //    tanpa harus tutup-buka app
            recreate();
        });
    }

    // ---------------------------------------------------------------
    // BOTTOM NAVIGATION
    // ---------------------------------------------------------------
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

    // ---------------------------------------------------------------
    // HELPER: navigasi ke HomeActivity
    // ---------------------------------------------------------------
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ---------------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------------
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