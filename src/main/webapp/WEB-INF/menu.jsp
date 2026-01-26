<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="model.Dish"%>
<%@ page import="java.util.List"%>
<%@ page import="model.CartItem"%>

<%
Integer tableNum = (Integer) session.getAttribute("tableNum");
if (tableNum == null) {
	response.sendRedirect("index.jsp");
	return;
}

@SuppressWarnings("unchecked")
List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

Integer cartCount = 0;
Integer cartTotal = 0;

if (cart != null) {
	for (CartItem item : cart) {
		cartCount += item.getQuantity();
		cartTotal += item.getSubtotal();
	}
}

String message = (String) session.getAttribute("message");
if (message != null) {
	session.removeAttribute("message");
}
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文画面</title>
<link rel="stylesheet" href="./css/orderCommon.css">
<link rel="stylesheet" href="./css/orderMenu.css?v=999">
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
				if (cart == null || cart.isEmpty()) {
				%>
				<li>カートは空です</li>
				<%
				} else {
				for (CartItem item : cart) {
				%>

				<li class="cart-item">
					<!-- 商品名 + 单价 -->
					<div class="cart-left">
						<div class="cart-name"><%=item.getName()%></div>
						<div class="cart-unit-price">
							¥<%=item.getPrice()%>
						</div>
					</div> <!-- 数量操作 -->
					<div class="cart-right">

						<!-- 减号 -->
						<form action="<%=request.getContextPath()%>/menu" method="post">
							<input type="hidden" name="action" value="decrease"> <input
								type="hidden" name="dishId" value="<%=item.getDishId()%>">
							<button type="submit" class="qty-btn">−</button>
						</form>

						<span class="cart-qty"><%=item.getQuantity()%></span>

						<!-- 加号 -->
						<form action="<%=request.getContextPath()%>/menu" method="post">
							<input type="hidden" name="action" value="add"> <input
								type="hidden" name="dishId" value="<%=item.getDishId()%>">
							<input type="hidden" name="quantity" value="1">
							<button type="submit" class="qty-btn">＋</button>
						</form>

					</div>

				</li>

				<%
				}
				}
				%>
			</ul>

			<!-- 清空按钮 -->
			<%
			if (cart != null && !cart.isEmpty()) {
			%>
			<form action="<%=request.getContextPath()%>/menu" method="post"
				style="text-align: center; margin-top: 10px;">
				<input type="hidden" name="action" value="clear">
				<button type="submit" class="clear-btn">リスト取消</button>
			</form>
			<%
			}
			%>
		</div>

	</div>

	<!--		 カテゴリ -->
	<div class="container">

		<div class="category-area">
			<a class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT001">麺類</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT002">ご飯</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT003">点心</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT004">揚げ物</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT005">ドリンク</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=CAT006">デザート</a>
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
			onclick="location.href='<%=request.getContextPath()%>/order'">
			注文確認</button>

		<form action="<%=request.getContextPath()%>/order" method="post">
			<button type="submit" class="send-btn">注文送信</button>
		</form>


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
