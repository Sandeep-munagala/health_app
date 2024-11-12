package com.sandeep.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailEditText, loginPasswordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = loginEmailEditText.getText().toString().trim();
            String password = loginPasswordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    fetchUserData(user.getUid());
                                } else {
                                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData(String userId) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String userData = task.getResult().getString("data");
                        Toast.makeText(LoginActivity.this, "Welcome! Your data: " + userData, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
