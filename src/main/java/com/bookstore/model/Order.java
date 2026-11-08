package com.bookstore.model;

import java.util.List;
import java.util.Locale;

/**
 * Abstract base class for every order in the bookstore.
 *
 * OOP concepts used here:
 *  - Abstraction     : abstract methods describe WHAT a subclass must provide.
 *  - Static factory  : create() / fromFileLine() choose the right subclass, so callers only use Order.
 *  - Encapsulation   : all fields are private and final; they are read only through getters.
 *  - Information hiding : validation lives in the constructor, so an invalid Order can never exist.
 *  - Inheritance     : PhysicalOrder and EbookOrder extend this class.
 *  - Polymorphism    : calculateTotal() / toFileLine() call the subclass versions of the abstract methods.
 */
public abstract class Order {

    private final String id;
    private final String customerName;
    private final String bookTitle;
    private final String status;
    private final int quantity;
    private final double unitPrice;
    private final String paymentMethod;   // COD, CARD or BANK
    private final String receiptFile;     // stored file name of the bank receipt, "-" when none

    public static final String PAY_COD = "COD";
    public static final String PAY_CARD = "CARD";
    public static final String PAY_BANK = "BANK";
    public static final String NO_RECEIPT = "-";

    protected Order(String id, String customerName, String bookTitle, String status,
                    int quantity, double unitPrice, String paymentMethod, String receiptFile) {
        this.id = requireText(id, "Order ID");
        this.customerName = requireText(customerName, "Customer name");
        this.bookTitle = requireText(bookTitle, "Book title");
        this.status = requireText(status, "Status");
        if (quantity < 1 || quantity > 100) {
            throw new IllegalArgumentException("Quantity must be between 1 and 100");
        }
        if (unitPrice < 0 || Double.isNaN(unitPrice) || Double.isInfinite(unitPrice)) {
            throw new IllegalArgumentException("Unit price must be a positive number");
        }
        this.quantity = quantity;
        this.unitPrice = unitPrice;

        String pay = requireText(paymentMethod, "Payment method").toUpperCase();
        if (!isValidPayment(pay)) {
            throw new IllegalArgumentException("Unknown payment method");
        }
        String receipt = (receiptFile == null || receiptFile.trim().isEmpty()) ? NO_RECEIPT : receiptFile.trim();
        if (PAY_BANK.equals(pay)) {
            if (NO_RECEIPT.equals(receipt)) {
                throw new IllegalArgumentException("Bank deposit receipt is required");
            }
            if (!receipt.matches("[A-Za-z0-9_.-]+")) {          // blocks commas and path tricks
                throw new IllegalArgumentException("Invalid receipt file name");
            }
        } else {
            receipt = NO_RECEIPT;                                // only bank deposits keep a receipt
        }
        this.paymentMethod = pay;
        this.receiptFile = receipt;
    }

    /** Shared validation helper (hidden from outside the class hierarchy). */
    protected static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required");
        }
        return value.trim();
    }

    // ---------- Getters (encapsulation) ----------

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getBookTitle() { return bookTitle; }
    public String getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReceiptFile() { return receiptFile; }
    public boolean hasReceipt() { return !NO_RECEIPT.equals(receiptFile); }

    public String getPaymentLabel() {
        switch (paymentMethod) {
            case PAY_COD:  return "Cash on Delivery";
            case PAY_CARD: return "Online Card Payment";
            default:       return "Bank Deposit";
        }
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    // ---------- Abstract methods: each subclass supplies its own behaviour ----------

    /** Code stored in the data file, e.g. PHYSICAL or EBOOK. */
    public abstract String getTypeCode();

    /** Friendly name shown in the UI. */
    public abstract String getOrderType();

    /** Where / how the book reaches the customer. */
    public abstract String getDeliveryInfo();

    /** The one extra value that belongs to the subclass (address or file format). */
    public abstract String getExtraField();

    /** Shipping fee (positive) or discount (negative) applied on top of the subtotal. */
    public abstract double getAdjustment();

    /** Payment methods this kind of order accepts (an e-book cannot be paid for on delivery). */
    public abstract List<String> getAllowedPayments();

    /** Label for the adjustment line on the invoice. */
    public abstract String getAdjustmentLabel();

    /** The normal order of statuses for this kind of order (used for the progress tracker). */
    public abstract List<String> getStatusFlow();

    // ---------- Template methods built on top of the abstract ones ----------

    /** Total = subtotal + the subclass-specific adjustment (shipping fee or discount). */
    public final double calculateTotal() {
        return Math.max(0, getSubtotal() + getAdjustment());
    }

    /** Line stored in orders.txt: TYPE,id,customer,book,status,qty,unitPrice,extra,payment,receipt */
    public String toFileLine() {
        return String.join(",",
                getTypeCode(), id, customerName, bookTitle, status,
                String.valueOf(quantity),
                String.format(Locale.ROOT, "%.2f", unitPrice),
                getExtraField(), paymentMethod, receiptFile);
    }

    @Override
    public String toString() {
        return getOrderType() + " #" + id + " (" + bookTitle + " x" + quantity + ", " + status + ")";
    }

    // ---------- Static factory methods (decide which subclass to build) ----------

    public static boolean isValidType(String typeCode) {
        return PhysicalOrder.TYPE_CODE.equals(typeCode) || EbookOrder.TYPE_CODE.equals(typeCode);
    }

    public static boolean isValidPayment(String code) {
        return PAY_COD.equals(code) || PAY_CARD.equals(code) || PAY_BANK.equals(code);
    }

    /** Payment used for old data rows that were saved before payment existed. */
    public static String defaultPayment(String typeCode) {
        return EbookOrder.TYPE_CODE.equals(typeCode) ? PAY_CARD : PAY_COD;
    }

    public static double defaultPrice(String typeCode) {
        return EbookOrder.TYPE_CODE.equals(typeCode)
                ? EbookOrder.DEFAULT_UNIT_PRICE
                : PhysicalOrder.DEFAULT_UNIT_PRICE;
    }

    /** @throws IllegalArgumentException when a value is invalid */
    public static Order create(String typeCode, String id, String customerName, String bookTitle,
                               String status, int quantity, double unitPrice, String extra,
                               String paymentMethod, String receiptFile) {
        if (PhysicalOrder.TYPE_CODE.equals(typeCode)) {
            return new PhysicalOrder(id, customerName, bookTitle, status, quantity, unitPrice, extra,
                    paymentMethod, receiptFile);
        }
        if (EbookOrder.TYPE_CODE.equals(typeCode)) {
            return new EbookOrder(id, customerName, bookTitle, status, quantity, unitPrice, extra,
                    paymentMethod, receiptFile);
        }
        throw new IllegalArgumentException("Unknown order type");
    }

    /**
     * Rebuilds an Order from one line of orders.txt.
     * Newest format : TYPE,id,customer,book,status,qty,unitPrice,extra,payment,receipt
     * Older format  : TYPE,id,customer,book,status,qty,unitPrice,extra  (no payment info)
     * Oldest format : id,customer,book,status  (treated as a printed-book order)
     * Returns null for blank or corrupted lines.
     */
    public static Order fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] d = line.split(",", -1);
        try {
            if (d.length >= 10 && isValidType(d[0].trim())) {
                return create(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim(),
                        Integer.parseInt(d[5].trim()), Double.parseDouble(d[6].trim()), d[7].trim(),
                        d[8].trim(), d[9].trim());
            }
            if (d.length >= 8 && isValidType(d[0].trim())) {
                return create(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim(),
                        Integer.parseInt(d[5].trim()), Double.parseDouble(d[6].trim()), d[7].trim(),
                        defaultPayment(d[0].trim()), NO_RECEIPT);
            }
            if (d.length >= 4) {
                return new PhysicalOrder(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(),
                        1, PhysicalOrder.DEFAULT_UNIT_PRICE, "Not provided", PAY_COD, NO_RECEIPT);
            }
        } catch (IllegalArgumentException e) {
            // corrupted line: skip it instead of crashing the whole page
        }
        return null;
    }
}