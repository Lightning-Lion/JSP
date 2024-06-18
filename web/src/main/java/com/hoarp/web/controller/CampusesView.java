package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.Campus;
import com.hoarp.web.data.CampusDetail;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/campuses/*")
public class CampusesView extends HttpServlet {
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String id = pathInfo.substring(1);

        CampusDetail campusData = jdbcTemplate.query(
                "SELECT name, photo, description, location FROM Campuses WHERE id = ?",
                rs -> new CampusDetail(
                        rs.getString("name"),
                        rs.getString("photo"),
                        rs.getString("description"),
                        rs.getString("location")
                ),
                id
        );
        if (campusData == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Campus> campusesData = jdbcTemplate.queryList(
                "SELECT id, name FROM Campuses",
                rs -> new Campus(
                        rs.getString("id"),
                        rs.getString("name")
                )
        );
        if (campusesData.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("title", "院区介绍 - 浙大一院网上预约挂号平台");
        pageData.put("view", "CampusesView");
        pageData.put("route", "/campuses");

        HttpSession session = req.getSession(true);
        String user = "";
        String userJson = (String) session.getAttribute("user");
        if (userJson != null) {
            user = mapper.readValue(userJson, User.class).getPhone();
        }
        pageData.put("user", user);

        pageData.put("campusData", campusData);
        pageData.put("campusesData", campusesData);

        req.setAttribute("pageData", pageData);
        req.getRequestDispatcher("/App.jsp").forward(req, resp);
    }
}
