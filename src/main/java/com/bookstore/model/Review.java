package com.bookstore.model;

/**
 * Base class for every review.
 *
 * Encapsulation : all fields are private; they are changed only through setters that validate the data.
 * Abstraction   : getType(), getBadge() and getDisplayText() are abstract, each review type defines them.
 * Polymorphism  : the pages call getDisplayText(adminView) on a Review and the correct subclass answers.
 */
public abstract class Review {

    private final String reviewId;
    private String bookTitle;
    private int rating;
    private String reviewer;
    private String comment;

    protected Review(String reviewId, String bookTitle, int rating, String reviewer, String comment) {
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID is required.");
        }
        this.reviewId = reviewId.trim();
        setBookTitle(bookTitle);
        setRating(rating);
        setReviewer(reviewer);
        setComment(comment);
    }

    // ---------- getters ----------
    public String getReviewId() { return reviewId; }
    public String getBookTitle() { return bookTitle; }
    public int getRating() { return rating; }
    public String getReviewer() { return reviewer; }
    public String getComment() { return comment; }

    // ---------- setters with validation ----------
    public final void setBookTitle(String bookTitle) {
        String clean = bookTitle == null ? "" : bookTitle.replaceAll("[,\\s]+", " ").trim();
        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.bookTitle = clean;
    }

    public final void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public final void setReviewer(String reviewer) {
        String clean = reviewer == null ? "" : reviewer.replaceAll("[,\\s]+", " ").trim();
        this.reviewer = clean.isEmpty() ? "guest" : clean;
    }

    public final void setComment(String comment) {
        String clean = comment == null ? "" : comment.replaceAll("[\\r\\n]+", " ").trim();
        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        this.comment = clean;
    }

    // ---------- behaviour shared by all reviews ----------
    /** Five-star string, e.g. 4 -> ★★★★☆ */
    public String getStars() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? '\u2605' : '\u2606');
        }
        return sb.toString();
    }

    /** One line of reviews.txt: id,book,rating,type,reviewer,comment (comment is last so it may contain commas). */
    public String toFileString() {
        return reviewId + "," + bookTitle + "," + rating + "," + getType() + "," + reviewer + "," + comment;
    }

    // ---------- behaviour each review type must provide ----------
    public abstract String getType();

    public abstract String getBadge();

    /** Text shown in the review list. Admins see extra moderation details. */
    public abstract String getDisplayText(boolean adminView);
}