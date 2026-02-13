<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.VisitWithDetails"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${year}年${month}月${day}日売上詳細</title>
<!-- 既存のCSSを使用（統一性のため） -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/general.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/analysis.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/daily-sales.css">
</head>
<body>
	<div class="container">
		<!-- ヘッダー -->
		<div class="header">
			<h1>📅 ${year}年${month}月${day}日 売上詳細</h1>
			<a href="${pageContext.request.contextPath}/admin/sales?year=${year}"
				class="back-button"> ← 売上分析に戻る </a>
		</div>

		<!-- サマリー -->
		<div class="summary">
			<div class="summary-card">
				<div class="label">来店数</div>
				<div class="value">${visitCount}<span style="font-size: 0.5em">組</span>
				</div>
			</div>
			<div class="summary-card">
				<div class="label">総注文数</div>
				<div class="value">${totalOrderCount}<span
						style="font-size: 0.5em">件</span>
				</div>
			</div>
			<div class="summary-card">
				<div class="label">日別合計</div>
				<div class="value">
					¥
					<fmt:formatNumber value="${dailyTotal}" pattern="#,###" />
				</div>
			</div>
		</div>

		<!-- 来店一覧 -->
		<div class="visit-list">
			<c:choose>
				<c:when test="${empty visitDetails}">
					<div class="no-data">
						<div class="no-data-icon">📭</div>
						<p>この日の来店記録はありません</p>
					</div>
				</c:when>
				<c:otherwise>
					<c:forEach var="detail" items="${visitDetails}">
						<div class="visit-card">
							<!-- 来店情報ヘッダー -->
							<div class="visit-header">
								<div class="visit-info">
									<span class="table-badge">テーブル ${detail.visit.tableNum}</span>

									<div class="visit-info-item">
										<span class="label">来店時刻</span> <span class="value">${detail.arrivalTimeFormatted}</span>
									</div>

									<div class="visit-info-item">
										<span class="label">会計時刻</span> <span class="value">${detail.paymentTimeFormatted}</span>
									</div>

									<div class="visit-info-item">
										<span class="label">注文数</span> <span class="value">${detail.orderItemCount}件</span>
									</div>
								</div>

								<span class="total-badge">¥<fmt:formatNumber
										value="${detail.totalAmount}" pattern="#,###" /></span>
							</div>

							<!-- 注文明細 -->
							<div class="order-details">
								<table>
									<thead>
										<tr>
											<th>料理名</th>
											<th style="text-align: center">数量</th>
											<th style="text-align: right">単価</th>
											<th style="text-align: right">小計</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${detail.orderItems}">
											<tr>
												<td>${item.dishName}</td>
												<td style="text-align: center">×${item.quantity}</td>
												<td style="text-align: right">¥<fmt:formatNumber
														value="${item.price}" pattern="#,###" /></td>
												<td style="text-align: right; font-weight: bold">¥<fmt:formatNumber
														value="${item.subtotal}" pattern="#,###" />
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</body>
</html>
