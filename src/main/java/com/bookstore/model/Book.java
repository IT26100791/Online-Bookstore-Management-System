package com.bookstore.model;

import com.bookstore.util.TextCodec;


public abstract class Book {
    private final String id;
    private final String title;
    private final String genre;
    private final String price;
    private final String image;        // saved cover file name, "" when the book has no cover
    private String authorId;           // stored reference ID to an existing author
    private Author author;             // resolved Author object for OOP relationship
    private String legacyAuthorName;   // preserved legacy free-text author name if unlinked
    private final String description;  // short description of the book

    protected Book(String id, String title, String genre, String price,
                   String image, String authorId, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.price = price;
        this.image = image == null ? "" : image;
        this.authorId = authorId == null ? "" : authorId;
        this.description = description == null ? "" : description;
    }

    protected Book(String id, String title, String genre, String price,
                   String image, Author author, String description) {
        this(id, title, genre, price, image, author != null ? author.getId() : "", description);
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPrice() { return price; }
    public String getImage() { return image; }
    public String getDescription() { return description; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId == null ? "" : authorId; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) {
        this.author = author;
        if (author != null) {
            this.authorId = author.getId();
        }
    }

    public String getLegacyAuthorName() { return legacyAuthorName; }
    public void setLegacyAuthorName(String legacyAuthorName) { this.legacyAuthorName = legacyAuthorName; }

    public boolean hasAuthor() { return author != null; }
    public boolean hasLegacyAuthor() { return legacyAuthorName != null && !legacyAuthorName.trim().isEmpty(); }
    public boolean isAuthorUnresolved() { return author == null; }

    /**
     * Resolves the display name for the author:
     * 1. Returns the linked Author's name if resolved.
     * 2. Returns the preserved legacy author name if present.
     * 3. Returns the authorId if present.
     * 4. Returns empty string otherwise.
     */
    public String getAuthorName() {
        if (author != null && author.getName() != null && !author.getName().trim().isEmpty()) {
            return author.getName();
        }
        if (legacyAuthorName != null && !legacyAuthorName.trim().isEmpty()) {
            return legacyAuthorName;
        }
        return authorId != null ? authorId : "";
    }

    public boolean hasImage() { return image != null && !image.isEmpty(); }
    public boolean hasDescription() { return description != null && !description.isEmpty(); }

    /** Code saved in books.txt, e.g. "PRINTED" or "EBOOK". */
    public abstract String getType();

    /** Name shown to the user, e.g. "Printed book". */
    public abstract String getTypeLabel();

    /** What the format detail means, e.g. "Binding" or "File format". */
    public abstract String getFormatLabel();

    /** The format detail itself, e.g. "Hardcover" or "EPUB". */
    public abstract String getFormatValue();

    /** Note shown on the details page; each sub class words it differently. */
    public abstract String getDeliveryNote();

    /**
     * One line of books.txt:
     * id,title,genre,price,image,authorId,description,type,format
     * (authorId/legacyAuthor, description and format are encoded so they can hold commas / new lines)
     */
    public String toFileLine() {
        String authorRef = (authorId != null && !authorId.isEmpty())
                ? authorId
                : (legacyAuthorName != null ? legacyAuthorName : "");
        return String.join(",",
                id, title, genre, price, image,
                TextCodec.encode(authorRef),
                TextCodec.encode(description),
                getType(),
                TextCodec.encode(getFormatValue()));
    }
}
