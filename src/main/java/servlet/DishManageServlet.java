package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DishDAO;
import model.Dish;

/**
 * DishManageServlet - 料理マスタ管理
 * 料理の一覧・追加・編集・削除
 */
@WebServlet("/admin/dish-manage")
public class DishManageServlet extends HttpServlet {

	private DishDAO dishDAO = new DishDAO();

	/**
	 * 料理一覧表示 or 料理編集フォーム表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		try {
			if ("edit".equals(action)) {
				// 編集フォーム表示
				showEditForm(request, response);

			} else if ("add".equals(action)) {
				// 追加フォーム表示
				showAddForm(request, response);

			} else {
				// 一覧表示（デフォルト）
				showList(request, response);
			}

		} catch (SQLException e) {
			throw new ServletException("料理管理エラー", e);
		}
	}

	/**
	 * 料理の追加・編集・削除処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		HttpSession session = request.getSession();

		try {
			if ("add".equals(action)) {
				// 追加処理
				addDish(request, session);

			} else if ("edit".equals(action)) {
				// 編集処理
				editDish(request, session);

			} else if ("toggle".equals(action)) {
				// 有効/無効切り替え
				toggleAvailability(request, session);

			} else if ("delete".equals(action)) {
				// 論理削除
				deleteDish(request, session);
			}

		} catch (SQLException e) {
			session.setAttribute("error", "エラーが発生しました: " + e.getMessage());
		}

		// 一覧画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/dish-manage");
	}

	// ========================================
	// GET処理用メソッド
	// ========================================

	/**
	 * 料理一覧表示
	 */
	private void showList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {

		String category = request.getParameter("category");
		String keyword = request.getParameter("keyword");

		// 【核心修复】：直接从数据库查询所有分类名，不再从 allDishes 里提取
		List<String> allCategories = dishDAO.getAllCategoryNames();

		// 获取用于展示的过滤列表
		List<Dish> displayDishes;
		if (keyword != null && !keyword.trim().isEmpty()) {
			displayDishes = dishDAO.findByKeyword(keyword);
		} else if (category != null && !category.isEmpty() && !"すべて".equals(category)) {
			displayDishes = dishDAO.findByKeyword(category);
		} else {
			displayDishes = dishDAO.findAll();
		}

		// 将两个列表传给 JSP
		request.setAttribute("fullCategories", allCategories);
		request.setAttribute("dishes", displayDishes);

		request.getRequestDispatcher("/WEB-INF/admin/dish-list.jsp").forward(request, response);
	}

	/**
	 * 追加フォーム表示
	 */
	private void showAddForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {

		// 次のIDを自動生成
		String nextId = dishDAO.generateNextId();
		request.setAttribute("nextId", nextId);
		request.setAttribute("mode", "add");

		// フォーム画面へ
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/dish-form.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 編集フォーム表示
	 */
	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {

		String dishId = request.getParameter("id");

		if (dishId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/dish-manage");
			return;
		}

		// 料理情報を取得
		Dish dish = dishDAO.findById(dishId);

		if (dish == null) {
			response.sendRedirect(request.getContextPath() + "/admin/dish-manage");
			return;
		}

		request.setAttribute("dish", dish);
		request.setAttribute("mode", "edit");

		// フォーム画面へ
		request.getRequestDispatcher("/WEB-INF/admin/dish-form.jsp")
				.forward(request, response);
	}

	// ========================================
	// POST処理用メソッド
	// ========================================

	/**
	 * 料理追加
	 */
	private void addDish(HttpServletRequest request, HttpSession session)
			throws SQLException {

		// パラメータ取得
		String dishId = request.getParameter("dishId");
		String dishName = request.getParameter("dishName");
		int dishPrice = Integer.parseInt(request.getParameter("dishPrice"));
		String dishCategory = request.getParameter("dishCategory");
		String dishPhoto = request.getParameter("dishPhoto");
		boolean available = "1".equals(request.getParameter("available"));

		// Dishオブジェクト作成
		Dish dish = new Dish();
		dish.setDishId(dishId);
		dish.setName(dishName);
		dish.setPrice(dishPrice);
		dish.setCategory(dishCategory);
		dish.setPhoto(dishPhoto);
		dish.setAvailable(available);

		// DB登録
		dishDAO.insert(dish);

		System.out.println("料理追加: " + dishName + " (ID: " + dishId + ")");

		session.setAttribute("message", "料理「" + dishName + "」を追加しました");
	}

	/**
	 * 料理編集
	 */
	private void editDish(HttpServletRequest request, HttpSession session)
			throws SQLException {

		// パラメータ取得
		String dishId = request.getParameter("dishId");
		String dishName = request.getParameter("dishName");
		int dishPrice = Integer.parseInt(request.getParameter("dishPrice"));
		String dishCategory = request.getParameter("dishCategory");
		String dishPhoto = request.getParameter("dishPhoto");
		boolean available = "1".equals(request.getParameter("available"));

		// Dishオブジェクト作成
		Dish dish = new Dish();
		dish.setDishId(dishId);
		dish.setName(dishName);
		dish.setPrice(dishPrice);
		dish.setCategory(dishCategory);
		dish.setPhoto(dishPhoto);
		dish.setAvailable(available);

		// DB更新
		dishDAO.update(dish);

		System.out.println("料理更新: " + dishName + " (ID: " + dishId + ")");

		session.setAttribute("message", "料理「" + dishName + "」を更新しました");
	}

	/**
	 * 有効/無効切り替え
	 */
	private void toggleAvailability(HttpServletRequest request, HttpSession session)
			throws SQLException {

		String dishId = request.getParameter("dishId");

		if (dishId != null) {
			dishDAO.toggleAvailability(dishId);

			System.out.println("料理状態切り替え: " + dishId);

			session.setAttribute("message", "料理の状態を切り替えました");
		}
	}

	/**
	 * 論理削除
	 */
	private void deleteDish(HttpServletRequest request, HttpSession session)
			throws SQLException {

		String dishId = request.getParameter("dishId");

		if (dishId != null) {
			dishDAO.softDelete(dishId);

			System.out.println("料理削除（論理）: " + dishId);

			session.setAttribute("message", "料理を削除しました");
		}
	}
}
