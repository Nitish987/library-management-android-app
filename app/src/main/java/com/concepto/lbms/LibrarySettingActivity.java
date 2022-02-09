package com.concepto.lbms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.concepto.lbms.sheet.ChangeFieldBottomSheet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LibrarySettingActivity extends AppCompatActivity {

    private String doc;
    private TextView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        doc = getIntent().getStringExtra("doc");

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Library Settings";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        contact = findViewById(R.id.contact);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().document("library/" + doc).addSnapshotListener((value, error) -> {
            String email = value.get("contact").toString();
            contact.setText(email);
        });

        String path = "library/" + doc + "/members/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(this, documentSnapshot -> {
            String role = (String) documentSnapshot.get("role").toString();
            if (role.equals("admin")) {
                listener();
            }
        });
    }

    private void listener() {
        contact.setOnClickListener(view -> {
            ChangeFieldBottomSheet.newInstance("Contact", "contact", "library/" + doc).show(getSupportFragmentManager(), "DIALOG");
        });
    }
}