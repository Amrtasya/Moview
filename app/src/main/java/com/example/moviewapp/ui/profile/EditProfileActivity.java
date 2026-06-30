package com.example.moviewapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.ui.auth.LoginActivity;

public class EditProfileActivity extends AppCompatActivity {

    // Views
    private ImageView ivProfilePhoto;
    private Button btnChangePhoto, btnSaveChanges;
    private EditText etName, etUsername, etEmail, etPassword;

    // Data
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private String selectedPhotoUri = null; // URI foto yang dipilih dari galeri

    // Launcher untuk buka galeri
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                selectedPhotoUri = imageUri.toString();
                                // Preview foto baru
                                ivProfilePhoto.setImageURI(imageUri);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ambil DAO lewat DatabaseClient
        AppDatabase db    = DatabaseClient.getInstance(this);
        userDao           = db.userDao();
        sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId     = sharedPreferences.getInt(LoginActivity.KEY_USER_ID, -1);

        if (currentUserId == -1) {
            finish();
            return;
        }

        bindViews();
        loadCurrentUserData();

        btnChangePhoto.setOnClickListener(v -> openGallery());
        btnSaveChanges.setOnClickListener(v -> attemptSave());
    }

    // ---------------------------------------------------------------
    // BIND VIEWS
    // ---------------------------------------------------------------
    private void bindViews() {
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        etName         = findViewById(R.id.etName);
        etUsername     = findViewById(R.id.etUsername);
        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    // ---------------------------------------------------------------
    // LOAD DATA USER (pre-fill form)
    // ---------------------------------------------------------------
    private void loadCurrentUserData() {
        UserEntity user = userDao.getUserById(currentUserId);

        if (user == null) return;

        etName.setText(user.getBio());         // nama disimpan di field bio
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        etPassword.setText("");                // jangan tampilkan password lama

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            selectedPhotoUri = user.getProfileImage();
            // Kalau mau preview foto asli, bisa pakai ivProfilePhoto.setImageURI(Uri.parse(...))
        }
    }

    // ---------------------------------------------------------------
    // BUKA GALERI
    // ---------------------------------------------------------------
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // ---------------------------------------------------------------
    // SAVE CHANGES
    // ---------------------------------------------------------------
    private void attemptSave() {
        String name     = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi field wajib
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
        // Password opsional — hanya diupdate kalau diisi
        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        // --- Update ke database ---
        UserEntity user = userDao.getUserById(currentUserId);
        if (user == null) return;

        user.setBio(name);
        user.setUsername(username);
        user.setEmail(email);

        if (!TextUtils.isEmpty(password)) {
            user.setPassword(password);
        }

        if (selectedPhotoUri != null) {
            user.setProfileImage(selectedPhotoUri);
        }

        userDao.update(user);

        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        finish(); // kembali ke ProfileActivity
    }
}