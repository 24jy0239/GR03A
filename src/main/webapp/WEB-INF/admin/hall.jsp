<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文確認(時間順)</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/general.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/orderConfirmationByTime.css">
</head>
<body>

	<header class="header">
		<div class="header-left"></div>

		<div class="center-group">
			<h1>🏃 注文確認(時間順)</h1>
			<button id="refreshBtn" onclick="location.reload()"
				class="square-refresh-btn">🔄</button>
		</div>

		<div class="header-right">
			<button id="topBtn"
				onclick="location.href='${pageContext.request.contextPath}/admin'">管理画面へ</button>
		</div>
	</header>

	<main class="wait-container">
		<c:set var="totalQuantity" value="0" />
		<c:forEach var="item" items="${allProgressItems}">
			<c:set var="totalQuantity" value="${totalQuantity + item.quantity}" />
		</c:forEach>

		<div class="summary-bar">📋 料理数量：${totalQuantity} 件</div>

		<section class="order-grid">
			<c:forEach var="item" items="${allProgressItems}">
				<div class="order-card">
					<div class="col name-col">
						<div class="dish-name">${item.dishName}</div>
						<div class="info-row-3">
							<div class="info-box">
								<span class="info-label">卓番</span><span class="info-value">${item.tableNum}</span>
							</div>
							<div class="info-box">
								<span class="info-label">経過</span><span class="info-value timer"
									data-order-time="${item.orderTime}">00:00</span>
							</div>
							<div class="info-box">
								<span class="info-label">数量</span><span class="info-value">${item.quantity}</span>
							</div>
						</div>
					</div>

					<div class="col action-col">
						<c:choose>
							<c:when test="${item.itemStatus == 2}">
								<form action="${pageContext.request.contextPath}/admin/hall"
									method="post"
									style="width: 100%; display: flex; justify-content: center;">
									<input type="hidden" name="action" value="serve"> <input
										type="hidden" name="orderItemId" value="${item.orderItemId}">
									<button type="submit" class="action-btn">配膳</button>
								</form>
							</c:when>
							<c:otherwise>
								<span class="cooking-status-text">${item.itemStatus == 1 ? '調理中' : '待ち'}</span>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</c:forEach>

			<c:set var="currentSize" value="${fn:length(allProgressItems)}" />
			<c:if test="${currentSize < 12}">
				<c:forEach begin="${currentSize + 1}" end="12">
					<div class="order-card empty">
						<div class="col name-col"></div>
						<div class="col action-col"></div>
					</div>
				</c:forEach>
			</c:if>
		</section>
	</main>

	<script>
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
    setInterval(updateTimers, 1000);
    updateTimers();
    setTimeout(function() { location.reload(); }, 15000);
    </script>
</body>
</html>