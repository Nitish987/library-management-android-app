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
import com.concepto.lbms.sheet.ReturnBottomSheet;
import com.concepto.lbms.util.holder.NotificationHolder;
import com.concepto.lbms.util.model.Handover;
import com.concepto.lbms.util.model.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HandoverActivity extends AppCompatActivity {

    private String doc;
    private RecyclerView handoverRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handover);

        doc = getIntent().getStringExtra("doc");

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "Handed Books";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        handoverRV = findViewById(R.id.handover_rv);
        handoverRV.setLayoutManager(new LinearLayoutManager(this));
        handoverRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = "library/" + doc + "/handover";
        long millis = System.currentTimeMillis();
        Query query = FirebaseFirestore.getInstance().collection(path).whereGreaterThanOrEqualTo("sd",millis).limit(50);
        FirestoreRecyclerOptions<Handover> options = new FirestoreRecyclerOptions.Builder<Handover>().setQuery(query, Handover.class).build();
        FirestoreRecyclerAdapter<Handover, NotificationHolder> adapter = new FirestoreRecyclerAdapter<Handover, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Handover model) {
                holder.setIssueTime(model.getIt());
                holder.setOnTime(model.getTime());
                holder.setUsername(model.getBy());
                holder.setSubmitTime(model.getSd());
                FirebaseFirestore.getInstance().document("library/"+doc+"/books/"+model.getBook()).get().addOnSuccessListener(documentSnapshot -> {
                    holder.setBookName(documentSnapshot.get("name").toString());
                });

                holder.itemView.setOnClickListener(view -> {
                    ReturnBottomSheet.newInstance(model).show(getSupportFragmentManager(), "DIALOG");
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
        handoverRV.setAdapter(adapter);
    }
}