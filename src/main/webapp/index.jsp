<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>レストラン注文システム</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	min-height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 20px;
}

.container {
	background: white;
	padding: 60px 40px;
	border-radius: 20px;
	box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
	max-width: 500px;
	width: 100%;
	text-align: center;
}

h1 {
	color: #333;
	margin-bottom: 10px;
	font-size: 2.5em;
}

.subtitle {
	color: #666;
	margin-bottom: 40px;
	font-size: 1.1em;
}

.form-group {
	margin-bottom: 30px;
}

label {
	display: block;
	color: #555;
	margin-bottom: 10px;
	font-size: 1.1em;
	font-weight: bold;
}

input[type="number"] {
	width: 100%;
	padding: 15px;
	font-size: 1.5em;
	border: 2px solid #ddd;
	border-radius: 10px;
	text-align: center;
	transition: border-color 0.3s;
}

input[type="number"]:focus {
	outline: none;
	border-color: #667eea;
}

button {
	width: 100%;
	padding: 18px;
	font-size: 1.3em;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	border: none;
	border-radius: 10px;
	cursor: pointer;
	transition: transform 0.2s, box-shadow 0.2s;
	font-weight: bold;
}

button:hover {
	transform: translateY(-2px);
	box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
}

button:active {
	transform: translateY(0);
}

.admin-links {
	margin-top: 40px;
	padding-top: 30px;
	border-top: 1px solid #eee;
}

.admin-links a {
	display: inline-block;
	margin: 5px 10px;
	padding: 10px 20px;
	background: #f0f0f0;
	color: #666;
	text-decoration: none;
	border-radius: 5px;
	transition: background 0.3s;
}

.admin-links a:hover {
	background: #e0e0e0;
}
</style>
</head>
<body>
	<div class="container">
		<h1>🍜 ようこそ</h1>
		<p class="subtitle">レストラン注文システム</p>

		<form action="${pageContext.request.contextPath}/menu" method="get">
			<div class="form-group">
				<label for="tableNum">テーブル番号を入力してください</label> <input type="number"
					id="tableNum" name="tableNum" min="1" max="50" required autofocus
					placeholder="例: 5">
			</div>

			<button type="submit">メニューを見る 🍽️</button>
		</form>

		<div class="admin-links">
			<p style="color: #999; margin-bottom: 10px;">管理画面</p>
			<a href="${pageContext.request.contextPath}/admin/kitchen">🔪キッチン画面</a> 
			<a href="${pageContext.request.contextPath}/admin/hall">🚶ホール画面</a> 
			<a href="${pageContext.request.contextPath}/admin/table-status">📊テーブル状態</a> 
			<a href="${pageContext.request.contextPath}/admin/dish-manage">🍽️料理管理</a>
		</div>
	</div>
</body>
</html>
