package com.bookstore.dao;

import com.bookstore.model.Author;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    private final Path filePath;

    public AuthorDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, "authors.txt");
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare authors file: " + e.getMessage(), e);
        }
    }

    // READ all
    public synchronized List<Author> findAll() throws IOException {
        List<Author> authors = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length == 3) {
                    authors.add(new Author(d[0], d[1], d[2]));
                }
            }
        }
        return authors;
    }

    // READ one
    public synchronized Author findById(String id) throws IOException {
        for (Author a : findAll()) {
            if (a.getAuthorId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }

    // CREATE (false if the ID already exists)
    public synchronized boolean add(Author author) throws IOException {
        if (findById(author.getAuthorId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(author.toFileLine());
            writer.newLine();
        }
        return true;
    }

    // UPDATE
    public synchronized boolean update(String id, String newName, String newCountry) throws IOException {
        List<Author> authors = findAll();
        boolean found = false;
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).getAuthorId().equalsIgnoreCase(id)) {
                authors.set(i, new Author(authors.get(i).getAuthorId(), newName, newCountry));
                found = true;
            }
        }
        if (found) writeAll(authors);
        return found;
    }

    // DELETE
    public synchronized boolean delete(String id) throws IOException {
        List<Author> authors = findAll();
        boolean removed = authors.removeIf(a -> a.getAuthorId().equalsIgnoreCase(id));
        if (removed) writeAll(authors);
        return removed;
    }

    // write to a temp file first, then replace (safe against data loss)
    private void writeAll(List<Author> authors) throws IOException {
        Path temp = filePath.resolveSibling("authors_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Author a : authors) {
                writer.write(a.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
