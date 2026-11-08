package com.bookstore.dao;

import com.bookstore.model.Admin;
import com.bookstore.model.Customer;
import com.bookstore.model.User;
import com.bookstore.util.DataPaths;
import com.bookstore.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * File based DAO.
 *  - users.txt   : username,passwordHash,role,email,avatarFile,securityQuestion,securityAnswerHash
 *                  (old files with only 3 columns still work)
 *  - avatars/    : profile photos (random file names)
 */
public class UserDAO {
    public static final long MAX_AVATAR_BYTES = 2L * 1024 * 1024;   // 2 MB
    private static final Pattern SAFE_AVATAR = Pattern.compile("^[0-9a-f]{32}\\.(jpg|png|gif|webp)$");
    private static final Object LOCK = new Object();                // shared by every UserDAO object

    private final Path filePath;
    private final Path avatarDir;

    // Default: -Dbookstore.data=<path> if given, otherwise <working dir>/data
    public UserDAO() {
        this(DataPaths.dataDir());
    }

    public UserDAO(String dataDir) {
        if (dataDir == null || dataDir.trim().isEmpty()) {
            dataDir = DataPaths.dataDir();
        }
        this.filePath = Paths.get(dataDir, "users.txt").toAbsolutePath();
        this.avatarDir = Paths.get(dataDir, "avatars").toAbsolutePath();
        initFile();
        System.out.println("USERS FILE: " + filePath);
    }

    private void initFile() {
        synchronized (LOCK) {
            try {
                if (filePath.getParent() != null) {
                    Files.createDirectories(filePath.getParent());
                }
                Files.createDirectories(avatarDir);
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                // first run: create a default admin so someone can log in
                if (Files.size(filePath) == 0) {
                    User admin = new Admin("admin", PasswordUtil.hash("admin123"));
                    Files.write(filePath, (admin.toFileLine() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                    System.out.println("Default admin created -> username: admin / password: admin123");
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot prepare users file: " + e.getMessage(), e);
            }
        }
    }

    // Factory: returns the right subclass (polymorphism)
    public static User createUser(String username, String passwordHash, String role) {
        if ("Admin".equalsIgnoreCase(role)) return new Admin(username, passwordHash);
        return new Customer(username, passwordHash);
    }

    /** Same person with another role (keeps password, e-mail, photo, security question). */
    public static User withRole(User old, String newRole) {
        User u = createUser(old.getUsername(), old.getPasswordHash(), newRole);
        u.copyProfileFrom(old);
        return u;
    }

    // one line of users.txt -> User (null for a bad line)
    private User parse(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] d = line.split(",", -1);
        if (d.length < 3 || d[0].trim().isEmpty()) return null;
        User u = createUser(d[0].trim(), d[1].trim(), d[2].trim());
        if (d.length > 3) u.setEmail(d[3]);
        if (d.length > 4) u.setAvatarFile(d[4]);
        if (d.length > 6) {
            int q = -1;
            try { q = Integer.parseInt(d[5].trim()); } catch (NumberFormatException ignored) { }
            u.setSecurity(q, d[6].trim());
        }
        return u;
    }

    // ---------- READ all ----------
    public List<User> findAll() throws IOException {
        synchronized (LOCK) {
            List<User> users = new ArrayList<>();
            if (!Files.exists(filePath)) return users;
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    User u = parse(line);
                    if (u != null) users.add(u);
                }
            }
            return users;
        }
    }

    // ---------- READ one ----------
    public User findByUsername(String username) throws IOException {
        if (username == null) return null;
        String wanted = username.trim();
        for (User u : findAll()) {
            if (u.getUsername().equalsIgnoreCase(wanted)) return u;
        }
        return null;
    }

    // ---------- SEARCH (username, e-mail or role contains the text) ----------
    public List<User> search(String text) throws IOException {
        if (text == null || text.trim().isEmpty()) return findAll();
        String q = text.trim().toLowerCase(Locale.ROOT);
        List<User> result = new ArrayList<>();
        for (User u : findAll()) {
            if (u.getUsername().toLowerCase(Locale.ROOT).contains(q)
                    || u.getEmail().toLowerCase(Locale.ROOT).contains(q)
                    || u.getRole().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(u);
            }
        }
        return result;
    }

    // ---------- LOGIN check (hashed and old plain-text passwords) ----------
    public User authenticate(String username, String password) throws IOException {
        User u = findByUsername(username);
        return (u != null && u.checkPassword(password)) ? u : null;
    }

    // ---------- CREATE (false if the username already exists) ----------
    public boolean add(User user) throws IOException {
        synchronized (LOCK) {
            if (findByUsername(user.getUsername()) != null) return false;
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
                writer.write(user.toFileLine());
                writer.newLine();
            }
            return true;
        }
    }

    // ---------- UPDATE (replaces the whole record of that username) ----------
    public boolean update(User updated) throws IOException {
        synchronized (LOCK) {
            List<User> users = findAll();
            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).equals(updated)) {
                    users.set(i, updated);
                    found = true;
                }
            }
            if (found) writeAll(users);
            return found;
        }
    }

    // ---------- DELETE ----------
    public boolean delete(String username) throws IOException {
        synchronized (LOCK) {
            List<User> users = findAll();
            boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
            if (removed) writeAll(users);
            return removed;
        }
    }

    public int countAdmins() throws IOException {
        int n = 0;
        for (User u : findAll()) {
            if (u.canManageUsers()) n++;
        }
        return n;
    }

    // write to a temp file first, then replace (safe against data loss)
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

    // =====================================================================
    //  Profile photos (files in <data>/avatars)
    // =====================================================================

    /**
     * Saves an uploaded photo and returns its new random file name.
     * The CONTENT must really be a JPG/PNG/GIF/WEBP image (the file name is not trusted).
     * Throws IllegalArgumentException with a message that can be shown to the user.
     */
    public String saveAvatar(InputStream in, long size) throws IOException {
        if (size > MAX_AVATAR_BYTES) throw new IllegalArgumentException("Photo must be 2 MB or smaller");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream src = in) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = src.read(buf)) != -1) {
                out.write(buf, 0, n);
                if (out.size() > MAX_AVATAR_BYTES) throw new IllegalArgumentException("Photo must be 2 MB or smaller");
            }
        }
        byte[] b = out.toByteArray();
        String ext = null;
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) ext = "jpg";
        else if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') ext = "png";
        else if (b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') ext = "gif";
        else if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') ext = "webp";
        if (ext == null) throw new IllegalArgumentException("Photo must be a JPG, PNG, GIF or WEBP image");

        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Files.write(avatarDir.resolve(name), b, StandardOpenOption.CREATE_NEW);
        return name;
    }

    public void deleteAvatar(String fileName) {
        Path p = findAvatar(fileName);
        if (p == null) return;
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
            // an old photo that cannot be removed should not fail the request
        }
    }

    /** Path of a stored photo, or null when the name is unsafe or the file is missing. */
    public Path findAvatar(String fileName) {
        if (fileName == null || !SAFE_AVATAR.matcher(fileName).matches()) return null;
        Path p = avatarDir.resolve(fileName);
        return Files.isRegularFile(p) ? p : null;
    }

    public static String avatarContentType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}
