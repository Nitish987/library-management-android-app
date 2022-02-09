package com.concepto.lbms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.concepto.lbms.util.holder.LibraryViewHolder;
import com.concepto.lbms.util.model.Library;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchLibraryActivity extends AppCompatActivity {

    private EditText searchText;
    private ImageButton searchButton;
    private RecyclerView libraryRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Library Search";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        searchText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        libraryRV = findViewById(R.id.search_library_rv);
        libraryRV.setLayoutManager(new LinearLayoutManager(this));
        libraryRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(searchText.getText())) {
                Toast.makeText(this, "Search Field is empty.", Toast.LENGTH_SHORT).show();
            } else {
                loadSearch(searchText.getText().toString().trim());
            }
        });
    }

    private void loadSearch(String search) {
        Query query = FirebaseFirestore.getInstance().collection("library").whereArrayContains("search", search);
        FirestoreRecyclerOptions<Library> options = new FirestoreRecyclerOptions.Builder<Library>().setQuery(query, Library.class).build();
        FirestoreRecyclerAdapter<Library, LibraryViewHolder> adapter = new FirestoreRecyclerAdapter<Library, LibraryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LibraryViewHolder holder, int position, @NonNull Library model) {
                holder.setlName(model.getLib());
                holder.setCsName(model.getCsn());
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(SearchLibraryActivity.this, ShowLibraryActivity.class);
                    intent.putExtra("lib_data",model);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_library, parent, false);
                return new LibraryViewHolder(view);
            }
        };
        adapter.startListening();
        libraryRV.setAdapter(adapter);
    }
}