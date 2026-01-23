<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="model.Dish" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%
    @SuppressWarnings("unchecked")
    List<Dish> dishes = (List<Dish>) request.getAttribute("dishes");
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
    
    if (dishes == null) dishes = new java.util.ArrayList<>();
    
    NumberFormat formatter = NumberFormat.getInstance();
    
    // メッセージを表示したら削除
    if (message != null) {
        session.removeAttribute("message");
    }
    if (error != null) {
        session.removeAttribute("error");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🍽️ 料理マスタ管理 - レストラン注文システム</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
            background: #f5f5f5;
        }
        
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .header-content {
            max-width: 1400px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .title {
            font-size: 1.8em;
            font-weight: bold;
        }
        
        .nav-links {
            display: flex;
            gap: 15px;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            background: rgba(255,255,255,0.2);
            border-radius: 5px;
            transition: background 0.3s;
        }
        
        .nav-links a:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .container {
            max-width: 1400px;
            margin: 20px auto;
            padding: 0 20px;
        }
        
        .message {
            background: #4CAF50;
            color: white;
            padding: 15px 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .error {
            background: #ff5252;
            color: white;
            padding: 15px 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .toolbar h2 {
            color: #333;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 5px;
            font-size: 1em;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
        }
        
        .btn-sm {
            padding: 8px 16px;
            font-size: 0.9em;
        }
        
        .btn-warning {
            background: #ffc107;
            color: #333;
        }
        
        .btn-warning:hover {
            background: #ffb300;
        }
        
        .btn-danger {
            background: #ff5252;
            color: white;
        }
        
        .btn-danger:hover {
            background: #ff1744;
        }
        
        .dish-table {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th {
            background: #f5f5f5;
            padding: 15px;
            text-align: left;
            font-weight: bold;
            color: #555;
            border-bottom: 2px solid #ddd;
        }
        
        td {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        
        tr:last-child td {
            border-bottom: none;
        }
        
        tr:hover {
            background: #f9f9f9;
        }
        
        .dish-id {
            font-family: monospace;
            color: #666;
        }
        
        .dish-name {
            font-weight: bold;
            color: #333;
        }
        
        .dish-price {
            color: #4CAF50;
            font-weight: bold;
        }
        
        .category-badge {
            display: inline-block;
            padding: 5px 15px;
            background: #e3f2fd;
            color: #1976d2;
            border-radius: 20px;
            font-size: 0.9em;
        }
        
        .status-badge {
            display: inline-block;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 0.9em;
        }
        
        .status-available {
            background: #c8e6c9;
            color: #2e7d32;
        }
        
        .status-unavailable {
            background: #ffcdd2;
            color: #c62828;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-icon {
            font-size: 5em;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <div class="title">🍽️ 料理マスタ管理</div>
            
            <div class="nav-links">
                <a href="<%= request.getContextPath() %>/admin/kitchen">🔪 キッチン</a>
                <a href="<%= request.getContextPath() %>/admin/hall">🚶 ホール</a>
                <a href="<%= request.getContextPath() %>/admin/table-status">📊 テーブル</a>
                <a href="<%= request.getContextPath() %>/">🏠 トップ</a>
            </div>
        </div>
    </div>
    
    <div class="container">
        <% if (message != null) { %>
            <div class="message">
                ✅ <%= message %>
            </div>
        <% } %>
        
        <% if (error != null) { %>
            <div class="error">
                ❌ <%= error %>
            </div>
        <% } %>
        
        <div class="toolbar">
            <h2>料理一覧（全<%= dishes.size() %>件）</h2>
            <a href="<%= request.getContextPath() %>/admin/dish-manage?action=add" class="btn btn-primary">
                ➕ 新規追加
            </a>
        </div>
        
        <% if (dishes.isEmpty()) { %>
            <div class="dish-table">
                <div class="empty-state">
                    <div class="empty-icon">🍽️</div>
                    <h2>料理が登録されていません</h2>
                    <p>「新規追加」ボタンから料理を登録してください</p>
                </div>
            </div>
        <% } else { %>
            <div class="dish-table">
                <table>
                    <thead>
                        <tr>
                            <th>料理ID</th>
                            <th>料理名</th>
                            <th>価格</th>
                            <th>カテゴリ</th>
                            <th>状態</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Dish dish : dishes) { %>
                            <tr>
                                <td class="dish-id"><%= dish.getDishId() %></td>
                                <td class="dish-name"><%= dish.getName() %></td>
                                <td class="dish-price">¥<%= formatter.format(dish.getPrice()) %></td>
                                <td>
                                    <span class="category-badge"><%= dish.getCategory() %></span>
                                </td>
                                <td>
                                    <span class="status-badge <%= dish.isAvailable() ? "status-available" : "status-unavailable" %>">
                                        <%= dish.isAvailable() ? "有効" : "無効" %>
                                    </span>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a href="<%= request.getContextPath() %>/admin/dish-manage?action=edit&id=<%= dish.getDishId() %>" 
                                           class="btn btn-primary btn-sm">
                                            ✏️ 編集
                                        </a>
                                        
                                        <form action="<%= request.getContextPath() %>/admin/dish-manage" 
                                              method="post" 
                                              style="display: inline;">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="dishId" value="<%= dish.getDishId() %>">
                                            <button type="submit" class="btn btn-warning btn-sm">
                                                <%= dish.isAvailable() ? "🚫 無効化" : "✅ 有効化" %>
                                            </button>
                                        </form>
                                        
                                        <form action="<%= request.getContextPath() %>/admin/dish-manage" 
                                              method="post" 
                                              style="display: inline;"
                                              onsubmit="return confirm('本当に削除しますか？');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="dishId" value="<%= dish.getDishId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">
                                                🗑️ 削除
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</body>
</html>
