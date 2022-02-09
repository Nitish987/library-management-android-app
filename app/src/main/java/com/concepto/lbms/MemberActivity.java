package com.concepto.lbms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.concepto.lbms.sheet.MemberBottomSheet;
import com.concepto.lbms.util.holder.MemberViewHolder;
import com.concepto.lbms.util.model.Member;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MemberActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView membersRV;
    private String doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        auth = FirebaseAuth.getInstance();
        doc = getIntent().getStringExtra("doc");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Members");
        setSupportActionBar(toolbar);

        membersRV = findViewById(R.id.members_rv);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
        membersRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MemberActivity.this, MainActivity.class));
            finish();
        } else {
            String path = "library/" + doc + "/members";
            Query query = FirebaseFirestore.getInstance().collection(path);
            FirestoreRecyclerOptions<Member> options = new FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member.class).build();
            FirestoreRecyclerAdapter<Member, MemberViewHolder> adapter = new FirestoreRecyclerAdapter<Member, MemberViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull Member model) {
                    String path = "users/" + model.getUid();
                    FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(snap -> {
                        holder.setPhoto(snap.get("photo").toString());
                        holder.setName(snap.get("name").toString());
                        holder.setRole(model.getRole());
                    });
                    holder.itemView.setOnClickListener(view -> {
                        MemberBottomSheet.newInstance(doc, model).show(getSupportFragmentManager(), "DIALOG");
                    });
                }

                @NonNull
                @Override
                public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_member, parent, false);
                    return new MemberViewHolder(view);
                }
            };
            adapter.startListening();
            membersRV.setAdapter(adapter);
        }
    }
}