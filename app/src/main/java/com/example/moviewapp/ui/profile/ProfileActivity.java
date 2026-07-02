package com.example.moviewapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.moviewapp.ui.movie.SearchActivity;

public class ProfileActivity extends AppCompatActivity {

    // Views
    private ImageView ivProfilePhoto;
    private TextView tvProfileName, tvUsername;
    private TextView tvMovieCount, tvThisYear, tvAvgRating;
    private Button btnEditProfile, btnShare;
    private Button btnMovies, btnDiary, btnWatchlist, btnFavorite;
    private Switch switchDarkMode;
    private LinearLayout rowAppSettings, rowAbout, rowLogOut;

    // Data
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ambil DAO lewat DatabaseClient
        AppDatabase db    = DatabaseClient.getInstance(this);
        userDao           = db.userDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId     = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        // Kalau session tidak ada, balik ke login
        if (currentUserId == -1) {
            goToLogin();
            return;
        }

        bindViews();
        loadUserData();
        setupClickListeners();
        setupDarkModeSwitch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data profil setiap kembali ke halaman ini (misalnya setelah Edit Profile)
        loadUserData();
    }

    // ---------------------------------------------------------------
    // BIND VIEWS
    // ---------------------------------------------------------------
    private void bindViews() {
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

    // ---------------------------------------------------------------
    // LOAD DATA USER DARI ROOM DB
    // ---------------------------------------------------------------
    private void loadUserData() {
        UserEntity user = userDao.getUserById(currentUserId);

        if (user == null) return;

        tvProfileName.setText(user.getBio() != null ? user.getBio() : "No Name");
        tvUsername.setText("@" + user.getUsername());

        // Catatan: ivProfilePhoto masih pakai drawable bawaan Android (ic_menu_myplaces).
        // Kalau mau load foto asli dari user.getProfileImage(), nanti bisa
        // ditambahkan library Glide/Picasso untuk load dari URI/path.

        // Stats — nanti bisa diisi dari DiaryDao (dikerjakan Rahma)
        // contoh: List<DiaryEntity> diaryList = diaryDao.getDiaryByUser(currentUserId);
        // tvMovieCount.setText(String.valueOf(diaryList.size()));
    }

    // ---------------------------------------------------------------
    // SETUP CLICK LISTENERS
    // ---------------------------------------------------------------
    private void setupClickListeners() {

        // Edit Profile
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // Share — Android Share Intent
        btnShare.setOnClickListener(v -> {
            String name = tvProfileName.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Cek profil film " + name + " di Moview!");
            startActivity(Intent.createChooser(shareIntent, "Bagikan profil via"));
        });

        // Navigasi ke activity Cindy & Rahma (uncomment kalau activity-nya sudah ada)
        btnMovies.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        // startActivity(new Intent(this, MovieListActivity.class));

        btnDiary.setOnClickListener(v ->
                Toast.makeText(this, "Diary — coming soon", Toast.LENGTH_SHORT).show());
        // startActivity(new Intent(this, DiaryLogsActivity.class));

        btnWatchlist.setOnClickListener(v ->
                Toast.makeText(this, "Watchlist — coming soon", Toast.LENGTH_SHORT).show());
        // startActivity(new Intent(this, WatchlistActivity.class));

        btnFavorite.setOnClickListener(v ->
                Toast.makeText(this, "Favorite — coming soon", Toast.LENGTH_SHORT).show());
        // startActivity(new Intent(this, FavoriteActivity.class));

        // App Settings
        rowAppSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings — coming soon", Toast.LENGTH_SHORT).show());
        // startActivity(new Intent(this, SettingsActivity.class));

        // About Moview
        rowAbout.setOnClickListener(v ->
                Toast.makeText(this, "About — coming soon", Toast.LENGTH_SHORT).show());
        // startActivity(new Intent(this, AboutActivity.class));

        // Log Out — dengan dialog konfirmasi
        rowLogOut.setOnClickListener(v -> showLogoutDialog());
    }

    // ---------------------------------------------------------------
    // DARK MODE SWITCH
    // ---------------------------------------------------------------
    private void setupDarkModeSwitch() {
        boolean isDark = sharedPreferences.getBoolean("dark_mode", true);
        switchDarkMode.setChecked(isDark);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
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