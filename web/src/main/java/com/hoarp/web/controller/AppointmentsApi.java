package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoarp.web.data.Appointment;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import com.hoarp.web.utils.RedisTemplate;
import com.hoarp.web.utils.SQLCustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/appointments")
public class AppointmentsApi extends HttpServlet {
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userJson = (String) req.getSession(true).getAttribute("user");
        if (userJson == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"用户未登录\"}");
            return;
        }
        String userId = mapper.readValue(userJson, User.class).getId();

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JsonObject body = JsonParser.parseString(requestBody.toString()).getAsJsonObject();
        String scheduleId = body.get("schedule_id").getAsString();
        String profileId = body.get("profile_id").getAsString();

        if (!scheduleId.matches("\\d+") || !profileId.matches("\\d+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"非法参数\"}");
            return;
        }
        try {
            long result = jdbcTemplate.update(
                    "INSERT INTO Appointments (user_id, schedule_id, profile_id) VALUES (?, ?, ?)",
                    userId, scheduleId, profileId
            );

            if (result == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"预约失败\"}");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"message\": \"预约完成\"}");
        } catch (SQLCustomException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userJson = (String) req.getSession(true).getAttribute("user");
        if (userJson == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"用户未登录\"}");
            return;
        }
        String userId = mapper.readValue(userJson, User.class).getId();

        List<Appointment> appointments = jdbcTemplate.queryList(
                "SELECT " +
                "    a.id, " +
                "    s.schedule_date, " +
                "    s.start_time, " +
                "    s.end_time, " +
                "    de.name as department, " +
                "    t.title, " +
                "    c.name as campus, " +
                "    do.name as doctor, " +
                "    p.name as patient, " +
                "    p.phone as patient_phone, " +
                "    CASE " +
                "        WHEN a.used = 1 THEN 0 " +
                "        ELSE " +
                "            CASE " +
                "                WHEN NOW() > CONCAT(s.schedule_date, ' ', s.end_time) THEN 0 " +
                "                ELSE 1 " +
                "            END " +
                "    END AS available " +
                "FROM Appointments as a " +
                "JOIN Schedules AS s ON s.id = a.schedule_id " +
                "JOIN Profiles AS p ON p.id = a.profile_id " +
                "JOIN Campuses AS c ON c.id = s.campus_id " +
                "JOIN Doctors AS do ON do.id = s.doctor_id " +
                "JOIN Departments AS de ON de.id = do.department_id " +
                "JOIN Types AS t ON t.id = do.type " +
                "WHERE a.user_id = ?",
                rs -> new Appointment(
                        rs.getString("id"),
                        rs.getString("schedule_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("department"),
                        rs.getString("title"),
                        rs.getString("campus"),
                        rs.getString("doctor"),
                        rs.getString("patient"),
                        rs.getString("patient_phone"),
                        rs.getBoolean("available")
                ),
                userId
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(appointments));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userJson = (String) req.getSession(true).getAttribute("user");
        if (userJson == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"用户未登录\"}");
            return;
        }
        String userId = mapper.readValue(userJson, User.class).getId();

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JsonObject body = JsonParser.parseString(requestBody.toString()).getAsJsonObject();
        String id = body.get("id").getAsString();

        if (!id.matches("\\d+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"非法参数\"}");
            return;
        }

        long result = jdbcTemplate.update(
                "DELETE FROM Appointments WHERE id = ? AND user_id = ?",
                id, userId
        );

        if (result == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"删除失败\"}");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"message\": \"删除成功\"}");
    }
}
