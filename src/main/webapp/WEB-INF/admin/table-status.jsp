<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>📊 テーブル状態管理 - レストラン注文システム</title>
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
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
	border-left: 5px solid #667eea;
	padding-left: 15px;
}

.table-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
	gap: 20px;
}

.table-card {
	border: 3px solid #4CAF50;
	border-radius: 10px;
	padding: 20px;
	transition: all 0.3s;
	background: white;
}

.table-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.table-number {
	font-size: 2em;
	font-weight: bold;
	color: #667eea;
	margin-bottom: 15px;
	text-align: center;
}

.table-status {
	display: inline-block;
	padding: 5px 15px;
	border-radius: 20px;
	font-weight: bold;
	margin-bottom: 15px;
}

.status-occupied {
	background: #4CAF50;
	color: white;
}

.status-empty {
	background: #f0f0f0;
	color: #999;
}

.table-info {
	margin-top: 15px;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 10px 0;
	border-bottom: 1px solid #eee;
}

.info-row:last-child {
	border-bottom: none;
}

.info-label {
	color: #666;
}

.info-value {
	font-weight: bold;
	color: #333;
}

.total-amount {
	font-size: 1.3em;
	color: #4CAF50;
}

.visit-id {
	font-size: 0.85em;
	color: #999;
	word-break: break-all;
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

.summary-cards {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	gap: 20px;
	margin-bottom: 30px;
}

.summary-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 25px;
	border-radius: 10px;
	text-align: center;
}

.summary-label {
	font-size: 1em;
	opacity: 0.9;
	margin-bottom: 10px;
}

.summary-value {
	font-size: 2.5em;
	font-weight: bold;
}
</style>
<script>
	// 自動リフレッシュ（15秒ごと）
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
					<div class="stat-value">${occupiedCount}</div>
				</div>
				<div class="stat-item">
					<div class="stat-label">売上合計</div>
					<div class="stat-value">
						¥
						<fmt:formatNumber value="${totalSales}" pattern="#,###" />
					</div>
				</div>
			</div>

			<div class="nav-links">
				<a href="${pageContext.request.contextPath}/admin/kitchen">🔪
					キッチン画面</a> <a href="${pageContext.request.contextPath}/admin/hall">🚶
					ホール画面</a> <a href="${pageContext.request.contextPath}/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<c:choose>
			<c:when test="${empty tableStatusList}">
				<div class="section">
					<div class="empty-state">
						<div class="empty-icon">🪑</div>
						<h2>使用中のテーブルはありません</h2>
						<p>すべてのテーブルが空席です</p>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="section">
					<h2 class="section-title">🍽️ テーブル状態一覧</h2>

					<div class="table-grid">
						<c:forEach var="table" items="${tableStatusList}">
							<div class="table-card">
								<div class="table-number">テーブル ${table.tableNum}</div>

								<div style="text-align: center;">
									<span
										class="table-status ${table.occupied ? 'status-occupied' : 'status-empty'}">
										${table.status} </span>
								</div>

								<div class="table-info">
									<div class="info-row">
										<span class="info-label">来店時刻</span> <span class="info-value">${table.formattedArrivalTime}</span>
									</div>

									<div class="info-row">
										<span class="info-label">滞在時間</span> <span class="info-value">${table.formattedStayTime}</span>
									</div>

									<div class="info-row">
										<span class="info-label">合計金額</span> <span
											class="info-value total-amount">
											${table.formattedTotal} </span>
									</div>

									<div class="info-row">
										<span class="info-label">来店ID</span> <span
											class="info-value visit-id">${table.visitId}</span>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="auto-refresh">🔄 15秒ごとに自動更新</div>
</body>
</html>
