<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.TableStatus"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
@SuppressWarnings("unchecked")
List<TableStatus> tables = (List<TableStatus>) request.getAttribute("tables");
Integer inUseCount = (Integer) request.getAttribute("inUseCount");
Integer totalRevenue = (Integer) request.getAttribute("totalRevenue");

if (inUseCount == null)
	inUseCount = 0;
if (totalRevenue == null)
	totalRevenue = 0;
if (tables == null)
	tables = new java.util.ArrayList<>();

NumberFormat formatter = NumberFormat.getInstance();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>📊 テーブル状態 - レストラン注文システム</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
	background: #f5f5f5;
}

.header {
	background: linear-gradient(135deg, #9c27b0 0%, #6a1b9a 100%);
	color: white;
	padding: 20px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	position: sticky;
	top: 0;
	z-index: 100;
}

.header-content {
	max-width: 1400px;
	margin: 0 auto;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.title {
	font-size: 1.8em;
	font-weight: bold;
}

.stats {
	display: flex;
	gap: 30px;
}

.stat-item {
	text-align: center;
}

.stat-label {
	font-size: 0.9em;
	opacity: 0.9;
}

.stat-value {
	font-size: 2em;
	font-weight: bold;
	margin-top: 5px;
}

.nav-links {
	display: flex;
	gap: 15px;
}

.nav-links a {
	color: white;
	text-decoration: none;
	padding: 10px 20px;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 5px;
	transition: background 0.3s;
}

.nav-links a:hover {
	background: rgba(255, 255, 255, 0.3);
}

.container {
	max-width: 1400px;
	margin: 20px auto;
	padding: 0 20px;
}

.tables-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 20px;
}

.table-card {
	background: white;
	border-radius: 10px;
	padding: 25px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	transition: all 0.3s;
	border: 3px solid transparent;
}

.table-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.table-card.in-use {
	border-color: #4CAF50;
}

.table-card.available {
	border-color: #e0e0e0;
	opacity: 0.7;
}

.table-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}

.table-number {
	font-size: 2em;
	font-weight: bold;
	color: #333;
}

.status-badge {
	padding: 8px 15px;
	border-radius: 20px;
	font-size: 0.9em;
	font-weight: bold;
}

.status-badge.in-use {
	background: #c8e6c9;
	color: #2e7d32;
}

.status-badge.available {
	background: #e0e0e0;
	color: #666;
}

.table-info {
	margin: 15px 0;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 10px 0;
	border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
	border-bottom: none;
}

.info-label {
	color: #666;
	font-size: 0.95em;
}

.info-value {
	font-weight: bold;
	color: #333;
}

.revenue {
	font-size: 1.3em;
	color: #4CAF50;
}

.duration {
	font-size: 1.1em;
	color: #666;
}

.empty-state {
	text-align: center;
	padding: 60px 20px;
	color: #999;
	background: white;
	border-radius: 10px;
}

.empty-icon {
	font-size: 5em;
	margin-bottom: 20px;
}

.auto-refresh {
	position: fixed;
	bottom: 20px;
	right: 20px;
	background: white;
	padding: 15px 25px;
	border-radius: 10px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
	font-size: 0.9em;
	color: #666;
}
</style>
<script>
	setTimeout(function() {
		location.reload();
	}, 15000);
</script>
</head>
<body>
	<div class="header">
		<div class="header-content">
			<div class="title">📊 テーブル状態管理</div>

			<div class="stats">
				<div class="stat-item">
					<div class="stat-label">使用中</div>
					<div class="stat-value"><%=inUseCount%></div>
				</div>
				<div class="stat-item">
					<div class="stat-label">本日売上</div>
					<div class="stat-value">
						¥<%=formatter.format(totalRevenue)%></div>
				</div>
			</div>

			<div class="nav-links">
				<a href="<%=request.getContextPath()%>/admin/kitchen">🔪
					キッチン画面</a> <a href="<%=request.getContextPath()%>/admin/hall">🚶
					ホール画面</a> <a href="<%=request.getContextPath()%>/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<%
		if (tables.isEmpty()) {
		%>
		<div class="empty-state">
			<div class="empty-icon">🍽️</div>
			<h2>テーブル情報がありません</h2>
		</div>
		<%
		} else {
		%>
		<div class="tables-grid">
			<%
			for (TableStatus table : tables) {
			%>
			<div
				class="table-card <%=table.isOccupied() ? "in-use" : "available"%>">
				<div class="table-header">
					<div class="table-number">
						🍽️ テーブル
						<%=table.getTableNum()%>
					</div>
					<div
						class="status-badge <%=table.isOccupied() ? "in-use" : "available"%>">
						<%=table.isOccupied() ? "使用中" : "空席"%>
					</div>
				</div>

				<%
				if (table.isOccupied()) {
				%>
				<div class="table-info">
					<div class="info-row">
						<span class="info-label">訪問ID</span> <span class="info-value"><%=table.getVisitId()%></span>
					</div>
					<div class="info-row">
						<span class="info-label">来店時刻</span> <span class="info-value"><%=table.getFormattedArrivalTime()%></span>
					</div>
					<div class="info-row">
						<span class="info-label">滞在時間</span> <span
							class="info-value duration"><%=table.getFormattedStayTime()%></span>
					</div>
					<div class="info-row">
						<span class="info-label">売上</span> <span
							class="info-value revenue">¥<%=formatter.format(table.getTotalAmount())%></span>
					</div>
				</div>
				<%
				} else {
				%>
				<div class="table-info">
					<p style="text-align: center; color: #999; padding: 20px 0;">
						利用可能</p>
				</div>
				<%
				}
				%>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>
	</div>

	<div class="auto-refresh">🔄 15秒ごとに自動更新</div>
</body>
</html>
