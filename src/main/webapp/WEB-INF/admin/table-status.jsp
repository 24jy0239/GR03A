<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ page import="model.TableStatus"%>
<%@ page import="model.Order"%>
<%@ page import="model.OrderItem"%>
<%@ page import="servlet.TableStatusServlet.OrderDetailsInfo"%>

<%
@SuppressWarnings("unchecked")
List<TableStatus> tableStatusList = (List<TableStatus>) request.getAttribute("tableStatusList");
@SuppressWarnings("unchecked")
Map<String, OrderDetailsInfo> orderDetailsByVisit = (Map<String, OrderDetailsInfo>) request
		.getAttribute("orderDetailsByVisit");

Integer occupiedCount = (Integer) request.getAttribute("occupiedCount");
Integer totalSales = (Integer) request.getAttribute("totalSales");
Integer totalOrders = (Integer) request.getAttribute("totalOrders");

if (occupiedCount == null)
	occupiedCount = 0;
if (totalSales == null)
	totalSales = 0;
if (totalOrders == null)
	totalOrders = 0;
if (tableStatusList == null)
	tableStatusList = new java.util.ArrayList<>();
if (orderDetailsByVisit == null)
	orderDetailsByVisit = new java.util.HashMap<>();

NumberFormat formatter = NumberFormat.getInstance();
DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>テーブル状態管理 - 注文明細</title>
<style>
body {
	font-family: 'Arial', sans-serif;
	margin: 0;
	padding: 0;
	background-color: #E9EBF5;
}

.header {
	background: linear-gradient(135deg, #9c27b0 0%, #6a1b9a 100%);
	color: white;
	padding: 20px;
	text-align: center;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
	margin: 0;
	font-size: 2em;
}

.nav-links {
	display: flex;
	gap: 10px;
	justify-content: center;
	margin-top: 15px;
}

.nav-links a {
	padding: 10px 20px;
	background-color: rgba(255, 255, 255, 0.2);
	color: white;
	text-decoration: none;
	border-radius: 8px;
	font-weight: bold;
	transition: all 0.3s;
}

.nav-links a:hover {
	background-color: rgba(255, 255, 255, 0.3);
}

/* サマリーバー（hall.jspスタイル） */
.summary-bar {
	display: flex;
	justify-content: space-around;
	background-color: white;
	padding: 20px;
	margin: 20px;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.summary-item {
	text-align: center;
	padding: 15px 25px;
	border-radius: 10px;
	min-width: 120px;
}

.summary-item.tables {
	background-color: #e1bee7;
	border: 3px solid #9c27b0;
}

.summary-item.orders {
	background-color: #fff3e0;
	border: 3px solid #ff9800;
}

.summary-item.sales {
	background-color: #e8f5e9;
	border: 3px solid #4caf50;
}

.summary-label {
	font-size: 14px;
	color: #666;
	margin-bottom: 8px;
	font-weight: bold;
}

.summary-count {
	font-size: 2.5em;
	font-weight: bold;
	margin: 0;
}

.summary-item.tables .summary-count {
	color: #9c27b0;
}

.summary-item.orders .summary-count {
	color: #ff9800;
}

.summary-item.sales .summary-count {
	color: #4caf50;
}

.container {
	padding: 0 20px 20px 20px;
}

.tables-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
	gap: 20px;
}

.table-card {
	background: white;
	border-radius: 12px;
	padding: 20px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
}

.table-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.table-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
	padding-bottom: 15px;
	border-bottom: 2px solid #E9EBF5;
}

.table-number {
	font-size: 1.8em;
	font-weight: bold;
	color: #9c27b0;
}

.table-badge {
	background-color: #9c27b0;
	color: white;
	padding: 6px 16px;
	border-radius: 20px;
	font-weight: bold;
	font-size: 14px;
}

.table-info {
	margin-bottom: 15px;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 8px 0;
	border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
	border-bottom: none;
}

.info-label {
	color: #666;
	font-size: 14px;
}

.info-value {
	font-weight: bold;
	color: #333;
}

.info-value.revenue {
	color: #4caf50;
	font-size: 1.2em;
}

.order-section {
	margin-top: 20px;
	padding-top: 15px;
	border-top: 2px solid #E9EBF5;
}

.section-title {
	font-size: 1.1em;
	font-weight: bold;
	color: #333;
	margin-bottom: 12px;
	display: flex;
	align-items: center;
	gap: 8px;
}

.order-item {
	display: flex;
	align-items: center;
	padding: 10px;
	margin: 8px 0;
	border-radius: 8px;
	border-left: 4px solid #ccc;
	background-color: #f9f9f9;
	transition: background-color 0.2s;
}

.order-item:hover {
	background-color: #f0f0f0;
}

/* ステータスごとのボーダー色（hall.jspと同じ） */
.order-item.status-0 {
	border-left-color: #2196f3;
	background-color: #e3f2fd;
}

.order-item.status-1 {
	border-left-color: #ff9800;
	background-color: #fff3e0;
}

.order-item.status-2 {
	border-left-color: #4caf50;
	background-color: #e8f5e9;
}

.order-item.status-3 {
	border-left-color: #9e9e9e;
	background-color: #f5f5f5;
}

.status-badge {
	padding: 4px 10px;
	border-radius: 6px;
	font-size: 12px;
	font-weight: bold;
	margin-right: 10px;
	min-width: 55px;
	text-align: center;
}

.status-badge.status-0 {
	background-color: #2196f3;
	color: white;
}

.status-badge.status-1 {
	background-color: #ff9800;
	color: white;
}

.status-badge.status-2 {
	background-color: #4caf50;
	color: white;
}

.status-badge.status-3 {
	background-color: #9e9e9e;
	color: white;
}

.dish-info {
	flex-grow: 1;
	margin-right: 10px;
}

.dish-name {
	font-weight: bold;
	color: #333;
	margin-bottom: 3px;
}

.order-time {
	font-size: 0.85em;
	color: #999;
}

.quantity {
	font-size: 1.1em;
	font-weight: bold;
	color: #666;
	margin-right: 10px;
}

.empty-state {
	text-align: center;
	padding: 60px 20px;
	background: white;
	border-radius: 12px;
	color: #999;
}

.empty-state h2 {
	color: #666;
	margin-bottom: 10px;
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

@media ( max-width : 768px) {
	.tables-grid {
		grid-template-columns: 1fr;
	}
	.summary-bar {
		flex-direction: column;
		gap: 15px;
	}
}
</style>
</head>

<body>
	<div class="header">
		<h1>📊 テーブル状態管理 - 注文明細</h1>
		<div class="nav-links">
			<a href="<%=request.getContextPath()%>/admin/kitchen">🔪 キッチン</a> <a
				href="<%=request.getContextPath()%>/admin/hall">🚶 ホール</a> <a
				href="<%=request.getContextPath()%>/admin">🏠 管理トップ</a>
		</div>
	</div>

	<!-- サマリーバー -->
	<div class="summary-bar">
		<div class="summary-item tables">
			<div class="summary-label">🍽️ 使用中テーブル</div>
			<div class="summary-count"><%=occupiedCount%></div>
		</div>
		<div class="summary-item orders">
			<div class="summary-label">📋 総注文数</div>
			<div class="summary-count"><%=totalOrders%></div>
		</div>
		<div class="summary-item sales">
			<div class="summary-label">💰 合計売上</div>
			<div class="summary-count">
				¥<%=formatter.format(totalSales)%></div>
		</div>
	</div>

	<div class="container">
		<%
		if (tableStatusList.isEmpty() || occupiedCount == 0) {
		%>
		<div class="empty-state">
			<div style="font-size: 5em; margin-bottom: 20px;">🍽️</div>
			<h2>現在、使用中のテーブルはありません</h2>
			<p>テーブルが使用されると、ここに表示されます</p>
		</div>
		<%
		} else {
		%>
		<div class="tables-grid">
			<%
			for (TableStatus table : tableStatusList) {
				if (!table.isOccupied())
					continue; // 使用中のみ表示

				OrderDetailsInfo detailsInfo = orderDetailsByVisit.get(table.getVisitId());
			%>
			<div class="table-card">
				<!-- テーブルヘッダー -->
				<div class="table-header">
					<div class="table-number">
						🍽️ テーブル
						<%=table.getTableNum()%></div>
					<div class="table-badge">使用中</div>
				</div>

				<!-- テーブル情報 -->
				<div class="table-info">
					<div class="info-row">
						<span class="info-label">来店時刻</span> <span class="info-value"><%=table.getFormattedArrivalTime()%></span>
					</div>
					<div class="info-row">
						<span class="info-label">滞在時間</span> <span class="info-value"><%=table.getFormattedStayTime()%></span>
					</div>
					<div class="info-row">
						<span class="info-label">売上</span> <span
							class="info-value revenue">¥<%=formatter.format(table.getTotalAmount())%></span>
					</div>
				</div>

				<!-- 注文明細セクション -->
				<%
				if (detailsInfo != null && detailsInfo.orders != null && !detailsInfo.orders.isEmpty()) {
				%>
				<div class="order-section">
					<div class="section-title">
						📋 注文明細（<%=detailsInfo.totalItems%>件）
					</div>

					<%
					for (Order order : detailsInfo.orders) {
						for (OrderItem item : order.getOrderItems()) {
							int status = item.getItemStatus();
							String statusText = "";
							switch (status) {
						case 0 :
							statusText = "注文";
							break;
						case 1 :
							statusText = "調理中";
							break;
						case 2 :
							statusText = "完了";
							break;
						case 3 :
							statusText = "配膳済";
							break;
							}
					%>
					<div class="order-item status-<%=status%>">
						<span class="status-badge status-<%=status%>"><%=statusText%></span>

						<div class="dish-info">
							<div class="dish-name"><%=item.getDishName()%></div>
							<div class="order-time"><%=order.getOrderTime().format(timeFormatter)%></div>
						</div>

						<div class="quantity">
							×<%=item.getQuantity()%></div>
					</div>
					<%
					}
					}
					%>
				</div>
				<%
				} else {
				%>
				<div class="order-section">
					<div style="text-align: center; color: #999; padding: 20px 0;">
						まだ注文がありません</div>
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

	<!-- 自動更新スクリプト -->
	<script>
		setTimeout(function() {
			location.reload();
		}, 15000);
	</script>
</body>
</html>
