<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%
  Map<String, Object> pageData = (Map<String, Object>) request.getAttribute("pageData");
  String user = (String) pageData.get("user");
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageData.title}</title>
    <meta name="description" content="${pageData.description}">
    <meta name="keywords" content="${pageData.keywords}">
    <link rel="stylesheet" type="text/css" href="/assets/css/style.css">
  </head>
  <body>
    <header class="header">
      <button class="site-nav-toggle" id="site-nav-toggle" type="button" title="最大/小化菜单"><jsp:include page="/components/icon/IconMenu.jsp" /></button>
      <img class="site-logo" src="/assets/images/logo.png" alt="Logo">
      <div class="header-toolbar toolbar">
      <% if (user != "") { %>
        <a class="toolbar-item toolbar-item-profiles" href="/profiles"><%= user %></a>
        <a class="toolbar-item toolbar-item-logout" href="/logout">退出</a>
      <% } else { %>
        <a class="toolbar-item toolbar-item-login" href="/login">登录</a>
      <% } %>
      </div>
    </header>

    <aside class="sidebar" id="sidebar">
      <nav class="site-nav">
        <ul class="menu">
          <li class="menu-item${pageData.route == "/" ? " menu-item-active" : ""}"><a class="menu-item-home" href="/"><jsp:include page="/components/icon/IconHome.jsp" /><span>首页</span></a></li>
          <li class="menu-item${pageData.route == "/campuses" ? " menu-item-active" : ""}"><a class="menu-item-campuses" href="/campuses/1"><jsp:include page="/components/icon/IconCampus.jsp" /><span>院区介绍</span></a></li>
          <li class="menu-item${pageData.route == "/departments" ? " menu-item-active" : ""}"><a class="menu-item-departments" href="/departments"><jsp:include page="/components/icon/IconDoctor.jsp" /><span>科室医生</span></a></li>
        </ul>
        <ul class="menu">
          <li class="menu-item${pageData.route == "/appointment" ? " menu-item-active" : ""}"><a class="menu-item-appointment" href="/appointment"><jsp:include page="/components/icon/IconAppointment.jsp" /><span>预约挂号</span></a></li>
          <li class="menu-item${pageData.route == "/history" ? " menu-item-active" : ""}"><a class="menu-item-history" href="/history"><jsp:include page="/components/icon/IconHistory.jsp" /><span>挂号历史</span></a></li>
        </ul>
        <ul class="menu">
          <li class="menu-item${pageData.route == "/profiles" ? " menu-item-active" : ""}"><a class="menu-item-profiles" href="/profiles"><jsp:include page="/components/icon/IconProfiles.jsp" /><span>信息管理</span></a></li>
          </ul>
        <ul class="menu">
          <li class="menu-item${pageData.route == "/feedback" ? " menu-item-active" : ""}"><a class="menu-item-feedback" href="/feedback"><jsp:include page="/components/icon/IconFeedback.jsp" /><span>联系我们</span></a></li>
        </ul>
      </nav>
    </aside>

    <main class="site-body main" id="site-body">
      <jsp:include page="/views/${pageData.view}.jsp">
        <jsp:param name="pageData" value="${pageData}" />
      </jsp:include>
    </main>

    <script type="module">
      "use strict";
      const isDesktopView = () => window.innerWidth >= 992;
      let showMenu = isDesktopView();
      let userWant = true;
      const toggle = document.getElementById('site-nav-toggle');
      const sidebar = document.getElementById('sidebar');
      const siteBody = document.getElementById('site-body');
      const changeMenu = () => {
        if (showMenu) {
          sidebar?.classList.add('sidebar-active');
          siteBody?.classList.remove('site-body-full');
        }
        else {
          sidebar?.classList.remove('sidebar-active');
          siteBody?.classList.add('site-body-full');
        }
      };
      window.addEventListener('resize', () => {
        showMenu = userWant && isDesktopView();
        changeMenu();
      });
      toggle?.addEventListener('click', () => {
        showMenu = !showMenu;
        userWant = showMenu;
        changeMenu();
      });
      changeMenu();
    </script>
  </body>
</html>
