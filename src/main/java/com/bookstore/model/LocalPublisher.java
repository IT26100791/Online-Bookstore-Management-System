package com.bookstore.model;

/** A publisher based inside the country. Its extra detail is the district. */
public class LocalPublisher extends Publisher {

    public static final String TYPE = "LOCAL";

    private String district;

    public LocalPublisher(String id, String name, String phone, String district) {
        super(id, name, phone);
        this.district = cleanText(district, "District", MAX_EXTRA_LENGTH);
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = cleanText(district, "District", MAX_EXTRA_LENGTH);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getExtraLabel() {
        return "District";
    }

    @Override
    public String getExtra() {
        return district;
    }
}