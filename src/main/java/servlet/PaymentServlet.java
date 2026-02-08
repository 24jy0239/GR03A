package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DishDAO;
import manager.OrderManager;
import model.CartItem;
import model.Dish;
import model.Order;
import model.OrderItem;
import model.Visit;

/**
 * PaymentServlet - 会計処理（詳細表示付き会計確認画面）
 * 
 * 機能:
 * 1. 会計確認画面の表示 (GET) - 注文明細付き
 * 2. 会計処理の実行 (POST)
 * 3. Visit完了処理 (paymentTime設定)
 * 4. データベースへの保存（重複チェック付き）
 * 5. 会計完了画面への遷移
 * 
 * URL: /order/payment （統一性のため）
 * 
 * フロー:
 * GET  /order/payment → payment-confirm.jsp（会計確認画面：明細付き）
 * POST /order/payment → 会計処理 → payment-complete.jsp（会計完了画面）
 * 
 * 変更履歴:
 * 2026-02-02: 重複保存防止機能追加
 * 2026-02-02: 会計確認画面追加
 * 2026-02-02: 注文明細表示追加（既存コード活用）
 * 2026-02-02: URL変更 /payment → /order/payment（統一性のため）
 */
@WebServlet("/order/payment")
public class PaymentServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * 会計確認画面表示 (GET) - 注文明細付き
	 * 
	 * 処理フロー:
	 * 1. visitId検証
	 * 2. Visit取得
	 * 3. 注文チェック
	 * 4. 会計済みチェック
	 * 5. 注文明細をCartItemに変換（OrderHistoryServletと同じロジック）
	 * 6. 会計確認画面へ転送（注文明細付き）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String visitId = (String) session.getAttribute("visitId");

		// セッションチェック
		if (visitId == null) {
			System.out.println("エラー: visitIdがセッションにありません");
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// Visit取得
		Visit visit = manager.getVisit(visitId);
		if (visit == null) {
			System.out.println("エラー: visitが見つかりません: " + visitId);
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 注文チェック
		if (visit.getOrderCount() == 0) {
			System.out.println("エラー: 注文がありません");
			session.setAttribute("error", "注文がありません。");
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		// 既に会計済みかチェック
		if (visit.getPaymentTime() != null) {
			System.out.println("警告: 既に会計済みです: " + visitId);
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());
			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);
			return;
		}

		// ========================================
		// 注文明細をCartItemに変換
		// ========================================

		// sessionのdishMapを取得（最適化）
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) session.getAttribute("dishMap");
		DishDAO dishDAO = null;

		// Order → CartItem 変換（同じ商品を集計）
		Map<String, CartItem> summaryMap = new HashMap<>();
		// ========== 削除: int visitTotal = 0; ==========

		List<Order> orders = visit.getOrders();

		if (orders != null && !orders.isEmpty()) {
			for (Order order : orders) {
				List<OrderItem> orderItems = order.getOrderItems();

				if (orderItems != null) {
					for (OrderItem item : orderItems) {
						String dishId = item.getDishId();

						// 既存のCartItemを取得、なければ新規作成
						CartItem cartItem = summaryMap.get(dishId);

						if (cartItem == null) {
							String photo = null;

							// まずsessionのdishMapから取得を試みる
							if (dishMap != null && dishMap.containsKey(dishId)) {
								Dish dish = dishMap.get(dishId);
								photo = dish.getPhoto();
							} else {
								// dishMapにない場合のみDBアクセス
								if (dishDAO == null) {
									dishDAO = new DishDAO();
								}

								try {
									Dish dish = dishDAO.findById(dishId);
									if (dish != null) {
										photo = dish.getPhoto();
									}
								} catch (Exception e) {
									System.err.println("写真取得エラー: dishId=" + dishId);
									e.printStackTrace();
								}
							}

							// CartItem作成
							cartItem = new CartItem(
									dishId,
									item.getDishName(),
									item.getPrice(),
									0,
									photo);

							summaryMap.put(dishId, cartItem);
						}

						// 数量を加算
						cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());

						// ========== 削除: visitTotal += item.getSubtotal(); ==========
					}
				}
			}
		}

		// MapをListに変換
		List<CartItem> orderDetailsList = new ArrayList<>(summaryMap.values());

		// ========================================
		// 新コード: Visit.getTotalAmount()を使用（統一！）
		// ========================================
		int visitTotal = visit.getTotalAmount();

		System.out.println("====================================");
		System.out.println("会計確認画面表示:");
		System.out.println("  visitId: " + visitId);
		System.out.println("  テーブル番号: " + visit.getTableNum());
		System.out.println("  注文件数: " + visit.getOrderCount());
		System.out.println("  明細項目数: " + orderDetailsList.size());
		System.out.println("  合計金額: ¥" + visitTotal);
		System.out.println("====================================");

		// Visit情報とカート情報をrequestに設定
		request.setAttribute("visit", visit);
		request.setAttribute("totalAmount", visitTotal);
		request.setAttribute("tableNum", visit.getTableNum());
		request.setAttribute("orderCount", visit.getOrderCount());
		request.setAttribute("orderDetailsList", orderDetailsList);

		// 会計確認画面へ転送（注文明細付き）
		request.getRequestDispatcher("/WEB-INF/payment-confirm.jsp")
				.forward(request, response);

	}

	/**
	 * 会計処理実行 (POST)
	 * 
	 * 処理フロー:
	 * 1. visitId検証
	 * 2. Visit取得
	 * 3. 会計済みチェック（重複防止）
	 * 4. 会計完了処理 (completeVisit)
	 * 5. DB保存 (saveVisitWithAllOrders) - 重複エラー対応
	 * 6. セッションクリア
	 * 7. 会計完了画面へ遷移
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String visitId = (String) session.getAttribute("visitId");

		// セッションチェック
		if (visitId == null) {
			System.out.println("エラー: visitIdがセッションにありません");
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// Visit取得
		Visit visit = manager.getVisit(visitId);
		if (visit == null) {
			System.out.println("エラー: visitが見つかりません: " + visitId);
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 既に会計済みかチェック（重複防止）
		if (visit.getPaymentTime() != null) {
			System.out.println("====================================");
			System.out.println("警告: 既に会計済みです");
			System.out.println("  visitId: " + visitId);
			System.out.println("  会計時刻: " + visit.getPaymentTime());
			System.out.println("  合計金額: ¥" + visit.getTotalAmount());
			System.out.println("====================================");

			// 会計完了画面へ遷移（再表示）
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);
			return;
		}

		try {
			// 会計時刻記録
			LocalDateTime paymentTime = LocalDateTime.now();

			// Visit完了処理
			manager.completeVisit(visitId, paymentTime);

			System.out.println("====================================");
			System.out.println("会計完了処理:");
			System.out.println("  visitId: " + visitId);
			System.out.println("  テーブル番号: " + visit.getTableNum());
			System.out.println("  合計金額: ¥" + visit.getTotalAmount());
			System.out.println("  注文件数: " + visit.getOrderCount());
			System.out.println("  会計時刻: " + paymentTime);
			System.out.println("====================================");

			// データベース保存（重複エラー処理付き）
			try {
				manager.saveVisitWithAllOrders(visitId);
				System.out.println("✅ DB保存完了: visitId=" + visitId);

			} catch (SQLIntegrityConstraintViolationException e) {
				// PRIMARY KEY重複エラー（既にDB保存済み）
				System.out.println("⚠️ DB保存スキップ: 既に保存済みです");
				System.out.println("  visitId: " + visitId);
				System.out.println("  エラー詳細: " + e.getMessage());

				// エラーログは出すが、処理は継続
				// （会計完了画面は正常に表示）
			}

			// 会計完了情報をrequestに設定
			// 重要: payment-complete.jspはrequest属性から取得
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			// セッションクリア（次の来店のため）
			session.removeAttribute("visitId");
			session.removeAttribute("tableNum");
			session.removeAttribute("cart");
			session.removeAttribute("cartTotal");
			session.removeAttribute("cartCount");

			System.out.println("🔄 セッションクリア完了");

			// 会計完了画面へ転送
			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);

		} catch (SQLException e) {
			// その他のDB保存エラー
			System.err.println("❌ DB保存エラー: " + e.getMessage());
			e.printStackTrace();

			// エラー画面へ遷移
			request.setAttribute("errorMessage",
					"データベース保存中にエラーが発生しました。システム管理者に連絡してください。");
			request.setAttribute("errorDetail", e.getMessage());

			// エラーページへ転送（または適切なエラーハンドリング）
			throw new ServletException("データベース保存中にエラーが発生しました", e);
		}
	}
}
