package com.android.firelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUserActivity extends AppCompatActivity {
    EditText fullName, eMail, mobileNumber, dob, address, password, confPassword;
    Spinner genderSpinner;
    Button registerUser, buttonClose;
    ProgressDialog progressDialog;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String currentUser = "", selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Toolbar toolbar2 = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar2);
        fAuth = FirebaseAuth.getInstance();
        fullName = findViewById(R.id.editFullName);
        eMail = findViewById(R.id.editEMail);
        mobileNumber = findViewById(R.id.editMobileNumber);
        dob = findViewById(R.id.editDateOfBirth);
        address = findViewById(R.id.editAddress);
        password = findViewById(R.id.editPassword);
        confPassword = findViewById(R.id.editConfPassword);
        registerUser = findViewById(R.id.buttonSignUp);
        buttonClose = findViewById(R.id.buttonClose2);
        genderSpinner = findViewById(R.id.genderSpinner);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registerUser.setOnClickListener(v -> {
            String fName = fullName.getText().toString();
            String mNumber = mobileNumber.getText().toString();
            String dOB = dob.getText().toString();
            String Address = address.getText().toString();
            String Email = eMail.getText().toString();
            String passwd = password.getText().toString();
            String confPasswd = confPassword.getText().toString();
            if (fName.isEmpty()) {
                fullName.setError("Full Name can't be empty!");
                return;
            }
            if (Email.isEmpty()) {
                eMail.setError("E-Mail can't be empty!");
                return;
            }
            if (mNumber.isEmpty()) {
                mobileNumber.setError("Mobile Number can't be empty!");
                return;
            }
            if (dOB.isEmpty()) {
                dob.setError("Date of Birth can't be empty!");
                return;
            }
            if (Address.isEmpty()) {
                address.setError("Address can't be empty!");
                return;
            }

            if (passwd.isEmpty()) {
                password.setError("Password can't be empty!");
                return;
            }
            if (confPasswd.isEmpty()) {
                confPassword.setError("Confirm password can't be empty!");
                return;
            }
            if (!confPasswd.equals(passwd)) {
                confPassword.setError("Passwords don't match!");
                return;
            }
            progressDialog = new ProgressDialog(RegisterUserActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            fAuth.createUserWithEmailAndPassword(Email, passwd).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    currentUser = user.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
                    HashMap<String, Object> usersMap = new HashMap<>();
                    usersMap.put("Full Name", fName);
                    usersMap.put("Mobile Number", mNumber);
                    usersMap.put("Date of Birth", dOB);
                    usersMap.put("Address", Address);
                    usersMap.put("E-Mail", Email);
                    usersMap.put("Gender", selectedGender);
                    databaseReference.setValue(usersMap).addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Users' details successfully added to database!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "User registration unsuccessfully!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(RegisterUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        });

        buttonClose.setOnClickListener(v -> finish());
    }
}