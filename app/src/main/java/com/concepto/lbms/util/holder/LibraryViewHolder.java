package com.concepto.lbms.util.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.concepto.lbms.R;

public class LibraryViewHolder extends RecyclerView.ViewHolder {

    private final TextView lName, csName;

    public LibraryViewHolder(@NonNull View itemView) {
        super(itemView);
        lName = itemView.findViewById(R.id.l_name);
        csName = itemView.findViewById(R.id.s_c_name);
    }


    public void setlName(String lName) {
        this.lName.setText(lName);
    }

    public void setCsName(String csName) {
        this.csName.setText(csName);
    }
}
