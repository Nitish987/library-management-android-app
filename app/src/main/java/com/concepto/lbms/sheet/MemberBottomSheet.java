package com.concepto.lbms.sheet;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.concepto.lbms.R;

import com.concepto.lbms.util.model.Library;
import com.concepto.lbms.util.model.Member;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MemberBottomSheet extends BottomSheetDialogFragment {

    private String doc;
    private Member member;
    private Spinner role;
    private Button apply, remove;

    public static MemberBottomSheet newInstance(String doc, Member member) {
        MemberBottomSheet fragment = new MemberBottomSheet();
        Bundle args = new Bundle();
        args.putString("doc", doc);
        args.putSerializable("member", member);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.doc = getArguments().getString("doc");
            this.member = (Member) getArguments().getSerializable("member");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_bottom_sheet, container, false);
        role = view.findViewById(R.id.role);
        apply = view.findViewById(R.id.apply);
        remove = view.findViewById(R.id.remove);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (member.getRole()) {
            case "admin": role.setSelection(1); break;
            case "co-admin": role.setSelection(2); break;
            case "elder": role.setSelection(3); break;
            case "member": role.setSelection(4); break;
        }

        if (member.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            setEnabled(false);
        }

        FirebaseFirestore.getInstance().document("library/" + doc + "/members/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.get("role").toString().equals("admin")) {
                setEnabled(false);
            }
        });

        apply.setOnClickListener(view -> {
            int i = role.getSelectedItemPosition();
            String path = "library/" + doc + "/members/" + member.getUid();
            FirebaseFirestore.getInstance().document(path).update("role", role.getSelectedItem().toString().toLowerCase()).addOnSuccessListener(unused -> {
                FirebaseFirestore.getInstance().document("library/" + doc).get().addOnSuccessListener(documentSnapshot -> {
                    Library library = (Library) documentSnapshot.toObject(Library.class);
                    List<String> list = library.getBy();
                    list.remove(member.getUid());
                    if (i == 1) {
                        list.add(member.getUid());
                    }
                    FirebaseFirestore.getInstance().document("library/" + doc).update("by",list).addOnSuccessListener(unused1 -> {
                        Toast.makeText(getContext(), "Role Changed!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                });
            });
        });

        remove.setOnClickListener(view -> {
            String path = "library/" + doc + "/members/" + member.getUid();
            FirebaseFirestore.getInstance().document(path).delete().addOnSuccessListener(unused -> {
                FirebaseFirestore.getInstance().document("library/" + doc).get().addOnSuccessListener(documentSnapshot -> {
                    Library library = (Library) documentSnapshot.toObject(Library.class);
                    List<String> list = library.getBy();
                    list.remove(member.getUid());
                    FirebaseFirestore.getInstance().document("library/" + doc).update("by",list).addOnSuccessListener(unused1 -> {
                        List<String> list1 = library.getAll();
                        list1.remove(member.getUid());
                        FirebaseFirestore.getInstance().document("library/" + doc).update("all",list1).addOnSuccessListener(unused2 -> {
                            Toast.makeText(getContext(), "Member Removed!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    });
                });
            });
        });
    }

    private void setEnabled(boolean change) {
        role.setEnabled(change);
        apply.setEnabled(change);
        remove.setEnabled(change);
    }
}