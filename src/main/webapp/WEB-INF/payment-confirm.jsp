<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="model.CartItem"%>

<%
// リクエスト属性から取得
@SuppressWarnings("unchecked")
List<CartItem> orderDetailsList = (List<CartItem>) request.getAttribute("orderDetailsList");

Integer tableNum = (Integer) request.getAttribute("tableNum");
if (tableNum == null) {
	tableNum = 0;
}

Integer totalAmount = (Integer) request.getAttribute("totalAmount");
if (totalAmount == null) {
	totalAmount = 0;
}

Integer orderCount = (Integer) request.getAttribute("orderCount");
if (orderCount == null) {
	orderCount = 0;
}

NumberFormat formatter = NumberFormat.getInstance();
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>会計確認</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderCommon.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderConfirmation.css?v=5">
</head>

<body>

	<div class="header">
		<h1>会計確認</h1>
	</div>

	<div class="order-list" id="orderList">

		<%
		for (CartItem item : orderDetailsList) {
		%>
		<div class="order-item">
			<img class="item-photo"
				src="<%=request.getContextPath()%>/<%=item.getPhotoPath()%>"
				alt="<%=item.getName()%>">
			<div class="item-info">
				<div class="item-name">
					<%=item.getName()%>
				</div>
				<div class="item-detail">
					￥<%=formatter.format(item.getPrice())%>
					×
					<%=item.getQuantity()%>
				</div>
			</div>
			<div class="item-subtotal">
				￥<%=formatter.format(item.getSubtotal())%>
			</div>
		</div>
		<%
		}
		%>
	</div>

	<!-- 合計金額 -->
	<div class="bottom-area">
	<div class="total">
		合計：￥<%=formatter.format(totalAmount)%>（税込）
	</div>

	<div class="btn-area">
		<button class="btn back-btn"
			onclick="location.href='<%=request.getContextPath()%>/menu'">
			戻る</button>

		<form action="<%=request.getContextPath()%>/order/payment"
			method="post" style="margin: 0;">
			<button type="submit" class="btn confirm-btn" style=background:red>会計確定</button>
		</form>
	</div>
</div>
</body>
</html>
