<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>キッチン管理</title>
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

.kitchen-items {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 20px;
}

.kitchen-card {
	background: white;
	border-radius: 12px;
	padding: 20px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
}

.kitchen-card:hover {
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
	background-color: #4472C4;
	color: white;
	padding: 5px 15px;
	border-radius: 20px;
	font-weight: bold;
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

.status-badge {
	display: inline-block;
	padding: 5px 12px;
	border-radius: 5px;
	font-size: 12px;
	font-weight: bold;
}

.status-0 {
	background-color: #fff3cd;
	color: #856404;
}

.status-1 {
	background-color: #d1ecf1;
	color: #0c5460;
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

.btn-start {
	background-color: #28a745;
	color: white;
}

.btn-start:hover {
	background-color: #218838;
}

.btn-finish {
	background-color: #007bff;
	color: white;
}

.btn-finish:hover {
	background-color: #0056b3;
}

.btn-start:disabled, .btn-finish:disabled {
	background-color: #ccc;
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
</style>
</head>
<body>
	<div class="header">
		<h1>👨‍🍳 キッチン管理画面</h1>
		<div class="button-group">
			<button class="btn btn-secondary" onclick="location.reload()">🔄
				更新</button>
			<a href="${pageContext.request.contextPath}/admin"
				class="btn btn-secondary">🏠 管理トップへ</a>
		</div>
	</div>

	<c:choose>
		<c:when test="${empty kitchenItems}">
			<div class="empty-message">
				<h2>📭 注文はありません</h2>
				<p>新しい注文が入るまでお待ちください</p>
			</div>
		</c:when>
		<c:otherwise>
			<div class="kitchen-items">
				<c:forEach var="item" items="${kitchenItems}">
					<div class="kitchen-card">
						<div class="card-header">
							<div class="dish-name">${item.dishName}</div>
							<div class="table-badge">テーブル ${item.tableNum}</div>
						</div>

						<div class="card-body">
							<div class="info-row">
								<span class="info-label">数量</span> <span class="info-value">${item.quantity}</span>
							</div>
							<div class="info-row">
								<span class="info-label">注文時刻</span> <span class="info-value">${item.orderTime}</span>
							</div>
							<div class="info-row">
								<span class="info-label">状態</span> <span
									class="status-badge status-${item.itemStatus}"> <c:choose>
										<c:when test="${item.itemStatus == 0}">調理待ち</c:when>
										<c:when test="${item.itemStatus == 1}">調理中</c:when>
									</c:choose>
								</span>
							</div>
						</div>

						<!-- ★★★ 重要: form action と orderItemId の渡し方 ★★★ -->
						<div class="card-actions">
							<c:if test="${item.itemStatus == 0}">
								<form action="${pageContext.request.contextPath}/admin/kitchen"
									method="post" style="flex: 1;">
									<input type="hidden" name="action" value="start"> <input
										type="hidden" name="orderItemId" value="${item.orderItemId}">
									<button type="submit" class="action-btn btn-start">調理開始</button>
								</form>
							</c:if>

							<c:if test="${item.itemStatus == 1}">
								<form action="${pageContext.request.contextPath}/admin/kitchen"
									method="post" style="flex: 1;">
									<input type="hidden" name="action" value="finish"> <input
										type="hidden" name="orderItemId" value="${item.orderItemId}">
									<button type="submit" class="action-btn btn-finish">調理完了</button>
								</form>
							</c:if>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>
