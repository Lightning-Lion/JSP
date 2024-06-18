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

@WebServlet("/api/logout")
public class LogoutApi extends HttpServlet {
    private final RedisTemplate redisTemplate = RedisTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String userJson = (String) session.getAttribute("user");
        if (userJson == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"ÓÃ»§Î´µÇÂ¼\"}");
            return;
        }
        String userId = mapper.readValue(userJson, User.class).getId();

        redisTemplate.delete("spring:session:users:" + userId);
        redisTemplate.delete("spring:session:sessions:" + session.getId());
        session.invalidate();

        resp.sendRedirect("/login");
    }
}
