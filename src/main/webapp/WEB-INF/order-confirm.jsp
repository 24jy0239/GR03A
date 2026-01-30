<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="model.CartItem"%>

<%
@SuppressWarnings("unchecked")
List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

Integer cartTotal = (Integer) request.getAttribute("cartTotal");
if (cartTotal == null) {
	cartTotal = 0;
}

Integer tableNum = (Integer) session.getAttribute("tableNum");
if (tableNum == null) {
	tableNum = 0;
}

NumberFormat formatter = NumberFormat.getInstance();
%>

<!DOCTYPE html>
<html lang="ja">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>送信確認</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderCommon.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderConfirmation.css?v">

</head>

<body>

	<!-- 上：标题 -->
	<div class="header">
		<h1>送信確認</h1>
	</div>

	<!-- 中：注文内容 -->
	<div class="order-list" id="orderList">

		<%
		if (cart == null || cart.isEmpty()) {
		%>
		<div class="order-item">注文内容がありません</div>
		<%
		} else {
		for (CartItem item : cart) {
		%>
		<div class="order-item">
			<div class="item-name">
				<%=item.getName()%>
			</div>
			<div class="item-detail">
				￥<%=formatter.format(item.getPrice())%>
				×
				<%=item.getQuantity()%>
			</div>
			<div class="item-subtotal">
				小計：￥<%=formatter.format(item.getSubtotal())%>
			</div>
		</div>
		<%
		}
		}
		%>

	</div>

	<!-- 下：合计 + 按钮 -->
	<div class="bottom-area">
		<div class="total" id="totalAmount">
			合計：￥<%=formatter.format(cartTotal)%>（税込）
		</div>

		<div class="btn-area">
			<button class="btn back-btn"
				onclick="location.href='<%=request.getContextPath()%>/menu'">
				戻る</button>

			<form action="<%=request.getContextPath()%>/order" method="post">
				<button class="btn send-btn">送信</button>
			</form>
		</div>
	</div>

</body>
</html>
