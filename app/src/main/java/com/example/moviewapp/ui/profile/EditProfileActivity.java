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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.ui.auth.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProfileActivity extends AppCompatActivity {

    // Views
    private ImageView ivProfilePhoto;
    private Button btnChangePhoto, btnSaveChanges;
    private ImageButton btnBack;
    private EditText etName, etUsername, etEmail, etPassword;

    // Data
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    // Path foto (di internal storage app), BUKAN content:// uri dari galeri
    private String selectedPhotoPath = null;

    // Launcher galeri — pakai Photo Picker API modern (androidx.activity 1.7.0+),
    // jauh lebih stabil daripada ACTION_PICK gallery app lama yang sering error
    // di emulator ("Error getting selected files"), dan tidak butuh permission storage.
    private final ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {
                        if (uri != null) {
                            copyImageToInternalStorage(uri);
                        }
                        // uri == null artinya user cuma batal milih, bukan error, jadi dibiarkan saja
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        AppDatabase db    = DatabaseClient.getInstance(this);
        userDao           = db.userDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId     = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            finish();
            return;
        }

        bindViews();
        setupCircularAvatar();
        loadCurrentUserData();

        // ===== TOMBOL BACK → ke ProfileActivity =====
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnChangePhoto.setOnClickListener(v -> openGallery());
        btnSaveChanges.setOnClickListener(v -> attemptSave());
    }

    private void bindViews() {
        btnBack        = findViewById(R.id.btnBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        etName         = findViewById(R.id.etName);
        etUsername     = findViewById(R.id.etUsername);
        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    /**
     * Clip ivProfilePhoto jadi lingkaran sempurna, apapun bentuk/aspect ratio
     * gambar yang dimuat (icon default maupun foto asli dari galeri).
     * Ini yang bikin foto CENTER_CROP tetap keliatan bulat, bukan kotak.
     */
    private void setupCircularAvatar() {
        ivProfilePhoto.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        ivProfilePhoto.setClipToOutline(true);
    }

    private void loadCurrentUserData() {
        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;

        etName.setText(user.getBio());
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        etPassword.setText("");

        selectedPhotoPath = user.getProfileImage();
        showPhotoPreview(selectedPhotoPath);
    }

    /**
     * Copy gambar yang dipilih dari galeri ke internal storage app
     * (filesDir/profile_photos/profile_<userId>.jpg).
     * Ini menghindari crash SecurityException karena content:// uri
     * dari galeri hanya punya izin baca sementara, sedangkan file
     * di internal storage milik app sendiri jadi selalu bisa diakses.
     */
    private void copyImageToInternalStorage(Uri sourceUri) {
        try {
            File profileDir = new File(getFilesDir(), "profile_photos");
            if (!profileDir.exists()) {
                profileDir.mkdirs();
            }
            File destFile = new File(profileDir, "profile_" + currentUserId + ".jpg");

            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(destFile)) {
                if (in == null) throw new IOException("Tidak bisa membaca gambar yang dipilih");
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            selectedPhotoPath = destFile.getAbsolutePath();
            showPhotoPreview(selectedPhotoPath);

        } catch (IOException e) {
            Toast.makeText(this, "Gagal memuat foto, coba pilih foto lain", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tampilkan foto profil di ImageView.
     * - Kalau path kosong/null -> tampilkan icon default (dengan tint hijau seperti semula).
     * - Kalau ada file valid -> tampilkan foto asli TANPA tint (biar warnanya gak ikut ke-hijaukan)
     *   dan pakai centerCrop biar penuh mengisi frame.
     */
    private void showPhotoPreview(String path) {
        if (TextUtils.isEmpty(path)) {
            setDefaultPhotoIcon();
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            // path lama/rusak (misal masih content:// uri dari versi sebelumnya)
            setDefaultPhotoIcon();
            return;
        }

        try {
            ivProfilePhoto.setImageTintList(null);
            ivProfilePhoto.setPadding(0, 0, 0, 0);
            ivProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivProfilePhoto.setImageURI(Uri.fromFile(file));
        } catch (Exception e) {
            setDefaultPhotoIcon();
        }
    }

    private void setDefaultPhotoIcon() {
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        ivProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivProfilePhoto.setPadding(pad, pad, pad, pad);
        ivProfilePhoto.setImageResource(R.drawable.outline_account_circle_50);
        ivProfilePhoto.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00E5A8")));
    }

    private void openGallery() {
        galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void attemptSave() {
        String name     = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;

        user.setBio(name);
        user.setUsername(username);
        user.setEmail(email);

        if (!TextUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        if (selectedPhotoPath != null) {
            user.setProfileImage(selectedPhotoPath);
        }

        userDao.update(user);

        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();

        // Kembali ke ProfileActivity setelah save
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}