package com.bookstore.model;


public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(String username, String passwordHash) {
        super(username, passwordHash);
    }

    @Override public String getRole() { return "Admin"; }
    @Override public String getRoleDescription() { return "Can manage books, orders and users"; }
    @Override public boolean canManageUsers() { return true; }
    @Override public String getHomePage() { return "user"; }
    @Override public String getRoleBadgeClass() { return "role-admin"; }
}
