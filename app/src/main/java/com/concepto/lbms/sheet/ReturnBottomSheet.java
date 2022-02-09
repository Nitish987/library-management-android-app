package com.concepto.lbms.sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.concepto.lbms.R;
import com.concepto.lbms.util.model.Handover;
import com.concepto.lbms.util.model.Notification;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReturnBottomSheet extends BottomSheetDialogFragment {

    private Handover handover;
    private TextView bookName, bookAuthor, issueTime, onTime, submitTime, username, libraryID, rollNO, phone;
    private Button returned;

    public static ReturnBottomSheet newInstance(Handover handover) {
        ReturnBottomSheet fragment = new ReturnBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("notify", handover);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.handover = (Handover) getArguments().getSerializable("notify");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_return_bottom_sheet, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        issueTime = view.findViewById(R.id.issue_time);
        onTime = view.findViewById(R.id.on_time);
        submitTime = view.findViewById(R.id.submit_time);
        username = view.findViewById(R.id.user_name);
        libraryID = view.findViewById(R.id.lid);
        rollNO = view.findViewById(R.id.roll_no);
        phone = view.findViewById(R.id.phone);
        returned = view.findViewById(R.id.returned);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String bookPath = "library/" + handover.getLib() + "/books/" + handover.getBook();
        FirebaseFirestore.getInstance().document(bookPath).get().addOnSuccessListener(documentSnapshot -> {
            bookName.setText(documentSnapshot.get("name").toString());
            bookAuthor.setText(documentSnapshot.get("author").toString());
        });

        issueTime.setText(handover.getIt());
        String d;
        Date date = new Date(handover.getTime());
        d = "Issue on : " + date.toString();
        onTime.setText(d);
        Date date1 = new Date(handover.getSd());
        d = "Submit on : " + date1.toString();
        submitTime.setText(d);

        String userPath = "users/" + handover.getBy();
        FirebaseFirestore.getInstance().document(userPath).get().addOnSuccessListener(documentSnapshot -> {
            String text;
            text = "Name : " + documentSnapshot.get("name").toString();
            username.setText(text);
            text = "Library ID : " + documentSnapshot.get("lid").toString();
            libraryID.setText(text);
            text = "Roll No : " + documentSnapshot.get("roll").toString();
            rollNO.setText(text);
            text = "Phone : " + documentSnapshot.get("phone").toString();
            phone.setText(text);
        });

        returned.setOnClickListener(view -> {
            String path1 = "library/" + handover.getLib() + "/handover/" + handover.getRef();
            FirebaseFirestore.getInstance().document(path1).delete().addOnSuccessListener(unused -> {
                String path2 = "users/" + handover.getBy() + "/notify/" + handover.getRef();
                FirebaseFirestore.getInstance().document(path2).delete().addOnSuccessListener(unused1 -> {
                    Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            });
        });
    }
}