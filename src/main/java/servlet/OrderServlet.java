package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import manager.OrderManager;
import model.CartItem;
import model.Order;

/**
 * OrderServlet - 注文処理
 * カートから注文を作成
 */
@WebServlet("/order")
public class OrderServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * 注文確認画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null || cart.isEmpty()) {
			// カートが空の場合はメニューへ戻す
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		// カート合計計算
		int cartTotal = cart.stream()
				.mapToInt(CartItem::getSubtotal)
				.sum();

		request.setAttribute("cartTotal", cartTotal);

		// 注文確認画面へ
		request.getRequestDispatcher("/WEB-INF/order-confirm.jsp").forward(request, response);
	}

	/**
	 * 注文確定
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String visitId = (String) session.getAttribute("visitId");

		if (visitId == null) {
			// visitIdがない場合はエラー
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null || cart.isEmpty()) {
			// カートが空の場合はメニューへ戻す
			response.sendRedirect(request.getContextPath() + "/menu?sent=1");
			return;
		}

		try {
			// カートから注文作成（メモリに保存）
			Order order = manager.createOrderFromCart(visitId, cart);

			System.out.println("注文確定: orderId=" + order.getOrderId()
					+ ", items=" + order.getItemCount()
					+ ", total=" + order.calculateTotal());

			// データベースに保存（重要！）
			try {
				manager.saveOrderItems(order);
				System.out.println("DB保存完了: orderId=" + order.getOrderId());
			} catch (Exception e) {
				System.err.println("DB保存エラー: " + e.getMessage());
				e.printStackTrace();
				// DB保存に失敗してもメモリには保存されているので続行
			}

			// カートをクリア
			cart.clear();

			// sessionのカウントもクリア
			session.setAttribute("cartCount", 0);
			session.setAttribute("cartTotal", 0);

			// 成功メッセージ
			session.setAttribute("message", "ご注文ありがとうございます！");

			// メニュー画面へリダイレクト（追加注文可能）
			response.sendRedirect(request.getContextPath() + "/menu?sent=1");

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("注文処理エラー", e);
		}
	}
}
