package com.concepto.lbms.util.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.concepto.lbms.R;

public class BookViewHolder extends RecyclerView.ViewHolder {
    private ImageView photo;
    private TextView bookName, bookAuthor, bookCategory, bookCS;
    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        bookName = itemView.findViewById(R.id.name);
        bookAuthor = itemView.findViewById(R.id.author);
        bookCategory = itemView.findViewById(R.id.category);
        bookCS = itemView.findViewById(R.id.cs);
    }

    public void setPhoto(String photo) {
        if (!photo.equals("")) {
            Glide.with(itemView.getContext()).load(photo).into(this.photo);
        }
    }

    public void setBookName(String bookName) {
        this.bookName.setText(bookName);
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor.setText(bookAuthor);
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory.setText(bookCategory);
    }

    public void setBookCS(String bookCS) {
        this.bookCS.setText(bookCS);
    }
}
