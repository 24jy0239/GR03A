package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * OrderConfirmServlet - 注文確認画面
 * カートの内容を確認して注文確認ページを表示
 */
@WebServlet("/order-confirm")
public class OrderConfirmServlet extends HttpServlet {
    
    /**
     * 注文確認画面を表示
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // カートが空の場合はメニューに戻す
        Integer cartCount = (Integer) session.getAttribute("cartCount");
        if (cartCount == null || cartCount == 0) {
            response.sendRedirect(request.getContextPath() + "/menu");
            return;
        }
        
        // テーブル番号がない場合もメニューに戻す
        Integer tableNum = (Integer) session.getAttribute("tableNum");
        if (tableNum == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // 注文確認画面を表示
        // cart, cartCount, cartTotal はすでにセッションにあるので
        // 追加のデータ準備は不要
		request.getRequestDispatcher("/order-confirm.jsp").forward(request, response);
	}
}
