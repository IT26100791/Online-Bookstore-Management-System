package com.bookstore.dao;

import com.bookstore.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Path filePath;

    // 1. Default constructor එකක් එක් කරන ලදී (Tomcat එකෙන් පහසුවෙන් call කිරීමට)
    public UserDAO() {
        // Default ලෙස user.home හෝ java.io.tmpdir යොදාගත හැක,
        // නැතහොත් Servlet එකෙන් getRealPath ලබා දී පහත overloaded constructor එක පාවිච්චි කළ හැක.
        String defaultDir = System.getProperty("user.home");
        this.filePath = Paths.get(defaultDir, "users.txt");
        initFile();
    }

    // 2. Servlet එකෙන් getRealPath ලබාදී call කිරීමට ඇති Constructor එක
    public UserDAO(String dataDir) {
        if (dataDir == null || dataDir.trim().isEmpty()) {
            dataDir = System.getProperty("user.home");
        }
        this.filePath = Paths.get(dataDir, "users.txt");
        initFile();
    }

    // File එක සහ Directories සකසාගැනීමට පොදු method එකක්
    private void initFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare users file: " + e.getMessage(), e);
        }
    }

    // Factory: returns the right subclass
    private User createUser(String username, String password, String role) {
        if ("Admin".equalsIgnoreCase(role)) return new Admin(username, password);
        if ("Customer".equalsIgnoreCase(role)) return new Customer(username, password);
        return new User(username, password, role);
    }

    // READ all
    public synchronized List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return users;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                if (d.length >= 3) {
                    users.add(createUser(d[0], d[1], d[2]));
                }
            }
        }
        return users;
    }

    // READ one
    public synchronized User findByUsername(String username) throws IOException {
        for (User u : findAll()) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    // LOGIN check: returns the user if username + password match, else null
    public synchronized User authenticate(String username, String password) throws IOException {
        User u = findByUsername(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    // CREATE (false if username already exists)
    public synchronized boolean add(User user) throws IOException {
        if (findByUsername(user.getUsername()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(user.toFileLine());
            writer.newLine();
        }
        return true;
    }

    // UPDATE
    public synchronized boolean update(String username, String newPassword, String newRole) throws IOException {
        List<User> users = findAll();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(username)) {
                users.set(i, createUser(users.get(i).getUsername(), newPassword, newRole));
                found = true;
            }
        }
        if (found) writeAll(users);
        return found;
    }

    // DELETE
    public synchronized boolean delete(String username) throws IOException {
        List<User> users = findAll();
        boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) writeAll(users);
        return removed;
    }

    // write to temp file first, then replace (safe against data loss)
    private void writeAll(List<User> users) throws IOException {
        Path temp = filePath.resolveSibling("users_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (User u : users) {
                writer.write(u.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}