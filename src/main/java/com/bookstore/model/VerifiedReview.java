package com.bookstore.model;

/** A review written by a reader who bought the book. */
public class VerifiedReview extends Review {

    public VerifiedReview(String reviewId, String bookTitle, int rating, String reviewer, String comment) {
        super(reviewId, bookTitle, rating, reviewer, comment);
    }

    @Override
    public String getType() {
        return "VERIFIED";
    }

    @Override
    public String getBadge() {
        return "Verified purchase";
    }

    @Override
    public String getDisplayText(boolean adminView) {
        String text = "Verified buyer " + getReviewer() + " says: " + getComment();
        return adminView ? "[" + getReviewId() + " | verified] " + text : text;
    }
}

