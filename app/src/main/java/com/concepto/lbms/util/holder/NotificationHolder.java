package com.concepto.lbms.util.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.concepto.lbms.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NotificationHolder extends RecyclerView.ViewHolder {
    private TextView bookName, issueTime, username, onTime, submitTime;
    public NotificationHolder(@NonNull View itemView) {
        super(itemView);
        bookName = itemView.findViewById(R.id.book_name);
        issueTime = itemView.findViewById(R.id.issue_time);
        username = itemView.findViewById(R.id.user_name);
        onTime = itemView.findViewById(R.id.on_time);
        submitTime = itemView.findViewById(R.id.submit_time);
    }

    public void setBookName(String bookName) {
        this.bookName.setText(bookName);
    }

    public void setIssueTime(String issueTime) {
        String text = "Timing : " + issueTime;
        this.issueTime.setText(text);
    }

    public void setUsername(String uid) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener(snap -> {
            String text = "Issued By : " + snap.get("name").toString();
            this.username.setText(text);
        });
    }

    public void setOnTime(long onTime) {
        Date date = new Date(onTime);
        String text = "Issue Date : " + date.toString();
        this.onTime.setText(text);
    }

    public void setSubmitTime(long submitTime) {
        Date date = new Date(submitTime);
        String text = "Submit Date : " + date.toString();
        this.submitTime.setText(text);
    }
}
