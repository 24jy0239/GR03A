<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>🍽️ 料理マスタ管理 - レストラン注文システム</title>
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

.message {
	background: #4CAF50;
	color: white;
	padding: 15px;
	border-radius: 5px;
	margin-bottom: 20px;
}

.error {
	background: #ff5252;
	color: white;
	padding: 15px;
	border-radius: 5px;
	margin-bottom: 20px;
}

.toolbar {
	background: white;
	padding: 20px;
	border-radius: 10px;
	margin-bottom: 20px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.btn {
	padding: 12px 24px;
	border: none;
	border-radius: 5px;
	font-size: 1em;
	cursor: pointer;
	text-decoration: none;
	display: inline-block;
	transition: all 0.3s;
}

.btn-primary {
	background: #667eea;
	color: white;
}

.btn-primary:hover {
	background: #5568d3;
	transform: translateY(-2px);
}

.btn-success {
	background: #4CAF50;
	color: white;
}

.btn-success:hover {
	background: #45a049;
}

.btn-warning {
	background: #ffc107;
	color: #333;
}

.btn-warning:hover {
	background: #ffb300;
}

.btn-danger {
	background: #ff5252;
	color: white;
}

.btn-danger:hover {
	background: #ff1744;
}

.btn-sm {
	padding: 8px 16px;
	font-size: 0.9em;
}

.dish-table {
	background: white;
	border-radius: 10px;
	overflow: hidden;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

table {
	width: 100%;
	border-collapse: collapse;
}

thead {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
}

th {
	padding: 15px;
	text-align: left;
	font-weight: bold;
}

td {
	padding: 15px;
	border-bottom: 1px solid #eee;
}

tr:hover {
	background: #f9f9f9;
}

.dish-id {
	font-family: monospace;
	color: #666;
}

.dish-name {
	font-weight: bold;
	font-size: 1.1em;
}

.dish-price {
	font-size: 1.2em;
	color: #667eea;
	font-weight: bold;
}

.category-badge {
	display: inline-block;
	padding: 5px 15px;
	background: #e0e0e0;
	border-radius: 20px;
	font-size: 0.9em;
}

.status-badge {
	display: inline-block;
	padding: 5px 15px;
	border-radius: 20px;
	font-weight: bold;
	font-size: 0.9em;
}

.status-available {
	background: #c8e6c9;
	color: #2e7d32;
}

.status-unavailable {
	background: #ffcdd2;
	color: #c62828;
}

.actions {
	display: flex;
	gap: 10px;
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
</style>
</head>
<body>
	<div class="header">
		<div class="header-content">
			<div class="title">🍽️ 料理マスタ管理</div>

			<div class="nav-links">
				<a href="${pageContext.request.contextPath}/admin/kitchen">🔪
					キッチン</a> <a href="${pageContext.request.contextPath}/admin/hall">🚶
					ホール</a> <a href="${pageContext.request.contextPath}/admin/table-status">📊
					テーブル</a> <a href="${pageContext.request.contextPath}/">🏠 トップ</a>
			</div>
		</div>
	</div>

	<div class="container">
		<c:if test="${not empty sessionScope.message}">
			<div class="message">✅ ${sessionScope.message}</div>
			<c:remove var="message" scope="session" />
		</c:if>

		<c:if test="${not empty sessionScope.error}">
			<div class="error">❌ ${sessionScope.error}</div>
			<c:remove var="error" scope="session" />
		</c:if>

		<div class="toolbar">
			<h2>料理一覧（全${dishes.size()}件）</h2>
			<a
				href="${pageContext.request.contextPath}/admin/dish-manage?action=add"
				class="btn btn-primary"> ➕ 新規追加 </a>
		</div>

		<c:choose>
			<c:when test="${empty dishes}">
				<div class="dish-table">
					<div class="empty-state">
						<div class="empty-icon">🍽️</div>
						<h2>料理が登録されていません</h2>
						<p>「新規追加」ボタンから料理を登録してください</p>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="dish-table">
					<table>
						<thead>
							<tr>
								<th>料理ID</th>
								<th>料理名</th>
								<th>価格</th>
								<th>カテゴリ</th>
								<th>状態</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="dish" items="${dishes}">
								<tr>
									<td class="dish-id">${dish.dishId}</td>
									<td class="dish-name">${dish.name}</td>
									<td class="dish-price">¥<fmt:formatNumber
											value="${dish.price}" pattern="#,###" /></td>
									<td><span class="category-badge">${dish.category}</span></td>
									<td><span
										class="status-badge ${dish.available ? 'status-available' : 'status-unavailable'}">
											${dish.available ? '有効' : '無効'} </span></td>
									<td>
										<div class="actions">
											<a
												href="${pageContext.request.contextPath}/admin/dish-manage?action=edit&id=${dish.dishId}"
												class="btn btn-primary btn-sm"> ✏️ 編集 </a>

											<form
												action="${pageContext.request.contextPath}/admin/dish-manage"
												method="post" style="display: inline;">
												<input type="hidden" name="action" value="toggle"> <input
													type="hidden" name="dishId" value="${dish.dishId}">
												<button type="submit" class="btn btn-warning btn-sm">
													${dish.available ? '🚫 無効化' : '✅ 有効化'}</button>
											</form>

											<form
												action="${pageContext.request.contextPath}/admin/dish-manage"
												method="post" style="display: inline;"
												onsubmit="return confirm('本当に削除しますか？');">
												<input type="hidden" name="action" value="delete"> <input
													type="hidden" name="dishId" value="${dish.dishId}">
												<button type="submit" class="btn btn-danger btn-sm">
													🗑️ 削除</button>
											</form>
										</div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>
