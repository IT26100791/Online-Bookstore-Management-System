package com.bookstore.dao;

import com.bookstore.model.Author;
import com.bookstore.model.InternationalAuthor;
import com.bookstore.model.LocalAuthor;
import com.bookstore.util.DataPaths;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    private final Path filePath;

    public AuthorDAO() {
        this(DataPaths.dataDir());
    }

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

    // Factory: returns the right subclass (same idea as createUser in UserDAO)
    public Author createAuthor(String id, String name, String email, String type) {
        if ("International".equalsIgnoreCase(type)) return new InternationalAuthor(id, name, email);
        return new LocalAuthor(id, name, email);
    }

    // same factory, plus the optional photo file name and description
    public Author createAuthor(String id, String name, String email, String type, String photo, String bio) {
        Author a = createAuthor(id, name, email, type);
        a.setPhoto(photo);
        a.setBio(bio);
        return a;
    }

    public synchronized List<Author> findAll() throws IOException {
        List<Author> authors = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length >= 3) {
                    // old 3-column lines (before types existed) are treated as Local
                    String type = d.length >= 4 ? d[3].trim() : "Local";
                    Author a = createAuthor(d[0].trim(), d[1].trim(), d[2].trim(), type);
                    if (d.length >= 5) a.setPhoto(d[4].trim());
                    if (d.length >= 6) a.setBio(decode(d[5]));
                    authors.add(a);
                }
            }
        }
        return authors;
    }

    public synchronized Author findById(String id) throws IOException {
        for (Author a : findAll()) {
            if (a.getId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }

    public synchronized boolean add(Author author) throws IOException {
        if (findById(author.getId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(author.toFileLine());
            writer.newLine();
        }
        return true;
    }

    public synchronized boolean update(Author updated) throws IOException {
        List<Author> authors = findAll();
        boolean found = false;
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).getId().equalsIgnoreCase(updated.getId())) {
                authors.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(authors);
        return found;
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Author> authors = findAll();
        boolean removed = authors.removeIf(a -> a.getId().equalsIgnoreCase(id));
        if (removed) writeAll(authors);
        return removed;
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            return s;
        }
    }

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