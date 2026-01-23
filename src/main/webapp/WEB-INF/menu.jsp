<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.Dish"%>
<%@ page import="model.CartItem"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
Integer tableNum = (Integer) session.getAttribute("tableNum");
String message = (String) session.getAttribute("message");

@SuppressWarnings("unchecked")
Map<String, Dish> dishMap = (Map<String, Dish>) session.getAttribute("dishMap");

@SuppressWarnings("unchecked")
List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

Integer cartCount = (Integer) session.getAttribute("cartCount");
Integer cartTotal = (Integer) session.getAttribute("cartTotal");

if (tableNum == null)
	tableNum = 0;
if (cartCount == null)
	cartCount = 0;
if (cartTotal == null)
	cartTotal = 0;

NumberFormat formatter = NumberFormat.getInstance();

// メッセージを表示したら削除
if (message != null) {
	session.removeAttribute("message");
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>メニュー - レストラン注文システム</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
	background: #f5f5f5;
	padding-bottom: 100px;
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
	max-width: 1200px;
	margin: 0 auto;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.table-info {
	font-size: 1.2em;
}

.message {
	background: #4CAF50;
	color: white;
	padding: 15px;
	text-align: center;
	margin: 20px;
	border-radius: 5px;
}

.container {
	max-width: 1200px;
	margin: 20px auto;
	padding: 0 20px;
}

.menu-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
	gap: 20px;
	margin-bottom: 30px;
}

.dish-card {
	background: white;
	border-radius: 10px;
	overflow: hidden;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s, box-shadow 0.2s;
}

.dish-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.dish-image {
	width: 100%;
	height: 180px;
	background: #eee;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 4em;
}

.dish-info {
	padding: 15px;
}

.dish-name {
	font-size: 1.2em;
	font-weight: bold;
	margin-bottom: 10px;
	color: #333;
}

.dish-price {
	font-size: 1.3em;
	color: #4CAF50;
	margin-bottom: 15px;
	font-weight: bold;
}

.add-to-cart {
	display: flex;
	gap: 10px;
	align-items: center;
}

.add-to-cart input[type="number"] {
	width: 60px;
	padding: 8px;
	border: 2px solid #ddd;
	border-radius: 5px;
	font-size: 1em;
	text-align: center;
}

.add-to-cart button {
	flex: 1;
	padding: 10px;
	background: #667eea;
	color: white;
	border: none;
	border-radius: 5px;
	cursor: pointer;
	font-size: 1em;
	font-weight: bold;
	transition: background 0.3s;
}

.add-to-cart button:hover {
	background: #5568d3;
}

.cart-footer {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 15px 20px;
	box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
	z-index: 99;
}

.cart-footer-content {
	max-width: 1200px;
	margin: 0 auto;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.cart-info {
	display: flex;
	gap: 30px;
	font-size: 1.1em;
}

.cart-total {
	font-weight: bold;
	color: #4CAF50;
	font-size: 1.3em;
}

.btn-success {
	padding: 12px 30px;
	background: #4CAF50;
	border: none;
	border-radius: 5px;
	font-size: 1.1em;
	font-weight: bold;
	cursor: pointer;
	color: white;
}

.btn-success:hover {
	background: #45a049;
}

.cart-section {
	background: white;
	padding: 20px;
	border-radius: 10px;
	margin-bottom: 20px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.cart-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px;
	border-bottom: 1px solid #eee;
}

.cart-item:last-child {
	border-bottom: none;
}

.cart-item-name {
	font-weight: bold;
}

.cart-item-controls {
	display: flex;
	gap: 15px;
	align-items: center;
}

.remove-btn {
	padding: 5px 15px;
	background: #ff5252;
	color: white;
	border: none;
	border-radius: 3px;
	cursor: pointer;
}

.remove-btn:hover {
	background: #ff1744;
}
</style>
</head>
<body>
	<div class="header">
		<div class="header-content">
			<div class="table-info">
				🍽️ テーブル
				<%=tableNum%>
				番
			</div>
			<div>
				<a href="<%=request.getContextPath()%>/payment"
					style="color: white; text-decoration: none; margin-right: 20px;">
					💳 会計へ </a> <a href="<%=request.getContextPath()%>/reset"
					style="color: white; text-decoration: none;"> 🏠 トップへ戻る </a>
			</div>
		</div>
	</div>

	<%
	if (message != null) {
	%>
	<div class="message">
		✅
		<%=message%>
	</div>
	<%
	}
	%>

	<div class="container">
		<h2 style="margin-bottom: 20px;">📋 メニュー</h2>

		<div class="menu-grid">
			<%
			if (dishMap != null) {
				for (Map.Entry<String, Dish> entry : dishMap.entrySet()) {
					Dish dish = entry.getValue();
			%>
			<div class="dish-card">
				<div class="dish-image">🍜</div>
				<div class="dish-info">
					<div class="dish-name"><%=dish.getDishName()%></div>
					<div class="dish-price">
						¥<%=formatter.format(dish.getPrice())%></div>

					<form action="<%=request.getContextPath()%>/menu" method="post"
						class="add-to-cart">
						<input type="hidden" name="action" value="add"> <input
							type="hidden" name="dishId" value="<%=dish.getDishId()%>">
						<input type="number" name="quantity" value="1" min="1" max="10">
						<button type="submit">🛒 追加</button>
					</form>
				</div>
			</div>
			<%
			}
			}
			%>
		</div>

		<%
		if (cart != null && !cart.isEmpty()) {
		%>
		<div class="cart-section">
			<h2 style="margin-bottom: 15px;">🛒 カート</h2>

			<%
			for (CartItem item : cart) {
			%>
			<div class="cart-item">
				<div>
					<div class="cart-item-name"><%=item.getDishName()%></div>
					<div style="color: #666;">
						¥<%=formatter.format(item.getPrice())%>
						×
						<%=item.getQuantity()%>
						= ¥<%=formatter.format(item.getSubtotal())%>
					</div>
				</div>
				<div class="cart-item-controls">
					<form action="<%=request.getContextPath()%>/menu" method="post"
						style="display: inline;">
						<input type="hidden" name="action" value="update"> <input
							type="hidden" name="dishId" value="<%=item.getDishId()%>">
						<input type="number" name="quantity"
							value="<%=item.getQuantity()%>" min="1" max="10"
							onchange="this.form.submit()"
							style="width: 60px; padding: 5px; text-align: center;">
					</form>

					<form action="<%=request.getContextPath()%>/menu" method="post"
						style="display: inline;">
						<input type="hidden" name="action" value="remove"> <input
							type="hidden" name="dishId" value="<%=item.getDishId()%>">
						<button type="submit" class="remove-btn">削除</button>
					</form>
				</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>
	</div>

	<div class="cart-footer">
		<div class="cart-footer-content">
			<div class="cart-info">
				<span>カート: <%=cartCount%> 品
				</span> <span class="cart-total">合計: ¥<%=formatter.format(cartTotal)%></span>
			</div>
			<form action="<%=request.getContextPath()%>/order-confirm"
				method="get">
				<button type="submit" class="btn-success">✅ 注文確認へ</button>
			</form>
		</div>
	</div>
</body>
</html>
