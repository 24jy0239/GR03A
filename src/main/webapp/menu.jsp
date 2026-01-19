<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="model.Dish" %>
<%@ page import="java.util.List" %>

<%
// ===== Session 桌号校验 =====
    Integer tableNum = (Integer) session.getAttribute("tableNum");
    if (tableNum == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    List<Dish> dishList = (List<Dish>) request.getAttribute("dishList");
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

	<!-- リスト -->
	<div id="sideList" class="side-list">
		<button id="listBtn" class="side-list-btn">リスト≫</button>

		<div class="list-panel">
			<ul>
				<li>メニュー1</li>
				<li>メニュー2</li>
				<li>メニュー3</li>
			</ul>
		</div>
	</div>

	<div class="container">

		<!-- カテゴリ -->
		<div class="category-area">
			<button class="category-btn" onclick="showMenu('ramen')">ラーメン</button>
			<button class="category-btn" onclick="showMenu('teishoku')">定食</button>
			<button class="category-btn" onclick="showMenu('fried')">揚げ物</button>
		</div>

		<!-- メニュー -->
		
		<%
    // MenuServlet 中放进 session 的 dishMap
    Map<String, Dish> dishMap =
        (Map<String, Dish>) session.getAttribute("dishMap");
%>
		
		
<% if (dishMap == null || dishMap.isEmpty()) { %>
    <p>メニューがありません。</p>
<% } else { %>

		
<div class="menu-area" id="menuArea">
<%
    if (dishList != null) {
        for (Dish dish : dishList) {
%>
    <div class="menu-item">
        <img src="<%= dish.getPhotoPath() %>">
        <p>
            <%= dish.getName() %><br>
            <%= dish.getFormattedPrice() %>
        </p>
    </div>
<%
        }
    }
%>
</div>

<% } %>
</div>

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
