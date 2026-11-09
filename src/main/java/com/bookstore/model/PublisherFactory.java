package com.bookstore.model;

public final class PublisherFactory {

    private PublisherFactory() {
        // static helper - no instances
    }


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
