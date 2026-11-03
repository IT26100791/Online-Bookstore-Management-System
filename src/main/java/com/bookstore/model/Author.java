package com.bookstore.model;

public class Author {
    private String authorId;
    private String name;
    private String country;

    public Author(String authorId, String name, String country) {
        this.authorId = authorId;
        this.name = name;
        this.country = country;
    }

    public String getId() {
        return authorId;
    }

    public void setId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTypeDescription() {
        return "Standard author";
    }

    public String toFileLine() {
        return authorId + "," + name + "," + country;
    }
}