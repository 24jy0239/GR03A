<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="model.Dish" %>
<%
    String mode = (String) request.getAttribute("mode");
    Dish dish = (Dish) request.getAttribute("dish");
    String nextId = (String) request.getAttribute("nextId");
    
    if (mode == null) mode = "add";
    
    String pageTitle = "add".equals(mode) ? "料理追加" : "料理編集";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %> - レストラン注文システム</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            text-align: center;
        }
        
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 40px;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        label {
            display: block;
            color: #555;
            margin-bottom: 8px;
            font-weight: bold;
        }
        
        .required {
            color: #ff5252;
        }
        
        input[type="text"],
        input[type="number"],
        select {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        
        input[type="text"]:focus,
        input[type="number"]:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        input[type="text"]:read-only {
            background: #f0f0f0;
            cursor: not-allowed;
        }
        
        .hint {
            margin-top: 5px;
            font-size: 0.9em;
            color: #999;
        }
        
        .category-info {
            margin-top: 15px;
            padding: 15px;
            background: #f9f9f9;
            border-radius: 5px;
            border-left: 4px solid #667eea;
        }
        
        .category-list {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
            margin-top: 10px;
        }
        
        .category-item {
            padding: 8px;
            background: white;
            border-radius: 3px;
            font-size: 0.9em;
        }
        
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        input[type="checkbox"] {
            width: 20px;
            height: 20px;
            cursor: pointer;
        }
        
        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 40px;
        }
        
        .btn {
            flex: 1;
            padding: 15px;
            border: none;
            border-radius: 5px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: #f0f0f0;
            color: #666;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1><%= pageTitle %></h1>
        <p class="subtitle">料理情報を入力してください</p>
        
        <form action="<%= request.getContextPath() %>/admin/dish-manage" method="post">
            <input type="hidden" name="action" value="<%= mode %>">
            
            <div class="form-group">
                <label for="dishId">
                    料理ID <span class="required">*</span>
                </label>
                <% if ("add".equals(mode)) { %>
                    <input type="text" 
                           id="dishId" 
                           name="dishId" 
                           value="<%= nextId != null ? nextId : "" %>" 
                           required 
                           pattern="DIS[0-9]{3}" 
                           maxlength="6">
                    <div class="hint">形式: DIS001 ～ DIS999</div>
                <% } else { %>
                    <input type="text" 
                           id="dishId" 
                           name="dishId" 
                           value="<%= dish != null ? dish.getDishId() : "" %>" 
                           readonly>
                    <div class="hint">料理IDは変更できません</div>
                <% } %>
            </div>
            
            <div class="form-group">
                <label for="dishName">
                    料理名 <span class="required">*</span>
                </label>
                <input type="text" 
                       id="dishName" 
                       name="dishName" 
                       value="<%= "edit".equals(mode) && dish != null ? dish.getName() : "" %>" 
                       required 
                       maxlength="30"
                       placeholder="例: 醤油ラーメン">
                <div class="hint">最大30文字</div>
            </div>
            
            <div class="form-group">
                <label for="dishPrice">
                    価格（円） <span class="required">*</span>
                </label>
                <input type="number" 
                       id="dishPrice" 
                       name="dishPrice" 
                       value="<%= "edit".equals(mode) && dish != null ? dish.getPrice() : "" %>" 
                       required 
                       min="0" 
                       max="99999"
                       placeholder="例: 800">
                <div class="hint">0 ～ 99,999円</div>
            </div>
            
            <div class="form-group">
                <label for="dishCategory">
                    カテゴリ <span class="required">*</span>
                </label>
                <select id="dishCategory" name="dishCategory" required>
                    <option value="">-- 選択してください --</option>
                    <option value="CAT001" <%= "edit".equals(mode) && dish != null && "CAT001".equals(dish.getCategory()) ? "selected" : "" %>>CAT001 - 麺類</option>
                    <option value="CAT002" <%= "edit".equals(mode) && dish != null && "CAT002".equals(dish.getCategory()) ? "selected" : "" %>>CAT002 - ご飯</option>
                    <option value="CAT003" <%= "edit".equals(mode) && dish != null && "CAT003".equals(dish.getCategory()) ? "selected" : "" %>>CAT003 - 点心</option>
                    <option value="CAT004" <%= "edit".equals(mode) && dish != null && "CAT004".equals(dish.getCategory()) ? "selected" : "" %>>CAT004 - 揚げ物</option>
                    <option value="CAT005" <%= "edit".equals(mode) && dish != null && "CAT005".equals(dish.getCategory()) ? "selected" : "" %>>CAT005 - ドリンク</option>
                    <option value="CAT006" <%= "edit".equals(mode) && dish != null && "CAT006".equals(dish.getCategory()) ? "selected" : "" %>>CAT006 - デザート</option>
                </select>
                
                <div class="category-info">
                    💡 カテゴリ一覧
                    <div class="category-list">
                        <div class="category-item">CAT001: 麺類</div>
                        <div class="category-item">CAT002: ご飯</div>
                        <div class="category-item">CAT003: 点心</div>
                        <div class="category-item">CAT004: 揚げ物</div>
                        <div class="category-item">CAT005: ドリンク</div>
                        <div class="category-item">CAT006: デザート</div>
                    </div>
                </div>
            </div>
            
            <div class="form-group">
                <label for="dishPhoto">
                    写真ファイル名
                </label>
                <input type="text" 
                       id="dishPhoto" 
                       name="dishPhoto" 
                       value="<%= "edit".equals(mode) && dish != null ? (dish.getPhoto() != null ? dish.getPhoto() : "") : "" %>" 
                       maxlength="50"
                       placeholder="例: ramen.jpg">
                <div class="hint">最大50文字（省略可）</div>
            </div>
            
            <div class="form-group">
                <label>状態</label>
                <div class="checkbox-group">
                    <input type="checkbox" 
                           id="dishAvailable" 
                           name="dishAvailable" 
                           value="1" 
                           <%= "edit".equals(mode) && dish != null ? (dish.isAvailable() ? "checked" : "") : "checked" %>>
                    <label for="dishAvailable" style="font-weight: normal; margin: 0;">有効（メニューに表示）</label>
                </div>
                <div class="hint">チェックを外すと無効（非表示）になります</div>
            </div>
            
            <div class="button-group">
                <a href="<%= request.getContextPath() %>/admin/dish-manage" class="btn btn-secondary">
                    ← キャンセル
                </a>
                <button type="submit" class="btn btn-primary">
                    <%= "add".equals(mode) ? "➕ 追加" : "💾 保存" %>
                </button>
            </div>
        </form>
    </div>
</body>
</html>
