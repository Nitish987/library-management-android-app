package com.concepto.lbms;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.concepto.lbms.util.Compare;
import com.concepto.lbms.util.model.Library;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    private Library library;
    private FirebaseAuth auth;
    private Spinner category;
    private EditText name, author, isbn, quantity, pub, bNum, cs;
    private Button addBook;
    private ImageView photoView;
    private ImageButton pickPhoto;
    private ProgressBar photoProgress;
    private static StorageReference reference;
    private static ActivityResultLauncher<Intent> photoClickResult;
    private Bitmap photo = null;
    private int agn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        auth = FirebaseAuth.getInstance();
        library = (Library) getIntent().getSerializableExtra("library");


        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference().child("library").child(library.getDoc()).child("books");

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Book Details";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        author = findViewById(R.id.author);
        isbn = findViewById(R.id.isbn);
        quantity = findViewById(R.id.quantity);
        pub = findViewById(R.id.publication);
        bNum = findViewById(R.id.book_number);
        cs = findViewById(R.id.book_class_sem);
        addBook = findViewById(R.id.add_book);
        photoProgress = findViewById(R.id.photo_progress);
        photoView = findViewById(R.id.photo);
        pickPhoto = findViewById(R.id.change_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        photoClickResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            photo = (Bitmap) intent.getExtras().get("data");
            Glide.with(getApplicationContext()).load(photo).into(photoView);
        });

        pickPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoClickResult.launch(intent);
        });

        addBook.setOnClickListener(view -> {
            int index = category.getSelectedItemPosition();
            if (index == 0 || TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(author.getText()) || TextUtils.isEmpty(quantity.getText())) {
                Toast.makeText(this, "Required fields are Empty", Toast.LENGTH_SHORT).show();
            } else {
                agn = library.getBa() + 1;

                Map<String, Object> map = new HashMap<>();
                map.put("category", category.getSelectedItem().toString().toLowerCase());
                map.put("name", name.getText().toString());
                map.put("author", author.getText().toString());
                map.put("isbn", isbn.getText().toString());
                map.put("quantity", Integer.parseInt(quantity.getText().toString()));
                map.put("issued", 0);
                map.put("pub", pub.getText().toString());
                map.put("number", bNum.getText().toString());
                map.put("cs", cs.getText().toString());
                map.put("agn", agn);

                List<String> search = new ArrayList<String>();
                search.add(category.getSelectedItem().toString().toLowerCase());
                search.add(author.getText().toString().toLowerCase());
                search.add(pub.getText().toString().toLowerCase());
                search.add(name.getText().toString().toLowerCase());
                String[] tags1 = name.getText().toString().toLowerCase().split(" ");
                search.addAll(Arrays.asList(tags1));
                String[] tags2 = author.getText().toString().toLowerCase().split(" ");
                search.addAll(Arrays.asList(tags2));

                map.put("search", search);

                String path = "library/" + library.getDoc() + "/books/";
                DocumentReference ref = FirebaseFirestore.getInstance().collection(path).document();

                map.put("doc", ref.getId());

                Uri photoUri;
                try {
                    photoUri = getImageUri(getApplicationContext(), photo);
                } catch (Exception e) {
                    photoUri = null;
                }

                if (photoUri == null) {
                    map.put("photo", "");
                    ref.set(map).addOnSuccessListener(unused -> {
                        String r = "library/" + library.getDoc();
                        FirebaseFirestore.getInstance().document(r).update("ba", agn).addOnSuccessListener(unused1 -> {
                            Toast.makeText(this, "Book added.", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "unable to add book.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    StorageReference storageReference = reference.child(ref.getId() + ".png");
                    storageReference.putFile(photoUri).addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            map.put("photo", uri.toString());
                            ref.set(map).addOnSuccessListener(unused -> {
                                String r = "library/" + library.getDoc();
                                FirebaseFirestore.getInstance().document(r).update("ba", agn).addOnSuccessListener(unused1 -> {
                                    Toast.makeText(this, "Book added.", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "unable to add book.", Toast.LENGTH_SHORT).show();
                            });
                        });
                    }).addOnProgressListener(snapshot -> {
                        photoProgress.setVisibility(View.VISIBLE);
                    }).addOnCompleteListener(task -> {
                        photoProgress.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}