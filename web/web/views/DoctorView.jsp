<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hoarp.web.data.Doctor" %>
<%@ page import="com.hoarp.web.data.DoctorTime" %>
<%
  Map<String, Object> pageData = (Map<String, Object>) request.getAttribute("pageData");
  Doctor doctorData = (Doctor) pageData.get("doctorData");
  List<DoctorTime> doctorTimeData = (List<DoctorTime>) pageData.get("doctorTimeData");
%>
<article class="doctor">
  <header class="doctor-profile">
    <img class="doctor-photo" src="/assets/images/${pageData.doctorData.photo}" alt="医生照片">
    <h1 class="doctor-name"><%= doctorData.getName() %></h1>
    <div class="doctor-info">
      <p class="doctor-detail"><span>科 室：</span><%= doctorData.getDepartment() %></p>
      <p class="doctor-detail"><span>性 别：</span><%= doctorData.getSex() %></p>
      <p class="doctor-detail"><span>最高学历、学位：</span><%= doctorData.getQualification() %></p>
      <p class="doctor-detail"><span>职 称：</span><%= doctorData.getPosition() %></p>
      <p class="doctor-detail"><span>院内职务：</span><%= doctorData.getWork() %></p>
    </div>
  </header>
  <section class="doctor-schedule">
    <h2>出诊信息</h2>
    <table class="table doctor-schedule-table">
      <thead>
        <tr>
          <th>门诊时间</th>
          <th>门诊地点</th>
        </tr>
      </thead>
      <tbody>
      <% for (DoctorTime item : doctorTimeData) { %>
        <tr>
          <td><%= item.getTime() %></td>
          <td><%= item.getCampus() %></td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </section>
  <section class="doctor-bio">
    <h2>个人简介</h2>
  <% for (String bio : doctorData.getBio().split("\n")) { %>
    <p><%= bio %></p>
  <% } %>
  </section>
  <section class="doctor-specialty">
    <h2>专业擅长</h2>
  <% for (String specialty : doctorData.getSpecialty().split("\n")) { %>
    <p><%= specialty %></p>
  <% } %>
  </section>
</article>