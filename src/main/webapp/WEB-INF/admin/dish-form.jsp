<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>     ← これになっているか？
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>   ← これになっているか？
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${mode == 'add' ? '料理追加' : '料理編集'}- レストラン注文システム</title>
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
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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

input[type="text"], input[type="number"], select {
	width: 100%;
	padding: 12px;
	border: 2px solid #ddd;
	border-radius: 5px;
	font-size: 1em;
	transition: border-color 0.3s;
}

input[type="text"]:focus, input[type="number"]:focus, select:focus {
	outline: none;
	border-color: #667eea;
}

input[type="text"]:read-only {
	background: #f0f0f0;
	cursor: not-allowed;
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

.hint {
	font-size: 0.9em;
	color: #999;
	margin-top: 5px;
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
	cursor: pointer;
	transition: all 0.3s;
	font-weight: bold;
}

.btn-primary {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
}

.btn-primary:hover {
	transform: translateY(-2px);
	box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
	background: #f0f0f0;
	color: #666;
}

.btn-secondary:hover {
	background: #e0e0e0;
}

.category-info {
	background: #f9f9f9;
	padding: 15px;
	border-radius: 5px;
	margin-top: 10px;
	font-size: 0.9em;
	color: #666;
}

.category-list {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 10px;
	margin-top: 10px;
}

.category-item {
	padding: 8px;
	background: white;
	border: 1px solid #ddd;
	border-radius: 3px;
	font-size: 0.85em;
}
</style>
</head>
<body>
	<div class="container">
		<h1>${mode == 'add' ? '🆕 料理追加' : '✏️ 料理編集'}</h1>
		<p class="subtitle">${mode == 'add' ? '新しい料理を登録します' : '料理情報を編集します'}</p>

		<form action="${pageContext.request.contextPath}/admin/dish-manage"
			method="post">
			<input type="hidden" name="action" value="${mode}">

			<div class="form-group">
				<label for="dishId"> 料理ID <span class="required">*</span>
				</label>
				<c:choose>
					<c:when test="${mode == 'add'}">
						<input type="text" id="dishId" name="dishId" value="${nextId}"
							required pattern="DIS[0-9]{3}" maxlength="6">
						<div class="hint">形式: DIS001 ～ DIS999</div>
					</c:when>
					<c:otherwise>
						<input type="text" id="dishId" name="dishId"
							value="${dish.dishId}" readonly>
						<div class="hint">料理IDは変更できません</div>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="form-group">
				<label for="dishName"> 料理名 <span class="required">*</span>
				</label> <input type="text" id="dishName" name="dishName"
					value="${mode == 'edit' ? dish.name : ''}" required maxlength="30"
					placeholder="例: 醤油ラーメン">
				<div class="hint">最大30文字</div>
			</div>

			<div class="form-group">
				<label for="dishPrice"> 価格（円） <span class="required">*</span>
				</label> <input type="number" id="dishPrice" name="dishPrice"
					value="${mode == 'edit' ? dish.price : ''}" required min="0"
					max="99999" placeholder="例: 800">
				<div class="hint">0 ～ 99,999円</div>
			</div>

			<div class="form-group">
				<label for="dishCategory"> カテゴリ <span class="required">*</span>
				</label> <select id="dishCategory" name="dishCategory" required>
					<option value="">-- 選択してください --</option>
					<option value="CAT001"
						${mode == 'edit' && dish.category == 'CAT001' ? 'selected' : ''}>CAT001
						- 麺類</option>
					<option value="CAT002"
						${mode == 'edit' && dish.category == 'CAT002' ? 'selected' : ''}>CAT002
						- ご飯</option>
					<option value="CAT003"
						${mode == 'edit' && dish.category == 'CAT003' ? 'selected' : ''}>CAT003
						- 点心</option>
					<option value="CAT004"
						${mode == 'edit' && dish.category == 'CAT004' ? 'selected' : ''}>CAT004
						- 揚げ物</option>
					<option value="CAT005"
						${mode == 'edit' && dish.category == 'CAT005' ? 'selected' : ''}>CAT005
						- ドリンク</option>
					<option value="CAT006"
						${mode == 'edit' && dish.category == 'CAT006' ? 'selected' : ''}>CAT006
						- デザート</option>
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
				<label for="dishPhoto"> 写真ファイル名 </label> <input type="text"
					id="dishPhoto" name="dishPhoto"
					value="${mode == 'edit' ? dish.photo : ''}" maxlength="50"
					placeholder="例: ramen.jpg">
				<div class="hint">最大50文字（省略可）</div>
			</div>

			<div class="form-group">
				<label>状態</label>
				<div class="checkbox-group">
					<input type="checkbox" id="available" name="available" value="1"
						${mode == 'add' || (mode == 'edit' && dish.available) ? 'checked' : ''}>
					<label for="available" style="margin-bottom: 0;">
						有効（メニューに表示する） </label>
				</div>
			</div>

			<div class="button-group">
				<a href="${pageContext.request.contextPath}/admin/dish-manage"
					class="btn btn-secondary"> ← キャンセル </a>

				<button type="submit" class="btn btn-primary">${mode == 'add' ? '➕ 追加' : '💾 更新'}
				</button>
			</div>
		</form>
	</div>
</body>
</html>
