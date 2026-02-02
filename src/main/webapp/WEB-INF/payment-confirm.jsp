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
<style>
.header h1 {
	color: #d32f2f;
}

.summary-box {
	background-color: #f9f9f9;
	border: 2px solid #ddd;
	border-radius: 8px;
	padding: 15px;
	margin: 15px auto;
	max-width: 600px;
}

.summary-row {
	display: flex;
	justify-content: space-between;
	padding: 8px 0;
	border-bottom: 1px solid #eee;
}

.summary-row:last-child {
	border-bottom: none;
}

.summary-label {
	font-size: 1em;
	color: #555;
}

.summary-value {
	font-size: 1em;
	font-weight: bold;
	color: #333;
}

.section-title {
	font-size: 1.2em;
	font-weight: bold;
	color: #333;
	margin: 20px 0 10px 0;
	text-align: center;
	border-bottom: 2px solid #4caf50;
	padding-bottom: 5px;
}

.warning-message {
	background-color: #fff3cd;
	border: 1px solid #ffc107;
	border-radius: 4px;
	padding: 15px;
	margin: 20px auto;
	max-width: 600px;
	text-align: center;
	color: #856404;
}

.warning-icon {
	font-size: 2em;
	margin-bottom: 10px;
}

.btn-area {
	display: flex;
	gap: 10px;
	justify-content: center;
	margin-top: 30px;
	padding: 20px;
}

.btn {
	padding: 15px 40px;
	font-size: 1.1em;
	border: none;
	border-radius: 8px;
	cursor: pointer;
	transition: all 0.3s;
	font-weight: bold;
}

.back-btn {
	background-color: #9e9e9e;
	color: white;
}

.back-btn:hover {
	background-color: #757575;
}

.confirm-btn {
	background-color: #4caf50;
	color: white;
}

.confirm-btn:hover {
	background-color: #388e3c;
	transform: scale(1.05);
}

/* order-confirm.jspのスタイルを活用 */
.order-list {
	max-width: 600px;
	margin: 0 auto;
}

.order-item {
	display: flex;
	align-items: center;
	padding: 15px;
	margin: 10px 0;
	border: 1px solid #ddd;
	border-radius: 8px;
	background-color: white;
	gap: 15px;
}

.item-photo {
	width: 80px;
	height: 80px;
	object-fit: cover;
	border-radius: 8px;
	flex-shrink: 0;
}

.item-info {
	flex-grow: 1;
}

.item-name {
	font-size: 1.1em;
	font-weight: bold;
	color: #333;
	margin-bottom: 5px;
}

.item-detail {
	font-size: 0.95em;
	color: #666;
}

.item-subtotal {
	font-size: 1em;
	font-weight: bold;
	color: #d32f2f;
	white-space: nowrap;
}

.total {
	font-size: 1.3em;
	font-weight: bold;
	color: #d32f2f;
	text-align: center;
	padding: 15px;
	background-color: #f9f9f9;
	border-radius: 8px;
	margin: 20px auto;
	max-width: 600px;
}

@media ( max-width : 600px) {
	.btn-area {
		flex-direction: column;
	}
	.btn {
		width: 100%;
	}
	.order-item {
		flex-direction: column;
		text-align: center;
	}
	.item-info {
		width: 100%;
	}
}
</style>
</head>

<body>

	<div class="header">
		<h1>会計確認</h1>
	</div>

	<!-- サマリー情報 -->
	<div class="summary-box">
		<div class="summary-row">
			<span class="summary-label">テーブル番号:</span> <span
				class="summary-value"><%=tableNum%> 番</span>
		</div>
		<div class="summary-row">
			<span class="summary-label">注文件数:</span> <span class="summary-value"><%=orderCount%>
				件</span>
		</div>
	</div>

	<!-- 注文明細 -->
	<div class="section-title">注文明細</div>

	<div class="order-list" id="orderList">
		<%
		if (orderDetailsList == null || orderDetailsList.isEmpty()) {
		%>
		<div class="order-item">注文内容がありません</div>
		<%
		} else {
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
		}
		%>
	</div>

	<!-- 合計金額 -->
	<div class="total">
		合計：￥<%=formatter.format(totalAmount)%>（税込）
	</div>

	<!-- 警告メッセージ -->
	<div class="warning-message">
		<div class="warning-icon">⚠️</div>
		<strong>ご確認ください</strong>
		<p>会計を確定すると、この来店の注文を締めます。</p>
		<p>よろしいですか？</p>
	</div>

	<!-- ボタンエリア -->
	<div class="btn-area">
		<button class="btn back-btn"
			onclick="location.href='<%=request.getContextPath()%>/menu'">
			戻る</button>

		<form action="<%=request.getContextPath()%>/order/payment"
			method="post" style="margin: 0;">
			<button type="submit" class="btn confirm-btn">会計を確定する</button>
		</form>
	</div>

</body>
</html>
