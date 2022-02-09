package com.concepto.lbms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.concepto.lbms.sheet.BookBottomSheet;
import com.concepto.lbms.util.holder.BookViewHolder;
import com.concepto.lbms.util.model.Book;
import com.concepto.lbms.util.model.Library;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class SearchBookActivity extends AppCompatActivity {

    private String doc;
    private RecyclerView booksRV;
    private ImageButton searchButton;
    private EditText queryText;
    private Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        doc = getIntent().getStringExtra("doc");

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Book Search";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        booksRV = findViewById(R.id.books_rv);
        booksRV.setLayoutManager(new LinearLayoutManager(this));
        booksRV.setHasFixedSize(true);

        queryText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        category = findViewById(R.id.category);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(queryText.getText())) {
                Toast.makeText(this, "Search field is empty.", Toast.LENGTH_SHORT).show();
            } else {
                if (category.getSelectedItemPosition() == 0) {
                    loadSearch(queryText.getText().toString(), null, false);
                } else {
                    loadSearch(queryText.getText().toString(), category.getSelectedItem().toString(), true);
                }
            }
        });

        loadSearch("", null, false);
    }

    private void loadSearch(String queryText, String category, boolean isCategorized) {
        String path = "library/" + doc + "/books";
        Query query;
        if (!queryText.equals("")) {
            queryText = queryText.toLowerCase();
            String[] searchTags = queryText.split(" ");
            if (isCategorized) {
                category = category.toLowerCase();
                query = FirebaseFirestore.getInstance().collection(path).whereArrayContainsAny("search", Arrays.asList(searchTags)).whereEqualTo("category", category);
            } else {
                query = FirebaseFirestore.getInstance().collection(path).whereArrayContainsAny("search", Arrays.asList(searchTags));
            }
        } else {
            query = FirebaseFirestore.getInstance().collection(path).orderBy("agn", Query.Direction.DESCENDING);
        }
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
                    BookBottomSheet.newInstance(doc, model).show(getSupportFragmentManager(), "DIALOG");
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
        booksRV.setAdapter(adapter);
    }
}