package com.example.advancedapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int EXIT_WINDOW_MS = 2000;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private View root;

    private boolean backPressedOnce = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable resetBackFlag = () -> backPressedOnce = false;

    private final TextWatcher validationWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
        @Override public void afterTextChanged(Editable s) { validateInputs(); }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setEnabled(false);
        etEmail.addTextChangedListener(validationWatcher);
        etPassword.addTextChangedListener(validationWatcher);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, ModeActivity.class)));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedOnce) {
                    handler.removeCallbacks(resetBackFlag);
                    finish();
                    return;
                }
                backPressedOnce = true;
                Snackbar.make(root, "Press back again to exit", Snackbar.LENGTH_SHORT).show();
                handler.postDelayed(resetBackFlag, EXIT_WINDOW_MS);
            }
        });
    }

    private void validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean passwordValid = password.length() >= MIN_PASSWORD_LENGTH;

        if (!email.isEmpty() && !emailValid) {
            etEmail.setError("Invalid email");
        } else {
            etEmail.setError(null);
        }
        btnLogin.setEnabled(emailValid && passwordValid);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(resetBackFlag);
        super.onDestroy();
    }
}