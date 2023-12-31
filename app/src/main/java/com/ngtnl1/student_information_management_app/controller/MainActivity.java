package com.ngtnl1.student_information_management_app.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.fragment.CertificateManagementFragment;
import com.ngtnl1.student_information_management_app.controller.fragment.StudentManagementFragment;
import com.ngtnl1.student_information_management_app.controller.fragment.ProfileManagementFragment;
import com.ngtnl1.student_information_management_app.controller.fragment.UserManagementFragment;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.service.UserService;
import com.ngtnl1.student_information_management_app.service.AppStatusService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Inject
    UserService userService;
    @Inject
    StorageReference storageReference;
    @Inject
    AppStatusService appStatusService;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ShapeableImageView imageMainAvatar;
    private TextView textViewMainUsername;
    private TextView textViewMainEmail;
    private MenuItem menuItemMainLogin;
    private MenuItem menuItemMainLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        configureDrawer();
        setDefaultFragment();
        userService.getRole();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(this);
        imageMainAvatar = navigationView.getHeaderView(0).findViewById(R.id.imageMainUserManagementAvatar);
        textViewMainUsername = navigationView.getHeaderView(0).findViewById(R.id.textViewMainUsername);
        textViewMainEmail = navigationView.getHeaderView(0).findViewById(R.id.textViewMainEmail);
        menuItemMainLogin = navigationView.getMenu().findItem(R.id.menuItemMainLogin);
        menuItemMainLogout = navigationView.getMenu().findItem(R.id.menuItemMainLogout);
    }

    private void configureDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setDefaultFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new StudentManagementFragment()).commit();
        navigationView.setCheckedItem(R.id.menuItemMainStudentManagement);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.menuItemMainStudentManagement) {
            selectedFragment = new StudentManagementFragment();
        } else if (itemId == R.id.menuItemMainUserManagement) {
            if (!appStatusService.isOnline()) {
                Toast.makeText(this, "Không có kết nối internet.", Toast.LENGTH_SHORT).show();
            } else if (!userService.current_role.equals("ADMIN")) {
                Toast.makeText(this, "Bạn không có quyền truy cập.", Toast.LENGTH_SHORT).show();
            } else {
                selectedFragment = new UserManagementFragment();
            }
        } else if (itemId == R.id.menuItemMainProfileManagement) {
            if (appStatusService.isOnline()) {
                selectedFragment = new ProfileManagementFragment();
            } else {
                Toast.makeText(this, "Không có kết nối internet.", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.menuItemMainCertificateManagement) {
            if (!appStatusService.isOnline()) {
                Toast.makeText(this, "Không có kết nối internet.", Toast.LENGTH_SHORT).show();
            } else if (!userService.current_role.equals("ADMIN") && !userService.current_role.equals("MANAGER")) {
                Toast.makeText(this, "Bạn không có quyền truy cập.", Toast.LENGTH_SHORT).show();
            } else {
                selectedFragment = new CertificateManagementFragment();
            }
        } else if (itemId == R.id.menuItemMainLogin) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (itemId == R.id.menuItemMainLogout) {
            FirebaseAuth.getInstance().signOut();
            setAuthStatusViews(false);
            changeToLogin();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void setAuthStatusViews(boolean isLogged) {
        if (isLogged) {
            menuItemMainLogin.setVisible(false);
            menuItemMainLogout.setVisible(true);

            userService.getUserDataRaw().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);

                if (user != null) {
                    textViewMainUsername.setText(user.getName());
                    textViewMainEmail.setText(user.getEmail());
                }
            });
        } else {
            menuItemMainLogin.setVisible(true);
            menuItemMainLogout.setVisible(false);
            textViewMainUsername.setText("   Guest");
            textViewMainEmail.setText("");
        }

        setProfileImage();
    }

    private void setProfileImage() {
        if (userService.isUserSignedIn() && appStatusService.isOnline()) {
            storageReference.child("images/" + userService.getUserEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(imageMainAvatar)).addOnFailureListener(exception -> Glide.with(this).load(R.drawable.img_sample_avatar).into(imageMainAvatar));
        } else {
            Glide.with(this).load(R.drawable.img_sample_avatar).into(imageMainAvatar);
        }
    }

    private void changeToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_header, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.something_1) {
            // do something
            return true;
        } else if (item.getItemId() == R.id.something_2){
            // do something
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setAuthStatusViews(userService.isUserSignedIn());
    }
}