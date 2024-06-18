<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hoarp.web.data.Department" %>
<%@ page import="com.hoarp.web.data.DepartmentLocation" %>
<%@ page import="java.util.Objects" %>
<%
  Map<String, Object> pageData = (Map<String, Object>) request.getAttribute("pageData");
  Department departmentData = (Department) pageData.get("departmentData");
  List<DepartmentLocation> departmentLocationsData = (List<DepartmentLocation>) pageData.get("departmentLocationsData");
%>
<article class="dept">
  <header class="dept-profile">
    <h1 class="dept-title"><%= departmentData.getName() %></h1>
  </header>
  <section class="dept-location">
    <h2>院内位置</h2>
    <div class="dept-location-container">
      <table class="table dept-location-table">
        <thead>
          <tr>
            <th>院区</th>
          <% for (DepartmentLocation item : departmentLocationsData) { %>
            <th><%= item.getName() %></th>
          <% } %>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>门诊</td>
          <% for (DepartmentLocation item : departmentLocationsData) { %>
            <th><%=!Objects.equals(item.getOutpatientLocation(), "") ? item.getOutpatientLocation() : "/" %></th>
          <% } %>
          </tr>
          <tr>
            <td>病区</td>
          <% for (DepartmentLocation item : departmentLocationsData) { %>
            <th><%=!Objects.equals(item.getWardLocation(), "") ? item.getWardLocation() : "/" %></th>
          <% } %>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
  <section class="dept-intro">
    <h2>科室简介</h2>
  <% for (String intro : departmentData.getIntro().split("\n")) { %>
    <p><%= intro %></p>
  <% } %>
  </section>
  <section class="dept-features">
    <h2>专科特色</h2>
  <% for (String feature : departmentData.getFeatures().split("\n")) { %>
    <p><%= feature %></p>
  <% } %>
  </section>
  <section class="dept-research">
    <h2>科研教学</h2>
  <% for (String research : departmentData.getResearch().split("\n")) { %>
    <p><%= research %></p>
  <% } %>
  </section>
</article>