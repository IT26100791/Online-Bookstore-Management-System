package com.bookstore.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A digital book delivered by download link. No shipping, 10% digital discount. */
public class EbookOrder extends Order {

    public static final String TYPE_CODE = "EBOOK";
    public static final double DEFAULT_UNIT_PRICE = 900.00;
    public static final double DISCOUNT_RATE = 0.10;
    public static final List<String> FORMATS = Collections.unmodifiableList(
            Arrays.asList("PDF", "EPUB", "MOBI"));

    private static final List<String> FLOW = Collections.unmodifiableList(
            Arrays.asList("Pending", "Processing", "Delivered"));

    private static final List<String> PAYMENTS = Collections.unmodifiableList(
            Arrays.asList(PAY_CARD, PAY_BANK));          // no cash on delivery for downloads

    private final String fileFormat;

    public EbookOrder(String id, String customerName, String bookTitle, String status,
                      int quantity, double unitPrice, String fileFormat,
                      String paymentMethod, String receiptFile) {
        super(id, customerName, bookTitle, status, quantity, unitPrice, paymentMethod, receiptFile);
        if (!PAYMENTS.contains(getPaymentMethod())) {
            throw new IllegalArgumentException("Cash on delivery is not available for e-books");
        }
        String f = requireText(fileFormat, "File format").toUpperCase();
        if (!FORMATS.contains(f)) {
            throw new IllegalArgumentException("File format must be PDF, EPUB or MOBI");
        }
        this.fileFormat = f;
    }

    public String getFileFormat() { return fileFormat; }

    @Override public String getTypeCode() { return TYPE_CODE; }
    @Override public String getOrderType() { return "E-Book"; }
    @Override public String getDeliveryInfo() { return "Download link sent by email (" + fileFormat + ")"; }
    @Override public String getExtraField() { return fileFormat; }

    @Override
    public double getAdjustment() {
        return -(getSubtotal() * DISCOUNT_RATE);
    }

    @Override
    public String getAdjustmentLabel() { return "Digital discount (10%)"; }

    @Override
    public List<String> getStatusFlow() { return FLOW; }

    @Override
    public List<String> getAllowedPayments() { return PAYMENTS; }
}