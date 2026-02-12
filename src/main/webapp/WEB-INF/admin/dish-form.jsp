<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.Dish"%>
<%
Dish dish = (Dish) request.getAttribute("dish");
String mode = (String) request.getAttribute("mode");
String nextId = (String) request.getAttribute("nextId");

if (mode == null)
	mode = "add";
boolean isEdit = "edit".equals(mode);

String pageTitle = isEdit ? "メニュー詳細" : "メニュー新規";
String currentCat = (isEdit && dish != null && dish.getCategory() != null) ? dish.getCategory().trim() : "";
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><%=pageTitle%></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/addMenu.css">
</head>
<body>
	<header class="header">
		<h1 class="page-title"><%=pageTitle%></h1>
		<button id="topBtn" type="button" onclick="history.back()">戻る</button>
	</header>

	<form action="${pageContext.request.contextPath}/admin/dish-manage"
		method="post" id="dishForm" enctype="multipart/form-data">

		<main class="new-menu-container">
			<input type="hidden" name="action" id="formAction" value="<%=mode%>">
			<input type="hidden" name="dishId"
				value="<%=isEdit ? dish.getDishId() : nextId%>">

			<section class="new-menu-row">
				<div class="photo-column">
					<div class="upload-box" id="dropArea">
						<%
						String fileName = (dish != null) ? dish.getPhoto() : null;
						if (fileName != null && !fileName.isEmpty() && !"no-image.jpg".equals(fileName) && !"null".equals(fileName)) {
						%>
						<img src="${pageContext.request.contextPath}/images/<%=fileName%>"
							alt="料理写真">
						<%
						} else {
						%>
						<p style="color: #aaa; font-weight: bold; font-size: 1.2rem;">写真プレビュー</p>
						<%
						}
						%>
					</div>

					<div class="dev-option-area">
						<label class="dev-option-title"> 【開発用】ソースフォルダ保存パス </label>
						<div class="dev-option-desc">
							※設定するとEclipse再起動後も画像が消えません。<br> ※空欄またはパスが存在しない場合は無視されます。
						</div>
						<input type="text" name="sourcePath" class="dev-option-input"
							value="C:\pleiades\2024-12\workspace\GR03A\src\main\webapp\images"
							placeholder="例: C:\Projects\MyApp\src\main\webapp\images">
					</div>
				</div>

				<div class="form-fields">
					<div class="field-row">
						<label class="field-label">種類：</label> <select name="dishCategory"
							class="field-input" required>
							<option value="">-- 選択 --</option>
							<option value="CAT001"
								<%="CAT001".equals(currentCat) ? "selected" : ""%>>麺類</option>
							<option value="CAT002"
								<%="CAT002".equals(currentCat) ? "selected" : ""%>>ご飯</option>
							<option value="CAT003"
								<%="CAT003".equals(currentCat) ? "selected" : ""%>>点心</option>
							<option value="CAT004"
								<%="CAT004".equals(currentCat) ? "selected" : ""%>>揚げ物</option>
							<option value="CAT005"
								<%="CAT005".equals(currentCat) ? "selected" : ""%>>ドリンク</option>
							<option value="CAT006"
								<%="CAT006".equals(currentCat) ? "selected" : ""%>>デザート</option>
						</select>
					</div>
					<div class="field-row">
						<label class="field-label">品名：</label> <input type="text"
							name="dishName" class="field-input"
							value="<%=isEdit ? dish.getName() : ""%>" required>
					</div>
					<div class="field-row">
						<label class="field-label">価格：</label>
						<div style="display: flex; align-items: center; flex: 1;">
							<input type="number" name="dishPrice" class="field-input"
								value="<%=isEdit ? dish.getPrice() : ""%>" required> <span
								class="price-unit">円</span>
						</div>
					</div>
					<div class="field-row">
						<span class="field-label"></span> <label class="checkbox-label">
							<input type="checkbox" name="available" value="1"
							class="checkbox-input"
							<%=(!isEdit || dish.isAvailable()) ? "checked" : ""%>>
							有効（メニューに表示）
						</label>
					</div>
				</div>
			</section>

			<section class="new-menu-buttons">
				<button type="button" class="btn-large btn-primary"
					onclick="document.getElementById('fileInput').click()">写真を選択</button>

				<button type="button" class="btn-large btn-primary"
					onclick="confirmUpdate()"><%=isEdit ? "更新" : "保存"%></button>

				<%
				if (isEdit) {
				%>
				<button type="button" class="btn-large btn-danger"
					onclick="confirmDelete()">削除</button>
				<%
				} else {
				%>
				<button type="button" class="btn-large btn-danger"
					onclick="history.back()">取消す</button>
				<%
				}
				%>
			</section>

			<input type="file" id="fileInput" name="fileInput"
				style="display: none;" accept="image/*"> <input
				type="hidden" name="dishPhoto" id="dishPhoto"
				value="<%=(isEdit && dish.getPhoto() != null) ? dish.getPhoto() : ""%>">

		</main>
	</form>

	<script>
		function confirmUpdate() {
			if (confirm("この内容で保存してもよろしいですか？")) {
				document.getElementById('dishForm').submit();
			}
		}

		function confirmDelete() {
			if (confirm("この料理を削除してもよろしいですか？")) {
				const actionInput = document.getElementById('formAction');
				actionInput.value = 'delete';
				document.getElementById('dishForm').submit();
			}
		}

		document.getElementById('fileInput').addEventListener('change',
				function(e) {
					const file = e.target.files[0];
					const dropArea = document.getElementById('dropArea');

					if (file) {
						if (!file.type.startsWith('image/')) {
							alert('画像ファイルを選択してください。');
							return;
						}
						const reader = new FileReader();
						reader.onload = function(event) {
							dropArea.innerHTML = '';
							const img = document.createElement('img');
							img.src = event.target.result;
							dropArea.appendChild(img);
						};
						reader.readAsDataURL(file);
					}
				});
	</script>
</body>
</html>