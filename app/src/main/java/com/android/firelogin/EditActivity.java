package com.android.firelogin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    EditText fullName, eMail, mobileNumber, dob, address;
    Spinner genderSpinner;
    Button buttonUpdate, buttonClose;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String currentUser = "", selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        fullName = findViewById(R.id.editFullName);
        eMail = findViewById(R.id.editEMail);
        mobileNumber = findViewById(R.id.editMobileNumber);
        dob = findViewById(R.id.editDateOfBirth);
        address = findViewById(R.id.editAddress);
        genderSpinner = findViewById(R.id.genderSpinner);
        buttonUpdate = findViewById(R.id.buttonSave);
        buttonClose = findViewById(R.id.buttonClose);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.support_simple_spinner_dropdown_item);
        genderAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setPrompt("Gender");
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                fullName.setText(dataFullName);
                mobileNumber.setText(dataMobileNumber);
                dob.setText(dataDOB);
                address.setText(dataAddress);
                eMail.setText(dataEMail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        buttonUpdate.setOnClickListener(v -> {
            String fName = fullName.getText().toString();
            String mNumber = mobileNumber.getText().toString();
            String dOB = dob.getText().toString();
            String Address = address.getText().toString();
            String Email = eMail.getText().toString();
            String gender = selectedGender;
            progressDialog = new ProgressDialog(EditActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            updateData(fName, mNumber, dOB, Address, Email, gender);
        });

        buttonClose.setOnClickListener(v -> finish());
    }

    private void updateData(String fName, String mNumber, String dOB, String address, String email, String gender) {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("Full Name", fName);
        usersMap.put("Mobile Number", mNumber);
        usersMap.put("Date of Birth", dOB);
        usersMap.put("Address", address);
        usersMap.put("E-Mail", email);
        usersMap.put("Gender",gender);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUser = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        databaseReference.updateChildren(usersMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                finish();
            }
            else {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, "User details could not be updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}