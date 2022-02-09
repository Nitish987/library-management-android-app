package com.concepto.lbms.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.concepto.lbms.R;
import com.concepto.lbms.util.Compare;
import com.concepto.lbms.util.model.Library;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMemberDialog extends Dialog {

    private Spinner role;
    private EditText email;
    private Button addMember;
    private final Library library;
    private List<String> by;

    public AddMemberDialog(@NonNull Context context, Library library) {
        super(context);
        this.library = library;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_member);

        role = findViewById(R.id.role);
        email = findViewById(R.id.email);
        addMember = findViewById(R.id.add_member);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addMember.setOnClickListener(view -> {
            int i = role.getSelectedItemPosition();
            if ((i == 1 || i == 2 || i == 3 || i == 4) && !TextUtils.isEmpty(email.getText())) {
                String mail = email.getText().toString();
                FirebaseFirestore.getInstance().collection("emails").document(mail).get().addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        String uid = snap.get("uid").toString();
                        if (Compare.asStringExistenceInList(uid, library.getBy()) || Compare.asStringExistenceInList(uid, library.getAll())) {
                            Toast.makeText(getContext(), "User is already in Library!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("uid",uid);
                            map.put("role", role.getSelectedItem().toString().toLowerCase());
                            FirebaseFirestore.getInstance().document("library/" + library.getDoc() + "/members/" + uid).set(map).addOnSuccessListener(unused -> {
                                String path = "library/" + library.getDoc();
                                List<String> bys = library.getBy();
                                if (i == 1) {
                                    bys.add(uid);
                                    FirebaseFirestore.getInstance().document(path).update("by",bys).addOnSuccessListener(unused1 -> {
                                        List<String> alls = library.getAll();
                                        alls.add(uid);
                                        FirebaseFirestore.getInstance().document(path).update("all",alls).addOnSuccessListener(unused2 -> {
                                            Toast.makeText(getContext(), "User is Added to the Library", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        });
                                    });
                                } else {
                                    List<String> alls = library.getAll();
                                    alls.add(uid);
                                    FirebaseFirestore.getInstance().document(path).update("all",alls).addOnSuccessListener(unused2 -> {
                                        Toast.makeText(getContext(), "User is Added to the Library", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    });
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), "No user found!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Fields are Empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
