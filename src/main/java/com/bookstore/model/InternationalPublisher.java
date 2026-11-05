package com.bookstore.model;

/** A publisher based in another country. Its extra detail is the country. */
public class InternationalPublisher extends Publisher {

    public static final String TYPE = "INTERNATIONAL";

    private String country;

    public InternationalPublisher(String id, String name, String phone, String country) {
        super(id, name, phone);
        this.country = cleanText(country, "Country", MAX_EXTRA_LENGTH);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = cleanText(country, "Country", MAX_EXTRA_LENGTH);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getExtraLabel() {
        return "Country";
    }

    @Override
    public String getExtra() {
        return country;
    }
}