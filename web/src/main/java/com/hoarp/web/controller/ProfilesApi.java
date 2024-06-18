package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoarp.web.data.CaptchaData;
import com.hoarp.web.data.Profile;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import com.hoarp.web.utils.RedisTemplate;
import com.hoarp.web.utils.SQLDuplicateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@WebServlet("/api/profiles")
public class ProfilesApi extends HttpServlet {
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
        String name = body.get("name").getAsString();
        String idCard = body.get("id_card").getAsString();
        String phone = body.get("phone").getAsString();

        if (Objects.equals(name, "") || Objects.equals(idCard, "") || Objects.equals(phone, "")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"不允许为空\"}");
            return;
        }

        try {
            long result = jdbcTemplate.update(
                    "INSERT INTO Profiles (user_id, name, id_card, phone) VALUES (?, ?, ?, ?)",
                    userId, name, idCard, phone
            );

            if (result == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"添加失败\"}");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"message\": \"添加完成\"}");
        } catch (SQLDuplicateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"存在相同记录\"}");
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

        List<Profile> profiles = jdbcTemplate.queryList(
                "SELECT id, name, id_card, phone FROM Profiles WHERE user_id = ? ORDER BY id DESC",
                rs -> new Profile(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("id_card"),
                        rs.getString("phone")
                ),
                userId
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(profiles));
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

        long result = jdbcTemplate.update(
                "DELETE FROM Profiles WHERE id = ? AND user_id = ?",
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
