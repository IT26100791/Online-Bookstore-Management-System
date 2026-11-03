package com.bookstore.model;

public class Publisher {
    private String publisherId;
    private String name;
    private String phone;

    public Publisher(String publisherId, String name, String phone) {
        this.publisherId = publisherId;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return publisherId;
    }

    public void setId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // සබ්-ක්ලාස් (Subclasses) මඟින් Override කර ඇති මෙතඩ්ස් සඳහා මූලික (Base) ක්‍රමවේදයන්:
    public String getType() {
        return "STANDARD";
    }

    public String getExtraLabel() {
        return "Phone/Info";
    }

    public String getExtra() {
        return phone;
    }

    public String toFileLine() {
        return publisherId + "," + name + "," + phone;
    }
}