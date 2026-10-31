package com.bookstore.dao;

import com.bookstore.model.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private final Path filePath;

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

    public synchronized List<Review> findAll() throws IOException {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", 4);
                if (d.length >= 4) {
                    reviews.add(new Review(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim()));
                }
            }
        }
        return reviews;
    }

    public synchronized Review findById(String id) throws IOException {
        for (Review r : findAll()) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public synchronized boolean add(Review review) throws IOException {
        if (findById(review.getId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(review.toFileLine());
            writer.newLine();
        }
        return true;
    }

    public synchronized boolean update(Review updated) throws IOException {
        List<Review> reviews = findAll();
        boolean found = false;
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equalsIgnoreCase(updated.getId())) {
                reviews.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(reviews);
        return found;
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Review> reviews = findAll();
        boolean removed = reviews.removeIf(r -> r.getId().equalsIgnoreCase(id));
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
