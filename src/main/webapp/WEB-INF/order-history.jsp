<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="model.CartItem"%>

<%
@SuppressWarnings("unchecked")
List<CartItem> summaryList = (List<CartItem>) request.getAttribute("summaryList");

Integer visitTotal = (Integer) request.getAttribute("visitTotal");
if (visitTotal == null) {
	visitTotal = 0;
}

NumberFormat formatter = NumberFormat.getInstance();
%>


<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>注文履歴</title>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderCommon.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderConfirmation.css?v=5">
</head>

<body>


	<div class="header">
		<h1>注文履歴</h1>
	</div>

	<div class="order-list" id="orderList">

		<%
		if (summaryList == null || summaryList.isEmpty()) {
		%>
		<div class="order-item">まだ注文履歴はありません</div>
		<%
		} else {
		for (CartItem item : summaryList) {
		%>

		<div class="order-item">
			<img class="item-photo"
				src="<%=request.getContextPath()%>/<%=item.getPhotoPath()%>"
				alt="<%=item.getName()%>">
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

	<!-- 合計 -->
	<div class="bottom-area">
		<div class="total">
			合計：￥<%=formatter.format(visitTotal)%>（税込）
		</div>

		<div class="btn-area">
			<button class="btn back-btn" onclick="history.back()">戻る</button>
		</div>
	</div>

</body>
</html>
