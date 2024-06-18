package com.hoarp.web.data;

public class CampusDetail {
    private String name;
    private String photo;
    private String description;
    private String location;

    public CampusDetail(String name, String photo, String description, String location) {
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}
