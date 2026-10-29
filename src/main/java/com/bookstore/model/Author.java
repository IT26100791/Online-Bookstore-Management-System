package com.bookstore.model;

public class Author {
    private final String authorId;
    private final String name;
    private final String country;

    public Author(String authorId, String name, String country) {
        this.authorId = authorId;
        this.name = name;
        this.country = country;
    }

    public String getAuthorId() { return authorId; }
    public String getName()     { return name; }
    public String getCountry()  { return country; }

    // one line in authors.txt:  id,name,country
    public String toFileLine() {
        return authorId + "," + name + "," + country;
    }
}
