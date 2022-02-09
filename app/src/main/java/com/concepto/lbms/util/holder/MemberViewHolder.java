package com.concepto.lbms.util.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.concepto.lbms.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView photo;
    private TextView name, role;

    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        name = itemView.findViewById(R.id.name);
        role = itemView.findViewById(R.id.role);
    }

    public void setPhoto(String photo) {
        if (!photo.equals("")) {
            Glide.with(itemView.getRootView()).load(photo).into(this.photo);
        }
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setRole(String role) {
        this.role.setText(role);
    }
}
