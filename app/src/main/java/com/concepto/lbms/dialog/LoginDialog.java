package com.concepto.lbms.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.concepto.lbms.DashBoardActivity;
import com.concepto.lbms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends Dialog {

    private Context context;
    private Spinner role;
    private EditText name, phone, lid, rollNo;
    private Button apply;
    private String uid;

    public LoginDialog(@NonNull Context context, String uid) {
        super(context);
        this.context = context;
        this.uid = uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);

        role = findViewById(R.id.role);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        lid = findViewById(R.id.lid);
        rollNo = findViewById(R.id.roll_no);
        apply = findViewById(R.id.apply);
    }

    @Override
    protected void onStart() {
        super.onStart();
        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1 || i == 2 || i == 3) {
                    rollNo.setVisibility(View.GONE);
                } else if (i == 4){
                    rollNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        apply.setOnClickListener(view -> {
            int i = role.getSelectedItemPosition();
            if (i != 0) {
                String mRole = "", path = "users/" + uid;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", name.getText().toString());
                map.put("phone", phone.getText().toString());
                map.put("roll", rollNo.getText().toString());
                map.put("lid", lid.getText().toString());
                map.put("uid", uid);
                map.put("photo", "");
                switch (i) {
                    case 1 : mRole = "admin"; break;
                    case 2 : mRole = "worker"; break;
                    case 3 : mRole = "teacher"; break;
                    case 4 : mRole = "student"; break;
                }
                map.put("role",mRole);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnSuccessListener(getTokenResult -> {
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        map.put("token", getTokenResult.getToken());
                        map.put("email", email);
                        FirebaseFirestore.getInstance().document(path).set(map).addOnSuccessListener(unused -> {
                            Map<String, Object> user = new HashMap<String, Object>();
                            user.put("uid",uid);
                            FirebaseFirestore.getInstance().collection("emails").document(email).set(user).addOnSuccessListener(unused1 -> {
                                openDashBoardActivity();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Unable to create an account.", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            } else {
                Toast.makeText(context, "Select a Role.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDashBoardActivity() {
        Intent i = new Intent(context, DashBoardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(context, "First fill the details.", Toast.LENGTH_SHORT).show();
    }
}
