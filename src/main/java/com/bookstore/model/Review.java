package com.bookstore.model;

/** Base class for all reviews (Inheritance + Polymorphism: Verified / Public). */
public abstract class Review {
    private final String reviewId;
    private String bookTitle;
    private int rating;
    private final String reviewer;
    private String comment;

    protected Review(String reviewId, String bookTitle, int rating, String reviewer, String comment) {
        this.reviewId = reviewId;
        this.reviewer = reviewer;
        setBookTitle(bookTitle);
        setRating(rating);
        setComment(comment);
    }

    public String getReviewId() { return reviewId; }
    public String getBookTitle() { return bookTitle; }
    public int getRating() { return rating; }
    public String getReviewer() { return reviewer; }
    public String getComment() { return comment; }

    // Setters validate the values (Encapsulation)
    public void setBookTitle(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.bookTitle = bookTitle.trim().replace(",", " ");
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Choose a rating from 1 to 5.");
        }
        this.rating = rating;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        this.comment = comment.trim().replace("\n", " ").replace("\r", " ");
    }

    // Polymorphism: each subclass overrides these
    public abstract String getType();
    public abstract String getBadge();
    public abstract String getDisplayText(boolean adminView);

    /** type,id,title,rating,reviewer,comment */
    public String toFileLine() {
        return getType() + "," + reviewId + "," + bookTitle + "," + rating + "," + reviewer + "," + comment;
    }
}