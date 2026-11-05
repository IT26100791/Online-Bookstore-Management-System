package com.bookstore.dao;

import com.bookstore.model.LocalPublisher;
import com.bookstore.model.Publisher;
import com.bookstore.model.PublisherFactory;
import com.bookstore.util.DataPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based data access for publishers (publishers.txt).
 *
 * <p>Line format: {@code id,name,phone,TYPE,extra}. Older three-column lines
 * ({@code id,name,phone}) are still read and are treated as local publishers
 * until they are edited and saved.</p>
 *
 * <p>The file format is hidden inside this class: callers only see {@link Publisher} objects.</p>
 */
public class PublisherDAO {

    private static final String FILE_NAME = "publishers.txt";
    private static final String TEMP_FILE_NAME = "publishers_temp.txt";
    private static final String SEPARATOR = ",";

    private static final int LEGACY_FIELD_COUNT = 3;
    private static final int FIELD_COUNT = 5;
    private static final String LEGACY_DISTRICT = "Not specified";

    private final Path filePath;

    /** Uses the shared data directory configured in {@link DataPaths}. */
    public PublisherDAO() {
        this(DataPaths.dataDir());
    }

    public PublisherDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, FILE_NAME);
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare publishers file: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------- read

    public synchronized List<Publisher> findAll() throws IOException {
        List<Publisher> publishers = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Publisher publisher = parseLine(line);
                if (publisher != null) {
                    publishers.add(publisher);
                }
            }
        }
        return publishers;
    }

    public synchronized Publisher findById(String id) throws IOException {
        if (id == null) {
            return null;
        }
        for (Publisher p : findAll()) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Filters publishers by keyword and type.
     *
     * @param keyword matched against ID, name, phone and district/country; blank matches all
     * @param type    {@code LOCAL} or {@code INTERNATIONAL}; blank matches all
     */
    public synchronized List<Publisher> search(String keyword, String type) throws IOException {
        boolean anyType = type == null || type.trim().isEmpty();
        List<Publisher> results = new ArrayList<>();
        for (Publisher p : findAll()) {
            boolean typeMatches = anyType || p.getType().equalsIgnoreCase(type.trim());
            if (typeMatches && p.matches(keyword)) {
                results.add(p);
            }
        }
        return results;
    }

    // ---------------------------------------------------------------- write

    /** @return false when a publisher with the same ID already exists */
    public synchronized boolean add(Publisher publisher) throws IOException {
        if (findById(publisher.getId()) != null) {
            return false;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(publisher.toFileLine());
            writer.newLine();
        }
        return true;
    }

    /** @return false when no publisher with that ID exists */
    public synchronized boolean update(Publisher updated) throws IOException {
        List<Publisher> publishers = findAll();
        for (int i = 0; i < publishers.size(); i++) {
            if (publishers.get(i).equals(updated)) {
                publishers.set(i, updated);
                writeAll(publishers);
                return true;
            }
        }
        return false;
    }

    /** @return false when no publisher with that ID exists */
    public synchronized boolean delete(String id) throws IOException {
        List<Publisher> publishers = findAll();
        boolean removed = publishers.removeIf(p -> p.getId().equalsIgnoreCase(id == null ? "" : id.trim()));
        if (removed) {
            writeAll(publishers);
        }
        return removed;
    }

    // ---------------------------------------------------------------- helpers

    /** Turns one file line into the matching subclass, or null when the line is unusable. */
    private Publisher parseLine(String line) {
        if (line.trim().isEmpty()) {
            return null;
        }
        String[] f = line.split(SEPARATOR, -1);
        try {
            if (f.length >= FIELD_COUNT) {
                return PublisherFactory.create(f[3].trim(), f[0].trim(), f[1].trim(), f[2].trim(), f[4].trim());
            }
            if (f.length == LEGACY_FIELD_COUNT) {
                return new LocalPublisher(f[0].trim(), f[1].trim(), f[2].trim(), LEGACY_DISTRICT);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Skipping invalid publisher line: " + line + " (" + e.getMessage() + ")");
        }
        return null;
    }

    /** Writes to a temp file first, then swaps it in, so a crash cannot leave a half-written file. */
    private void writeAll(List<Publisher> publishers) throws IOException {
        Path temp = filePath.resolveSibling(TEMP_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Publisher p : publishers) {
                writer.write(p.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}