package com.concepto.lbms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.concepto.lbms.sheet.HandoverBottomSheet;
import com.concepto.lbms.util.holder.NotificationHolder;
import com.concepto.lbms.util.model.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserNotificationActivity extends AppCompatActivity {

    private String doc;
    private RecyclerView notifyRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);

        doc = getIntent().getStringExtra("doc");

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Notifications";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        notifyRV = findViewById(R.id.notify_rv);
        notifyRV.setLayoutManager(new LinearLayoutManager(this));
        notifyRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/notify";
        Query query = FirebaseFirestore.getInstance().collection(path).orderBy("time", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();
        FirestoreRecyclerAdapter<Notification, NotificationHolder> adapter = new FirestoreRecyclerAdapter<Notification, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notification model) {
                holder.setIssueTime(model.getIt());
                holder.setOnTime(model.getTime());
                holder.setUsername(model.getBy());
                holder.setSubmitTime(model.getSd());
                FirebaseFirestore.getInstance().document("library/"+doc+"/books/"+model.getBook()).get().addOnSuccessListener(documentSnapshot -> {
                    holder.setBookName(documentSnapshot.get("name").toString());
                });
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_notification, parent, false);
                return new NotificationHolder(view);
            }
        };
        adapter.startListening();
        notifyRV.setAdapter(adapter);
    }
}