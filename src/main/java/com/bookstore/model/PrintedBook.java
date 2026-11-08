package com.bookstore.model;

import java.util.Arrays;
import java.util.List;

/** A physical (paper) book, delivered to the customer's address. */
public class PrintedBook extends Book {
    public static final String TYPE = "PRINTED";
    public static final List<String> BINDINGS = Arrays.asList("Paperback", "Hardcover");

    private final String binding;

    public PrintedBook(String id, String title, String genre, String price,
                       String image, String author, String description, String binding) {
        super(id, title, genre, price, image, author, description);
        this.binding = (binding == null || binding.isEmpty()) ? BINDINGS.get(0) : binding;
    }

    @Override
    public String getType() { return TYPE; }

    @Override
    public String getTypeLabel() { return "Printed book"; }

    @Override
    public String getFormatLabel() { return "Binding"; }

    @Override
    public String getFormatValue() { return binding; }

    @Override
    public String getDeliveryNote() { return "Delivered to your address in 3-5 working days."; }
}