package com.concepto.lbms.sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.concepto.lbms.R;
import com.concepto.lbms.util.FutureDate;
import com.concepto.lbms.util.model.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookBottomSheet extends BottomSheetDialogFragment {

    private String doc;
    private Book book;
    private TextView bookName, bookAuthor;
    private Spinner issueTime;
    private Button apply;

    public static BookBottomSheet newInstance(String doc, Book book) {
        BookBottomSheet fragment = new BookBottomSheet();
        Bundle args = new Bundle();
        args.putString("doc",doc);
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doc = getArguments().getString("doc");
            book = (Book) getArguments().getSerializable("book");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_book_bottom_sheet, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        issueTime = view.findViewById(R.id.issue_time);
        apply = view.findViewById(R.id.apply);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        load();

        apply.setOnClickListener(view -> {
            Map<String, Object> map = new HashMap<>();
            map.put("it", issueTime.getSelectedItem().toString().toLowerCase());
            map.put("by", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("book", book.getDoc());
            map.put("lib", doc);
            map.put("time", System.currentTimeMillis());
            map.put("sd", FutureDate.getFutureMillis(System.currentTimeMillis(), issueTime.getSelectedItem().toString().toLowerCase()));

            String path = "library/" + doc + "/notifications";
            DocumentReference ref = FirebaseFirestore.getInstance().collection(path).document();
            map.put("ref", ref.getId());

            ref.set(map).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Book application placed.", Toast.LENGTH_SHORT).show();
                dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Application not done.", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }

    private void load() {
        bookName.setText(book.getName());
        bookAuthor.setText(book.getAuthor());
    }
}