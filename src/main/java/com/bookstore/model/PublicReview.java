package com.bookstore.model;

/** A normal review that anyone can post. */
public class PublicReview extends Review {

    public PublicReview(String reviewId, String bookTitle, int rating, String reviewer, String comment) {
        super(reviewId, bookTitle, rating, reviewer, comment);
    }

    @Override
    public String getType() {
        return "PUBLIC";
    }

    @Override
    public String getBadge() {
        return "Public";
    }

    @Override
    public String getDisplayText(boolean adminView) {
        String text = getReviewer() + " says: " + getComment();
        return adminView ? "[" + getReviewId() + " | public] " + text : text;
    }
}
