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
 * 
 * 機能:
 * 1. カートの内容を確認
 * 2. 合計金額計算
 * 3. 注文確認画面へ遷移
 * 
 * URL: /order-confirm
 */
@WebServlet("/order/confirm")
public class OrderConfirmServlet extends HttpServlet {

	/**
	 * 注文確認画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		// カート取得
		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		// カート空チェック
		if (cart == null || cart.isEmpty()) {
			System.out.println("エラー: カートが空です");
			session.setAttribute("error", "カートが空です。商品を選択してください。");
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		// テーブル番号チェック（セッション確認）
		Integer tableNum = (Integer) session.getAttribute("tableNum");
		if (tableNum == null) {
			System.out.println("エラー: テーブル番号がセッションにありません");
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 合計金額計算
		int cartTotal = cart.stream()
				.mapToInt(CartItem::getSubtotal)
				.sum();

		System.out.println("====================================");
		System.out.println("注文確認画面:");
		System.out.println("  テーブル番号: " + tableNum);
		System.out.println("  カート商品数: " + cart.size());
		System.out.println("  合計金額: ¥" + cartTotal);
		for (CartItem item : cart) {
			System.out.println("    - " + item.getName()
					+ " x" + item.getQuantity()
					+ " = ¥" + item.getSubtotal());
		}
		System.out.println("====================================");

		// requestに設定（JSP表示用）
		request.setAttribute("cartTotal", cartTotal);

		// 注文確認画面へ転送
		request.getRequestDispatcher("/WEB-INF/order-confirm.jsp")
				.forward(request, response);
	}
}