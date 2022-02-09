package com.concepto.lbms.util.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String book, by, it, lib, ref;
    private long sd, time;

    public Notification(){}

    public Notification(String book, String by, String it, String lib, String ref, long sd, long time) {
        this.book = book;
        this.by = by;
        this.it = it;
        this.lib = lib;
        this.ref = ref;
        this.sd = sd;
        this.time = time;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSd() {
        return sd;
    }

    public void setSd(long sd) {
        this.sd = sd;
    }
}
