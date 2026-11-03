package com.bookstore.model;

public class InternationalAuthor extends Author {

    public InternationalAuthor(String authorId, String name, String email) {
        super(authorId, name, email, "International");
    }

    @Override
    public String getTypeDescription() {
        return "International author (based or published abroad)";
    }
}
