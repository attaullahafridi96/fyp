package com.android.documentationrecordviafingerprint.View;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.DB;
import com.android.documentationrecordviafingerprint.controller.MyFilesAdapter;
import com.android.documentationrecordviafingerprint.controller.SessionManagement;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.internetchecking.ConnectivityReceiver;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private SearchView text_search;
    private RecyclerView recyclerView;
    private MyFilesAdapter myAdapter;
    private Context context;
    private DatabaseReference parent_node;
    private String email_identifier;
    private static BroadcastReceiver internet_broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        parent_node = DB.getDBFirstNodeReference();

        text_search = findViewById(R.id.text_search);

        text_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDocument(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchDocument(newText.toLowerCase());
                return false;
            }
        });

        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ImageButton voice_search = findViewById(R.id.voice_search);
        voice_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    voiceSearch();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void searchDocument(String toSearch) {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            Query query = childReference.child("files").orderByChild("file_name")
                    .startAt(toSearch).endAt(toSearch + "\uf8ff");
            FirebaseRecyclerOptions<UserFile> filter_options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(query, UserFile.class).build();
            myAdapter.updateOptions(filter_options);
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        email_identifier = new SessionManagement(context).getEmailIdentifier();

        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            FirebaseRecyclerOptions<UserFile> options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(childReference.child("files"), UserFile.class)
                    .build();
            myAdapter = new MyFilesAdapter(SearchActivity.this, options);
            recyclerView.setAdapter(myAdapter);
            if (myAdapter != null) {
                myAdapter.startListening();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void voiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(context, "Your device does not support voice search", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text_search.setQuery(result.get(0), true);
                }
                break;
        }
    }

}