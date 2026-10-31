package com.bookstore.model;

public class Order {
    private int orderId;
    private String customerName;
    private String bookTitle;
    private int quantity;
    private double totalAmount;
    private String status;

    public Order() {
    }

    public Order(int orderId, String customerName, String bookTitle,
                 int quantity, double totalAmount, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // One line in orders.txt: id,customer,book,qty,total,status
    public String toFileLine() {
        return orderId + "," + customerName + "," + bookTitle + ","
                + quantity + "," + totalAmount + "," + status;
    }
}