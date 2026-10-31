package com.bookstore.model;

public class Order {
    private final String id;
    private final String customerName;
    private final String bookTitle;
    private final String status;

    public Order(String id, String customerName, String bookTitle, String status) {
        this.id = id;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getBookTitle() { return bookTitle; }
    public String getStatus() { return status; }

    public String toFileLine() {
        return id + "," + customerName + "," + bookTitle + "," + status;
    }
}
