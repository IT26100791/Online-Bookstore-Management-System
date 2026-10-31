package com.bookstore.model;

public class Customer extends User {
    public Customer(String username, String password) {
        super(username, password, "Customer");
    }

    @Override
    public String getRoleDescription() {
        return "Can browse books and place orders";
    }
}
