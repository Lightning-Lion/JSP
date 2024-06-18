package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.Filters;
import com.hoarp.web.utils.JdbcTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/filters")
public class FiltersApi extends HttpServlet {
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(true).getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"ÓÃ»§Î´µÇÂ¼\"}");
            return;
        }

        List<String> scheduleDates = jdbcTemplate.queryList(
                "SELECT DISTINCT schedule_date FROM Schedules ORDER BY schedule_date",
                rs -> rs.getString("schedule_date")
        );

        Map<String, List<String>> departments = new HashMap<>();
        jdbcTemplate.queryList(
                "SELECT di.name AS divisions, GROUP_CONCAT(de.name ORDER BY de.name SEPARATOR ', ') AS departments " +
                "FROM Divisions AS di " +
                "JOIN Departments AS de ON di.id = de.division_id " +
                "GROUP BY di.name",
                rs -> {
                    String division = rs.getString("divisions");
                    String[] departmentsArray = rs.getString("departments").split(", ");
                    departments.put(division, List.of(departmentsArray));
                    return null;
                }
        );

        List<String> campuses = jdbcTemplate.queryList(
                "SELECT name FROM Campuses",
                rs -> rs.getString("name")
        );

        List<String> types = jdbcTemplate.queryList(
                "SELECT title FROM Types",
                rs -> rs.getString("title")
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(
                new Filters(
                        scheduleDates,
                        departments,
                        campuses,
                        types
                )
        ));
    }
}
