<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Dish, java.util.List, java.util.stream.Collectors" %>
<%
    List<Dish> dishes = (List<Dish>) request.getAttribute("dishes");
    if (dishes == null) dishes = new java.util.ArrayList<>();

    List<String> categories = dishes.stream()
                                    .map(Dish::getCategory)
                                    .distinct()
                                    .collect(Collectors.toList());
    String selectedCat = request.getParameter("category");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>メニュー管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menuManagement.css">
</head>
<body>
    <header class="page-header">
        <h1 class="page-title">メニュー管理</h1>
        <div class="header-btn-wrap">
            <button id="topBtn" class="effect-btn" onclick="location.href='${pageContext.request.contextPath}/administration.jsp'">TOPに戻る</button>
        </div>
    </header>

    <main class="main-container">
        <section class="control-panel">
            <form action="${pageContext.request.contextPath}/admin/dish-manage" method="get" class="search-row">
                <div class="keyword-box">
                    <input class="keyword-input" type="text" name="keyword" placeholder="味噌ラーメン など">
                </div>
                <button type="submit" class="effect-btn btn-primary">検索</button>
                <button type="button" class="effect-btn btn-primary" 
                        onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?action=add'">＋新規</button>
            </form>

            <div class="category-row">
                <button class="effect-btn cat-btn <%= (selectedCat == null) ? "active" : "" %>" 
                        onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage'">すべて</button>
                <% for (String cat : categories) { %>
                    <button class="effect-btn cat-btn <%= (cat.equals(selectedCat)) ? "active" : "" %>"
                            onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?category=<%= cat %>'">
                        <%= cat %>
                    </button>
                <% } %>
            </div>
        </section>

        <section class="dish-scroll-area">
            <div class="dish-grid">
                <% for (Dish dish : dishes) { %>
                    <article class="dish-card" onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?action=edit&id=<%= dish.getDishId() %>'">
                        <div class="image-box">
                            <% if (dish.getPhoto() != null && !dish.getPhoto().isEmpty()) { %>
                                <img src="<%= dish.getPhoto() %>" alt="<%= dish.getName() %>">
                            <% } else { %>
                                <span class="no-image">Photo</span>
                            <% } %>
                        </div>
                        <p class="dish-label"><%= dish.getName() %></p>
                    </article>
                <% } %>
            </div>
        </section>
    </main>
</body>
</html>