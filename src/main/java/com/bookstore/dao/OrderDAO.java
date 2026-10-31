package com.bookstore.dao;

import com.bookstore.model.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final Path filePath;

    public OrderDAO(String dataDir) {
        this.filePath = Paths.get(dataDir, "orders.txt");
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare orders file: " + e.getMessage(), e);
        }
    }

    // READ all
    public synchronized List<Order> findAll() throws IOException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 6) {
                    try {
                        orders.add(new Order(
                                Integer.parseInt(d[0]),
                                d[1],
                                d[2],
                                Integer.parseInt(d[3]),
                                Double.parseDouble(d[4]),
                                d[5]));
                    } catch (NumberFormatException ignored) {
                        // skip corrupted line
                    }
                }
            }
        }
        return orders;
    }

    // READ one
    public synchronized Order findById(int id) throws IOException {
        for (Order o : findAll()) {
            if (o.getOrderId() == id) return o;
        }
        return null;
    }

    // CREATE (id is generated automatically)
    public synchronized Order add(String customerName, String bookTitle,
                                  int quantity, double totalAmount, String status) throws IOException {
        int nextId = 1;
        for (Order o : findAll()) {
            if (o.getOrderId() >= nextId) nextId = o.getOrderId() + 1;
        }
        Order order = new Order(nextId, customerName, bookTitle, quantity, totalAmount, status);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(order.toFileLine());
            writer.newLine();
        }
        return order;
    }

    // UPDATE
    public synchronized boolean update(int id, String customerName, String bookTitle,
                                       int quantity, double totalAmount, String status) throws IOException {
        List<Order> orders = findAll();
        boolean found = false;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId() == id) {
                orders.set(i, new Order(id, customerName, bookTitle, quantity, totalAmount, status));
                found = true;
            }
        }
        if (found) writeAll(orders);
        return found;
    }

    // DELETE
    public synchronized boolean delete(int id) throws IOException {
        List<Order> orders = findAll();
        boolean removed = orders.removeIf(o -> o.getOrderId() == id);
        if (removed) writeAll(orders);
        return removed;
    }

    // write to temp file first, then replace (safe against data loss)
    private void writeAll(List<Order> orders) throws IOException {
        Path temp = filePath.resolveSibling("orders_temp.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Order o : orders) {
                writer.write(o.toFileLine());
                writer.newLine();
            }
        }
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}