package com.concepto.lbms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.concepto.lbms.dialog.CreateLibraryDialog;
import com.concepto.lbms.util.holder.LibraryViewHolder;
import com.concepto.lbms.util.model.Bookmark;
import com.concepto.lbms.util.model.Library;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FloatingActionButton createLibrary;
    private RecyclerView libraryRV, bookmarkRV;
    private TextView libHeading, bmHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        createLibrary = findViewById(R.id.create_library);

        libHeading = findViewById(R.id.library_heading);
        bmHeading = findViewById(R.id.bookmark_heading);

        libraryRV = findViewById(R.id.library_rv);
        libraryRV.setLayoutManager(new LinearLayoutManager(this));
        libraryRV.setHasFixedSize(true);

        bookmarkRV = findViewById(R.id.bookmark_rv);
        bookmarkRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        bookmarkRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        createLibrary.setOnClickListener(view -> {
            CreateLibraryDialog dialog = new CreateLibraryDialog(DashBoardActivity.this, auth.getCurrentUser().getUid());
            dialog.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            Query query = FirebaseFirestore.getInstance().collection("library").whereArrayContains("all", uid);
            FirestoreRecyclerOptions<Library> options = new FirestoreRecyclerOptions.Builder<Library>().setQuery(query, Library.class).build();
            FirestoreRecyclerAdapter<Library, LibraryViewHolder> adapter = new FirestoreRecyclerAdapter<Library, LibraryViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull LibraryViewHolder holder, int position, @NonNull Library model) {
                    holder.setlName(model.getLib());
                    holder.setCsName(model.getCsn());
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(DashBoardActivity.this,LibraryActivity.class);
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

            String path = "users/" + uid + "/bookmarks";
            Query query1 = FirebaseFirestore.getInstance().collection(path);
            FirestoreRecyclerOptions<Bookmark> options1 = new FirestoreRecyclerOptions.Builder<Bookmark>().setQuery(query1, Bookmark.class).build();
            FirestoreRecyclerAdapter<Bookmark, LibraryViewHolder> adapter1 = new FirestoreRecyclerAdapter<Bookmark, LibraryViewHolder>(options1) {
                @Override
                protected void onBindViewHolder(@NonNull LibraryViewHolder holder, int position, @NonNull Bookmark model) {
                    String path = "library/" + model.getDoc();
                    FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(documentSnapshot -> {
                        holder.setlName(documentSnapshot.get("lib").toString());
                        holder.setCsName(documentSnapshot.get("csn").toString());
                    });

                    holder.itemView.setOnClickListener(view -> {
                        FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(documentSnapshot -> {
                            Library library = (Library) documentSnapshot.toObject(Library.class);
                            Intent intent = new Intent(DashBoardActivity.this, ShowLibraryActivity.class);
                            intent.putExtra("lib_data", library);
                            startActivity(intent);
                        });
                    });
                }

                @NonNull
                @Override
                public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_library, parent, false);
                    return new LibraryViewHolder(view);
                }
            };
            adapter1.startListening();
            bookmarkRV.setAdapter(adapter1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash_board_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_library:
                Intent intent1 = new Intent(this, SearchLibraryActivity.class);
                startActivity(intent1);
                break;
            case R.id.profile:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}