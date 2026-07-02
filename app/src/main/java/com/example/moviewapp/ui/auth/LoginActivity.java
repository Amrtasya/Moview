package com.example.moviewapp.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviewapp.R;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.database.AppDatabase;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.ui.profile.ProfileActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    // Key untuk SharedPreferences (dipakai juga di Activity lain)
    public static final String PREF_NAME        = "moview_session";
    public static final String KEY_USER_ID      = "user_id";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ambil DAO lewat DatabaseClient
        AppDatabase db    = DatabaseClient.getInstance(this);
        userDao           = db.userDao();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Cek apakah sudah login sebelumnya (auto-login)
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            goToProfile();
            return;
        }

        // Bind Views
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSignUp   = findViewById(R.id.tvSignUp);

        // Tombol LOGIN
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Link ke Sign Up
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // --- Validasi field kosong ---
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // --- Cek ke database ---
        UserEntity user = userDao.login(email, password);

        if (user != null) {
            // Login berhasil — simpan sesi
            saveSession(user.getId());
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
            goToProfile();
        } else {
            // Login gagal
            Toast.makeText(this, "Email atau password salah.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSession(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    private void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}