package com.bookstore.model;

public class Author {
    private String authorId;
    private String name;
    private String email;
    private String authorType;   // "Local" or "International"

    public Author(String authorId, String name, String email, String authorType) {
        this.authorId = authorId;
        this.name = name;
        this.email = email;
        this.authorType = authorType;
    }

    public Author(String authorId, String name, String email) {
        this(authorId, name, email, "Local");
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    // overridden by LocalAuthor and InternationalAuthor
    public String getTypeDescription() {
        return "Standard author";
    }

    // one line in authors.txt:  id,name,email,type
    public String toFileLine() {
        return authorId + "," + name + "," + email + "," + authorType;
    }
}
