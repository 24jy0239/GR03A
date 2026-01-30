<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
     <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./css/orderCommon.css">
    <title>注文開始</title>
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
<!--    <% if (request.getAttribute("error") != null) { %>-->
<!--        <p style="color:red;"><%= request.getAttribute("error") %></p>-->
<!--    <% } %>-->
    <form action="${pageContext.request.contextPath}/menu" method="get">
        <label>
            テーブル番号：
            <input type="number"
                   name="tableNum"
                   min="1"
                   max="50"
                   required>
        </label>
        <br><br>
        <button type="submit">注文開始</button>
    </form>
</body>
</html>