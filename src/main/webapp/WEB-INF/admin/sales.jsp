<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>売上分析</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/general.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/analysis.css">
</head>
<body>
	<div class="header-section">
		<h1>売り上げ分析</h1>
		<button id="return"
			onclick="location.href='${pageContext.request.contextPath}/administration.jsp'">管理画面へ</button>
	</div>

	<div class="main-container">
		<div class="top-toolbar">
			<div class="header-left">
				<form action="${pageContext.request.contextPath}/admin/sales"
					method="get">
					<select name="year" onchange="this.form.submit()">
						<c:forEach var="y" begin="2024" end="2026">
							<option value="${y}" ${y == selectedYear ? 'selected' : ''}>${y}</option>
						</c:forEach>
					</select> <span>年</span>
				</form>
			</div>

			<div class="header-center">
				<span class="total-display"> 年間合計：<fmt:formatNumber
						value="${yearlyTotal}" pattern="#,###" /> 円
				</span>
			</div>

			<div class="header-right">
				<span class="unit-text">単位：円</span>
			</div>
		</div>

		<div class="layout">
			<div class="month">
				<table>
					<tr>
						<th>月</th>
					</tr>
					<c:forEach var="m" begin="1" end="12">
						<tr>
							<th>${m}月</th>
						</tr>
					</c:forEach>
				</table>
			</div>

			<div class="date">
				<table>
					<thead>
						<tr>
							<c:forEach var="d" begin="1" end="31">
								<th>${d}日</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="m" begin="1" end="12">
							<tr>
								<c:forEach var="d" begin="1" end="31">
									<td><c:out value="${salesData[m][d]}" default="0" /></td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

			<div class="total">
				<table>
					<tr>
						<th>合計</th>
					</tr>
					<c:forEach var="m" begin="1" end="12">
						<c:set var="monthSum" value="0" />
						<c:forEach var="d" begin="1" end="31">
							<c:set var="monthSum"
								value="${monthSum + (empty salesData[m][d] ? 0 : salesData[m][d])}" />
						</c:forEach>
						<tr>
							<td><fmt:formatNumber value="${monthSum}" pattern="#,###" /></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
