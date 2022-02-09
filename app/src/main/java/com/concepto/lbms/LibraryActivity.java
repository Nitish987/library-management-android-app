package com.concepto.lbms;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.concepto.lbms.dialog.AddMemberDialog;
import com.concepto.lbms.util.Compare;
import com.concepto.lbms.util.holder.BookViewHolder;
import com.concepto.lbms.util.model.Book;
import com.concepto.lbms.util.model.Library;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LibraryActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FloatingActionButton addMember;
    private static Library library;
    private ImageButton photoChange, addBook;
    private ImageView photo;
    private ProgressBar photoProgress;
    private ActivityResultLauncher<Intent> photoChangeResult;
    private StorageReference reference;
    private CardView bookAddCard;
    private RecyclerView recentlyAddedBooksRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        auth = FirebaseAuth.getInstance();
        library = (Library) getIntent().getSerializableExtra("lib_data");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference("library").child(library.getDoc());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(library.getLib());
        setSupportActionBar(toolbar);

        addMember = findViewById(R.id.add_member);
        photoChange = findViewById(R.id.change_photo);
        photo = findViewById(R.id.photo);
        photoProgress = findViewById(R.id.photo_progress);
        addBook = findViewById(R.id.add_book);
        bookAddCard = findViewById(R.id.book_card);
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
            if (Compare.asStringExistenceInList(uid, library.getBy())){
                addMember.setVisibility(View.VISIBLE);
                photoChange.setVisibility(View.VISIBLE);
            } else {
                addMember.setVisibility(View.GONE);
                photoChange.setVisibility(View.GONE);
            }
            String path = "library/" + library.getDoc() + "/members/" + uid;
            FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(documentSnapshot -> {
                String role = (String) documentSnapshot.get("role").toString();
                if (role.equals("admin") || role.equals("co-admin")) {
                    bookAddCard.setVisibility(View.VISIBLE);
                }
            });
        }

        loadPhoto();

        addMember.setOnClickListener(view -> {
            AddMemberDialog dialog = new AddMemberDialog(LibraryActivity.this, library);
            dialog.show();
        });

        photoChange.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            photoChangeResult.launch(intent);
        });

        photoChangeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            Uri uri = intent.getData();
            library.setPhoto(uri.toString());
            loadPhoto();
            StorageReference storageReference = reference.child("photo.png");
            storageReference.putFile(uri).addOnProgressListener(snapshot -> {
                photoProgress.setVisibility(View.VISIBLE);
            }).addOnCompleteListener(task -> {
                photoProgress.setVisibility(View.GONE);
            }).addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    String path = "library/" + library.getDoc();
                    FirebaseFirestore.getInstance().document(path).update("photo",uri1.toString()).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Library Pic Changed.", Toast.LENGTH_SHORT).show();
                    });
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
            });
        });

        addBook.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddBookActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

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
    protected void onResume() {
        super.onResume();
        String path = "library/" + library.getDoc();
        FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(this, snap -> {
            library = (Library) snap.toObject(Library.class);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_library:
                Intent intent1 = new Intent(LibraryActivity.this, SearchBookActivity.class);
                intent1.putExtra("doc", library.getDoc());
                startActivity(intent1);
                break;
            case R.id.notify:
                Intent intent2 = new Intent(LibraryActivity.this, NotificationActivity.class);
                intent2.putExtra("doc", library.getDoc());
                startActivity(intent2);
                break;
            case R.id.handed_books:
                Intent intent3 = new Intent(LibraryActivity.this, HandoverActivity.class);
                intent3.putExtra("doc", library.getDoc());
                startActivity(intent3);
                break;
            case R.id.members:
                Intent intent4 = new Intent(LibraryActivity.this, MemberActivity.class);
                intent4.putExtra("doc", library.getDoc());
                startActivity(intent4);
                break;
            case R.id.settings:
                Intent intent5 = new Intent(LibraryActivity.this, LibrarySettingActivity.class);
                intent5.putExtra("doc", library.getDoc());
                startActivity(intent5);
                break;
        }
        return true;
    }
}