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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int RSRCS_REGISTER = R.id.register;
    private final static int RSRCS_SIGN_IN = R.id.signIn;

    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(RSRCS_REGISTER);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(RSRCS_SIGN_IN);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case RSRCS_REGISTER:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case RSRCS_SIGN_IN:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean isValidationSuccessful = true;

        if (email.isEmpty()) {
            isValidationSuccessful = false;
            editTextEmail.setError("Please enter your email.");
            editTextEmail.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValidationSuccessful = false;
            editTextEmail.setError("Please enter a valid email address.");
            editTextEmail.requestFocus();
        }

        if (password.isEmpty()) {
            isValidationSuccessful = false;
            editTextPassword.setError("Please enter your password.");
            editTextEmail.requestFocus();
        }

        if (isValidationSuccessful) {
            showProgressBar();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user.isEmailVerified()) {
                                // redirect to user profile
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            } else {
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Check your email to verify your account.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to login. Please check your credentials.", Toast.LENGTH_LONG).show();
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
}