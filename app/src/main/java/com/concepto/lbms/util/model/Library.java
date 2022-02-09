package com.concepto.lbms.util.model;

import java.io.Serializable;
import java.util.List;

public class Library implements Serializable {
    private List<String> all, by;
    private int ba;
    private String contact, csn, doc, lib, photo;
    public List<String> search;

    public Library(){}

    public Library(List<String> all, int ba, List<String> by, String contact, String csn, String doc, String lib, String photo, List<String> search) {
        this.all = all;
        this.ba = ba;
        this.by = by;
        this.contact = contact;
        this.csn = csn;
        this.doc = doc;
        this.lib = lib;
        this.photo = photo;
        this.search = search;
    }

    public List<String> getBy() {
        return by;
    }

    public void setBy(List<String> by) {
        this.by = by;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCsn() {
        return csn;
    }

    public void setCsn(String csn) {
        this.csn = csn;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getAll() {
        return all;
    }

    public void setAll(List<String> all) {
        this.all = all;
    }

    public int getBa() {
        return ba;
    }

    public void setBa(int ba) {
        this.ba = ba;
    }

    public List<String> getSearch() {
        return search;
    }

    public void setSearch(List<String> search) {
        this.search = search;
    }
}
