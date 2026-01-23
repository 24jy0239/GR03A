<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.OrderItemWithDetails"%>
<%@ page import="java.util.List"%>
<%
@SuppressWarnings("unchecked")
List<OrderItemWithDetails> hallItems = (List<OrderItemWithDetails>) request.getAttribute("hallItems");
Integer totalCount = (Integer) request.getAttribute("totalCount");

if (totalCount == null)
	totalCount = 0;
if (hallItems == null)
	hallItems = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>🚶 ホール画面 - レストラン注文システム</title>
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
	background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
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

.section {
	background: white;
	padding: 30px;
	border-radius: 10px;
	margin-bottom: 20px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-title {
	font-size: 1.5em;
	margin-bottom: 20px;
	color: #333;
	border-left: 5px solid #4CAF50;
	padding-left: 15px;
}

.table-group {
	margin-bottom: 30px;
}

.table-header {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 15px 20px;
	border-radius: 10px 10px 0 0;
	font-size: 1.3em;
	font-weight: bold;
}

.item-list {
	border: 2px solid #667eea;
	border-top: none;
	border-radius: 0 0 10px 10px;
}

.item-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20px;
	border-bottom: 1px solid #eee;
	transition: background 0.3s;
}

.item-row:last-child {
	border-bottom: none;
}

.item-row:hover {
	background: #f9f9f9;
}

.item-info {
	flex: 1;
}

.dish-name {
	font-size: 1.3em;
	font-weight: bold;
	color: #333;
	margin-bottom: 5px;
}

.quantity {
	font-size: 1.2em;
	color: #4CAF50;
	font-weight: bold;
}

.time-info {
	display: flex;
	gap: 20px;
	margin-top: 10px;
	font-size: 0.95em;
	color: #666;
}

.elapsed-time {
	font-weight: bold;
}

.elapsed-time.warning {
	color: #ffc107;
}

.elapsed-time.urgent {
	color: #ff5252;
}

.item-actions {
	margin-left: 20px;
}

.btn {
	padding: 15px 30px;
	border: none;
	border-radius: 5px;
	font-size: 1.1em;
	font-weight: bold;
	cursor: pointer;
	transition: all 0.3s;
}

.btn-serve {
	background: #4CAF50;
	color: white;
}

.btn-serve:hover {
	background: #45a049;
	transform: translateY(-2px);
	box-shadow: 0 5px 15px rgba(76, 175, 80, 0.4);
}

.empty-state {
	text-align: center;
	padding: 60px 20px;
	color: #999;
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
			<div class="title">🚶 ホール画面</div>

			<div class="stats">
				<div class="stat-item">
					<div class="stat-label">提供待ち</div>
					<div class="stat-value"><%=totalCount%></div>
				</div>
			</div>

			<div class="nav-links">
				<a href="<%=request.getContextPath()%>/admin/kitchen">🔪
					キッチン画面</a> <a href="<%=request.getContextPath()%>/admin/table-status">📊
					テーブル状態</a> <a href="<%=request.getContextPath()%>/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<%
		if (hallItems.isEmpty()) {
		%>
		<div class="section">
			<div class="empty-state">
				<div class="empty-icon">✅</div>
				<h2>提供待ちの料理はありません</h2>
				<p>すべて配膳完了しています</p>
			</div>
		</div>
		<%
		} else {
		%>
		<div class="section">
			<h2 class="section-title">🍽️ 提供待ちの料理</h2>

			<%
			int currentTable = -1;
			for (int i = 0; i < hallItems.size(); i++) {
				OrderItemWithDetails item = hallItems.get(i);

				if (currentTable != item.getTableNum()) {
					if (currentTable != -1) {
			%>
		</div>
	</div>
	<%
	}
	currentTable = item.getTableNum();
	%>
	<div class="table-group">
		<div class="table-header">
			🍽️ テーブル
			<%=currentTable%>
			番
		</div>
		<div class="item-list">
			<%
			}
			%>
			<div class="item-row">
				<div class="item-info">
					<div class="dish-name"><%=item.getDishName()%></div>
					<div class="quantity">
						×
						<%=item.getQuantity()%></div>

					<div class="time-info">
						<span>注文時刻: <%=item.getFormattedOrderTime()%></span> <span
							class="elapsed-time <%=item.getPriority()%>"> 経過: <%=item.getElapsedTimeText()%>
						</span>
					</div>
				</div>

				<div class="item-actions">
					<form action="<%=request.getContextPath()%>/admin/hall"
						method="post">
						<input type="hidden" name="action" value="serve"> <input
							type="hidden" name="orderItemId"
							value="<%=item.getOrderItemId()%>">
						<button type="submit" class="btn btn-serve">✅ 配膳完了</button>
					</form>
				</div>
			</div>
			<%
			if (i == hallItems.size() - 1) {
			%>
		</div>
	</div>
	<%
	}
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
