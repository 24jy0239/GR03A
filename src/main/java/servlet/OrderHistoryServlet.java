package servlet;

import java.io.IOException;
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
 * OrderHistoryServlet - 注文履歴表示（最適化版）
 * 
 * 機能:
 * 1. セッションのdishMapを優先使用（DBアクセス最小化）
 * 2. 同じ商品を集計してCartItemに変換
 * 3. 注文履歴画面に表示
 * 
 * URL: /order/history
 * 
 * 最適化ポイント:
 * - 初回: DBからdishMapをロード → セッションに保存（MenuServletで実施済み）
 * - 2回目以降: セッションのdishMapから取得（DBアクセスなし）
 * - dishMapにない場合のみDBアクセス（フォールバック）
 */
@WebServlet("/order/history")
public class OrderHistoryServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String visitId = (String) session.getAttribute("visitId");

		// visitIdチェック
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

		System.out.println("====================================");
		System.out.println("注文履歴表示:");
		System.out.println("  visitId: " + visitId);
		System.out.println("  テーブル番号: " + visit.getTableNum());

		// ========================================
		// 最適化: sessionのdishMapを優先使用
		// ========================================
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) session.getAttribute("dishMap");

		// DishDAOはフォールバック用（dishMapにない場合のみ）
		DishDAO dishDAO = null;

		// Order → OrderItem → CartItem 変換（同じ商品を集計）
		Map<String, CartItem> summaryMap = new HashMap<>();
		int visitTotal = 0;
		int dbAccessCount = 0; // DBアクセス回数（パフォーマンス確認用）

		List<Order> orders = visit.getOrders();

		if (orders != null && !orders.isEmpty()) {
			System.out.println("  注文件数: " + orders.size());

			for (Order order : orders) {
				List<OrderItem> orderItems = order.getOrderItems();

				if (orderItems != null) {
					for (OrderItem item : orderItems) {
						String dishId = item.getDishId();

						// 既存のCartItemを取得、なければ新規作成
						CartItem cartItem = summaryMap.get(dishId);

						if (cartItem == null) {
							String photo = null;

							// ========================================
							// 最適化: まずsessionのdishMapから取得を試みる
							// ========================================
							if (dishMap != null && dishMap.containsKey(dishId)) {
								Dish dish = dishMap.get(dishId);
								photo = dish.getPhoto();
								System.out.println("    [キャッシュ] dishId=" + dishId + " (session)");

							} else {
								// dishMapにない場合のみDBアクセス（フォールバック）
								if (dishDAO == null) {
									dishDAO = new DishDAO();
								}

								try {
									Dish dish = dishDAO.findById(dishId);
									if (dish != null) {
										photo = dish.getPhoto();
									}
									dbAccessCount++;
									System.out.println("    [DB] dishId=" + dishId + " (fallback)");

								} catch (Exception e) {
									System.err.println("写真取得エラー: dishId=" + dishId);
									e.printStackTrace();
									// photoはnullのまま（デフォルト画像が使用される）
								}
							}

							// CartItem作成（5引数コンストラクタ使用）
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

						// 合計金額を計算
						visitTotal += item.getSubtotal();

						System.out.println("    - " + item.getDishName()
								+ " x" + item.getQuantity()
								+ " (累計: " + cartItem.getQuantity() + ")");
					}
				}
			}
		} else {
			System.out.println("  注文なし");
		}

		// MapをListに変換
		List<CartItem> summaryList = new ArrayList<>(summaryMap.values());

		System.out.println("  合計金額: ¥" + visitTotal);
		System.out.println("  表示項目数: " + summaryList.size());
		System.out.println("  DBアクセス回数: " + dbAccessCount + " 回");
		System.out.println("====================================");

		// requestに設定
		request.setAttribute("summaryList", summaryList);
		request.setAttribute("visitTotal", visitTotal);

		// 注文履歴画面へ
		request.getRequestDispatcher("/WEB-INF/order-history.jsp")
				.forward(request, response);
	}
}
