package com.bookstore.model;

/** Base class for all publishers (Inheritance + Polymorphism: Local / International). */
public abstract class Publisher {
    private final String id;
    private String name;
    private String phone;

    protected Publisher(String id, String name, String phone) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher ID cannot be empty.");
        }
        this.id = id.trim().replace(",", " ");
        setName(name);
        setPhone(phone);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    // Setters validate the values (Encapsulation)
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty.");
        }
        this.name = name.trim().replace(",", " ");
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty.");
        }
        this.phone = phone.trim().replace(",", " ");
    }

    // Polymorphism: each subclass overrides these
    public abstract String getType();        // LOCAL / INTERNATIONAL
    public abstract String getExtraLabel();  // District / Country
    public abstract String getExtra();       // the district / country value

    /** type,id,name,phone,extra */
    public String toFileLine() {
        return getType() + "," + id + "," + name + "," + phone + "," + getExtra();
    }
}

