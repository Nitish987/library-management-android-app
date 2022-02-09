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
import com.concepto.lbms.util.FutureDate;
import com.concepto.lbms.util.model.Notification;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HandoverBottomSheet extends BottomSheetDialogFragment {

    private Notification notification;
    private TextView bookName, bookAuthor, issueTime, onTime, submitTime, username, libraryID, rollNO, phone;
    private Button handover, reject;

    public static HandoverBottomSheet newInstance(Notification notification) {
        HandoverBottomSheet fragment = new HandoverBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("notify",notification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.notification = (Notification) getArguments().getSerializable("notify");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handover_bottom_sheet, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        issueTime = view.findViewById(R.id.issue_time);
        onTime = view.findViewById(R.id.on_time);
        submitTime = view.findViewById(R.id.submit_time);
        username = view.findViewById(R.id.user_name);
        libraryID = view.findViewById(R.id.lid);
        rollNO = view.findViewById(R.id.roll_no);
        phone = view.findViewById(R.id.phone);
        handover = view.findViewById(R.id.handover);
        reject = view.findViewById(R.id.reject);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String bookPath = "library/" + notification.getLib() + "/books/" + notification.getBook();
        FirebaseFirestore.getInstance().document(bookPath).get().addOnSuccessListener(documentSnapshot -> {
            bookName.setText(documentSnapshot.get("name").toString());
            bookAuthor.setText(documentSnapshot.get("author").toString());
        });

        issueTime.setText(notification.getIt());
        String d;
        Date date = new Date(notification.getTime());
        d = "Issue on : " + date.toString();
        onTime.setText(d);
        Date date1 = new Date(notification.getSd());
        d = "Submit on : " + date1.toString();
        submitTime.setText(d);

        String userPath = "users/" + notification.getBy();
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

        handover.setOnClickListener(view -> {
            String path = "library/" + notification.getLib() + "/handover";
            DocumentReference reference = FirebaseFirestore.getInstance().collection(path).document();

            Map<String, Object> map = new HashMap<>();
            map.put("it", notification.getIt());
            map.put("by", notification.getBy());
            map.put("book", notification.getBook());
            map.put("lib", notification.getLib());
            map.put("time", notification.getTime());
            map.put("sd", notification.getSd());
            map.put("ref", reference.getId());

            reference.set(map).addOnSuccessListener(unused -> {
                FirebaseFirestore.getInstance().document("users/" + notification.getBy() + "/notify/" + reference.getId()).set(map).addOnSuccessListener(unused1 -> {
                    String path1 = "library/" + notification.getLib() + "/notifications/" + notification.getRef();
                    FirebaseFirestore.getInstance().document(path1).delete().addOnSuccessListener(unused2 -> {
                        Toast.makeText(getContext(), "Added to Handover list.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                });
            });
        });

        reject.setOnClickListener(view -> {
            String path = "library/" + notification.getLib() + "/notifications/" + notification.getRef();
            FirebaseFirestore.getInstance().document(path).delete().addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Rejected!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }
}