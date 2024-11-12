package com.sandeep.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText registerEmailEditText, registerPasswordEditText, registerConfirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        registerConfirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        // Check if a user is already logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Disable the register button and redirect to HomeActivity
            registerButton.setEnabled(false);
            Toast.makeText(this, "Already logged in. Redirecting to Home.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User already logged in, redirecting to HomeActivity");

            // Redirect to HomeActivity
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            registerButton.setOnClickListener(v -> registerUser());
        }
    }

    private void registerUser() {
        String email = registerEmailEditText.getText().toString().trim();
        String password = registerPasswordEditText.getText().toString().trim();
        String confirmPassword = registerConfirmPasswordEditText.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("data", "User-specific information");

                                firestore.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "User data saved in Firestore");
                                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                            // Redirect to HomeActivity
                                            Log.d(TAG, "Redirecting to HomeActivity");
                                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error saving data to Firestore: " + e.getMessage());
                                            Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Log.e(TAG, "User registration failed: " + task.getException().getMessage());
                            Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
