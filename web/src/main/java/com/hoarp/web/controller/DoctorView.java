package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.Doctor;
import com.hoarp.web.data.DoctorTime;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/doctors/*")
public class DoctorView extends HttpServlet {
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

        Doctor doctorData = jdbcTemplate.query(
                "SELECT do.name, do.photo, de.name AS department, do.sex, do.qualification, do.position, do.work, do.bio, do.specialty FROM Doctors AS do JOIN Departments AS de ON de.id = do.department_id WHERE do.id = ?",
                rs -> new Doctor(
                        rs.getString("name"),
                        rs.getString("photo"),
                        rs.getString("department"),
                        rs.getString("sex"),
                        rs.getString("qualification"),
                        rs.getString("position"),
                        rs.getString("work"),
                        rs.getString("bio"),
                        rs.getString("specialty")
                ),
                id
        );
        if (doctorData == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Map<String, Object>> doctorTimeRawData = jdbcTemplate.queryList(
                "SELECT ds.week_day, ds.start_time, ds.end_time, c.name AS campus FROM Doctor_schedules AS ds JOIN Campuses AS c ON c.id = ds.campus_id WHERE ds.doctor_id = ?",
                rs -> {
                    Map<String, Object> map = new HashMap<>();
                    try {
                        map.put("week_day", rs.getInt("week_day"));
                        map.put("start_time", rs.getString("start_time"));
                        map.put("end_time", rs.getString("end_time"));
                        map.put("campus", rs.getString("campus"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return map;
                },
                id
        );

        List<DoctorTime> doctorTimeData = doctorTimeRawData.stream().map(data -> {
            String startTime = ((String) data.get("start_time")).substring(0, ((String) data.get("start_time")).lastIndexOf(":"));
            String endTime = ((String) data.get("end_time")).substring(0, ((String) data.get("end_time")).lastIndexOf(":"));
            String weekDay = new String[]{"一", "二", "三", "四", "五", "六", "日"}[(int) data.get("week_day") - 1];

            return new DoctorTime(
                    "星期" + weekDay + " (" + startTime + " ~ " + endTime + ")",
                    (String) data.get("campus")
            );
        }).toList();

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("title", "XX - 浙大一院网上预约挂号平台");
        pageData.put("view", "DoctorView");
        pageData.put("route", "/departments");

        HttpSession session = req.getSession(true);
        String user = "";
        String userJson = (String) session.getAttribute("user");
        if (userJson != null) {
            user = mapper.readValue(userJson, User.class).getPhone();
        }
        pageData.put("user", user);

        pageData.put("doctorData", doctorData);
        pageData.put("doctorTimeData", doctorTimeData);

        req.setAttribute("pageData", pageData);
        req.getRequestDispatcher("/App.jsp").forward(req, resp);
    }
}
