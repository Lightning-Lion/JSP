package com.hoarp.web.data;

public class Campus {
    private String id;
    private String name;

    public Campus(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
