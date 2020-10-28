package com.android.documentationrecordviafingerprint.main;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.SessionManagement;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.UserDocument;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private ImageButton voice_search;
    private EditText text_search;
    private RecyclerView recyclerView;
    private MyDocumentsAdapter myAdapter;
    private FirebaseRecyclerOptions<UserDocument> options;
    private Context context;
    private DatabaseReference parent_node;
    private String email_identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        parent_node = DB.getFirstNodeReference();

        text_search = findViewById(R.id.text_search);
        TextWatcher textWatcherListener = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (filterLongEnough()) {
                    searchDocument(s.toString());
                }else{
                    myAdapter.updateOptions(options);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            private boolean filterLongEnough() {
                return text_search.getText().toString().trim().length() > 0;
            }
        };
        text_search.addTextChangedListener(textWatcherListener);

        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        email_identifier = new SessionManagement(context).getEmailIdentifier();

        if (CheckInternetConnectivity.isInternetConnected(context)) {
            DatabaseReference childReference = parent_node.child(email_identifier);
            options = new FirebaseRecyclerOptions.Builder<UserDocument>()
                    .setQuery(childReference.child("files"), UserDocument.class).build();
            myAdapter = new MyDocumentsAdapter(context, options);
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
            FirebaseRecyclerOptions<UserDocument> op = new FirebaseRecyclerOptions.Builder<UserDocument>()
                    .setQuery(query, UserDocument.class).build();
            myAdapter.updateOptions(op);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            myAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            myAdapter.stopListening();
        }
    }
}