<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.CartItem"%>

<%
    @SuppressWarnings("unchecked")
    List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

    Integer cartTotal = (Integer) session.getAttribute("cartTotal");
    if (cartTotal == null) {
        cartTotal = 0;
    }
%>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>送信確認</title>
    <link rel="stylesheet" href="./css/orderCommon.css">
    <link rel="stylesheet" href="./css/orderConfirmation.css">
</head>

<body>

    <!-- 上：タイトル -->
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
                <%= item.getName() %><br>
                数量：<%= item.getQuantity() %>
                ￥<%= item.getFormattedSubtotal() %>
            </div>
        <%
            }
        }
        %>

    </div>

    <!-- 下：合計＋ボタン -->
    <div class="bottom-area">
        <div class="total" id="totalAmount">
            合計：<%= cartTotal %>円(税込み)
        </div>

        <div class="btn-area">
            <button class="btn back-btn" style="background-color: #ccc;"
                onclick="location.href='<%= request.getContextPath() %>/menu'">
                戻る
            </button>

            <form action="<%= request.getContextPath() %>/order" method="post">
                <button class="btn send-btn" style="background-color: gold;">
                    送信
                </button>
            </form>
        </div>
    </div>

</body>
</html>
