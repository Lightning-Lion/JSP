package com.hoarp.web.data;

import java.util.List;
import java.util.Map;

public class Filters {
    private List<String> schedule_date;
    private Map<String, List<String>> departments;
    private List<String> campus;
    private List<String> type;

    public Filters(List<String> schedule_date, Map<String, List<String>> departments, List<String> campus,
                   List<String> type) {
        this.schedule_date = schedule_date;
        this.departments = departments;
        this.campus = campus;
        this.type = type;
    }

    public List<String> getSchedule_date() {
        return schedule_date;
    }

    public Map<String, List<String>> getDepartments() {
        return departments;
    }

    public List<String> getCampus() {
        return campus;
    }

    public List<String> getType() {
        return type;
    }
}
