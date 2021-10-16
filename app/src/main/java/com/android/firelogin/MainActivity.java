package com.android.firelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView textFullName, textEMail, textMobileNumber, textDOB, textAddress, textGender;
    Button buttonLogOut, buttonEdit;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogOut = findViewById(R.id.buttonLogOut);
        textFullName = findViewById(R.id.textFullName2);
        textEMail = findViewById(R.id.textEMail2);
        textMobileNumber = findViewById(R.id.textMobileNumber2);
        textDOB = findViewById(R.id.textDOB2);
        textAddress = findViewById(R.id.textAddress2);
        textGender = findViewById(R.id.textGender2);
        buttonEdit = findViewById(R.id.buttonEdit);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUser = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dataFullName = snapshot.child("Full Name").getValue(String.class);
                String dataMobileNumber = snapshot.child("Mobile Number").getValue(String.class);
                String dataDOB = snapshot.child("Date of Birth").getValue(String.class);
                String dataAddress = snapshot.child("Address").getValue(String.class);
                String dataEMail = snapshot.child("E-Mail").getValue(String.class);
                String dataGender = snapshot.child("Gender").getValue(String.class);
                textFullName.setText(dataFullName);
                textMobileNumber.setText(dataMobileNumber);
                textDOB.setText(dataDOB);
                textAddress.setText(dataAddress);
                textEMail.setText(dataEMail);
                textGender.setText(dataGender);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        buttonEdit.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EditActivity.class)));

        buttonLogOut.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }
}