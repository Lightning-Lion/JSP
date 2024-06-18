package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/departments")
public class DepartmentsView extends HttpServlet {
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Map<String, Object>> departmentsData = jdbcTemplate.queryList(
                "SELECT id, name FROM Departments",
                rs -> {
                    Map<String, Object> department = new HashMap<>();
                    department.put("name", rs.getString("name"));
                    department.put("doctors", new ArrayList<Map<String, Object>>());
                    return new AbstractMap.SimpleEntry<>(rs.getString("id"), department);
                }
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map<String, Object>> doctorsData = jdbcTemplate.queryList(
                "SELECT id, name, department_id FROM Doctors",
                rs -> {
                    Map<String, Object> doctor = new HashMap<>();
                    doctor.put("id", rs.getString("id"));
                    doctor.put("name", rs.getString("name"));
                    doctor.put("department_id", rs.getString("department_id"));
                    return doctor;
                }
        );

        for (Map<String, Object> doctor : doctorsData) {
            String departmentId = (String) doctor.get("department_id");
            Map<String, Object> department = departmentsData.get(departmentId);
            if (department != null) {
                ((List<Map<String, Object>>) department.get("doctors")).add(doctor);
            }
        }

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("title", "科室医生 - 浙大一院网上预约挂号平台");
        pageData.put("view", "DepartmentsView");
        pageData.put("route", "/departments");

        HttpSession session = req.getSession(true);
        String user = "";
        String userJson = (String) session.getAttribute("user");
        if (userJson != null) {
            user = mapper.readValue(userJson, User.class).getPhone();
        }
        pageData.put("user", user);

        pageData.put("departmentsData", departmentsData);

        req.setAttribute("pageData", pageData);
        req.getRequestDispatcher("/App.jsp").forward(req, resp);
    }
}
