<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>管理者画面</title>
<link rel="stylesheet" href="./css/general.css">
<link rel="stylesheet" href="./css/administration.css">
</head>
<body>
	<h1>管理者画面</h1>
	<div class="menu-grid">
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/dish-manage'">メニュー管理</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/kitchen'">キッチン画面</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/hall'">ホール画面</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/sales.jsp'">売上分析</button>
	</div>
</body>
</html>