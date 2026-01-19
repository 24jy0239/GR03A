<%@ page contentType="text/html; charset=UTF-8" %>

<%
    // ===== 桌号取得（URL 假设传入）=====
    String tableNumParam = request.getParameter("tableNum");

    if (tableNumParam == null) {
        out.println("テーブル番号が設定されていません。");
        return;
    }

    int tableNum;
    try {
        tableNum = Integer.parseInt(tableNumParam);
    } catch (NumberFormatException e) {
        out.println("テーブル番号が不正です。");
        return;
    }

    // ===== Session 保存 =====
    session.setAttribute("tableNum", tableNum);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注文開始</title>
    <link rel="stylesheet" href="./css/orderCommon.css">
</head>

<style>
	body {
		justify-content: center;
		/* 縦方向の中央寄せ */
		align-items: center;
		/* 横方向の中央寄せ */
	}
</style>

<body>
    <h1>いらっしゃいませ！</h1>

<!--     桌号显示（可留可不留） -->
<!--    <p>テーブル番号：<%= tableNum %></p>-->

    <button type="button"
        onclick="location.href='menu.jsp'"
        style="background-color: gold;">
        注文開始
    </button>
</body>
</html>
