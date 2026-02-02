<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.TableStatus, java.util.List, java.text.NumberFormat"%>
<%
    List<TableStatus> tables = (List<TableStatus>) request.getAttribute("tables");
    NumberFormat fmt = NumberFormat.getInstance();
    if (tables == null) tables = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注文確認画面（卓順）</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderConfirmationByTime.css">
    <script>
        // Auto refresh every 15 seconds
        setTimeout(() => { location.reload(); }, 15000);
    </script>
</head>
<body>

<header class="header" id="timeHeader">
    <button id="backBtn" onclick="location.href='${pageContext.request.contextPath}/hall.jsp'">
        ← 卓順を戻る
    </button>
    <h1>注文確認画面（卓順）</h1>
</header>

<main class="confirm-container">
    <section class="left-side">
        <% for (int i = 0; i < 7; i++) { 
            if (i < tables.size()) { 
                TableStatus t = tables.get(i); %>
                <div class="confirm-card">
                    <div class="name-col">
                        <div class="dish-name">🍽️ テーブル <%= t.getTableNum() %></div>
                        <div class="info-row-3">
                            <div class="info-box"><span>卓番</span><strong><%= t.getTableNum() %></strong></div>
                            <div class="info-box"><span>経過</span><strong><%= t.isOccupied() ? t.getFormattedStayTime() : "00:00" %></strong></div>
                            <div class="info-box"><span>売上</span><strong><%= fmt.format(t.getTotalAmount()) %></strong></div>
                        </div>
                    </div>
                    <div class="action-col">
                        <% if (t.isOccupied()) { %>
                            <form action="${pageContext.request.contextPath}/admin/complete" method="POST">
                                <input type="hidden" name="tableNum" value="<%= t.getTableNum() %>">
                                <button type="submit" class="action-btn">済む</button>
                            </form>
                        <% } else { %>
                            空席
                        <% } %>
                    </div>
                </div>
            <% } else { %>
                <div class="confirm-card empty"></div>
            <% } 
        } %>
    </section>

    <section class="right-side">
        <% for (int i = 7; i < 14; i++) { 
            if (i < tables.size()) { 
                TableStatus t = tables.get(i); %>
                <div class="confirm-card">
                    <div class="name-col">
                        <div class="dish-name">🍽️ テーブル <%= t.getTableNum() %></div>
                        <div class="info-row-3">
                            <div class="info-box"><span>卓番</span><strong><%= t.getTableNum() %></strong></div>
                            <div class="info-box"><span>経過</span><strong><%= t.isOccupied() ? t.getFormattedStayTime() : "00:00" %></strong></div>
                            <div class="info-box"><span>売上</span><strong><%= fmt.format(t.getTotalAmount()) %></strong></div>
                        </div>
                    </div>e
                    <div class="action-col">
                        <% if (t.isOccupied()) { %>
                            <form action="${pageContext.request.contextPath}/admin/complete" method="POST">
                                <input type="hidden" name="tableNum" value="<%= t.getTableNum() %>">
                                <button type="submit" class="action-btn">済む</button>
                            </form>
                        <% } else { %>
                            空席
                        <% } %>
                    </div>
                </div>
            <% } else { %>
                <div class="confirm-card empty"></div>
            <% } 
        } %>
    </section>
</main>

</body>
</html>