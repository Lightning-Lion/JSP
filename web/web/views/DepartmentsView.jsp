<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
  Map<String, Object> pageData = (Map<String, Object>) request.getAttribute("pageData");
  Map<String, Map<String, Object>> departmentsData = (Map<String, Map<String, Object>>) pageData.get("departmentsData");
%>
<div class="department">
  <div class="department-filter">
    <input type="text" class="input department-filter-input" id="department-filter-input" placeholder="筛选科目或专家...">
  </div>
  <ul class="department-list" id="department-list">
    <% for (Map.Entry<String, Map<String, Object>> departmentEntry : departmentsData.entrySet()) { %>
    <li class="department-item">
      <h2 class="department-name"><a href="/departments/<%= departmentEntry.getKey() %>" class="department-link"><%= departmentEntry.getValue().get("name") %></a></h2>
      <div class="department-doctor">
      <%
        List<Map<String, String>> doctors = (List<Map<String, String>>) departmentEntry.getValue().get("doctors");
        for (Map<String, String> doctor : doctors) {
      %>
        <a href="/doctors/<%= doctor.get("id") %>" class="department-doctor-link"><%= doctor.get("name") %></a>
      <% } %>
      </div>
    </li>
    <% } %>
  </ul>
</div>
<script type="module">
"use strict";
const filterInput = document.getElementById('department-filter-input');
const departmentList = document.getElementById('department-list');
filterInput.addEventListener('input', () => {
  const filterValue = filterInput.value;
  console.log(filterValue);
  if (departmentList) {
    Array.from(departmentList.children).forEach((item) => {
      const itemText = item.textContent || '';
      item.style.display = itemText.includes(filterValue) ? '' : 'none';
    });
  }
});
</script>