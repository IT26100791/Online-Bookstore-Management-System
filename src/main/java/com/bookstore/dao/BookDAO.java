package com.bookstore.dao;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.BookFactory;
import com.bookstore.util.DataPaths;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BookDAO {
    private final Path filePath;
    private final AuthorDAO authorDao;

    // Default constructor using DataPaths and default AuthorDAO
    public BookDAO() {
        this(DataPaths.dataDir());
    }

    public BookDAO(String dataDir) {
        this(dataDir, new AuthorDAO(dataDir));
    }

    public BookDAO(String dataDir, AuthorDAO authorDao) {
        this.filePath = Paths.get(dataDir, "books.txt");
        this.authorDao = authorDao != null ? authorDao : new AuthorDAO(dataDir);
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare books file: " + e.getMessage(), e);
        }
    }

    public AuthorDAO getAuthorDao() {
        return authorDao;
    }

    public synchronized List<Book> findAll() throws IOException {
        List<Author> authors = loadAuthorsSafely();
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Book book = BookFactory.fromFileLine(line);
                if (book != null) {
                    resolveAuthor(book, authors);
                    books.add(book);
                }
            }
        }
        return books;
    }

    public synchronized Book findById(String id) throws IOException {
        if (id == null) return null;
        for (Book b : findAll()) {
            if (b.getId().equalsIgnoreCase(id.trim())) return b;
        }
        return null;
    }

    /** Returns all books linked to a specific author ID (one-to-many relationship). */
    public synchronized List<Book> findByAuthorId(String authorId) throws IOException {
        if (authorId == null || authorId.trim().isEmpty()) return Collections.emptyList();
        String aid = authorId.trim();
        List<Book> result = new ArrayList<>();
        for (Book b : findAll()) {
            if (aid.equalsIgnoreCase(b.getAuthorId())) {
                result.add(b);
            }
        }
        return result;
    }

    /** Case-insensitive search in title, author name, author id, genre and book id. Empty text returns every book. */
    public synchronized List<Book> search(String text) throws IOException {
        if (text == null || text.trim().isEmpty()) return findAll();
        String q = text.trim().toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book b : findAll()) {
            String authorName = b.getAuthorName().toLowerCase(Locale.ROOT);
            String authorId = b.getAuthorId() != null ? b.getAuthorId().toLowerCase(Locale.ROOT) : "";
            if (b.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || authorName.contains(q)
                    || authorId.contains(q)
                    || b.getGenre().toLowerCase(Locale.ROOT).contains(q)
                    || b.getId().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(b);
            }
        }
        return result;
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

    /**
     * Safely migrates legacy records in books.txt:
     * If an existing record contains an author name that matches exactly one registered author,
     * it updates the stored record with the resolved Author ID.
     * Records with uncertain or non-matching names are preserved unchanged.
     * Returns the count of migrated records.
     */
    public synchronized int migrateLegacyAuthorRecords() throws IOException {
        List<Author> authors = loadAuthorsSafely();
        if (authors.isEmpty()) return 0;

        List<String> rawLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        boolean anyChanged = false;
        int migratedCount = 0;
        List<Book> updatedList = new ArrayList<>();

        for (String line : rawLines) {
            if (line.trim().isEmpty()) continue;
            Book b = BookFactory.fromFileLine(line);
            if (b == null) continue;
            String rawAuthor = b.getAuthorId(); // raw token from file line
            resolveAuthor(b, authors);
            if (b.getAuthor() != null && !b.getAuthorId().equalsIgnoreCase(rawAuthor)) {
                migratedCount++;
                anyChanged = true;
            }
            updatedList.add(b);
        }

        if (anyChanged) {
            writeAll(updatedList);
        }
        return migratedCount;
    }

    private List<Author> loadAuthorsSafely() {
        try {
            return authorDao != null ? authorDao.findAll() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Resolves the linked Author object for a book.
     * 1. Matches author ID case-insensitively.
     * 2. If not matched by ID, checks if raw value matches an author name unambiguously (exactly 1 match).
     * 3. If no safe match is found, preserves the raw string as legacyAuthorName without assigning an uncertain author.
     */
    private void resolveAuthor(Book book, List<Author> authors) {
        String raw = book.getAuthorId();
        if (raw == null || raw.trim().isEmpty()) {
            book.setAuthor(null);
            book.setAuthorId("");
            book.setLegacyAuthorName("");
            return;
        }
        raw = raw.trim();

        // 1. Try matching by Author ID
        for (Author a : authors) {
            if (a.getId() != null && a.getId().equalsIgnoreCase(raw)) {
                book.setAuthor(a);
                book.setAuthorId(a.getId());
                book.setLegacyAuthorName(null);
                return;
            }
        }

        // 2. Try matching by Author Name (case-insensitive exact match)
        List<Author> nameMatches = new ArrayList<>();
        for (Author a : authors) {
            if (a.getName() != null && a.getName().trim().equalsIgnoreCase(raw)) {
                nameMatches.add(a);
            }
        }

        if (nameMatches.size() == 1) {
            Author matched = nameMatches.get(0);
            book.setAuthor(matched);
            book.setAuthorId(matched.getId());
            book.setLegacyAuthorName(null);
            return;
        }

        // 3. Not safely matchable - preserve legacy name, do NOT assign an uncertain author
        book.setAuthor(null);
        book.setAuthorId("");
        book.setLegacyAuthorName(raw);
    }
}
