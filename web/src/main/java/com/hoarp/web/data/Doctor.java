package com.hoarp.web.data;

public class Doctor {
    private String name;
    private String photo;
    private String department;
    private String sex;
    private String qualification;
    private String position;
    private String work;
    private String bio;
    private String specialty;

    public Doctor(String name, String photo, String department, String sex, String qualification,
                  String position, String work, String bio, String specialty) {
        this.name = name;
        this.photo = photo;
        this.department = department;
        this.sex = sex;
        this.qualification = qualification;
        this.position = position;
        this.work = work;
        this.bio = bio;
        this.specialty = specialty;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDepartment() {
        return department;
    }

    public String getSex() {
        return sex;
    }

    public String getQualification() {
        return qualification;
    }

    public String getPosition() {
        return position;
    }

    public String getWork() {
        return work;
    }

    public String getBio() {
        return bio;
    }

    public String getSpecialty() {
        return specialty;
    }
}
