package com.example.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        boolean isValidationSuccessful = true;

        if (fullName.isEmpty()) {
            isValidationSuccessful = false;
            editTextFullName.setError("Please enter your full name.");
            editTextFullName.requestFocus();
        }

        if (age.isEmpty()) {
            isValidationSuccessful = false;
            editTextAge.setError("Please enter your age.");
            editTextAge.requestFocus();
        }

        if (email.isEmpty()) {
            isValidationSuccessful = false;
            editTextEmail.setError("Please enter your email address.");
            editTextEmail.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValidationSuccessful = false;
            editTextEmail.setError("Please provide a valid email address.");
            editTextEmail.requestFocus();
        }

        if (password.isEmpty()) {
            isValidationSuccessful = false;
            editTextPassword.setError("Please enter your password.");
            editTextPassword.requestFocus();
        }

        if (password.length() < 6) {
            isValidationSuccessful = false;
            editTextPassword.setError("Minimum password length should be 6 characters.");
            editTextPassword.requestFocus();
        }

        if (isValidationSuccessful) {
            showProgressBar();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, age, email);

                            FirebaseDatabase.getInstance("https://gfauth-78cd6-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(RegisterUser.this, "Welcome onboard!", Toast.LENGTH_LONG).show();
                                            hideProgressBar();

                                            // Re-direct to login layout!
                                        } else {
                                            toastFailedToRegisterUser();
                                        }
                                    });
                        } else {
                            toastFailedToRegisterUser();
                        }

                        hideProgressBar();
                    });
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void toastFailedToRegisterUser() {
        Toast.makeText(RegisterUser.this, "Failed to register. Try again!", Toast.LENGTH_LONG).show();
    }
}