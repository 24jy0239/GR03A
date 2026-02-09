<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>管理画面</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/general.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/administration.css">
</head>
<body>
	<div class="header">
		<h1>🏠 管理画面</h1>
	</div>

	<div class="container">
		<div class="button-grid">
			<!-- キッチン画面 -->
			<a href="<%=request.getContextPath()%>/admin/kitchen"
				class="admin-button kitchen">
				<div class="button-icon">🔪</div>
				<div class="button-title">キッチン画面</div>
				<div class="button-description">調理待ち一覧・調理管理</div>
			</a>

			<!-- ホール画面 -->
			<a href="<%=request.getContextPath()%>/admin/hall"
				class="admin-button hall">
				<div class="button-icon">🚶</div>
				<div class="button-title">ホール画面</div>
				<div class="button-description">配膳待ち一覧・配膳管理</div>
			</a>

			<!-- テーブル状態 -->
			<a href="<%=request.getContextPath()%>/admin/table-status"
				class="admin-button table-status">
				<div class="button-icon">📊</div>
				<div class="button-title">テーブル状態</div>
				<div class="button-description">使用状況・注文明細確認</div>
			</a>

			<!-- メニュー管理（既存のDishManageServlet使用）-->
			<a href="<%=request.getContextPath()%>/admin/dish-manage"
				class="admin-button menu">
				<div class="button-icon">📋</div>
				<div class="button-title">メニュー管理</div>
				<div class="button-description">メニュー編集・価格設定</div>
			</a>

			<!-- 売上分析 -->
			<a href="<%=request.getContextPath()%>/admin/sales"
				class="admin-button analytics">
				<div class="button-icon">💰</div>
				<div class="button-title">売上分析</div>
				<div class="button-description">売上統計・レポート</div>
			</a>
		</div>
	</div>

	<div class="footer">
		<p>© 2026 Restaurant Management System - GR03A</p>
	</div>
</body>
</html>
