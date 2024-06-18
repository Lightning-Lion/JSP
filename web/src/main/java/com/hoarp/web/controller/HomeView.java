package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoarp.web.data.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("")
public class HomeView extends HttpServlet {
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("title", "���һԺ����ԤԼ�Һ�ƽ̨");
        pageData.put("description", "test");
        pageData.put("view", "HomeView");
        pageData.put("route", "/");

        HttpSession session = req.getSession(true);
        String user = "";
        String userJson = (String) session.getAttribute("user");
        if (userJson != null) {
            user = mapper.readValue(userJson, User.class).getPhone();
        }
        pageData.put("user", user);

        req.setAttribute("pageData", pageData);
        req.getRequestDispatcher("/App.jsp").forward(req, resp);
    }
}
