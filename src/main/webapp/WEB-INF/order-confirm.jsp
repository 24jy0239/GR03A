<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="jp.ac.jec.restaurant.model.CartItem"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
Integer tableNum = (Integer) session.getAttribute("tableNum");

@SuppressWarnings("unchecked")
List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

Integer cartTotal = (Integer) session.getAttribute("cartTotal");

if (tableNum == null)
	tableNum = 0;
if (cartTotal == null)
	cartTotal = 0;

NumberFormat formatter = NumberFormat.getInstance();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文確認 - レストラン注文システム</title>
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
	align-items: center;
	justify-content: center;
	padding: 20px;
}

.container {
	background: white;
	padding: 40px;
	border-radius: 20px;
	max-width: 600px;
	width: 100%;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

h1 {
	text-align: center;
	color: #333;
	margin-bottom: 10px;
	font-size: 2em;
}

.subtitle {
	text-align: center;
	color: #666;
	margin-bottom: 30px;
}

.table-info {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 15px;
	border-radius: 10px;
	text-align: center;
	font-size: 1.3em;
	font-weight: bold;
	margin-bottom: 20px;
}

.order-list {
	margin-bottom: 20px;
}

.order-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px;
	border-bottom: 1px solid #eee;
}

.order-item:last-child {
	border-bottom: none;
}

.item-name {
	font-weight: bold;
	color: #333;
	margin-bottom: 5px;
}

.item-details {
	color: #666;
	font-size: 0.9em;
}

.item-price {
	font-size: 1.2em;
	font-weight: bold;
	color: #4CAF50;
}

.total-section {
	background: #f9f9f9;
	padding: 20px;
	border-radius: 10px;
	margin-bottom: 30px;
}

.total-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 1.5em;
	font-weight: bold;
}

.total-row span:last-child {
	color: #4CAF50;
}

.button-group {
	display: flex;
	gap: 15px;
}

.btn {
	flex: 1;
	padding: 18px;
	border: none;
	border-radius: 10px;
	font-size: 1.2em;
	cursor: pointer;
	transition: all 0.3s;
	font-weight: bold;
}

.btn-primary {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
}

.btn-primary:hover {
	transform: translateY(-2px);
	box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
	background: #f0f0f0;
	color: #666;
}

.btn-secondary:hover {
	background: #e0e0e0;
}

.note {
	background: #fff3cd;
	border: 1px solid #ffc107;
	padding: 15px;
	border-radius: 5px;
	margin-bottom: 20px;
	color: #856404;
}
</style>
</head>
<body>
	<div class="container">
		<h1>📝 注文確認</h1>
		<p class="subtitle">以下の内容で注文してよろしいですか？</p>

		<div class="table-info">
			🍽️ テーブル
			<%=tableNum%>
			番
		</div>

		<div class="note">💡 注文後も追加注文が可能です。会計はまとめて行えます。</div>

		<div class="order-list">
			<%
			if (cart != null) {
				for (CartItem item : cart) {
			%>
			<div class="order-item">
				<div>
					<div class="item-name"><%=item.getDishName()%></div>
					<div class="item-details">
						¥<%=formatter.format(item.getPrice())%>
						×
						<%=item.getQuantity()%>
					</div>
				</div>
				<div class="item-price">
					¥<%=formatter.format(item.getSubtotal())%>
				</div>
			</div>
			<%
			}
			}
			%>
		</div>

		<div class="total-section">
			<div class="total-row">
				<span>合計金額</span> <span>¥<%=formatter.format(cartTotal)%></span>
			</div>
		</div>

		<div class="button-group">
			<a href="<%=request.getContextPath()%>/menu"
				class="btn btn-secondary"
				style="text-decoration: none; text-align: center; line-height: 1.2;">
				← メニューに戻る </a>

			<form action="<%=request.getContextPath()%>/order" method="post"
				style="flex: 1;">
				<button type="submit" class="btn btn-primary">注文を確定する 🍽️</button>
			</form>
		</div>
	</div>
</body>
</html>
