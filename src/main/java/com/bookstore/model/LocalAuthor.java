package com.bookstore.model;

public class LocalAuthor extends Author {

    public LocalAuthor(String authorId, String name, String email) {
        super(authorId, name, email, "Local");
    }

    @Override
    public String getTypeDescription() {
        return "Local author (writes and publishes in the home country)";
    }
}
