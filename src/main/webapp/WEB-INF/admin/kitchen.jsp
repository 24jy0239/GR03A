<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
	background: linear-gradient(135deg, #FF6B6B 0%, #C44569 100%);
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
	border-left: 5px solid #FF6B6B;
	padding-left: 15px;
}

.order-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 20px;
}

.order-card {
	border: 2px solid #ddd;
	border-radius: 10px;
	padding: 20px;
	transition: all 0.3s;
	position: relative;
}

.order-card.status-0 {
	background: #fff3cd;
	border-color: #ffc107;
}

.order-card.status-1 {
	background: #d1ecf1;
	border-color: #17a2b8;
}

.order-card.priority-urgent {
	border-color: #ff5252;
	border-width: 3px;
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


rgba
(


255
,
82
,
82
,
0


)
;


}
}
.order-card.priority-warning {
	border-color: #ffc107;
	border-width: 3px;
}

.table-badge {
	display: inline-block;
	background: #667eea;
	color: white;
	padding: 5px 15px;
	border-radius: 20px;
	font-weight: bold;
	font-size: 1.1em;
	margin-bottom: 10px;
}

.dish-name {
	font-size: 1.3em;
	font-weight: bold;
	color: #333;
	margin: 10px 0;
}

.quantity {
	font-size: 1.5em;
	color: #FF6B6B;
	font-weight: bold;
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
}

.time-value {
	font-size: 1.1em;
	font-weight: bold;
	color: #333;
}

.elapsed-time {
	font-size: 1.2em;
	font-weight: bold;
	margin-top: 5px;
}

.elapsed-time.urgent {
	color: #ff5252;
}

.elapsed-time.warning {
	color: #ffc107;
}

.elapsed-time.normal {
	color: #4CAF50;
}

.action-buttons {
	display: flex;
	gap: 10px;
	margin-top: 15px;
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
	background: #17a2b8;
	color: white;
}

.btn-start:hover {
	background: #138496;
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
	// 自動リフレッシュ（10秒ごと）
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
					<div class="stat-label">注文</div>
					<div class="stat-value">${orderedCount}</div>
				</div>
				<div class="stat-item">
					<div class="stat-label">調理中</div>
					<div class="stat-value">${cookingCount}</div>
				</div>
				<div class="stat-item">
					<div class="stat-label">合計</div>
					<div class="stat-value">${totalCount}</div>
				</div>
			</div>

			<div class="nav-links">
				<a href="${pageContext.request.contextPath}/admin/hall">🚶 ホール画面</a>
				<a href="${pageContext.request.contextPath}/admin/table-status">📊
					テーブル状態</a> <a href="${pageContext.request.contextPath}/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<c:choose>
			<c:when test="${empty kitchenItems}">
				<div class="section">
					<div class="empty-state">
						<div class="empty-icon">✅</div>
						<h2>すべての注文が完了しています</h2>
						<p>新しい注文をお待ちしています...</p>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="section">
					<h2 class="section-title">📋 未完了の注文</h2>

					<div class="order-grid">
						<c:forEach var="item" items="${kitchenItems}">
							<div
								class="order-card status-${item.itemStatus} priority-${item.priority}">
								<div class="table-badge">テーブル ${item.tableNum} 番</div>

								<div class="dish-name">${item.dishName}</div>

								<div class="quantity">× ${item.quantity}</div>

								<div class="time-info">
									<div class="time-label">注文時刻</div>
									<div class="time-value">${item.formattedOrderTime}</div>

									<div class="elapsed-time ${item.priority}">経過:
										${item.elapsedTimeText}</div>
								</div>

								<div class="action-buttons">
									<c:choose>
										<c:when test="${item.itemStatus == 0}">
											<form
												action="${pageContext.request.contextPath}/admin/kitchen"
												method="post" style="flex: 1;">
												<input type="hidden" name="action" value="start"> <input
													type="hidden" name="orderItemId"
													value="${item.orderItemId}">
												<button type="submit" class="btn btn-start">🔥 調理開始
												</button>
											</form>
										</c:when>
										<c:when test="${item.itemStatus == 1}">
											<form
												action="${pageContext.request.contextPath}/admin/kitchen"
												method="post" style="flex: 1;">
												<input type="hidden" name="action" value="complete">
												<input type="hidden" name="orderItemId"
													value="${item.orderItemId}">
												<button type="submit" class="btn btn-complete">✅
													調理完了</button>
											</form>
										</c:when>
									</c:choose>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="auto-refresh">🔄 10秒ごとに自動更新</div>
</body>
</html>
