package com.hoarp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoarp.web.data.CaptchaData;
import com.hoarp.web.utils.RedisTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

@WebServlet("/api/captcha")
public class CaptchaApi extends HttpServlet {
    private final RedisTemplate redisTemplate = RedisTemplate.getInstance();
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

        if (!phone.matches("\\d{11}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"�� 11 λ�ֻ�����\"}");
            return;
        }

        if (req.getSession(true).getAttribute("user") != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"�û��Ѿ���¼\"}");
            return;
        }

        // 10 ����ȴ 30 ����Ч
        String lastCaptcha = redisTemplate.get("spring:session:captcha:" + phone);
        if (lastCaptcha != null) {
            CaptchaData captchaData = mapper.readValue(lastCaptcha, CaptchaData.class);
            if (System.currentTimeMillis() - captchaData.getTimestamp() < 10000) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"��ȡ��֤��������\"}");
                return;
            } else {
                redisTemplate.delete("spring:session:captcha:" + phone);
            }
        }

        CaptchaData captchaData = new CaptchaData(
                String.valueOf(new Random().nextInt(900000) + 100000),
                System.currentTimeMillis()
        );
        redisTemplate.set("spring:session:captcha:" + phone, mapper.writeValueAsString(captchaData), 30000);

        String responseBody = "{\"captcha\": \"" + captchaData.getCaptcha() + "\"}";
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(responseBody);
    }
}
