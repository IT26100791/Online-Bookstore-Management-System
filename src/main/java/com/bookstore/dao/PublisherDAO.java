package com.bookstore.dao;

import com.bookstore.model.Publisher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {
    private final Path filePath;

    public PublisherDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, "publishers.txt");
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare publishers file: " + e.getMessage(), e);
        }
    }

    public synchronized List<Publisher> findAll() throws IOException {
        List<Publisher> publishers = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length >= 3) {
                    publishers.add(new Publisher(d[0].trim(), d[1].trim(), d[2].trim()));
                }
            }
        }
        return publishers;
    }

    public synchronized Publisher findById(String id) throws IOException {
        for (Publisher p : findAll()) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public synchronized boolean add(Publisher publisher) throws IOException {
        if (findById(publisher.getId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(publisher.toFileLine());
            writer.newLine();
        }
        return true;
    }

    public synchronized boolean update(Publisher updated) throws IOException {
        List<Publisher> publishers = findAll();
        boolean found = false;
        for (int i = 0; i < publishers.size(); i++) {
            if (publishers.get(i).getId().equalsIgnoreCase(updated.getId())) {
                publishers.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(publishers);
        return found;
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Publisher> publishers = findAll();
        boolean removed = publishers.removeIf(p -> p.getId().equalsIgnoreCase(id));
        if (removed) writeAll(publishers);
        return removed;
    }

    private void writeAll(List<Publisher> publishers) throws IOException {
        Path temp = filePath.resolveSibling("publishers_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Publisher p : publishers) {
                writer.write(p.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
