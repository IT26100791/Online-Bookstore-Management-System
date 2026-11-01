package com.bookstore.dao;

import com.bookstore.model.PublicReview;
import com.bookstore.model.Review;
import com.bookstore.model.VerifiedReview;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private final Path filePath;

    // Same default folder as the User module
    public ReviewDAO() {
        this(System.getProperty("user.home") + "/bookstore-data");
    }

    public ReviewDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, "reviews.txt");
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare reviews file: " + e.getMessage(), e);
        }
    }

    // Builds the right subclass from one line of the file
    private Review parseLine(String line) {
        try {
            String[] d = line.split(",", 6);
            if (d.length == 6) {                       // new format
                String type = d[0].trim();
                String id = d[1].trim(), title = d[2].trim();
                int rating = Integer.parseInt(d[3].trim());
                String reviewer = d[4].trim(), comment = d[5].trim();
                if ("VERIFIED".equalsIgnoreCase(type)) {
                    return new VerifiedReview(id, title, rating, reviewer, comment);
                }
                return new PublicReview(id, title, rating, reviewer, comment);
            }
            d = line.split(",", 4);
            if (d.length == 4) {                       // old format: id,title,rating,comment
                return new PublicReview(d[0].trim(), d[1].trim(),
                        Integer.parseInt(d[2].trim()), "unknown", d[3].trim());
            }
        } catch (IllegalArgumentException e) {
            // bad line: skip it
        }
        return null;
    }

    // READ all
    public synchronized List<Review> getAll() throws IOException {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Review r = parseLine(line);
                if (r != null) reviews.add(r);
            }
        }
        return reviews;
    }

    // READ: search by book title
    public synchronized List<Review> searchByBook(String text) throws IOException {
        List<Review> result = new ArrayList<>();
        String key = text.trim().toLowerCase();
        for (Review r : getAll()) {
            if (r.getBookTitle().toLowerCase().contains(key)) result.add(r);
        }
        return result;
    }

    public synchronized Review findById(String id) throws IOException {
        for (Review r : getAll()) {
            if (r.getReviewId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    // CREATE (false if id already exists)
    public synchronized boolean add(Review review) throws IOException {
        if (findById(review.getReviewId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(review.toFileLine());
            writer.newLine();
        }
        return true;
    }

    // UPDATE
    public synchronized boolean update(Review updated) throws IOException {
        List<Review> reviews = getAll();
        boolean found = false;
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getReviewId().equalsIgnoreCase(updated.getReviewId())) {
                reviews.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(reviews);
        return found;
    }

    // DELETE
    public synchronized boolean delete(String id) throws IOException {
        List<Review> reviews = getAll();
        boolean removed = reviews.removeIf(r -> r.getReviewId().equalsIgnoreCase(id));
        if (removed) writeAll(reviews);
        return removed;
    }

    private void writeAll(List<Review> reviews) throws IOException {
        Path temp = filePath.resolveSibling("reviews_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Review r : reviews) {
                writer.write(r.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}