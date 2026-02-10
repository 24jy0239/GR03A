<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ page import="model.TableStatus, model.Order, model.OrderItem, java.util.*, java.time.*, java.lang.reflect.Field"%>

<%
    List<TableStatus> tableStatusList = (List<TableStatus>) request.getAttribute("tableStatusList");
    if (tableStatusList == null) tableStatusList = new ArrayList<>();
    
    Map<String, Object> orderDetailsByVisit = (Map<String, Object>) request.getAttribute("orderDetailsByVisit");
    if (orderDetailsByVisit == null) orderDetailsByVisit = new HashMap<>();

    class ItemWrapper {
        OrderItem item;
        long epochSecond;
        ItemWrapper(OrderItem i, LocalDateTime t) { 
            this.item = i; 
            this.epochSecond = (t != null) ? t.atZone(ZoneId.systemDefault()).toEpochSecond() : 0;
        }
    }
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>テーブル状態</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderConfirmationByTable.css">
    <script>
        function updateTimers() {
            const now = Math.floor(Date.now() / 1000);
            document.querySelectorAll('.cell-time[data-start-time]').forEach(cell => {
                const startTime = parseInt(cell.getAttribute('data-start-time'));
                if (startTime > 0) {
                    const diff = Math.max(0, now - startTime);
                    const mins = Math.floor(diff / 60);
                    const secs = diff % 60;
                    cell.textContent = mins + ":" + (secs < 10 ? "0" : "") + secs;
                }
            });
        }
        document.addEventListener('DOMContentLoaded', () => {
            updateTimers();
            setInterval(updateTimers, 1000);
            setInterval(() => location.reload(), 15000);
        });
        function submitSingleAction(action, id) {
            document.getElementById("batchAction").value = action;
            document.getElementById("batchIds").value = id;
            document.getElementById("batchForm").submit();
        }
    </script>
</head>
<body>

<header class="header">
    <h1>📊テーブル状態</h1>
    <button class="square-refresh-btn" onclick="location.reload()">🔄</button>
    <button id="topBtn" onclick="location.href='${pageContext.request.contextPath}/admin'">管理画面へ</button>
</header>

<main class="order-main">
    <div class="kanban-grid-container">
        <% for (int i = 1; i <= 50; i++) { %>
            <div class="kanban-section">
                <div class="table-side-index"><%= String.format("%02d", i) %></div>
                
                <div class="dish-data-container">
                    <%
                        final int currentTable = i;
                        List<ItemWrapper> itemsToDisplay = new ArrayList<>();
                        TableStatus found = tableStatusList.stream()
                            .filter(ts -> ts.getTableNum() == currentTable && ts.isOccupied())
                            .findFirst().orElse(null);

                        if (found != null) {
                            Object details = orderDetailsByVisit.get(found.getVisitId());
                            if (details != null) {
                                try {
                                    Field f = details.getClass().getDeclaredField("orders");
                                    f.setAccessible(true);
                                    List<Order> orders = (List<Order>) f.get(details);
                                    if (orders != null) {
                                        for (Order o : orders) {
                                            for (OrderItem oi : o.getOrderItems()) {
                                                itemsToDisplay.add(new ItemWrapper(oi, o.getOrderTime()));
                                            }
                                        }
                                    }
                                } catch (Exception e) {}
                            }
                        }

                        // 固定显示 5 行内容
                        for (int j = 0; j < 5; j++) {
                            if (j < itemsToDisplay.size()) {
                                ItemWrapper wrapper = itemsToDisplay.get(j);
                                int s = wrapper.item.getItemStatus();
                    %>
                        <div class="dish-row">
                            <div class="cell-name"><%= wrapper.item.getDishName() %></div>
                            <div class="cell-qty"><%= wrapper.item.getQuantity() %></div>
                            <div class="cell-time" data-start-time="<%= wrapper.epochSecond %>">0:00</div>
                            <div class="cell-status"><%= (s==0)?"注文":(s==1)?"調理中":(s==2)?"完了":(s==3)?"配膳済":"" %></div>
                            <div class="cell-action">
                                <% if(s == 0) { %>
                                    <button class="row-btn btn-cancel" onclick="submitSingleAction('cancel', '<%= wrapper.item.getOrderItemId() %>')">✕</button>
                                <% } else if(s == 3) { %>
                                    <button class="row-btn btn-remake" onclick="submitSingleAction('reset', '<%= wrapper.item.getOrderItemId() %>')">🔄</button>
                                <% } %>
                            </div>
                        </div>
                    <% } else { %>
                        <div class="dish-row empty">
                            <div class="cell-name"></div><div class="cell-qty"></div><div class="cell-time"></div><div class="cell-status"></div><div class="cell-action"></div>
                        </div>
                    <% } } %>
                </div>
            </div>
        <% } %>
    </div>
</main>

<form id="batchForm" action="${pageContext.request.contextPath}/admin/table-status" method="post" style="display:none;">
    <input type="hidden" id="batchAction" name="action" value="">
    <input type="hidden" id="batchIds" name="orderItemIds" value="">
</form>

</body>
</html>