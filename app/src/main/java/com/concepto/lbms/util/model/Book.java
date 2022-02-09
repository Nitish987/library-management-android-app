package com.concepto.lbms.util.model;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private String author, category, cs, doc, isbn;
    private int issued;
    private String name, number, photo, pub;
    private int quantity;
    private List<String> search;

    public Book(){}

    public Book(String author, String category, String cs, String doc, String isbn, int issued, String name, String number, String photo, String pub, int quantity, List<String> search) {
        this.author = author;
        this.category = category;
        this.cs = cs;
        this.doc = doc;
        this.isbn = isbn;
        this.issued = issued;
        this.name = name;
        this.number = number;
        this.photo = photo;
        this.pub = pub;
        this.quantity = quantity;
        this.search = search;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getIssued() {
        return issued;
    }

    public void setIssued(int issued) {
        this.issued = issued;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getSearch() {
        return search;
    }

    public void setSearch(List<String> search) {
        this.search = search;
    }
}
