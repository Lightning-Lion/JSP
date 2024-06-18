package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.User;
import com.hoarp.web.utils.RedisTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/logout")
public class LogoutView extends HttpServlet {
    private final RedisTemplate redisTemplate = RedisTemplate.getInstance();
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String userJson = (String) session.getAttribute("user");
        String userId = null;

        if (userJson != null) {
            User user = mapper.readValue(userJson, User.class);
            userId = user.getId();
        }

        if (userId == null) {
            resp.sendRedirect("/");
            return;
        }

        redisTemplate.delete("spring:session:users:" + userId);
        redisTemplate.delete("spring:session:sessions:" + session.getId());
        session.invalidate();

        resp.sendRedirect("/login");
    }
}
