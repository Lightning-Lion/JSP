<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hoarp.web.data.Campus" %>
<%@ page import="com.hoarp.web.data.CampusDetail" %>
<%@ page import="java.util.Objects" %>
<%
  Map<String, Object> pageData = (Map<String, Object>) request.getAttribute("pageData");
  CampusDetail campusData = (CampusDetail) pageData.get("campusData");
  List<Campus> campusesData = (List<Campus>) pageData.get("campusesData");
%>
<article class="campus">
  <h1 class="campus-title" style="background-image: url('/assets/images/<%= campusData.getPhoto() %>');"><%= campusData.getName() %></h1>
  <nav class="campus-nav">
  <% for (Campus item : campusesData) { %>
    <a class="campus-nav-link" href="/campuses/<%= item.getId() %>"><%= item.getName() %></a>
  <% } %>
  </nav>
  <section class="campus-content">
  <% for (String description : campusData.getDescription().split("\n")) { %>
    <p><%= description %></p>
  <% } %>
  <% if (!Objects.equals(campusData.getLocation(), "")) { %>
  <img src="/assets/images/<%= campusData.getLocation() %>" alt="地图">
  <% } %>
  </section>
</article>
