package com.hoarp.web.data;

public class DoctorTime {
    private String time;
    private String campus;

    public DoctorTime(String time, String campus) {
        this.time = time;
        this.campus = campus;
    }

    public String getTime() {
        return time;
    }

    public String getCampus() {
        return campus;
    }
}
