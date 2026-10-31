package com.bookstore.model;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public String getRoleDescription() {
        return "Can manage books, orders and users";
    }
}
