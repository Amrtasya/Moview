package com.example.moviewapp.ui.auth;

import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etUsername, etEmail, etPassword;
    private Button btnSignUp;
    private TextView tvLogin;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Ambil DAO lewat DatabaseClient
        AppDatabase db = DatabaseClient.getInstance(this);
        userDao = db.userDao();

        // Bind Views
        etName     = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp  = findViewById(R.id.btnSignUp);
        tvLogin    = findViewById(R.id.tvLogin);

        // Tombol SIGN UP
        btnSignUp.setOnClickListener(v -> attemptSignUp());

        // Link ke Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        String name     = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // --- Validasi field kosong ---
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
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        // --- Cek email sudah dipakai atau belum ---
        // (allowMainThreadQueries() aktif, jadi query bisa langsung dipanggil di sini)
        UserEntity existingUser = userDao.getUserByEmail(email);

        if (existingUser != null) {
            etEmail.setError("Email sudah terdaftar");
            etEmail.requestFocus();
            Toast.makeText(this, "Email sudah digunakan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Registrasi berhasil ---
        UserEntity newUser = new UserEntity(username, email, password, null, name);
        userDao.insert(newUser);

        Toast.makeText(this, "Akun berhasil dibuat! Silakan login.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}