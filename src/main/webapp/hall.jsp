<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注文確認画面（卓順）</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderConfirmationByTable.css">

    <script>
        // 10秒自動更新（選択モード中は停止）
        setInterval(function () {
            const mode = document.body.getAttribute("data-mode");
            if (mode === "normal") location.reload();
        }, 10000);

        function pad2(n){
            n = String(n);
            return n.length === 1 ? ("0" + n) : n;
        }

        function normalizeTableNo(raw){
            const num = parseInt(raw, 10);
            if (isNaN(num) || num <= 0) return null;
            return pad2(num);
        }

        function scrollToTable(tableNo){
            const el = document.getElementById("table-" + tableNo);
            if (!el) return;
            el.scrollIntoView({ behavior: "smooth", block: "start" });
            el.classList.add("table-highlight");
            setTimeout(() => el.classList.remove("table-highlight"), 900);
        }

        // ===== 入力値（footer表示） =====
        function getTableInput(){
            return (document.getElementById("tableInput").textContent || "").trim();
        }
        function setTableInput(v){
            document.getElementById("tableInput").textContent = v;
            document.getElementById("keypadValue").textContent = v;
        }

        // ===== 数字パッド =====
        function openKeypad(){
            if (document.body.getAttribute("data-mode") !== "normal") return;
            document.getElementById("keypadOverlay").style.display = "flex";
            setTableInput(getTableInput());
        }
        function closeKeypad(){
            document.getElementById("keypadOverlay").style.display = "none";
        }
        function keypadAppend(d){
            const v = getTableInput();
            if (v.length >= 3) return;
            setTableInput(v + d);
        }
        function keypadBackspace(){
            const v = getTableInput();
            setTableInput(v.slice(0, -1));
        }
        function keypadClear(){
            setTableInput("");
        }

        // 「検索」ボタンでのみ移動
        function searchTable(){
            const t = normalizeTableNo(getTableInput());
            if (!t) return;
            closeKeypad();
            scrollToTable(t);
        }

        // ===== 選択モード =====
        function enterSelectMode(mode){
            document.body.setAttribute("data-mode", mode);
            document.querySelectorAll(".dish-row.selectable").forEach(r => r.classList.remove("selected"));

            document.getElementById("footer-normal").style.display = "none";
            document.getElementById("footer-confirm").style.display = "flex";
        }

        function exitSelectMode(){
            document.body.setAttribute("data-mode", "normal");
            document.querySelectorAll(".dish-row.selectable").forEach(r => r.classList.remove("selected"));

            document.getElementById("footer-normal").style.display = "grid";
            document.getElementById("footer-confirm").style.display = "none";
        }

        function toggleRowSelection(rowEl){
            const mode = document.body.getAttribute("data-mode");
            if (mode === "normal") return;
            if (!rowEl.classList.contains("selectable")) return;
            rowEl.classList.toggle("selected");
        }

        function confirmBatch(){
            const mode = document.body.getAttribute("data-mode");
            if (mode !== "remake" && mode !== "delete") return;

            const selected = Array.from(document.querySelectorAll(".dish-row.selectable.selected"))
                .map(r => r.getAttribute("data-order-item-id"))
                .filter(Boolean);

            if (selected.length === 0) return;

            document.getElementById("batchAction").value = (mode === "remake") ? "remakeSelected" : "deleteSelected";
            document.getElementById("batchIds").value = selected.join(",");
            document.getElementById("batchForm").submit();
        }
    </script>
</head>

<body data-mode="normal">
<header class="header">
    <h1>注文確認画面（卓順）</h1>
    <button id="topBtn" class="btn-primary" 
            onclick="location.href='${pageContext.request.contextPath}/administration.jsp'">
        戻る
    </button>
</header>

<main class="order-main">
    <section class="tables-section">
        <c:choose>
            <c:when test="${empty hallItems}">
                <div class="empty-state">
                    <h2>配膳待ちの料理はありません</h2>
                    <p>すべて配膳完了しています</p>
                </div>
            </c:when>

            <c:otherwise>
                <c:set var="currentTable" value="" />
                <c:set var="rowCount" value="0" />

                <c:forEach var="item" items="${hallItems}" varStatus="st">

                    <c:if test="${currentTable != item.tableNum}">
                        <c:if test="${not empty currentTable}">
                            <c:forEach begin="1" end="${4 - rowCount}">
                                <div class="dish-row">
                                    <div class="dish-cell dish-name">&nbsp;</div>
                                    <div class="dish-cell dish-qty">&nbsp;</div>
                                    <div class="dish-cell dish-time">&nbsp;</div>
                                    <div class="dish-cell dish-status">&nbsp;</div>
                                </div>
                            </c:forEach>
                            </div></div></div>
                        </c:if>

                        <c:set var="currentTable" value="${item.tableNum}" />
                        <c:set var="rowCount" value="0" />

                        <div class="table-grid" id="table-<fmt:formatNumber value='${item.tableNum}' pattern='00'/>">
                            <div class="table-inner">
                                <div class="table-no-col">
                                    <span class="table-no"><fmt:formatNumber value="${item.tableNum}" pattern="00"/></span>
                                </div>
                                <div class="dish-list">
                    </c:if>

                    <c:set var="rowCount" value="${rowCount + 1}" />

                    <div class="dish-row selectable"
                         data-order-item-id="${item.orderItemId}"
                         onclick="toggleRowSelection(this)">
                        <div class="dish-cell dish-name">${item.dishName}</div>
                        <div class="dish-cell dish-qty">${item.quantity}</div>
                        <div class="dish-cell dish-time">${item.elapsedTimeText}</div>
                        <div class="dish-cell dish-status">
                            <form class="serve-form"
                                  action="${pageContext.request.contextPath}/admin/hall"
                                  method="post"
                                  onsubmit="return document.body.getAttribute('data-mode')==='normal';">
                                <input type="hidden" name="action" value="serve">
                                <input type="hidden" name="orderItemId" value="${item.orderItemId}">
                                <button type="submit" class="serve-btn">配膳完了</button>
                            </form>
                        </div>
                    </div>

                    <c:if test="${st.last}">
                        <c:forEach begin="1" end="${4 - rowCount}">
                            <div class="dish-row">
                                <div class="dish-cell dish-name">&nbsp;</div>
                                <div class="dish-cell dish-qty">&nbsp;</div>
                                <div class="dish-cell dish-time">&nbsp;</div>
                                <div class="dish-cell dish-status">&nbsp;</div>
                            </div>
                        </c:forEach>
                        </div></div></div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </section>

    <form id="batchForm" action="${pageContext.request.contextPath}/admin/hall" method="post" style="display:none;">
        <input type="hidden" id="batchAction" name="action" value="">
        <input type="hidden" id="batchIds" name="orderItemIds" value="">
    </form>

    <!-- フッター（通常：5等分。空白列なし） -->
    <footer class="order-footer" id="footer-normal">
        <div class="table-input" id="tableInput" onclick="openKeypad()"></div>

        <button class="btn-primary footer-btn" type="button" onclick="searchTable()">検索</button>

        <button class="btn-primary footer-btn" type="button" onclick="enterSelectMode('remake')">作り直し</button>

        <button class="btn-primary footer-btn" type="button" onclick="enterSelectMode('delete')">削除</button>

        <button class="btn-primary footer-btn footer-btn-long"
                type="button"
                onclick="location.href='${pageContext.request.contextPath}/table-status.jsp'">
            配膳待ち一覧<br>（時間順）
        </button>
    </footer>

    <!-- フッター（選択モード：中央寄せ、2ボタンのみ） -->
    <footer class="order-footer-confirm" id="footer-confirm" style="display:none;">
        <button class="btn-primary footer-btn confirm-btn" type="button" onclick="confirmBatch()">確定</button>
        <button class="btn-primary footer-btn confirm-btn" type="button" onclick="exitSelectMode()">取消</button>
    </footer>
</main>

<!-- 数字パッド（入力のみ） -->
<div class="keypad-overlay" id="keypadOverlay" onclick="closeKeypad()">
    <div class="keypad" onclick="event.stopPropagation()">
        <div class="keypad-top">
            <div class="keypad-display" id="keypadValue"></div>
            <button class="keypad-close" type="button" onclick="closeKeypad()">×</button>
        </div>

        <div class="keypad-grid">
            <button class="keypad-btn" type="button" onclick="keypadAppend('1')">1</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('2')">2</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('3')">3</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('4')">4</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('5')">5</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('6')">6</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('7')">7</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('8')">8</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('9')">9</button>
            <button class="keypad-btr" type="button" onclick="keypadClear()">クリア</button>
            <button class="keypad-btn" type="button" onclick="keypadAppend('0')">0</button>
            <button class="keypad-btn" type="button" onclick="keypadBackspace()">⌫</button>
        </div>

        <div class="keypad-actions">
            <button class="keypad-act" type="button" onclick="closeKeypad()">閉じる</button>
            <button class="keypad-act" type="button" onclick="keypadClear()">消去</button>
            <button class="keypad-act" type="button" onclick="closeKeypad()">OK</button>
        </div>
    </div>
</div>

</body>
</html>
