package com.concepto.lbms.sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.concepto.lbms.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeFieldBottomSheet extends BottomSheetDialogFragment {

    private String name, field, path;
    private TextView changeName;
    private EditText changeText;
    private Button changeButton;

    public static ChangeFieldBottomSheet newInstance(String name, String field, String path) {
        ChangeFieldBottomSheet fragment = new ChangeFieldBottomSheet();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("field", field);
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            field = getArguments().getString("field");
            path = getArguments().getString("path");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_field_bottom_sheet, container, false);
        changeName = view.findViewById(R.id.change_field_name);
        changeText = view.findViewById(R.id.change_text);
        changeButton = view.findViewById(R.id.apply);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String text = "Change " + name;
        changeText.setText(text);

        FirebaseFirestore.getInstance().document(path).get().addOnSuccessListener(snap -> {
            changeText.setText(snap.get(field).toString());
        });

        changeButton.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().document(path).update(field, changeText.getText().toString()).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Changed!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }
}