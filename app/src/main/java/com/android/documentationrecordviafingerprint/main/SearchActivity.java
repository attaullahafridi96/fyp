package com.android.documentationrecordviafingerprint.main;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

public class SearchActivity extends AppCompatActivity {
    private ImageButton voice_search;
    private EditText text_search;
    private RecyclerView recyclerView;
    private DatabaseReference parent_node;
    private String email_identifier = "";
    private MyFilesAdapter myAdapter;
    FirebaseRecyclerOptions<UserFile> options;
    private Context context;

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
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    //searchFileName(s.toString());
                }
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
            options = new FirebaseRecyclerOptions.Builder<UserFile>()
                    .setQuery(childReference.child("files"), UserFile.class)
                    .build();
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

    @Override
    protected void onStart() {
        super.onStart();
        if(CheckInternetConnectivity.isInternetConnected(context)){
            myAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(CheckInternetConnectivity.isInternetConnected(context)){
            myAdapter.stopListening();
        }
    }

    /*private void searchFileName(String key) {
        boolean isfound = false;
        ArrayList<String> foundItemList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_design);
        for (int i = 0; i < search_file_list.getCount(); i++) {
            String item = search_file_list.getItemAtPosition(i).toString();
            String[] filename = item.split("\\.");  //it separates filename and file extension
            if (filename[0].trim().equalsIgnoreCase(key)) {
                foundItemList.add(item);
                isfound = true;
            }
        }
        if (isfound) {
            adapter.addAll(foundItemList);
            search_file_list.setAdapter(adapter);
        } else {
            adapter.addAll(dummyfilenames);
            search_file_list.setAdapter(adapter);
        }
    }*/
}