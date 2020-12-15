package com.android.documentationrecordviafingerprint.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.adapter.MyFilesAdapter;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.SessionManagement;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.internetchecking.ConnectivityReceiver;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.UserUploads;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements IMyConstants, ConnectivityReceiver.ConnectivityReceiverListener {
    private SearchView text_search;
    private RecyclerView recyclerView;
    private MyFilesAdapter myFilesAdapter;
    private Context context;
    private DatabaseReference parent_node;
    private static String email_identifier;
    private static BroadcastReceiver internet_broadcast;
    private static String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //////////////ToolBar code///////////////
        Toolbar myToolbar = findViewById(R.id.search_activity_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setVisibility(View.VISIBLE);
        /////////////End ToolBar code////////////////
        context = SearchActivity.this;
        parent_node = DB.getRtDBFirstNodeReference();
        email_identifier = new SessionManagement(context).getEmailIdentifier();

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
                    Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_file_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_all_menu_item:
                type = "";
                break;
            case R.id.search_doc_menu_item:
                type = FILE_TYPE_DOCS;
                break;
            case R.id.search_image_menu_item:
                type = FILE_TYPE_IMAGE;
                break;
            default:
                return false;
        }
        onStart();
        return true;
    }

    private void searchDocument(String toSearch) {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            Query query = childReference.child(ID_FILES).orderByChild(KEY_TITLE)
                    .startAt(toSearch).endAt(toSearch + "\uf8ff");
            FirebaseRecyclerOptions<UserUploads> filter_options = new FirebaseRecyclerOptions.Builder<UserUploads>()
                    .setQuery(query, UserUploads.class).build();
            myFilesAdapter.updateOptions(filter_options);
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
                    new CustomMsgDialog(context, "Permissions Denied", "READ|WRITE PERMISSION REQUIRED!\n\nThis permission is required for saving files on your device, Please grant Permissions to Download File");
                }
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (myFilesAdapter == null) {
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
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            FirebaseRecyclerOptions<UserUploads> options;
            switch (type) {
                case FILE_TYPE_IMAGE:
                    options = new FirebaseRecyclerOptions.Builder<UserUploads>()
                            .setQuery(childReference.child(ID_FILES)
                                    .orderByChild(KEY_TYPE).equalTo(FILE_TYPE_IMAGE), UserUploads.class)
                            .build();
                    break;
                case FILE_TYPE_DOCS:
                    options = new FirebaseRecyclerOptions.Builder<UserUploads>()
                            .setQuery(childReference.child(ID_FILES)
                                    .orderByChild(KEY_TYPE).equalTo(FILE_TYPE_DOCS), UserUploads.class)
                            .build();
                    break;
                default:
                    options = new FirebaseRecyclerOptions.Builder<UserUploads>()
                            .setQuery(childReference.child(ID_FILES), UserUploads.class)
                            .build();
                    break;
            }
            myFilesAdapter = new MyFilesAdapter(SearchActivity.this, options);
            recyclerView.setAdapter(myFilesAdapter);
            if (myFilesAdapter != null) {
                myFilesAdapter.startListening();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myFilesAdapter != null) {
            myFilesAdapter.stopListening();
        }
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
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                text_search.setQuery(result.get(0), true);
            }
        }
    }

}