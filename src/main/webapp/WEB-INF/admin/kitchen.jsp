<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.OrderItemWithDetails"%>
<%@ page import="java.util.List"%>
<%
@SuppressWarnings("unchecked")
List<OrderItemWithDetails> kitchenItems = (List<OrderItemWithDetails>) request.getAttribute("kitchenItems");
Integer pendingCount = (Integer) request.getAttribute("pendingCount");
Integer cookingCount = (Integer) request.getAttribute("cookingCount");

if (pendingCount == null)
	pendingCount = 0;
if (cookingCount == null)
	cookingCount = 0;
if (kitchenItems == null)
	kitchenItems = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>🔪 キッチン画面 - レストラン注文システム</title>
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
	background: linear-gradient(135deg, #ff5252 0%, #c62828 100%);
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

.items-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
	gap: 20px;
}

.item-card {
	background: white;
	border-radius: 10px;
	padding: 25px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	transition: all 0.3s;
	border: 3px solid transparent;
}

.item-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.item-card.urgent {
	border-color: #ff5252;
	animation: pulse 2s infinite;
}

@
keyframes pulse { 0%, 100% {
	box-shadow: 0 0 0 0 rgba(255, 82, 82, 0.7);
}

50
%
{
box-shadow
:
0
0
0
10px
rgba(
255
,
82
,
82
,
0
);
}
}
.item-card.warning {
	border-color: #ffc107;
}

.item-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
}

.table-badge {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 8px 15px;
	border-radius: 20px;
	font-weight: bold;
	font-size: 1.1em;
}

.status-badge {
	padding: 5px 15px;
	border-radius: 20px;
	font-size: 0.9em;
	font-weight: bold;
}

.status-pending {
	background: #ffeb3b;
	color: #333;
}

.status-cooking {
	background: #ff9800;
	color: white;
}

.dish-name {
	font-size: 1.5em;
	font-weight: bold;
	color: #333;
	margin: 15px 0;
}

.quantity {
	font-size: 1.8em;
	color: #ff5252;
	font-weight: bold;
	margin: 10px 0;
}

.time-info {
	margin: 15px 0;
	padding: 10px;
	background: #f9f9f9;
	border-radius: 5px;
}

.time-label {
	font-size: 0.9em;
	color: #666;
	margin-right: 10px;
}

.elapsed-time {
	font-size: 1.2em;
	font-weight: bold;
}

.elapsed-time.normal {
	color: #4CAF50;
}

.elapsed-time.warning {
	color: #ffc107;
}

.elapsed-time.urgent {
	color: #ff5252;
}

.item-actions {
	display: flex;
	gap: 10px;
	margin-top: 20px;
}

.btn {
	flex: 1;
	padding: 12px;
	border: none;
	border-radius: 5px;
	font-size: 1em;
	font-weight: bold;
	cursor: pointer;
	transition: all 0.3s;
}

.btn-start {
	background: #ff9800;
	color: white;
}

.btn-start:hover {
	background: #f57c00;
	transform: translateY(-2px);
}

.btn-complete {
	background: #4CAF50;
	color: white;
}

.btn-complete:hover {
	background: #45a049;
	transform: translateY(-2px);
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
	}, 10000);
</script>
</head>
<body>
	<div class="header">
		<div class="header-content">
			<div class="title">🔪 キッチン画面</div>

			<div class="stats">
				<div class="stat-item">
					<div class="stat-label">未着手</div>
					<div class="stat-value"><%=pendingCount%></div>
				</div>
				<div class="stat-item">
					<div class="stat-label">調理中</div>
					<div class="stat-value"><%=cookingCount%></div>
				</div>
			</div>

			<div class="nav-links">
				<a href="<%=request.getContextPath()%>/admin/hall">🚶 ホール画面</a> <a
					href="<%=request.getContextPath()%>/admin/table-status">📊
					テーブル状態</a> <a href="<%=request.getContextPath()%>/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<%
		if (kitchenItems.isEmpty()) {
		%>
		<div class="empty-state">
			<div class="empty-icon">✅</div>
			<h2>未完了の注文はありません</h2>
			<p>すべて調理完了しています</p>
		</div>
		<%
		} else {
		%>
		<div class="items-grid">
			<%
			for (OrderItemWithDetails item : kitchenItems) {
			%>
			<div class="item-card <%=item.getPriority()%>">
				<div class="item-header">
					<div class="table-badge">
						🍽️ テーブル
						<%=item.getTableNum()%>
					</div>
					<div
						class="status-badge status-<%=item.getItemStatus() == 0 ? "pending" : "cooking"%>">
						<%=item.getItemStatus() == 0 ? "未着手" : "調理中"%>
					</div>
				</div>

				<div class="dish-name"><%=item.getDishName()%></div>
				<div class="quantity">
					×
					<%=item.getQuantity()%></div>

				<div class="time-info">
					<span class="time-label">注文時刻:</span>
					<%=item.getFormattedOrderTime()%>
					<br> <span class="time-label">経過時間:</span> <span
						class="elapsed-time <%=item.getPriority()%>"> <%=item.getElapsedTimeText()%>
					</span>
				</div>

				<div class="item-actions">
					<%
					if (item.getItemStatus() == 0) {
					%>
					<form action="<%=request.getContextPath()%>/admin/kitchen"
						method="post" style="flex: 1;">
						<input type="hidden" name="action" value="start"> <input
							type="hidden" name="orderItemId"
							value="<%=item.getOrderItemId()%>">
						<button type="submit" class="btn btn-start">🔥 調理開始</button>
					</form>
					<%
					} else {
					%>
					<form action="<%=request.getContextPath()%>/admin/kitchen"
						method="post" style="flex: 1;">
						<input type="hidden" name="action" value="complete"> <input
							type="hidden" name="orderItemId"
							value="<%=item.getOrderItemId()%>">
						<button type="submit" class="btn btn-complete">✅ 調理完了</button>
					</form>
					<%
					}
					%>
				</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>
	</div>

	<div class="auto-refresh">🔄 10秒ごとに自動更新</div>
</body>
</html>
