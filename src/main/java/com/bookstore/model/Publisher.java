package com.bookstore.model;

import java.util.Locale;

/**
 * Abstract base class for every publisher in the bookstore.
 *
 * <p>OOP concepts demonstrated here:</p>
 * <ul>
 *   <li><b>Abstraction</b> - the class cannot be instantiated; subclasses must say what
 *       kind of publisher they are and what extra detail they carry.</li>
 *   <li><b>Encapsulation / information hiding</b> - all fields are private, the ID is
 *       immutable, and every value is validated before it is stored.</li>
 *   <li><b>Polymorphism</b> - callers work with {@code Publisher} and the correct
 *       subclass behaviour runs ({@link #getType()}, {@link #getExtra()}, {@link #toFileLine()}).</li>
 * </ul>
 */
public abstract class Publisher {

    /** Separator used in publishers.txt. Values must never contain it. */
    private static final String FIELD_SEPARATOR = ",";

    private static final int MAX_ID_LENGTH = 20;
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MAX_PHONE_LENGTH = 20;

    /** Maximum length of the subclass-specific value (district or country). */
    protected static final int MAX_EXTRA_LENGTH = 40;

    private final String publisherId;
    private String name;
    private String phone;

    protected Publisher(String publisherId, String name, String phone) {
        this.publisherId = cleanText(publisherId, "Publisher ID", MAX_ID_LENGTH);
        this.name = cleanText(name, "Company name", MAX_NAME_LENGTH);
        this.phone = cleanText(phone, "Phone number", MAX_PHONE_LENGTH);
    }

    // ---------------------------------------------------------------- abstract

    /** Machine-readable type, e.g. {@code LOCAL}. Stored in the data file. */
    public abstract String getType();

    /** Label for the extra value, e.g. {@code District}. Shown in the UI. */
    public abstract String getExtraLabel();

    /** The extra value that only this kind of publisher has. */
    public abstract String getExtra();

    // ---------------------------------------------------------------- accessors

    public String getId() {
        return publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = cleanText(name, "Company name", MAX_NAME_LENGTH);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = cleanText(phone, "Phone number", MAX_PHONE_LENGTH);
    }

    // ---------------------------------------------------------------- behaviour

    /**
     * Template method: the layout of a data-file line is fixed here, while the
     * type and extra value come from the subclass.
     * Format: {@code id,name,phone,TYPE,extra} (the first three columns match the
     * older three-column format).
     */
    public final String toFileLine() {
        return String.join(FIELD_SEPARATOR, publisherId, name, phone, getType(), getExtra());
    }

    /** Case-insensitive search across every visible field. A blank keyword matches all. */
    public boolean matches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        return contains(publisherId, needle)
                || contains(name, needle)
                || contains(phone, needle)
                || contains(getExtra(), needle);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Publisher)) {
            return false;
        }
        return publisherId.equalsIgnoreCase(((Publisher) other).publisherId);
    }

    @Override
    public int hashCode() {
        return publisherId.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return getType() + " publisher " + publisherId + " (" + name + ")";
    }

    // ---------------------------------------------------------------- validation

    /**
     * Trims and validates a single-line text value that will be written to the data file.
     *
     * @throws IllegalArgumentException with a user-readable message when invalid
     */
    protected static String cleanText(String value, String label, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        if (text.contains(FIELD_SEPARATOR)) {
            throw new IllegalArgumentException(label + " cannot contain commas.");
        }
        if (text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(label + " cannot contain line breaks.");
        }
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(label + " must be " + maxLength + " characters or fewer.");
        }
        return text;
    }

    private static boolean contains(String field, String needle) {
        return field.toLowerCase(Locale.ROOT).contains(needle);
    }
}