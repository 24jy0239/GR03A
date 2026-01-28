<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Dish" %>
<%
    Dish dish = (Dish) request.getAttribute("dish");
    String mode = (String) request.getAttribute("mode");
    String nextId = (String) request.getAttribute("nextId");
    
    if (mode == null) mode = "add";
    boolean isEdit = "edit".equals(mode);
    String pageTitle = isEdit ? "メニュー詳細" : "メニュー新規";

    // 获取并清理分类 ID
    String currentCat = (isEdit && dish != null && dish.getCategory() != null) ? dish.getCategory().trim() : "";
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/addMenu.css">
</head>
<body>
    <header class="header">
        <h1 class="page-title"><%= pageTitle %></h1>
        <button id="topBtn" onclick="history.back()">戻る</button>
    </header>

    <main class="new-menu-container">
        <form action="${pageContext.request.contextPath}/admin/dish-manage" method="post" id="dishForm">
            <input type="hidden" name="action" id="formAction" value="<%= mode %>">
            <input type="hidden" name="dishId" value="<%= isEdit ? dish.getDishId() : (nextId != null ? nextId : "") %>">

            <section class="new-menu-row">
                <div class="photo-column">
                    <div class="upload-box" id="dropArea">
                        <% if (isEdit && dish != null && dish.getPhoto() != null && !dish.getPhoto().isEmpty()) { %>
                            <img src="<%= dish.getPhoto() %>" id="previewImg">
                        <% } else { %>
                            <p class="upload-text">プレビュー</p>
                        <% } %>
                    </div>
                </div>

                <div class="form-fields">
                    <div class="field-row">
                        <label class="field-label">種類：</label>
                        <select name="dishCategory" class="field-input" required>
                            <option value="">-- 選択 --</option>
                            <option value="CAT001" <%= "CAT001".equals(currentCat) ? "selected" : "" %>>麺類</option>
                            <option value="CAT002" <%= "CAT002".equals(currentCat) ? "selected" : "" %>>ご飯</option>
                            <option value="CAT003" <%= "CAT003".equals(currentCat) ? "selected" : "" %>>点心</option>
                            <option value="CAT004" <%= "CAT004".equals(currentCat) ? "selected" : "" %>>揚げ物</option>
                            <option value="CAT005" <%= "CAT005".equals(currentCat) ? "selected" : "" %>>ドリンク</option>
                            <option value="CAT006" <%= "CAT006".equals(currentCat) ? "selected" : "" %>>デザート</option>
                        </select>
                    </div>
                    <div class="field-row">
                        <label class="field-label">品名：</label>
                        <input type="text" name="dishName" class="field-input" value="<%= isEdit ? dish.getName() : "" %>" required>
                    </div>
                    <div class="field-row">
                        <label class="field-label">価格：</label>
                        <div class="price-wrapper">
                            <input type="number" name="dishPrice" class="field-input" value="<%= isEdit ? dish.getPrice() : "" %>" required>
                            <span class="price-unit">円</span>
                        </div>
                    </div>
                    <div class="field-row status-row">
                        <input type="checkbox" name="available" value="1" id="availableCheck" <%= (!isEdit || (dish != null && dish.isAvailable())) ? "checked" : "" %>>
                        <label for="availableCheck">有効（メニューに表示）</label>
                    </div>
                </div>
            </section>

            <section class="new-menu-buttons">
                <button type="button" class="btn-large btn-primary" onclick="document.getElementById('fileInput').click()">アップロード</button>
                <input type="file" id="fileInput" style="display: none;" accept="image/*">
                <input type="hidden" name="dishPhoto" id="dishPhoto" value="<%= isEdit ? dish.getPhoto() : "" %>">

                <button type="submit" class="btn-large btn-primary"><%= isEdit ? "更新" : "保存" %></button>
                
                <% if (isEdit) { %>
                    <button type="button" class="btn-large btn-danger" onclick="confirmDelete()">削除</button>
                <% } else { %>
                    <button type="button" class="btn-large btn-danger" onclick="history.back()">取消す</button>
                <% } %>
            </section>
        </form>
    </main>

    <script>
        function confirmDelete() {
            if (confirm("この料理を削除してもよろしいですか？")) {
                document.getElementById('formAction').value = 'delete';
                document.getElementById('dishForm').submit();
            }
        }
    </script>
</body>
</html>