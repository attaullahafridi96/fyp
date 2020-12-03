package com.android.documentationrecordviafingerprint.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.android.documentationrecordviafingerprint.adapter.MyNotesAdapter;
import com.android.documentationrecordviafingerprint.helper.SessionManagement;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.internetchecking.ConnectivityReceiver;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.IMyConstants;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressDialog;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity implements IMyConstants, ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView recyclerView;
    private SearchView text_search;
    private MyNotesAdapter myNotesAdapter;
    private Context context;
    private DatabaseReference parent_node;
    private static String email_identifier;
    private static BroadcastReceiver internet_broadcast;
    private FloatingActionButton floatingActionButton;
    private static CustomProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = NotesActivity.this;
        progDialog = new CustomProgressDialog(context, "Loading Content . . .");
        parent_node = DB.getDBFirstNodeReference();
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        floatingActionButton = findViewById(R.id.new_notes_floatBtn);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NotesEditorActivity.class));
            }
        });
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (myNotesAdapter == null) {
                Snackbar.make(findViewById(android.R.id.content), INTERNET_CONNECTED, Snackbar.LENGTH_LONG).show();
                onStart();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new CustomMsgDialog(context, "Permissions Granted", "Now you can Download File");
                } else {
                    new CustomMsgDialog(context, "Permissions Denied", "Please Grant Permissions to Download File");
                }
            }
        }*/
    }

    private void searchDocument(String toSearch) {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            Query query = childReference.child(ID_NOTES_PATH).orderByChild(KEY_NAME)
                    .startAt(toSearch).endAt(toSearch + "\uf8ff");
            FirebaseRecyclerOptions<UserNotes> filter_options = new FirebaseRecyclerOptions.Builder<UserNotes>()
                    .setQuery(query, UserNotes.class).build();
            myNotesAdapter.updateOptions(filter_options);
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
        progDialog.showDialog();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            FirebaseRecyclerOptions<UserNotes> options = new FirebaseRecyclerOptions.Builder<UserNotes>()
                    .setQuery(childReference.child(ID_NOTES_PATH), UserNotes.class)
                    .build();
            myNotesAdapter = new MyNotesAdapter(this, options);
            myNotesAdapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                    progDialog.dismissDialog();
                }

                @Override
                public void onDataChanged() {
                    progDialog.dismissDialog();
                    if (myNotesAdapter.getItemCount() == 0 && !recyclerView.hasPendingAdapterUpdates()) {
                        Snackbar.make(findViewById(android.R.id.content), "No data available to display", Snackbar.LENGTH_LONG).show();

                    //////////

                   //////////     HERE IS ERROR WHEN SEARCHING VIA VOICE

                    /////////////////


                    }
                }

                @Override
                public void onError(@NonNull DatabaseError databaseError) {
                    progDialog.dismissDialog();
                }
            });
            recyclerView.setAdapter(myNotesAdapter);
            if (myNotesAdapter != null) {
                myNotesAdapter.startListening();
            }
        } else {
            progDialog.dismissDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myNotesAdapter != null) {
            myNotesAdapter.stopListening();
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