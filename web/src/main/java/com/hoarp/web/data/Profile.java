package com.hoarp.web.data;

public class Profile {
    private String id;
    private String name;
    private String id_card;
    private String phone;

    public Profile(String id, String name, String id_card, String phone) {
        this.id = id;
        this.name = name;
        this.id_card = id_card;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getId_card() {
        return id_card;
    }

    public String getPhone() {
        return phone;
    }
}
