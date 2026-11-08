package com.bookstore.model;

import com.bookstore.util.PasswordUtil;

import java.io.Serializable;
import java.util.Locale;

/**
 * Abstract base class for every account in the system.
 *
 *  - Abstraction   : abstract methods say WHAT a user can do, subclasses decide HOW.
 *  - Encapsulation : all fields are private; passwords are checked with checkPassword().
 *  - Info hiding   : username is final, the role is fixed by the subclass (no setRole).
 *  - Inheritance   : Admin and Customer extend this class.
 *  - Polymorphism  : servlet/JSP only use "User"; behaviour comes from the subclass.
 */
public abstract class User implements Comparable<User>, Serializable {
    private static final long serialVersionUID = 3L;

    private final String username;
    private String passwordHash;
    private String email = "";
    private String avatarFile = "";          // generated file name of the profile photo
    private int securityQuestion = -1;       // index of the question, -1 = not set
    private String securityAnswerHash = "";

    protected User(String username, String passwordHash) {
        this.username = username == null ? "" : username.trim();
        this.passwordHash = passwordHash == null ? "" : passwordHash;
    }

    // ---------- decided by the subclass (polymorphism) ----------
    public abstract String getRole();
    public abstract String getRoleDescription();
    public abstract boolean canManageUsers();
    public abstract String getHomePage();
    public abstract String getRoleBadgeClass();

    public String getUsername() { return username; }

    // ---------- profile ----------
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email.trim(); }

    public String getAvatarFile() { return avatarFile; }
    public void setAvatarFile(String avatarFile) { this.avatarFile = avatarFile == null ? "" : avatarFile.trim(); }
    public boolean hasAvatar() { return !avatarFile.isEmpty(); }

    // ---------- password ----------
    public boolean checkPassword(String rawPassword) {
        return rawPassword != null && PasswordUtil.matches(rawPassword, passwordHash);
    }
    public void setPasswordHash(String newHash) {
        if (newHash != null && !newHash.isEmpty()) this.passwordHash = newHash;
    }
    /** Needed only by UserDAO when it re-creates a user with another role. */
    public String getPasswordHash() { return passwordHash; }

    // ---------- security question (Forgot password) ----------
    public int getSecurityQuestion() { return securityQuestion; }
    public boolean hasSecurityQuestion() { return securityQuestion >= 0 && !securityAnswerHash.isEmpty(); }
    public void setSecurity(int question, String answerHash) {
        this.securityQuestion = question;
        this.securityAnswerHash = answerHash == null ? "" : answerHash;
    }
    public boolean checkSecurityAnswer(String normalizedAnswer) {
        return hasSecurityQuestion() && normalizedAnswer != null
                && PasswordUtil.matches(normalizedAnswer, securityAnswerHash);
    }

    /** Copies e-mail, photo and security question (used when the role changes). */
    public void copyProfileFrom(User other) {
        this.email = other.email;
        this.avatarFile = other.avatarFile;
        this.securityQuestion = other.securityQuestion;
        this.securityAnswerHash = other.securityAnswerHash;
    }

    // ---------- one line of users.txt ----------
    // username,passwordHash,role,email,avatarFile,securityQuestion,securityAnswerHash
    public String toFileLine() {
        return String.join(",", username, passwordHash, getRole(), email, avatarFile,
                String.valueOf(securityQuestion), securityAnswerHash);
    }

    @Override public int compareTo(User o) { return username.compareToIgnoreCase(o.username); }
    @Override public boolean equals(Object o) { return o instanceof User && username.equalsIgnoreCase(((User) o).username); }
    @Override public int hashCode() { return username.toLowerCase(Locale.ROOT).hashCode(); }
    @Override public String toString() { return getRole() + "[" + username + "]"; }   // never prints the password
}
