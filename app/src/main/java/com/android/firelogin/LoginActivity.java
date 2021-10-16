package com.android.firelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText userEMailAddress, userPassword;
    TextView registerTextView;
    Button buttonLogIn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar3 = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar3);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterUserActivity.class)));
        userEMailAddress = findViewById(R.id.userEMailAddress);
        userPassword = findViewById(R.id.userPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        buttonLogIn.setOnClickListener(v -> {
            if (userEMailAddress.getText().toString().isEmpty()) {
                userEMailAddress.setError("Please enter your registered E-Mail Address!");
                return;
            }
            if (userPassword.getText().toString().isEmpty()) {
                userPassword.setError("Please enter your password!");
                return;
            }
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEMailAddress.getText().toString(), userPassword.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
    @Override
    protected void onStart() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        super.onStart();
    }
}