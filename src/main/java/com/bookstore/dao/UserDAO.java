package com.bookstore.dao;

import com.bookstore.model.*;
import com.bookstore.util.DataPaths;
import com.bookstore.util.PasswordUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Path filePath;

    // Default: -Dbookstore.data=<path> if given, otherwise <working dir>/data
    public UserDAO() {
        this(DataPaths.dataDir());
    }

    public UserDAO(String dataDir) {
        if (dataDir == null || dataDir.trim().isEmpty()) {
            dataDir = DataPaths.dataDir();
        }
        this.filePath = Paths.get(dataDir, "users.txt").toAbsolutePath();
        initFile();
        System.out.println("USERS FILE: " + filePath);
    }

    private void initFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            // first run: create a default admin so someone can log in
            if (Files.size(filePath) == 0) {
                String line = "admin," + PasswordUtil.hash("admin123") + ",Admin" + System.lineSeparator();
                Files.write(filePath, line.getBytes(StandardCharsets.UTF_8));
                System.out.println("Default admin created -> username: admin / password: admin123");
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
                    users.add(createUser(d[0].trim(), d[1].trim(), d[2].trim()));
                }
            }
        }
        return users;
    }

    // READ one
    public synchronized User findByUsername(String username) throws IOException {
        if (username == null) return null;
        for (User u : findAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) return u;
        }
        return null;
    }

    // LOGIN check (supports hashed and old plain-text passwords)
    public synchronized User authenticate(String username, String password) throws IOException {
        User u = findByUsername(username);
        if (u != null && PasswordUtil.matches(password, u.getPassword())) {
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

    // UPDATE (newPassword == null -> keep the current password)
    public synchronized boolean update(String username, String newPassword, String newRole) throws IOException {
        List<User> users = findAll();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            User old = users.get(i);
            if (old.getUsername().equalsIgnoreCase(username)) {
                String pw = (newPassword != null) ? newPassword : old.getPassword();
                users.set(i, createUser(old.getUsername(), pw, newRole));
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