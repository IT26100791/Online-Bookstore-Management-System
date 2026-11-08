package com.bookstore.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A printed book that is shipped to the customer's address. */
public class PhysicalOrder extends Order {

    public static final String TYPE_CODE = "PHYSICAL";
    public static final double DEFAULT_UNIT_PRICE = 1500.00;
    public static final double SHIPPING_FEE = 350.00;
    public static final double FREE_SHIPPING_THRESHOLD = 10000.00;

    private static final List<String> FLOW = Collections.unmodifiableList(
            Arrays.asList("Pending", "Processing", "Shipped", "Delivered"));

    private static final List<String> PAYMENTS = Collections.unmodifiableList(
            Arrays.asList(PAY_COD, PAY_CARD, PAY_BANK));

    private final String shippingAddress;

    public PhysicalOrder(String id, String customerName, String bookTitle, String status,
                         int quantity, double unitPrice, String shippingAddress,
                         String paymentMethod, String receiptFile) {
        super(id, customerName, bookTitle, status, quantity, unitPrice, paymentMethod, receiptFile);
        this.shippingAddress = requireText(shippingAddress, "Shipping address");
    }

    public String getShippingAddress() { return shippingAddress; }

    private boolean qualifiesForFreeShipping() {
        return getSubtotal() >= FREE_SHIPPING_THRESHOLD;
    }

    @Override public String getTypeCode() { return TYPE_CODE; }
    @Override public String getOrderType() { return "Printed Book"; }
    @Override public String getDeliveryInfo() { return "Ship to: " + shippingAddress; }
    @Override public String getExtraField() { return shippingAddress; }

    @Override
    public double getAdjustment() {
        return qualifiesForFreeShipping() ? 0 : SHIPPING_FEE;
    }

    @Override
    public String getAdjustmentLabel() {
        return qualifiesForFreeShipping() ? "Shipping (free over Rs. 10,000)" : "Shipping fee";
    }

    @Override
    public List<String> getStatusFlow() { return FLOW; }

    @Override
    public List<String> getAllowedPayments() { return PAYMENTS; }
}