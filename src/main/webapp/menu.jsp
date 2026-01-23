<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="model.Dish"%>
<%@ page import="java.util.List"%>
<%@ page import="model.CartItem"%>

<%
// ===== Session 桌号校验 =====
Integer tableNum = (Integer) session.getAttribute("tableNum");
if (tableNum == null) {
	response.sendRedirect("index.jsp");
	return;
}
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文画面</title>
<link rel="stylesheet" href="./css/orderCommon.css">
<link rel="stylesheet" href="./css/orderMenu.css?v=5">
</head>

<body>

	<!--     桌号表示（加分项） -->
	<!--    <div class="table-number">-->
	<!--        テーブルtableNumNum %>-->
	<!--    </div>-->

	<!-- cartリスト -->
	<div id="sideList" class="side-list">
		<button id="listBtn" class="side-list-btn">リスト≫</button>

		<div class="list-panel">
			<ul>
				<%
				List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

				if (cart == null || cart.isEmpty()) {
				%>
				<li>カートは空です</li>
				<%
				} else {
				for (CartItem item : cart) {
				%>
				<li class="cart-item"><img src="<%=item.getPhotoPath()%>"
					width="40"> <span class="cart-name"><%=item.getName()%></span>
					<span class="cart-qty">× <%=item.getQuantity()%></span> <span
					class="cart-price"> <%=item.getFormattedSubtotal()%>
				</span></li>
				<%
				}
				}
				%>
			</ul>
		</div>
	</div>



		<!--		 カテゴリ -->
	<div class="container">
    <a href="<%=request.getContextPath()%>/menu?category=CAT001">麺類</a>
    <a href="<%=request.getContextPath()%>/menu?category=CAT002">ご飯</a>
    <a href="<%=request.getContextPath()%>/menu?category=CAT003">点心</a>
    <a href="<%=request.getContextPath()%>/menu?category=CAT004">揚げ物</a>
    <a href="<%=request.getContextPath()%>/menu?category=CAT005">ドリンク</a>
    <a href="<%=request.getContextPath()%>/menu?category=CAT006">デザート</a>
</div>
	
		<!-- メニュー -->

		<%
		List<Dish> dishList = (List<Dish>) request.getAttribute("dishList");
		%>

		<%
		if (dishList == null || dishList.isEmpty()) {
		%>

		<p>メニューがありません。</p>

		<%
		} else {
		%>

		<div class="menu-area" id="menuArea">

			<%
			for (Dish dish : dishList) {
			%>

			<div class="menu-item">
				<form action="<%=request.getContextPath()%>/menu" method="post">
					<input type="hidden" name="action" value="add"> <input
						type="hidden" name="dishId" value="<%=dish.getDishId()%>">
					<input type="hidden" name="quantity" value="1">

					<button type="submit" style="all: unset; cursor: pointer;">
						<img src="<%=dish.getPhotoPath()%>" alt="">
						<p>
							<%=dish.getName()%><br>
							<%=dish.getFormattedPrice()%>
						</p>
					</button>
				</form>
			</div>

			<%
			} // for
			%>

		</div>

		<%
		}
		%>



		<!-- 注文操作 -->
		<button class="confirm-btn"
			onclick="location.href='payConfirmation.jsp'">注文確認</button>

		<button class="send-btn"
			onclick="location.href='orderConfirmation.jsp'">注文送信</button>
	</div>

	<!-- モーダル -->
	<div id="sentModal" class="modal">
		<div class="modal-content">
			<p>注文を送信しました！</p>
			<button id="closeModal">閉じる</button>
		</div>
	</div>

	<script>
        document.addEventListener("DOMContentLoaded", () => {

            /* リスト開閉 */
            const btn = document.getElementById("listBtn");
            const panel = document.getElementById("sideList");
            panel.classList.remove("open");

            btn.addEventListener("click", () => {
                panel.classList.toggle("open");
            });

            /* モーダル */
            const params = new URLSearchParams(window.location.search);
            const modal = document.getElementById("sentModal");
            const closeBtn = document.getElementById("closeModal");

            if (params.get('sent') === '1') {
                modal.classList.add("show");
            }

            closeBtn.addEventListener("click", () => {
                modal.classList.remove("show");
            });
        });
    </script>

</body>
</html>
