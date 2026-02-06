<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ホール管理 - 全進捗表示</title>
<style>
body {
	font-family: 'Arial', sans-serif;
	margin: 0;
	padding: 20px;
	background-color: #E9EBF5;
}

.header {
	text-align: center;
	margin-bottom: 30px;
}

h1 {
	color: #333;
	margin-bottom: 10px;
}

.button-group {
	display: flex;
	gap: 10px;
	justify-content: center;
	margin-bottom: 20px;
}

.btn {
	padding: 10px 20px;
	border: none;
	border-radius: 8px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	transition: all 0.3s;
	text-decoration: none;
	color: white;
	display: inline-block;
}

.btn-primary {
	background-color: #4472C4;
}

.btn-primary:hover {
	background-color: #365a9e;
}

.btn-secondary {
	background-color: #6c757d;
}

.btn-secondary:hover {
	background-color: #5a6268;
}

/* サマリーバー */
.summary-bar {
	display: flex;
	justify-content: space-around;
	background-color: white;
	padding: 20px;
	margin-bottom: 30px;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.summary-item {
	text-align: center;
	padding: 15px 25px;
	border-radius: 10px;
	min-width: 120px;
}

.summary-item.status-0 {
	background-color: #e3f2fd;
	border: 3px solid #2196f3;
}

.summary-item.status-1 {
	background-color: #fff3e0;
	border: 3px solid #ff9800;
}

.summary-item.status-2 {
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

.summary-item.status-0 .summary-count {
	color: #2196f3;
}

.summary-item.status-1 .summary-count {
	color: #ff9800;
}

.summary-item.status-2 .summary-count {
	color: #4caf50;
}

.hall-items {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
	gap: 20px;
}

.hall-card {
	background: white;
	border-radius: 12px;
	padding: 20px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
	border-left: 6px solid #ccc;
}

/* ステータスごとのボーダー色 */
.hall-card.status-0 {
	border-left-color: #2196f3;
}

.hall-card.status-1 {
	border-left-color: #ff9800;
}

.hall-card.status-2 {
	border-left-color: #4caf50;
}

.hall-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
	padding-bottom: 15px;
	border-bottom: 2px solid #E9EBF5;
}

.dish-name {
	font-size: 20px;
	font-weight: bold;
	color: #333;
}

.table-badge {
	background-color: #6c757d;
	color: white;
	padding: 6px 16px;
	border-radius: 20px;
	font-weight: bold;
	font-size: 14px;
}

.card-body {
	margin-bottom: 15px;
}

.info-row {
	display: flex;
	justify-content: space-between;
	margin: 10px 0;
	padding: 8px 0;
}

.info-label {
	color: #666;
	font-size: 14px;
}

.info-value {
	font-weight: bold;
	color: #333;
}

/* ステータスバッジ */
.status-badge {
	display: inline-block;
	padding: 6px 14px;
	border-radius: 6px;
	font-size: 13px;
	font-weight: bold;
}

.status-badge.status-0 {
	background-color: #e3f2fd;
	color: #1976d2;
	border: 1px solid #2196f3;
}

.status-badge.status-1 {
	background-color: #fff3e0;
	color: #f57c00;
	border: 1px solid #ff9800;
}

.status-badge.status-2 {
	background-color: #e8f5e9;
	color: #388e3c;
	border: 1px solid #4caf50;
}

.card-actions {
	display: flex;
	gap: 10px;
}

.action-btn {
	flex: 1;
	padding: 12px;
	border: none;
	border-radius: 8px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	transition: all 0.3s;
}

.action-btn.serve {
	background-color: #28a745;
	color: white;
}

.action-btn.serve:hover {
	background-color: #218838;
}

.action-btn:disabled {
	background-color: #ccc;
	color: #666;
	cursor: not-allowed;
}

.empty-message {
	text-align: center;
	padding: 60px 20px;
	background: white;
	border-radius: 12px;
	color: #999;
}

.empty-message h2 {
	color: #666;
	margin-bottom: 10px;
}

@media ( max-width : 768px) {
	.hall-items {
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
		<h1>🏃 ホール管理画面 - 全進捗表示</h1>
		<div class="button-group">
			<button class="btn btn-secondary" onclick="location.reload()">🔄
				更新</button>
			<a href="${pageContext.request.contextPath}/admin"
				class="btn btn-secondary">🏠 管理トップへ</a>
		</div>
	</div>

	<!-- サマリーバー -->
	<div class="summary-bar">
		<div class="summary-item status-0">
			<div class="summary-label">📋 注文</div>
			<div class="summary-count">${statusCounts[0]}</div>
		</div>
		<div class="summary-item status-1">
			<div class="summary-label">🔥 調理中</div>
			<div class="summary-count">${statusCounts[1]}</div>
		</div>
		<div class="summary-item status-2">
			<div class="summary-label">✅ 配膳待ち</div>
			<div class="summary-count">${statusCounts[2]}</div>
		</div>
	</div>

	<c:choose>
		<c:when test="${empty allProgressItems}">
			<div class="empty-message">
				<h2>📭 現在、注文はありません</h2>
				<p>注文が入ると、ここに表示されます</p>
			</div>
		</c:when>
		<c:otherwise>
			<div class="hall-items">
				<c:forEach var="item" items="${allProgressItems}">
					<div class="hall-card status-${item.itemStatus}">
						<div class="card-header">
							<div class="dish-name">${item.dishName}</div>
							<div class="table-badge">テーブル ${item.tableNum}</div>
						</div>

						<div class="card-body">
							<div class="info-row">
								<span class="info-label">数量</span> <span class="info-value">×${item.quantity}</span>
							</div>
							<div class="info-row">
								<span class="info-label">注文時刻</span> <span class="info-value">${item.formattedOrderTime}</span>
							</div>
							<div class="info-row">
								<span class="info-label">状態</span>
								<c:choose>
									<c:when test="${item.itemStatus == 0}">
										<span class="status-badge status-0">📋 注文</span>
									</c:when>
									<c:when test="${item.itemStatus == 1}">
										<span class="status-badge status-1">🔥 調理中</span>
									</c:when>
									<c:when test="${item.itemStatus == 2}">
										<span class="status-badge status-2">✅ 完了</span>
									</c:when>
								</c:choose>
							</div>
						</div>

						<div class="card-actions">
							<form action="${pageContext.request.contextPath}/admin/hall"
								method="post" style="flex: 1;">
								<input type="hidden" name="action" value="serve"> <input
									type="hidden" name="orderItemId" value="${item.orderItemId}">

								<c:choose>
									<c:when test="${item.itemStatus == 2}">
										<button type="submit" class="action-btn serve">配膳完了</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="action-btn" disabled>配膳待ち</button>
									</c:otherwise>
								</c:choose>
							</form>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:otherwise>
	</c:choose>

	<!-- 自動更新スクリプト -->
	<script>
		// 30秒ごとに自動更新
		setInterval(function() {
			location.reload();
		}, 30000);
	</script>
</body>
</html>
