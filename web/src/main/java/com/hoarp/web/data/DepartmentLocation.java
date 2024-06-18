package com.hoarp.web.data;

public class DepartmentLocation {
    private String name;
    private String outpatientLocation;
    private String wardLocation;

    public DepartmentLocation(String name, String outpatientLocation, String wardLocation) {
        this.name = name;
        this.outpatientLocation = outpatientLocation;
        this.wardLocation = wardLocation;
    }

    public String getName() {
        return name;
    }

    public String getOutpatientLocation() {
        return outpatientLocation;
    }

    public String getWardLocation() {
        return wardLocation;
    }
}
