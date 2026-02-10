<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="model.Dish, java.util.List, java.util.stream.Collectors"%>
<%
// 从 request 获取两个不同的属性
List<Dish> dishes = (List<Dish>) request.getAttribute("dishes");
List<String> fullCategories = (List<String>) request.getAttribute("fullCategories");

if (dishes == null)
	dishes = new java.util.ArrayList<>();
if (fullCategories == null)
	fullCategories = new java.util.ArrayList<>();

String selectedCat = request.getParameter("category");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>メニュー管理</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/menuManagement.css">
</head>
<body>
	<header class="page-header">
		<h1 class="page-title">メニュー管理</h1>
		<div class="header-btn-wrap">
			<button id="topBtn" class="effect-btn"
				onclick="location.href='${pageContext.request.contextPath}/admin'">管理画面へ</button>
		</div>
	</header>

	<main class="main-container">
		<section class="control-panel">
			<form action="${pageContext.request.contextPath}/admin/dish-manage"
				method="get" class="search-row">
				<div class="keyword-box">
					<input class="keyword-input" type="text" name="keyword"
						placeholder="味噌ラーメン など">
				</div>
				<button type="submit" class="effect-btn btn-primary">検索</button>
				<button type="button" class="effect-btn btn-primary"
					onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?action=add'">＋新規</button>
			</form>

			<div class="category-row">
				<button
					class="effect-btn cat-btn <%= (selectedCat == null || "すべて".equals(selectedCat)) ? "active" : "" %>"
					onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage'">すべて</button>

				<%
				for (String cat : fullCategories) {
					if (cat == null)
						continue;
				%>
				<button
					class="effect-btn cat-btn <%= (cat.equals(selectedCat)) ? "active" : "" %>"
					onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?category=<%= java.net.URLEncoder.encode(cat, "UTF-8") %>'">
					<%=cat%>
				</button>
				<%
				}
				%>
			</div>
		</section>

		<section class="dish-scroll-area">
			<div class="dish-grid">
				<%
    for (Dish dish : dishes) {
        // 1. 获取数据库中的文件名 (例如: "ramen01.jpg")
        String fileName = dish.getPhoto(); 
        
        // 2. 拼接固定路径：项目根路径 + /images/ + 文件名
        String imgPath;
        if (fileName != null && !fileName.isEmpty()) {
            imgPath = request.getContextPath() + "/images/" + fileName;
        } else {
            imgPath = null; // 或者设置为一个默认图片的路径
        }
    %>
				<article class="dish-card"
					onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage?action=edit&id=<%= dish.getDishId() %>'">
					<div class="image-box">
						<% if (imgPath != null) { %>
						<img src="<%= imgPath %>" alt="<%= dish.getName() %>">
						<% } else { %>
						<span class="no-image">No Photo</span>
						<% } %>
					</div>
					<p class="dish-label"><%= dish.getName() %></p>
				</article>
				<%
    }
    %>
			</div>
		</section>
	</main>
</body>
</html>