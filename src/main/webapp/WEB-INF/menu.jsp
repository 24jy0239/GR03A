<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="model.Dish"%>
<%@ page import="java.util.List"%>
<%@ page import="model.CartItem"%>

<%
Integer tableNum = (Integer) session.getAttribute("tableNum");
if (tableNum == null) {
	response.sendRedirect(request.getContextPath() + "/index.jsp");

	return;
}
boolean hasOrder = Boolean.TRUE.equals(request.getAttribute("hasOrder"));
@SuppressWarnings("unchecked")
List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

int cartCount = 0;
if (cart != null) {
	for (CartItem item : cart) {
		cartCount += item.getQuantity();
	}
}
%>


<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文画面</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderCommon.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/orderMenu.css?">
</head>

<body>

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
					<!-- 商品名 + 単価 -->
					<div class="cart-left">
						<div class="cart-name"><%=item.getName()%></div>
						<div class="cart-unit-price">
							¥<%=item.getPrice()%>
						</div>
					</div> <!-- 数量操作 -->
					<div class="cart-right">

						<!-- 减 -->
						<form action="<%=request.getContextPath()%>/menu" method="post">
							<input type="hidden" name="action" value="decrease"> <input
								type="hidden" name="dishId" value="<%=item.getDishId()%>">
							<button type="submit" class="qty-btn">−</button>
						</form>

						<span class="cart-qty"><%=item.getQuantity()%></span>

						<!-- 加 -->
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

			<!-- リスト取消 -->
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
			<a class="category-btn" href="<%=request.getContextPath()%>/menu?category=ALL">TOP</a>
			<a class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=麺類">麺類</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=ご飯">ご飯</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=点心">点心</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=揚げ物">揚げ物</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=ドリンク">ドリンク</a> <a
				class="category-btn"
				href="<%=request.getContextPath()%>/menu?category=デザート">デザート</a>
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
		<div class="menu-viewport">
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
				}
				%>

			</div>
		</div>
		<%
		}
		%>

		<!-- 注文操作 -->
		<button class="confirm-btn"
			onclick="location.href='<%=request.getContextPath()%>/order/history'">
			注文履歴</button>

		<button class="pay-btn" onclick="goPayment()">会計</button>

		<button class="send-btn"
			onclick="location.href='<%=request.getContextPath()%>/order/confirm'">
			注文送信</button>


		<!-- モーダル -->
		<div id="sentModal" class="modal">
			<div class="modal-content">
				<p>注文を送信しました！</p>
				<button id="closeModal">閉じる</button>
			</div>
		</div>

		<div id="noOrderModal" class="modal">
			<div class="modal-content">
				<p>まだ注文がありません</p>
				<button onclick="closeNoOrderModal()">閉じる</button>
			</div>
		</div>


		<script>
document.addEventListener("DOMContentLoaded", () => {

    /*リスト開閉*/
    const btn = document.getElementById("listBtn");
    const panel = document.getElementById("sideList");

    const isOpen = sessionStorage.getItem("cartOpen");

    if (isOpen === "true") {
        panel.classList.add("open");
    } else {
        panel.classList.remove("open");
    }

    btn.addEventListener("click", () => {
        panel.classList.toggle("open");

        sessionStorage.setItem(
            "cartOpen",
            panel.classList.contains("open")
        );
    });
    
	/*初めてカート追加時*/
    const cartCount = <%=cartCount%>;

    const everOpened = sessionStorage.getItem("cartEverOpened");

    if (cartCount > 0 && everOpened !== "true") {
        panel.classList.add("open");
        sessionStorage.setItem("cartOpen", "true");
        sessionStorage.setItem("cartEverOpened", "true");
    }

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

		<script>
/* ===== 会計ボタン ===== */
function goPayment() {
    const hasOrder = <%=hasOrder%>;

    if (!hasOrder) {
        showNoOrderModal();
        return;
    }

    location.href = "<%=request.getContextPath()%>/order/payment";
}

/* ===== 未注文モーダル ===== */
function showNoOrderModal() {
    document.getElementById("noOrderModal").classList.add("show");
}

function closeNoOrderModal() {
    document.getElementById("noOrderModal").classList.remove("show");
}
</script>
</body>
</html>
