package com.android.documentationrecordviafingerprint.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.SessionController;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class SearchActivity extends AppCompatActivity {
    private ImageButton voice_search;
    private SearchView text_search;
    private RecyclerView recyclerView;
    private MyFilesAdapter myAdapter;
    private FirebaseRecyclerOptions<UserFile> options;
    private Context context;
    private DatabaseReference parent_node;
    private String email_identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        parent_node = DB.getDbFirstNodeReference();

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

        email_identifier = new SessionController(context).getEmailIdentifier();

        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(childReference.child("files"), UserFile.class).build();
            myAdapter = new MyFilesAdapter(context, options);
            recyclerView.setAdapter(myAdapter);
        }

        voice_search = findViewById(R.id.voice_search);
        voice_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SearchActivity.this, "Functionality not Added Yet", Toast.LENGTH_LONG).show();
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