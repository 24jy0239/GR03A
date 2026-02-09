<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>調理待ち一覧</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/general.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/cookingList.css">
</head>
<body>

	<header class="header">
		<h1>🔪 調理待ち一覧</h1>
		<button id="refreshBtn" onclick="location.reload()"
			class="square-refresh-btn">🔄</button>
		<button id="topBtn"
			onclick="location.href='${pageContext.request.contextPath}/administration.jsp'">管理画面へ</button>
	</header>

	<main class="wait-container">
		<%-- 料理数量の合計計算 --%>
		<c:set var="totalQuantity" value="0" />
		<c:forEach var="item" items="${kitchenItems}">
			<c:set var="totalQuantity" value="${totalQuantity + item.quantity}" />
		</c:forEach>

		<div class="summary-bar">📋 料理数量：${totalQuantity} 件</div>

		<section class="order-grid">

			<%-- 実際の注文データを表示 --%>
			<c:forEach var="item" items="${kitchenItems}">
				<div class="order-card">
					<%-- 料理名・情報カラム --%>
					<div class="col name-col">
						<div class="dish-name">${item.dishName}</div>

						<div class="info-row-3">
							<div class="info-box">
								<span class="info-label">卓番</span> <span class="info-value">${item.tableNum}</span>
							</div>

							<div class="info-box">
								<span class="info-label">経過</span> <span
									class="info-value timer" data-order-time="${item.orderTime}">00:00</span>
							</div>

							<div class="info-box">
								<span class="info-label">数量</span> <span class="info-value">${item.quantity}</span>
							</div>
						</div>
					</div>

					<%-- 開始ボタンカラム --%>
					<div class="col start-col">
						<c:choose>
							<c:when test="${item.itemStatus == 0}">
								<form action="${pageContext.request.contextPath}/admin/kitchen"
									method="post"
									style="width: 100%; display: flex; justify-content: center;">
									<input type="hidden" name="action" value="start"> <input
										type="hidden" name="orderItemId" value="${item.orderItemId}">
									<button type="submit" class="action-btn">開始</button>
								</form>
							</c:when>
							<c:otherwise>
								<span class="cooking-status-text">調理中</span>
							</c:otherwise>
						</c:choose>
					</div>

					<%-- 完了ボタンカラム --%>
					<div class="col finish-col">
						<form action="${pageContext.request.contextPath}/admin/kitchen"
							method="post"
							style="width: 100%; display: flex; justify-content: center;">
							<input type="hidden" name="action" value="finish"> <input
								type="hidden" name="orderItemId" value="${item.orderItemId}">
							<button type="submit" class="action-btn"
								${item.itemStatus == 0 ? 'disabled' : ''}>完了</button>
						</form>
					</div>
				</div>
			</c:forEach>

			<%-- 12個のグリッドに満たない場合、空カードで補完 --%>
			<c:set var="currentSize" value="${fn:length(kitchenItems)}" />
			<c:if test="${currentSize < 12}">
				<c:forEach begin="${currentSize + 1}" end="12">
					<div class="order-card empty">
						<div class="col name-col"></div>
						<div class="col start-col"></div>
						<div class="col finish-col"></div>
					</div>
				</c:forEach>
			</c:if>

		</section>
	</main>

	<script>
    // 1. 経過時間タイマーの更新
    function updateTimers() {
        const timers = document.querySelectorAll('.timer');
        const now = new Date();

        timers.forEach(timer => {
            const orderTimeString = timer.getAttribute('data-order-time');
            const orderTime = new Date(orderTimeString);
            
            if (isNaN(orderTime)) return;

            const diff = Math.floor((now - orderTime) / 1000);
            if (diff < 0) return;

            const mins = Math.floor(diff / 60).toString().padStart(2, '0');
            const secs = (diff % 60).toString().padStart(2, '0');
            
            timer.innerText = mins + ":" + secs;
        });
    }

    // 2. 自動更新（15秒ごと）
    setTimeout(function() {
        location.reload();
    }, 15000);

    // 3. タイマーを1秒ごとに更新
    setInterval(updateTimers, 1000);
    updateTimers();
</script>

</body>
</html>
