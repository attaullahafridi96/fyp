package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.internetchecking.ConnectivityReceiver;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.userlogin.Login;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

public class DashboardActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private Context context;
    private static DrawerLayout drawerLayout;
    private MyFilesAdapter myAdapter;
    private static final Intent activity_opener = new Intent();
    private static SessionManagement session;
    private static DatabaseReference parent_node;
    private static RecyclerView recyclerView;
    private static BroadcastReceiver internet_broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        context = DashboardActivity.this;
        session = new SessionManagement(context);
        parent_node = DB.getDbFirstNodeReference();

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

        MyFirebaseDatabase.getFullName(context, fullname);
        email.setText(session.getSession());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new CustomMsgDialog(context, "Permissions Granted", "Now you can Download File");
                } else {
                    new CustomMsgDialog(context, "Permissions Denied", "Please Grant Permissions to Download File");
                }
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (myAdapter == null) {
                Snackbar.make(findViewById(android.R.id.content), "Internet Connected", Snackbar.LENGTH_LONG).show();
                onStart();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email_identifier = session.getEmailIdentifier();

        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            FirebaseRecyclerOptions<UserFile> options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(childReference.child("files"), UserFile.class)
                    .build();
            myAdapter = new MyFilesAdapter(DashboardActivity.this, options);
            recyclerView.setAdapter(myAdapter);
            if (myAdapter != null) {
                myAdapter.startListening();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        internet_broadcast = new ConnectivityReceiver();
        registerReceiver(internet_broadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internet_broadcast);
    }
/*private static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.close();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
            doubleBackToExitPressedOnce = true;
            Snackbar.make(findViewById(android.R.id.content), "Press back again to exit", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        }
    }*/
}