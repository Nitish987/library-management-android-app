package com.concepto.lbms.util.model;

import java.io.Serializable;

public class Member implements Serializable {
    private String role, uid;

    public Member(){}

    public Member(String role, String uid) {
        this.role = role;
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
