package com.bookstore.model;

/** A publisher based in another country. */
public class InternationalPublisher extends Publisher {
    private String country;

    public InternationalPublisher(String id, String name, String phone, String country) {
        super(id, name, phone);
        setCountry(country);
    }

    public void setCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty.");
        }
        this.country = country.trim().replace(",", " ");
    }

    @Override public String getType() { return "INTERNATIONAL"; }
    @Override public String getExtraLabel() { return "Country"; }
    @Override public String getExtra() { return country; }
}

