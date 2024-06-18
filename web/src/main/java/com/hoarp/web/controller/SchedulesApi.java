package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoarp.web.data.Schedule;
import com.hoarp.web.utils.JdbcTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/schedules")
public class SchedulesApi extends HttpServlet {
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(true).getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"ÓÃ»§Î´µÇÂ¼\"}");
            return;
        }

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JsonObject body = JsonParser.parseString(requestBody.toString()).getAsJsonObject();

        String wheres = "";
        List<String> params = new ArrayList<>();
        params.add(body.get("schedule_date").getAsString());
        params.add(body.get("department").getAsString());

        if (body.has("campus")) {
            wheres += " AND ca.name = ?";
            params.add(body.get("campus").getAsString());
        }
        if (body.has("doctor")) {
            wheres += " AND do.name = ?";
            params.add(body.get("doctor").getAsString());
        }
        if (body.has("type")) {
            wheres += " AND t.title = ?";
            params.add(body.get("type").getAsString());
        }

        List<Schedule> schedules = jdbcTemplate.queryList(
                "SELECT " +
                "    s.id, " +
                "    s.schedule_date, " +
                "    s.start_time, " +
                "    s.end_time, " +
                "    de.name AS department, " +
                "    ca.name AS campus, " +
                "    do.name AS doctor, " +
                "    t.title AS type, " +
                "    t.price, " +
                "    s.limit_number - COALESCE(si.count, 0) AS remaining " +
                "FROM Schedules AS s " +
                "LEFT JOIN ( " +
                "    SELECT schedule_id, count(*) AS count " +
                "    FROM Appointments " +
                "    GROUP BY schedule_id " +
                ") AS si " +
                "ON si.schedule_id = s.id " +
                "JOIN Doctors AS do ON do.id = s.doctor_id " +
                "JOIN Departments AS de ON de.id = do.department_id " +
                "JOIN Types AS t ON t.id = do.type " +
                "JOIN Campuses AS ca ON ca.id = s.campus_id " +
                "WHERE " +
                "    s.limit_number > COALESCE(si.count, 0) " +
                "    AND s.schedule_date = ? " +
                "    AND de.name = ? " +
                "    " + wheres,
                rs -> new Schedule(
                        rs.getString("id"),
                        rs.getString("schedule_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("department"),
                        rs.getString("campus"),
                        rs.getString("doctor"),
                        rs.getString("type"),
                        rs.getString("price"),
                        rs.getString("remaining")
                ),
                params.toArray()
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(schedules));
    }
}
