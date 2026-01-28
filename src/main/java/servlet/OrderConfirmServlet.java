package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.CartItem;

/**
 * OrderConfirmServlet - 注文確認画面
 * カートの内容を確認して注文確認ページを表示
 */
@WebServlet("/order-confirm")
public class OrderConfirmServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		// ① カートが空ならメニューへ
		if (cart == null || cart.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		// ② テーブル番号チェック
		Integer tableNum = (Integer) session.getAttribute("tableNum");
		if (tableNum == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// ⭐ ③【ここに加える】合計金額を計算
		int cartTotal = cart.stream()
				.mapToInt(CartItem::getSubtotal)
				.sum();

		// ⭐ ④ request に詰める
		request.setAttribute("cartTotal", cartTotal);

		// ⑤ JSPへ
		request.getRequestDispatcher("/WEB-INF/order-confirm.jsp")
				.forward(request, response);
	}
}
