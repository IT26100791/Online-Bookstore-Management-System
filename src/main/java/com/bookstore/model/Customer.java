package com.bookstore.model;


public class Customer extends User {
    private static final long serialVersionUID = 1L;

    public Customer(String username, String passwordHash) {
        super(username, passwordHash);
    }

    @Override public String getRole() { return "Customer"; }
    @Override public String getRoleDescription() { return "Can browse books and place orders"; }
    @Override public boolean canManageUsers() { return false; }
    @Override public String getHomePage() { return "welcome.jsp"; }
    @Override public String getRoleBadgeClass() { return "role-customer"; }
}

