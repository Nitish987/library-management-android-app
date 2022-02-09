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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.concepto.lbms.sheet.BookBottomSheet;
import com.concepto.lbms.util.holder.BookViewHolder;
import com.concepto.lbms.util.model.Book;
import com.concepto.lbms.util.model.Library;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ShowLibraryActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FloatingActionButton addBookmark;
    private static Library library;
    private ImageView photo;
    private RecyclerView recentlyAddedBooksRV;
    private static boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_library);

        auth = FirebaseAuth.getInstance();
        library = (Library) getIntent().getSerializableExtra("lib_data");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(library.getLib());
        setSupportActionBar(toolbar);

        photo = findViewById(R.id.photo);
        addBookmark = findViewById(R.id.add_bookmark);
        recentlyAddedBooksRV = findViewById(R.id.recently_books_rv);
        recentlyAddedBooksRV.setLayoutManager(new LinearLayoutManager(this));
        recentlyAddedBooksRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String uid = auth.getCurrentUser().getUid();
            String path = "users/" + uid + "/bookmarks/" + library.getDoc();
            FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(this,documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    addBookmark.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                    isBookmarked = true;
                }
            });

            addBookmark.setOnClickListener(view -> {
                if (isBookmarked) {
                    FirebaseFirestore.getInstance().document(path).delete().addOnSuccessListener(unused -> {
                        addBookmark.setImageResource(R.drawable.ic_outline_bookmark_add_24);
                        isBookmarked = false;
                    });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("doc", library.getDoc());
                    FirebaseFirestore.getInstance().document(path).set(map).addOnSuccessListener(unused -> {
                        addBookmark.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                        isBookmarked = true;
                    });
                }
            });

            loadPhoto();
        }

        String path = "library/" + library.getDoc() + "/books";
        Query query = FirebaseFirestore.getInstance().collection(path).orderBy("agn", Query.Direction.DESCENDING).limit(20);
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        FirestoreRecyclerAdapter<Book, BookViewHolder> adapter = new FirestoreRecyclerAdapter<Book, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
                holder.setPhoto(model.getPhoto());
                holder.setBookName(model.getName());
                holder.setBookAuthor(model.getAuthor());
                holder.setBookCategory(model.getCategory());
                holder.setBookCS(model.getCs());

                holder.itemView.setOnClickListener(view -> {
                    BookBottomSheet.newInstance(library.getDoc(), model).show(getSupportFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_book, parent, false);
                return new BookViewHolder(view);
            }
        };
        adapter.startListening();
        recentlyAddedBooksRV.setAdapter(adapter);
    }

    private void loadPhoto() {
        if (!library.getPhoto().equals("")) {
            Glide.with(getApplicationContext()).load(library.getPhoto()).into(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_library_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_library:
                Intent intent1 = new Intent(ShowLibraryActivity.this, SearchBookActivity.class);
                intent1.putExtra("doc", library.getDoc());
                startActivity(intent1);
                break;
            case R.id.notify:
                Intent intent2 = new Intent(ShowLibraryActivity.this, UserNotificationActivity.class);
                intent2.putExtra("doc", library.getDoc());
                startActivity(intent2);
                break;
        }
        return true;
    }
}