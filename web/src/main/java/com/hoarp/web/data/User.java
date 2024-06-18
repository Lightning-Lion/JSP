package com.hoarp.web.data;

public class User {
    private String id;
    private String phone;

    public User() {}

    public User(String id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }
}
