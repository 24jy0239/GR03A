<%@ page contentType="text/html; charset=UTF-8" %>
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
<!--        テーブル：<%= tableNum %>-->
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
        <div class="menu-area" id="menuArea">
            <div class="menu-item"><img src="img/ramen.jpg"><p>味噌ラーメン</p></div>
            <div class="menu-item"><img src="img/syougayaki.jpg"><p>生姜焼き定食</p></div>
            <div class="menu-item"><img src="img/karaage.jpg"><p>唐揚げ</p></div>
            <div class="menu-item"><img src="img/potato.jpg"><p>ポテト</p></div>
            <div class="menu-item"><img src="img/tyuukasoba.jpg"><p>中華そば</p></div>
        </div>

        <!-- 注文操作 -->
        <button class="confirm-btn"
            onclick="location.href='payConfirmation.jsp'">
            注文確認
        </button>

        <button class="send-btn"
            onclick="location.href='orderConfirmation.jsp'">
            注文送信
        </button>
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
