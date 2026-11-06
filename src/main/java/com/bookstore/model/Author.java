package com.bookstore.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Author {
    private String authorId;
    private String name;
    private String email;        // optional - may be empty
    private String authorType;   // "Local" or "International"
    private String photo = "";   // saved photo file name, empty when there is no photo
    private String bio = "";     // optional description about the author

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
        return email == null ? "" : email;
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

    public String getPhoto() {
        return photo == null ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo == null ? "" : photo;
    }

    public boolean hasPhoto() {
        return !getPhoto().isEmpty();
    }

    public String getBio() {
        return bio == null ? "" : bio;
    }

    public void setBio(String bio) {
        this.bio = bio == null ? "" : bio;
    }

    // overridden by LocalAuthor and InternationalAuthor
    public String getTypeDescription() {
        return "Standard author";
    }

    // one line in authors.txt:  id,name,email,type[,photo,bio]
    // The bio can contain commas and new lines, so it is stored URL-encoded.
    // An author without photo and bio is still written in the old 4-field format.
    public String toFileLine() {
        String line = authorId + "," + name + "," + getEmail() + "," + authorType;
        if (getPhoto().isEmpty() && getBio().isEmpty()) return line;
        return line + "," + getPhoto() + "," + encode(getBio());
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}