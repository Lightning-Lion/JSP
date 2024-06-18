package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.Department;
import com.hoarp.web.data.DepartmentLocation;
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

@WebServlet("/departments/*")
public class DepartmentView extends HttpServlet {
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

        Department departmentData = jdbcTemplate.query(
                "SELECT name, intro, features, research FROM Departments WHERE id = ?",
                rs -> new Department(
                        rs.getString("name"),
                        rs.getString("intro"),
                        rs.getString("features"),
                        rs.getString("research")
                ),
                id
        );
        if (departmentData == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<DepartmentLocation> departmentLocationsData = jdbcTemplate.queryList(
                "SELECT c.name, dl.outpatient_location, dl.ward_location FROM Department_locations AS dl JOIN Campuses AS c ON c.id = dl.campus_id WHERE dl.department_id = ?",
                rs -> new DepartmentLocation(
                        rs.getString("name"),
                        rs.getString("outpatient_location"),
                        rs.getString("ward_location")
                ),
                id
        );
        if (departmentLocationsData.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("title", "YY - 浙大一院网上预约挂号平台");
        pageData.put("view", "DepartmentView");
        pageData.put("route", "/departments");

        HttpSession session = req.getSession(true);
        String user = "";
        String userJson = (String) session.getAttribute("user");
        if (userJson != null) {
            user = mapper.readValue(userJson, User.class).getPhone();
        }
        pageData.put("user", user);

        pageData.put("departmentData", departmentData);
        pageData.put("departmentLocationsData", departmentLocationsData);

        req.setAttribute("pageData", pageData);
        req.getRequestDispatcher("/App.jsp").forward(req, resp);
    }
}
