package com.example.moviewapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
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
import com.example.moviewapp.ui.diary.DiaryLogsActivity;
import com.example.moviewapp.ui.diary.FavoriteActivity;
import com.example.moviewapp.ui.diary.HistoryActivity;
import com.example.moviewapp.ui.diary.WatchlistActivity;
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.movie.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePhoto;
    private TextView tvProfileName, tvUsername, tvMovieCount, tvThisYear, tvAvgRating;
    private Button btnEditProfile, btnShare, btnDiary, btnWatchlist, btnFavorite;
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

        AppDatabase db = DatabaseClient.getInstance(this);
        userDao = db.userDao();
        diaryDao = db.diaryDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            goToLogin();
            return;
        }

        bindViews();
        setupCircularAvatar();
        loadUserData();
        loadUserStats();
        setupClickListeners();
        setupBottomNav();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvUsername = findViewById(R.id.tvUsername);
        tvMovieCount = findViewById(R.id.tvMovieCount);
        tvThisYear = findViewById(R.id.tvThisYear);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnShare = findViewById(R.id.btnShare);
        btnDiary = findViewById(R.id.btnDiary);
        btnWatchlist = findViewById(R.id.btnWatchlist);
        btnFavorite = findViewById(R.id.btnFavorite);
        rowAppSettings = findViewById(R.id.rowAppSettings);
        rowAbout = findViewById(R.id.rowAbout);
        rowLogOut = findViewById(R.id.rowLogOut);
    }

    private void setupCircularAvatar() {
        ivProfilePhoto.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        ivProfilePhoto.setClipToOutline(true);
    }

    private void loadUserData() {
        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;
        tvProfileName.setText(user.getBio() != null ? user.getBio() : "No Name");
        tvUsername.setText("@" + user.getUsername());
        showProfilePhoto(user.getProfileImage());
    }

    private void showProfilePhoto(String path) {
        if (TextUtils.isEmpty(path)) { setDefaultPhotoIcon(); return; }
        File file = new File(path);
        if (!file.exists()) { setDefaultPhotoIcon(); return; }
        ivProfilePhoto.setImageURI(Uri.fromFile(file));
    }

    private void setDefaultPhotoIcon() {
        ivProfilePhoto.setImageResource(R.drawable.outline_account_circle_50);
        ivProfilePhoto.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00E5A8")));
    }

    private void loadUserStats() {
        tvMovieCount.setText(String.valueOf(diaryDao.getTotalWatchedMovies(currentUserId)));
        tvThisYear.setText(String.valueOf(diaryDao.getWatchedThisYear(currentUserId)));
        tvAvgRating.setText(String.format(Locale.getDefault(), "%.1f", diaryDao.getAvgRating(currentUserId)));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> goToHome());
        btnEditProfile.setOnClickListener(v -> Toast.makeText(this, "Edit Profile — coming soon", Toast.LENGTH_SHORT).show());
        btnShare.setOnClickListener(v -> Toast.makeText(this, "Share — coming soon", Toast.LENGTH_SHORT).show());
        btnDiary.setOnClickListener(v -> startActivity(new Intent(this, DiaryLogsActivity.class)));
        btnWatchlist.setOnClickListener(v -> startActivity(new Intent(this, WatchlistActivity.class)));
        btnFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        rowLogOut.setOnClickListener(v -> showLogoutDialog());
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (item.isChecked()) return false;
            Intent intent = null;
            if (id == R.id.menu_home) intent = new Intent(this, HomeActivity.class);
            else if (id == R.id.menu_search) intent = new Intent(this, SearchActivity.class);
            else if (id == R.id.menu_history) intent = new Intent(this, HistoryActivity.class);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this).setTitle("Log Out").setMessage("Yakin ingin keluar?").setPositiveButton("Ya", (d, w) -> logout()).setNegativeButton("Batal", null).show();
    }

    private void logout() {
        sharedPreferences.edit().remove(LoginActivity.KEY_IS_LOGGED_IN).remove(LoginActivity.KEY_USER_ID).apply();
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