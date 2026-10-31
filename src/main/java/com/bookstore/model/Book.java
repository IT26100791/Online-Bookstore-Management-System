package com.bookstore.model;

public class Book {
    private final String id;
    private final String title;
    private final String genre;
    private final String price;

    public Book(String id, String title, String genre, String price) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.price = price;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPrice() { return price; }

    public String toFileLine() {
        return id + "," + title + "," + genre + "," + price;
    }
}
