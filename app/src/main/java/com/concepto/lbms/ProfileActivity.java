package com.concepto.lbms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.concepto.lbms.util.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView name, phone, libID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        String title = "Profile";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        libID = findViewById(R.id.library_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String path = "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(documentSnapshot -> {
                User user = (User) documentSnapshot.toObject(User.class);
                name.setText(user.getName());
                phone.setText(user.getPhone());
                libID.setText(user.getLid());
            });
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}