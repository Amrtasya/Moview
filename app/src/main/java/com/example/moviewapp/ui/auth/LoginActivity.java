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
import com.example.moviewapp.ui.home.HomeActivity;
import com.example.moviewapp.ui.profile.ProfileActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    // ===== KEY SESSION — public static agar bisa dipakai Activity lain =====
    public static final String PREF_NAME        = "moview_session";
    public static final String KEY_USER_ID      = "user_id";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init SharedPreferences SEBELUM setContentView
        // supaya auto-login bisa redirect tanpa render layout dulu
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // ===== CEK SESSION — kalau sudah login, langsung ke Home =====
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            goToHome();
            return; // stop, tidak perlu lanjut render layout login
        }

        setContentView(R.layout.activity_login);

        AppDatabase db = DatabaseClient.getInstance(this);
        userDao        = db.userDao();

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSignUp   = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        UserEntity user = userDao.login(email, password);

        if (user != null) {
            saveSession(user.getId());
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
            goToHome();
        } else {
            Toast.makeText(this, "Email atau password salah.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSession(int userId) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putInt(KEY_USER_ID, userId)
                .apply(); // apply() = async, lebih aman dari commit()
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        // FLAG_ACTIVITY_CLEAR_TASK: hapus semua activity di back stack
        // supaya user tidak bisa back ke halaman login setelah login
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}