package com.android.documentationrecordviafingerprint.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.FirebaseController;
import com.android.documentationrecordviafingerprint.controller.SessionController;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.userlogin.Login;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

public class DashboardActivity extends AppCompatActivity {
    private Context context;
    private static DrawerLayout drawerLayout;
    private MyFilesAdapter myAdapter;
    private static final Intent activity_opener = new Intent();
    private static SessionController session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        context = DashboardActivity.this;
        session = new SessionController(context);
        DatabaseReference parent_node = DB.getDbFirstNodeReference();

        drawerLayout = findViewById(R.id.drawer_layout);
        /*Set App Version*/
        NavigationView navigationView = findViewById(R.id.drawer_nav_view);
        navigationView.setItemIconTintList(null);//This is make drawer icons colorful
        MenuItem app_version_menu_item = navigationView.getMenu().findItem(R.id.app_version_item);
        View headerView = navigationView.getHeaderView(0);
        TextView fullname = headerView.findViewById(R.id.nav_drawer_fullname);
        TextView email = headerView.findViewById(R.id.nav_drawer_email);
        String app_version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        app_version_menu_item.setTitle("App Version " + app_version);
        /*End Set App Version*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    switch (item.getItemId()) {
                        case R.id.drawer_settings_item:
                            startActivity(activity_opener.setClass(context, AppSettings.class));
                            break;
                        case R.id.drawer_logout_item:
                            session.destroySession();
                            finish();
                            startActivity(activity_opener.setClass(context, Login.class));
                            break;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error" + e, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        findViewById(R.id.open_drawer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.home_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(activity_opener.setClass(DashboardActivity.this, TextEditorActivity.class));
            }
        });

        FirebaseController.getFullName(context, fullname);
        email.setText(session.getSession());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        String email_identifier = session.getEmailIdentifier();

        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            FirebaseRecyclerOptions<UserFile> options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(childReference.child("files"), UserFile.class)
                    .build();
            myAdapter = new MyFilesAdapter(context, options);
            recyclerView.setAdapter(myAdapter);
        }

        findViewById(R.id.home_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(activity_opener.setClass(DashboardActivity.this, SearchActivity.class));
            }
        });

        findViewById(R.id.home_upload_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(activity_opener.setClass(DashboardActivity.this, UploadActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            if (myAdapter != null) {
                myAdapter.startListening();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            if (myAdapter != null) {
                myAdapter.stopListening();
            }
        }
    }
}