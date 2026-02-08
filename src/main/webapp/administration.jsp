<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>管理者画面</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/general.css">
<style>
body {
	font-family: 'Arial', sans-serif;
	background-color: #E9EBF5;
	margin: 0;
	padding: 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 100vh;
}

h1 {
	font-size: 3em;
	color: #333;
	margin-bottom: 40px;
	text-align: center;
}

.menu-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20px;
	max-width: 800px;
	width: 100%;
	padding: 20px;
}

.menu-grid button {
	height: 120px;
	font-size: 1.5em;
	background-color: #4472c4;
	color: white;
	border: none;
	border-radius: 10px;
	cursor: pointer;
	font-weight: bold;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
	transition: all 0.3s;
}

.menu-grid button:hover {
	background-color: #5583d5;
	transform: translateY(-5px);
	box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
}

.menu-grid button:active {
	transform: translateY(0);
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

@media ( max-width : 768px) {
	.menu-grid {
		grid-template-columns: 1fr;
	}
	h1 {
		font-size: 2em;
	}
	.menu-grid button {
		height: 100px;
		font-size: 1.2em;
	}
}
</style>
</head>
<body>
	<h1>🏢 管理者画面</h1>
	<div class="menu-grid">
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/kitchen'">
			🔪 キッチン画面</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/hall'">
			🚶 ホール画面</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/admin/table-status'">
			📊 テーブル状態</button>
		<button
			onclick="location.href='${pageContext.request.contextPath}/sales.jsp'">
			💰 売上分析</button>
	</div>
</body>
</html>
