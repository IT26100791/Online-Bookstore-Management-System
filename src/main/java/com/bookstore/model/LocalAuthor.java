package com.bookstore.model;

public class LocalAuthor extends Author {

    public LocalAuthor(String authorId, String name, String country) {
        super(authorId, name, country, "Local");
    }

    @Override
    public String getTypeDescription() {
        return "Local author (writes and publishes in the home country)";
    }
}
