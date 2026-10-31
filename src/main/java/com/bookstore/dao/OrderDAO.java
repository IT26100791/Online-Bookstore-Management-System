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

    public synchronized List<Order> findAll() throws IOException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length >= 4) {
                    orders.add(new Order(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim()));
                }
            }
        }
        return orders;
    }

    public synchronized Order findById(String id) throws IOException {
        for (Order o : findAll()) {
            if (o.getId().equalsIgnoreCase(id)) return o;
        }
        return null;
    }

    public synchronized boolean add(Order order) throws IOException {
        if (findById(order.getId()) != null) return false;
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(order.toFileLine());
            writer.newLine();
        }
        return true;
    }

    public synchronized boolean update(Order updated) throws IOException {
        List<Order> orders = findAll();
        boolean found = false;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equalsIgnoreCase(updated.getId())) {
                orders.set(i, updated);
                found = true;
            }
        }
        if (found) writeAll(orders);
        return found;
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Order> orders = findAll();
        boolean removed = orders.removeIf(o -> o.getId().equalsIgnoreCase(id));
        if (removed) writeAll(orders);
        return removed;
    }

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
