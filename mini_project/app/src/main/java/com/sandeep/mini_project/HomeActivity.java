package com.sandeep.mini_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private Map<Integer, Runnable> menuActions;

    private static final int CALL_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        initMenuActions();
        updateMenuItems();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Sign out the user
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                updateMenuItems(); // Update menu immediately after logout
            }

            Runnable action = menuActions.get(item.getItemId());
            if (action != null) {
                action.run();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuItems();
    }

    private void updateMenuItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        Menu menu = navigationView.getMenu();

        if (user != null) {
            menu.findItem(R.id.nav_login).setTitle("Logout");  // Change Login to Logout
            menu.findItem(R.id.nav_register).setVisible(false);  // Hide Register button
        } else {
            menu.findItem(R.id.nav_login).setTitle("Login");  // Set title back to Login
            menu.findItem(R.id.nav_register).setVisible(true);  // Show Register button
        }

        navigationView.invalidate();
    }

    private void initMenuActions() {
        menuActions = new HashMap<>();

        menuActions.put(R.id.nav_login, () -> {
            if (mAuth.getCurrentUser() != null) {
                // Log out if user is already logged in
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                updateMenuItems(); // Immediately update menu after logout
            } else {
                // Otherwise, go to LoginActivity
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        menuActions.put(R.id.nav_register, () -> {
            Intent registerIntent = new Intent(HomeActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        menuActions.put(R.id.nav_assessment, () -> {
            Intent assessmentIntent = new Intent(HomeActivity.this, AssessmentActivity.class);
            startActivity(assessmentIntent);
        });

        menuActions.put(R.id.nav_telephony, this::makeSupportCall);
    }

    private void makeSupportCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+91 6281766545"));

        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        } else {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                makeSupportCall();
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }
}