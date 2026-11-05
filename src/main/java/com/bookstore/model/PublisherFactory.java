package com.bookstore.model;

/**
 * Creates the correct {@link Publisher} subclass from a type code.
 * The servlet and the DAO both use this, so neither needs to know the subclasses.
 */
public final class PublisherFactory {

    private PublisherFactory() {
        // static helper - no instances
    }

    /**
     * @param type  {@code LOCAL} or {@code INTERNATIONAL} (case-insensitive)
     * @param extra district for a local publisher, country for an international one
     * @throws IllegalArgumentException if the type is unknown or any value is invalid
     */
    public static Publisher create(String type, String id, String name, String phone, String extra) {
        if (LocalPublisher.TYPE.equalsIgnoreCase(type)) {
            return new LocalPublisher(id, name, phone, extra);
        }
        if (InternationalPublisher.TYPE.equalsIgnoreCase(type)) {
            return new InternationalPublisher(id, name, phone, extra);
        }
        throw new IllegalArgumentException("Choose a publisher type: local or international.");
    }
}
