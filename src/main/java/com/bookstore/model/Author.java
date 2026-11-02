package com.bookstore.model;

public class Author {
    private final String authorId;
    private final String name;
    private final String country;
    private final String authorType;   // "Local" or "International"

    public Author(String authorId, String name, String country, String authorType) {
        this.authorId = authorId;
        this.name = name;
        this.country = country;
        this.authorType = authorType;
    }

    public String getAuthorId()   { return authorId; }
    public String getName()       { return name; }
    public String getCountry()    { return country; }
    public String getAuthorType() { return authorType; }

    // overridden by the subclasses (same idea as getRoleDescription() in User)
    public String getTypeDescription() {
        return "General author";
    }

    // one line in authors.txt:  id,name,country,type
    public String toFileLine() {
        return authorId + "," + name + "," + country + "," + authorType;
    }
}
