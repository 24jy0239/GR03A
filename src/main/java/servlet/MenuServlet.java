package servlet;

import java.io.IOException;
import java.sql.SQLException;
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
import model.Visit;

/**
 * MenuServlet - メニュー表示
 * 来店時の初期画面、カート管理
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

		// 料理マスタをSessionから取得（なければDB読み込み）
		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) session.getAttribute("dishMap");

		if (dishMap == null) {
			// 初回のみDB読み込み
			try {
				DishDAO dishDAO = new DishDAO();
				List<Dish> dishes = dishDAO.findAvailable();

				// Map化（高速アクセス用）
				dishMap = new HashMap<>();
				for (Dish dish : dishes) {
					dishMap.put(dish.getDishId(), dish);
				}

			
				session.setAttribute("dishMap", dishMap);

				System.out.println("料理マスタ読み込み: " + dishes.size() + "件");

			} catch (SQLException e) {
				throw new ServletException("料理マスタ読み込みエラー", e);
			}
		}

		// カートをSessionから取得（なければ新規作成）
		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
		}

		// カートの合計金額を計算
		int cartTotal = cart.stream()
				.mapToInt(CartItem::getSubtotal)
				.sum();

		request.setAttribute("cartTotal", cartTotal);
		request.setAttribute("cartCount", cart.size());

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

		String action = request.getParameter("action");

		@SuppressWarnings("unchecked")
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
		}

		@SuppressWarnings("unchecked")
		Map<String, Dish> dishMap = (Map<String, Dish>) session.getAttribute("dishMap");

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

		} else if ("clear".equals(action)) {
			// カートをクリア
			cart.clear();
			System.out.println("カートクリア");
		}

		// メニュー画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/menu");
	}
}
