package com.concepto.lbms.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.concepto.lbms.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateLibraryDialog extends Dialog {

    private String uid;
    private EditText libName, csName, cEmail;
    private Button create;

    public CreateLibraryDialog(@NonNull Context context, String uid) {
        super(context);
        this.uid = uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_library);

        libName = findViewById(R.id.lib_name);
        csName = findViewById(R.id.s_c_name);
        cEmail = findViewById(R.id.c_email);
        create = findViewById(R.id.create);
    }

    @Override
    protected void onStart() {
        super.onStart();
        create.setOnClickListener(view -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("lib",libName.getText().toString());
            map.put("csn", csName.getText().toString());
            map.put("contact",cEmail.getText().toString());
            map.put("photo","");
            map.put("by", Arrays.asList(uid));
            map.put("all", Arrays.asList(uid));
            map.put("ba",0);

            List<String> search = new ArrayList<>();
            search.add(libName.getText().toString().toLowerCase());
            search.add(csName.getText().toString().toLowerCase());
            search.addAll(Arrays.asList(libName.getText().toString().toLowerCase().split(" ")));
            search.addAll(Arrays.asList(csName.getText().toString().toLowerCase().split(" ")));
            map.put("search", search);

            if (TextUtils.isEmpty(libName.getText()) || TextUtils.isEmpty(csName.getText())) {
                Toast.makeText(getContext(), "Fields are empty.", Toast.LENGTH_SHORT).show();
            } else {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("library").document();
                map.put("doc", reference.getId());
                reference.set(map).addOnSuccessListener(unused -> {
                    String path = "library/"+reference.getId()+"/members/"+uid;
                    Map<String, Object> member = new HashMap<String, Object>();
                    member.put("uid",uid);
                    member.put("role","admin");
                    FirebaseFirestore.getInstance().document(path).set(member).addOnSuccessListener(unused1 -> {
                        Toast.makeText(getContext(), "Library Created", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                });
            }
        });
    }
}
