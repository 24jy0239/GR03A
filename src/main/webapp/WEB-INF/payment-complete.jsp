<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%
Integer totalAmount = (Integer) session.getAttribute("paymentAmount");

if (totalAmount == null)
	totalAmount = 0;

NumberFormat formatter = NumberFormat.getInstance();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>会計完了 - レストラン注文システム</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
	background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20px;
}

.container {
	background: white;
	padding: 60px 40px;
	border-radius: 20px;
	max-width: 500px;
	width: 100%;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
	text-align: center;
}

.success-icon {
	font-size: 5em;
	margin-bottom: 20px;
	animation: bounce 1s;
}

@
keyframes bounce { 0%, 100% {
	transform: translateY(0);
}

50
%
{
transform
:
translateY(
-20px
);
}
}
h1 {
	color: #333;
	margin-bottom: 15px;
	font-size: 2em;
}

.message {
	color: #666;
	margin-bottom: 30px;
	font-size: 1.1em;
	line-height: 1.6;
}

.amount-section {
	background: #f9f9f9;
	padding: 25px;
	border-radius: 15px;
	margin-bottom: 30px;
	border: 3px solid #4CAF50;
}

.amount-label {
	color: #666;
	font-size: 1em;
	margin-bottom: 10px;
}

.amount-value {
	font-size: 2.5em;
	font-weight: bold;
	color: #4CAF50;
}

.note {
	background: #e8f5e9;
	border: 1px solid #4CAF50;
	padding: 15px;
	border-radius: 10px;
	margin-bottom: 30px;
	color: #2e7d32;
	font-size: 0.95em;
}

.btn-home {
	display: inline-block;
	padding: 18px 40px;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	text-decoration: none;
	border-radius: 10px;
	font-size: 1.2em;
	font-weight: bold;
	transition: all 0.3s;
}

.btn-home:hover {
	transform: translateY(-2px);
	box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}
</style>
</head>
<body>
	<div class="container">
		<div class="success-icon">✅</div>

		<h1>会計完了</h1>

		<p class="message">
			ご利用ありがとうございました！<br> お支払いが完了しました。
		</p>

		<div class="amount-section">
			<div class="amount-label">お支払い金額</div>
			<div class="amount-value">
				¥<%=formatter.format(totalAmount)%></div>
		</div>

		<div class="note">💡 レシートは店舗スタッフからお受け取りください</div>

		<a href="<%=request.getContextPath()%>/" class="btn-home"> 🏠
			トップページへ </a>
	</div>
</body>
</html>
