package com.bookstore.model;

/** A publisher based inside the country. */
public class LocalPublisher extends Publisher {
    private String district;

    public LocalPublisher(String id, String name, String phone, String district) {
        super(id, name, phone);
        setDistrict(district);
    }

    public void setDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("District cannot be empty.");
        }
        this.district = district.trim().replace(",", " ");
    }

    @Override public String getType() { return "LOCAL"; }
    @Override public String getExtraLabel() { return "District"; }
    @Override public String getExtra() { return district; }
}

