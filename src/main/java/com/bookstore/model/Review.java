package com.bookstore.model;

public class Review {
    private final String id;
    private final String bookTitle;
    private final String rating;
    private final String comment;

    public Review(String id, String bookTitle, String rating, String comment) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id; }
    public String getBookTitle() { return bookTitle; }
    public String getRating() { return rating; }
    public String getComment() { return comment; }

    public String toFileLine() {
        return id + "," + bookTitle + "," + rating + "," + comment;
    }
}
