package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoarp.web.data.CaptchaData;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.JdbcTemplate;
import com.hoarp.web.utils.RedisTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/login")
public class LoginApi extends HttpServlet {
    private final RedisTemplate redisTemplate = RedisTemplate.getInstance();
    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JsonObject body = JsonParser.parseString(requestBody.toString()).getAsJsonObject();
        String phone = body.get("phone").getAsString();
        String captcha = body.get("captcha").getAsString();

        HttpSession newSession = req.getSession(true);
        if (newSession.getAttribute("user") != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"用户已经登录\"}");
            return;
        }

        if (!phone.matches("\\d{11}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"非 11 位手机号码\"}");
            return;
        }

        String lastCaptcha = redisTemplate.get("spring:session:captcha:" + phone);
        if (lastCaptcha == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"验证码错误\"}");
            return;
        }

        CaptchaData captchaData = mapper.readValue(lastCaptcha, CaptchaData.class);
        if (System.currentTimeMillis() - captchaData.getTimestamp() > 30000) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"验证码失效\"}");
            return;
        }

        if (!captcha.equals(captchaData.getCaptcha())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"验证码错误\"}");
            return;
        }

        User user = jdbcTemplate.query(
                "SELECT id, phone FROM Users WHERE phone = ?",
                rs -> new User(
                        rs.getString("id"),
                        rs.getString("phone")
                ),
                phone
        );

        if (user == null) {
            long id = jdbcTemplate.update("INSERT INTO Users (phone) VALUES (?)", phone);
            user = new User(String.valueOf(id), phone);
        }

        String rSessionKey = redisTemplate.get("spring:session:users:" + user.getId());
        if (rSessionKey != null) {
            redisTemplate.delete("spring:session:sessions:" + rSessionKey);
            redisTemplate.delete("spring:session:users:" + user.getId());
        }

        newSession.setAttribute("user", mapper.writeValueAsString(user));
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"message\": \"登录成功\"}");
    }
}
