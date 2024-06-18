package com.hoarp.web.data;

public class Appointment {
    private String id;
    private String schedule_date;
    private String start_time;
    private String end_time;
    private String department;
    private String title;
    private String campus;
    private String doctor;
    private String patient;
    private String patient_phone;
    private boolean available;

    public Appointment(String id, String schedule_date, String start_time, String end_time, String department,
                       String title, String campus, String doctor, String patient, String patient_phone,
                       boolean available) {
        this.id = id;
        this.schedule_date = schedule_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.department = department;
        this.title = title;
        this.campus = campus;
        this.doctor = doctor;
        this.patient = patient;
        this.patient_phone = patient_phone;
        this.available = available;
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

    public String getTitle() {
        return title;
    }

    public String getCampus() {
        return campus;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getPatient() {
        return patient;
    }

    public String getPatient_phone() {
        return patient_phone;
    }

    public boolean isAvailable() {
        return available;
    }
}
