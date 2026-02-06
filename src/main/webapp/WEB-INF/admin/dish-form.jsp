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
<style>
body {
	display: flex;
	flex-direction: column;
	height: 100vh;
	margin: 0;
}

.new-menu-container {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
	margin-top: 80px;
}

.btn-large {
	width: 320px !important;
	height: 75px;
	font-size: 26px;
	transition: all 0.1s;
}

.btn-large:active, #topBtn:active {
	transform: translateY(4px) scale(0.96);
	box-shadow: none;
}

#topBtn {
	position: absolute;
	right: 40px;
	top: 30px;
	width: 200px;
	height: 60px;
}
</style>
</head>
<body>
	<header class="header">
		<h1 class="page-title"><%=pageTitle%></h1>
		<button id="topBtn" class="btn-primary" onclick="history.back()">戻る</button>
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
						String imgPath = null;
						if (fileName != null && !fileName.isEmpty()) {
							imgPath = request.getContextPath() + "/images/" + fileName;
						}
						if (imgPath != null) {
						%>
						<img src="<%=imgPath%>" alt="料理写真"
							style="width: 100%; height: 100%; object-fit: cover;">
						<%
						} else {
						%>
						<p>プレビュー</p>
						<%
						}
						%>
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
						<div class="price-wrapper">
							<input type="number" name="dishPrice" class="field-input"
								value="<%=isEdit ? dish.getPrice() : ""%>" required> <span
								class="price-unit">円</span>
						</div>
					</div>
					<div class="field-row status-row">
						<input type="checkbox" name="available" value="1"
							id="availableCheck"
							<%=(!isEdit || dish.isAvailable()) ? "checked" : ""%>> <label
							for="availableCheck">有効（メニューに表示）</label>
					</div>
				</div>
			</section>

			<section class="new-menu-buttons">
				<button type="button" class="btn-large btn-primary"
					onclick="document.getElementById('fileInput').click()">写真をアップロード</button>
				<input type="file" id="fileInput" name="fileInput"
					style="display: none;" accept="image/*"> <input
					type="hidden" name="dishPhoto" id="dishPhoto"
					value="<%=isEdit ? dish.getPhoto() : ""%>">

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
		</main>
	</form>

	<script>
		// 提交表单前的确认
		function confirmUpdate() {
			if (confirm("この内容で保存してもよろしいですか？")) {
				document.getElementById('dishForm').submit();
			}
		}

		// 删除逻辑
		function confirmDelete() {
			if (confirm("この料理を削除してもよろしいですか？")) {
				const actionInput = document.getElementById('formAction');
				actionInput.value = 'delete';
				document.getElementById('dishForm').submit();
			}
		}

		// 图片实时预览功能
		document
				.getElementById('fileInput')
				.addEventListener(
						'change',
						function(e) {
							const file = e.target.files[0];
							const dropArea = document
									.getElementById('dropArea');

							if (file) {
								// 1. 验证是否为图片
								if (!file.type.startsWith('image/')) {
									alert('画像ファイルを選択してください。');
									return;
								}

								const reader = new FileReader();

								// 2. 文件读取成功后的回调
								reader.onload = function(event) {
									// 清空预览区域的所有文字（比如那个“プレビュー”）
									dropArea.innerHTML = '';

									// 创建并插入图片元素
									const img = document.createElement('img');
									img.src = event.target.result;
									img.style.width = '100%';
									img.style.height = '100%';
									img.style.objectFit = 'cover'; // 确保图片不变形

									dropArea.appendChild(img);

									// 更新隐藏域的文件名
									document.getElementById('dishPhoto').value = file.name;
								};

								// 3. 开始读取文件
								reader.readAsDataURL(file);
							}
						});
	</script>
</body>
</html>