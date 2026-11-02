package com.bookstore.model;

public class InternationalAuthor extends Author {

    public InternationalAuthor(String authorId, String name, String country) {
        super(authorId, name, country, "International");
    }

    @Override
    public String getTypeDescription() {
        return "International author (based or published abroad)";
    }
}
