package com.bookstore.dao;

import com.bookstore.model.PublicReview;
import com.bookstore.model.Review;
import com.bookstore.model.VerifiedReview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes reviews.txt. One review per line:
 *   id,book,rating,type,reviewer,comment
 *
 * Create = add(), Read = getAll()/findById()/searchByBook(), Update = update(), Delete = delete()
 */
public class ReviewDAO {

    // data/reviews.txt inside the folder Tomcat runs from. The server prints the full path at start-up.
    private static final Path FILE = Paths.get(System.getProperty("user.dir"), "data", "reviews.txt");
    private static final Object LOCK = new Object();
    private static boolean pathLogged = false;

    public ReviewDAO() {
        synchronized (LOCK) {
            try {
                Files.createDirectories(FILE.getParent());
                if (!Files.exists(FILE)) {
                    Files.createFile(FILE);
                }
                if (!pathLogged) {
                    System.out.println("[ReviewDAO] Using data file: " + FILE.toAbsolutePath());
                    pathLogged = true;
                }
            } catch (IOException e) {
                System.err.println("[ReviewDAO] Cannot prepare data file: " + e.getMessage());
            }
        }
    }

    // ---------------- READ ----------------
    public List<Review> getAll() {
        synchronized (LOCK) {
            List<Review> reviews = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    Review review = parse(line);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
            } catch (IOException e) {
                System.err.println("[ReviewDAO] Error reading reviews: " + e.getMessage());
            }
            return reviews;
        }
    }

    public Review findById(String id) {
        if (id == null) {
            return null;
        }
        for (Review review : getAll()) {
            if (review.getReviewId().equalsIgnoreCase(id.trim())) {
                return review;
            }
        }
        return null;
    }

    public List<Review> searchByBook(String keyword) {
        List<Review> matches = new ArrayList<>();
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        for (Review review : getAll()) {
            if (review.getBookTitle().toLowerCase().contains(key)) {
                matches.add(review);
            }
        }
        return matches;
    }

    // ---------------- CREATE ----------------
    public boolean add(Review review) {
        synchronized (LOCK) {
            try (BufferedWriter writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(review.toFileString());
                writer.newLine();
                return true;
            } catch (IOException e) {
                System.err.println("[ReviewDAO] Error saving review: " + e.getMessage());
                return false;
            }
        }
    }

    // ---------------- UPDATE ----------------
    public boolean update(Review updated) {
        synchronized (LOCK) {
            List<Review> reviews = getAll();
            boolean found = false;
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getReviewId().equalsIgnoreCase(updated.getReviewId())) {
                    reviews.set(i, updated);
                    found = true;
                    break;
                }
            }
            return found && writeAll(reviews);
        }
    }

    // ---------------- DELETE ----------------
    public boolean delete(String id) {
        synchronized (LOCK) {
            List<Review> reviews = getAll();
            boolean removed = reviews.removeIf(r -> r.getReviewId().equalsIgnoreCase(id));
            return removed && writeAll(reviews);
        }
    }

    // ---------------- helpers ----------------
    /** Next free id: R001, R002, ... */
    public String generateId() {
        synchronized (LOCK) {
            int max = 0;
            for (Review review : getAll()) {
                String id = review.getReviewId();
                if (id.length() > 1 && (id.charAt(0) == 'R' || id.charAt(0) == 'r')) {
                    try {
                        max = Math.max(max, Integer.parseInt(id.substring(1)));
                    } catch (NumberFormatException ignored) {
                        // not one of our generated ids
                    }
                }
            }
            return String.format("R%03d", max + 1);
        }
    }

    private boolean writeAll(List<Review> reviews) {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            for (Review review : reviews) {
                writer.write(review.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("[ReviewDAO] Error writing reviews: " + e.getMessage());
            return false;
        }
    }

    /** Turns one file line into the right Review subclass. Bad lines return null and are skipped. */
    private Review parse(String line) {
        String[] p = line.split(",", 6);
        if (p.length < 6) {
            return null;
        }
        try {
            int rating = Integer.parseInt(p[2].trim());
            if ("VERIFIED".equalsIgnoreCase(p[3].trim())) {
                return new VerifiedReview(p[0], p[1], rating, p[4], p[5]);
            }
            return new PublicReview(p[0], p[1], rating, p[4], p[5]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

