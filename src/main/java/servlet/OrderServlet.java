package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import manager.OrderManager;
import model.CartItem;
import model.Dish;
import model.Order;
import model.Visit;

/**
 * OrderServlet - 注文処理
 * カートから注文を作成（デバッグログ付き）
 * 
 * 変更履歴:
 * 2026-02-16: 注文確定前の料理有効性バリデーション追加
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

		// ========================================
		// 重要: 注文確定前の料理有効性バリデーション（NEW!）
		// ========================================
		String validationResult = validateCart(cart, session);
		if (validationResult != null) {
			// バリデーションエラー
			System.err.println("⚠️ 注文確定前バリデーションエラー: " + validationResult);
			session.setAttribute("error", validationResult);
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		System.out.println("✅ カート内の全料理が有効です");

		// ========================================
		// バリデーションOK: 注文を確定
		// ========================================
		try {
			// カートから注文作成（メモリに保存）
			Order order = manager.createOrderFromCart(visitId, cart);

			// ========== デバッグログ開始 ==========
			System.out.println("==========================================");
			System.out.println("【OrderServlet】注文確定");
			System.out.println("------------------------------------------");
			System.out.println("OrderManager instance: " + System.identityHashCode(manager));
			System.out.println("visitId: " + visitId);
			System.out.println("orderId: " + order.getOrderId());
			System.out.println("items: " + order.getItemCount());
			System.out.println("total: " + order.calculateTotal());

			// visits の中身を確認
			Visit visit = manager.getVisit(visitId);
			if (visit != null) {
				System.out.println("✅ visits に Visit が存在");
				System.out.println("   tableNum: " + visit.getTableNum());
				System.out.println("   orders count: " + visit.getOrders().size());

				// 全ての OrderItem の状態を確認
				System.out.println("   OrderItems:");
				for (Order o : visit.getOrders()) {
					for (model.OrderItem item : o.getOrderItems()) {
						System.out.println("     - " + item.getDishName()
								+ " (status=" + item.getItemStatus()
								+ ", qty=" + item.getQuantity() + ")");
					}
				}
			} else {
				System.out.println("❌ visits に Visit が存在しない！");
			}
			System.out.println("------------------------------------------");
			try {
				manager.saveOrderItems(order);
				System.out.println("✅ DB保存完了: orderId=" + order.getOrderId());
			} catch (Exception e) {
				System.err.println("❌ DB保存エラー: " + e.getMessage());
				e.printStackTrace();
				// DB保存に失敗してもメモリには保存されているので続行
			}

			System.out.println("==========================================");

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
			System.out.println("==========================================");
			System.out.println("❌❌❌ エラー発生 ❌❌❌");
			System.out.println("==========================================");
			e.printStackTrace();
			throw new ServletException("注文処理エラー", e);
		}
	}

	// ==================== バリデーションメソッド ====================

	/**
	 * カート内の全料理の有効性を検証
	 * 
	 * @param cart カート内の商品
	 * @param session HTTPセッション
	 * @return エラーメッセージ（null = 検証OK）
	 */
	private String validateCart(List<CartItem> cart, HttpSession session) {
		ServletContext context = getServletContext();
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) context.getAttribute("dishMap");

		// dishMapがnullの場合（異常事態）
		if (dishMap == null) {
			System.err.println("❌ エラー: dishMapがnull");
			return "メニューデータの読み込みに失敗しました。ページを更新してください。";
		}

		// 無効な料理を検出
		List<String> invalidDishes = new ArrayList<>();
		List<CartItem> validItems = new ArrayList<>();

		for (CartItem item : cart) {
			Dish dish = dishMap.get(item.getDishId());

			if (dish == null) {
				// 料理が無効化されている
				invalidDishes.add(item.getName());
				System.err.println("❌ 無効な料理を検出: " + item.getName() + " (" + item.getDishId() + ")");
			} else {
				// 料理は有効
				validItems.add(item);
			}
		}

		// 無効な料理が見つかった場合
		if (!invalidDishes.isEmpty()) {
			// カートから無効な料理を削除
			session.setAttribute("cart", validItems);

			// エラーメッセージを生成
			StringBuilder message = new StringBuilder();
			message.append("以下の料理は現在ご注文いただけません：\n");
			for (String dishName : invalidDishes) {
				message.append("・").append(dishName).append("\n");
			}
			message.append("\nカートから削除しました。メニューを確認してください。");

			System.out.println("⚠️ 無効な料理を" + invalidDishes.size() + "件検出 → カートから削除");

			return message.toString();
		}

		// すべて有効
		return null; // 検証OK
	}
}
