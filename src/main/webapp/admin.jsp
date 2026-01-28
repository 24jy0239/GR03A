<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理画面 - レストラン注文システム</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Hiragino Sans', 'メイリオ', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            padding: 60px 40px;
            border-radius: 20px;
            max-width: 800px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }
        
        h1 {
            text-align: center;
            color: #333;
            font-size: 2.5em;
            margin-bottom: 15px;
        }
        
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 50px;
            font-size: 1.1em;
        }
        
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }
        
        .menu-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            color: white;
            display: block;
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
        }
        
        .menu-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.5);
        }
        
        .menu-icon {
            font-size: 3em;
            margin-bottom: 15px;
        }
        
        .menu-title {
            font-size: 1.3em;
            font-weight: bold;
            margin-bottom: 10px;
        }
        
        .menu-description {
            font-size: 0.9em;
            opacity: 0.9;
        }
        
        .back-link {
            display: block;
            text-align: center;
            color: #667eea;
            text-decoration: none;
            font-size: 1.1em;
            padding: 15px;
            border: 2px solid #667eea;
            border-radius: 10px;
            transition: all 0.3s;
        }
        
        .back-link:hover {
            background: #667eea;
            color: white;
        }
        
        @media (max-width: 600px) {
            .container {
                padding: 40px 20px;
            }
            
            h1 {
                font-size: 2em;
            }
            
            .menu-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔧 管理画面</h1>
        <p class="subtitle">レストラン注文システム</p>
        
        <div class="menu-grid">
            <a href="<%= request.getContextPath() %>/admin/hall" class="menu-card">
                <div class="menu-icon">🏪</div>
                <div class="menu-title">ホール管理</div>
                <div class="menu-description">全テーブルの注文状況を確認</div>
            </a>
            
            <a href="<%= request.getContextPath() %>/admin/kitchen" class="menu-card">
                <div class="menu-icon">👨‍🍳</div>
                <div class="menu-title">キッチン管理</div>
                <div class="menu-description">調理待ちの注文を確認</div>
            </a>
            
            <a href="<%= request.getContextPath() %>/admin/table-status" class="menu-card">
                <div class="menu-icon">📊</div>
                <div class="menu-title">テーブル状況</div>
                <div class="menu-description">テーブルの利用状況を確認</div>
            </a>
            
            <a href="<%= request.getContextPath() %>/admin/dish-manage" class="menu-card">
                <div class="menu-icon">🍽️</div>
                <div class="menu-title">料理マスタ管理</div>
                <div class="menu-description">メニューの追加・編集・削除</div>
            </a>
        </div>
        
        <a href="<%= request.getContextPath() %>/" class="back-link">
            🏠 顧客画面に戻る
        </a>
    </div>
</body>
</html>
