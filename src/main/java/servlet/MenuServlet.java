package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
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
import model.Visit;

/**
 * MenuServlet - メニュー表示
 * 来店時の初期画面、カート管理
 * 
 * 変更履歴:
 * 2026-02-09: アプリケーションスコープからdishMapを取得（方法A実装）
 */
@WebServlet("/menu")
public class MenuServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * メニュー表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		ServletContext context = getServletContext(); // ← NEW! アプリケーションスコープ

		String categoryId = request.getParameter("category");

		if ("ALL".equals(categoryId)) {
			session.removeAttribute("selectedCategory");
			categoryId = null;
		} else if (categoryId != null) {
			session.setAttribute("selectedCategory", categoryId);
		} else {
			categoryId = (String) session.getAttribute("selectedCategory");
		}

		// テーブル番号を取得（初回のみパラメータから）
		String tableNumStr = request.getParameter("tableNum");

		if (tableNumStr != null) {
			// 新規来店
			int tableNum = Integer.parseInt(tableNumStr);

			// Visit作成
			Visit visit = manager.createVisit(tableNum);

			// Sessionに保存
			session.setAttribute("visitId", visit.getVisitId());
			session.setAttribute("tableNum", tableNum);

			System.out.println("新規来店: visitId=" + visit.getVisitId()
					+ ", table=" + tableNum);
		}

		// ========================================
		// アプリケーションスコープからdishMapを取得（NEW!）
		// ========================================
		// メリット:
		// - 全ユーザーで1つのdishMapを共有（メモリ効率）
		// - 管理画面での変更が即座に全ユーザーに反映
		// - セッション管理不要
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) context.getAttribute("dishMap");

		if (dishMap == null) {
			// フォールバック: DishInitializerListenerが動いていない場合
			// （通常は起動時に初期化されているので、ここには来ない）
			System.out.println("⚠️ dishMapが初期化されていません。再読み込みします。");

			try {
				DishDAO dishDAO = new DishDAO();
				List<Dish> dishes = dishDAO.findAvailable();

				// Map化（高速アクセス用）
				dishMap = new HashMap<>();
				for (Dish dish : dishes) {
					dishMap.put(dish.getDishId(), dish);
				}

				// アプリケーションスコープに保存
				context.setAttribute("dishMap", dishMap);

				System.out.println("料理マスタ読み込み（fallback）: " + dishes.size() + "件");

			} catch (SQLException e) {
				throw new ServletException("料理マスタ読み込みエラー", e);
			}
		}

		// カテゴリフィルター処理（グループメンバーの機能）
		List<Dish> dishList = new ArrayList<>();
		for (Dish dish : dishMap.values()) {

			if (categoryId == null || categoryId.isEmpty()) {
				dishList.add(dish);
			} else if (categoryId.equals(dish.getCategory())) {
				dishList.add(dish);
			}
		}

		request.setAttribute("dishList", dishList);
		request.setAttribute("selectedCategory", categoryId);

		// カートをSessionから取得（なければ新規作成）
		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
		}

		// カートの合計金額と商品数を計算
		int cartTotal = cart.stream()
				.mapToInt(CartItem::getSubtotal)
				.sum();
		int cartCount = cart.size();

		// requestにセット（JSP表示用）
		request.setAttribute("cartTotal", cartTotal);
		request.setAttribute("cartCount", cartCount);

		// sessionにもセット（OrderConfirmServlet用）
		session.setAttribute("cartTotal", cartTotal);
		session.setAttribute("cartCount", cartCount);

		// 注文履歴を取得（あなたのコードの機能）
		String visitId = (String) session.getAttribute("visitId");
		if (visitId != null) {
			Visit visit = manager.getVisit(visitId);
			if (visit != null) {
				// 注文履歴をrequestにセット
				request.setAttribute("visit", visit);
				request.setAttribute("orderHistory", visit.getOrders());
			}
		}

		boolean hasOrder = false;

		if (visitId != null) {
			Visit visit = manager.getVisit(visitId);
			if (visit != null && visit.getOrderCount() > 0) {
				hasOrder = true;
			}
		}

		request.setAttribute("hasOrder", hasOrder);

		// メニュー画面へ
		request.getRequestDispatcher("/WEB-INF/menu.jsp").forward(request, response);
	}

	/**
	 * カート操作
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		ServletContext context = getServletContext(); // ← NEW!

		String action = request.getParameter("action");

		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
		}

		// ========================================
		// アプリケーションスコープからdishMapを取得（NEW!）
		// ========================================
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) context.getAttribute("dishMap");

		if ("add".equals(action)) {
			// カートに追加
			String dishId = request.getParameter("dishId");
			int quantity = Integer.parseInt(request.getParameter("quantity"));

			Dish dish = dishMap.get(dishId);

			if (dish != null) {
				// 既にカートにある場合は数量を加算
				boolean found = false;
				for (CartItem item : cart) {
					if (item.getDishId().equals(dishId)) {
						item.setQuantity(item.getQuantity() + quantity);
						found = true;
						break;
					}
				}

				// なければ新規追加
				if (!found) {
					CartItem newItem = new CartItem(dish, quantity);
					cart.add(newItem);
				}

				System.out.println("カート追加: " + dish.getName() + " x" + quantity);
			}

		} else if ("remove".equals(action)) {
			// カートから削除
			String dishId = request.getParameter("dishId");
			cart.removeIf(item -> item.getDishId().equals(dishId));

			System.out.println("カート削除: dishId=" + dishId);

		} else if ("update".equals(action)) {
			// 数量更新
			String dishId = request.getParameter("dishId");
			int quantity = Integer.parseInt(request.getParameter("quantity"));

			for (CartItem item : cart) {
				if (item.getDishId().equals(dishId)) {
					item.setQuantity(quantity);
					break;
				}
			}

			System.out.println("カート更新: dishId=" + dishId + ", quantity=" + quantity);

		} else if ("decrease".equals(action)) {
			String dishId = request.getParameter("dishId");

			for (int i = 0; i < cart.size(); i++) {
				CartItem item = cart.get(i);
				if (item.getDishId().equals(dishId)) {
					int newQty = item.getQuantity() - 1;

					if (newQty <= 0) {
						cart.remove(i);
					} else {
						item.setQuantity(newQty);
					}
					break;
				}
			}

			System.out.println("カート減少: dishId=" + dishId);
		} else if ("clear".equals(action)) {
			// カートをクリア
			cart.clear();
			System.out.println("カートクリア");
		}

		// メニュー画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/menu");
	}
}
