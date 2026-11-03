package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.util.DataPaths;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Path filePath;

    // Default constructor using DataPaths
    public BookDAO() {
        this(DataPaths.dataDir());
    }

    public BookDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, "books.txt");
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare books file: " + e.getMessage(), e);
        }
    }

    public synchronized List<Book> findAll() throws IOException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 4) {
                    books.add(new Book(d[0], d[1], d[2], d[3]));
                } else if (d.length == 5) {
                    // 5th field = saved cover image file name
                    books.add(new Book(d[0], d[1], d[2], d[3], d[4]));
                }
            }
        }
        return books;
    }

    public synchronized Book findById(String id) throws IOException {
        for (Book b : findAll()) {
            if (b.getId().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    public synchronized boolean add(Book book) throws IOException {
        if (findById(book.getId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(book.toFileLine());
            writer.newLine();
        }
        return true;
    }

    public synchronized boolean update(Book updated) throws IOException {
        List<Book> books = findAll();
        boolean found = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equalsIgnoreCase(updated.getId())) {
                books.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(books);
        return found;
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Book> books = findAll();
        boolean removed = books.removeIf(b -> b.getId().equalsIgnoreCase(id));
        if (removed) writeAll(books);
        return removed;
    }

    private void writeAll(List<Book> books) throws IOException {
        Path temp = filePath.resolveSibling("books_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Book b : books) {
                writer.write(b.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
