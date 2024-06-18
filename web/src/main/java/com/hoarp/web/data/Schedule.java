package com.hoarp.web.data;

public class Schedule {
    private String id;
    private String schedule_date;
    private String start_time;
    private String end_time;
    private String department;
    private String campus;
    private String doctor;
    private String type;
    private String price;
    private String remaining;

    public Schedule(String id, String schedule_date, String start_time, String end_time, String department,
                    String campus, String doctor, String type, String price, String remaining) {
        this.id = id;
        this.schedule_date = schedule_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.department = department;
        this.campus = campus;
        this.doctor = doctor;
        this.type = type;
        this.price = price;
        this.remaining = remaining;
    }

    public String getId() {
        return id;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getDepartment() {
        return department;
    }

    public String getCampus() {
        return campus;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public String getRemaining() {
        return remaining;
    }
}
