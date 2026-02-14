package servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import dao.DishDAO;
import model.Dish;

/**
 * DishManageServlet - 料理管理
 * 
 * 変更履歴:
 * 2026-02-09: アプリケーションスコープのdishMap更新機能追加（方法A実装）
 */
@WebServlet("/admin/dish-manage")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 15 // 15MB
)
public class DishManageServlet extends HttpServlet {

	private DishDAO dishDAO = new DishDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");

		try {
			if ("edit".equals(action)) {
				showEditForm(request, response);
			} else if ("add".equals(action)) {
				showAddForm(request, response);
			} else {
				showList(request, response);
			}
		} catch (SQLException e) {
			throw new ServletException("料理管理エラー", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		HttpSession session = request.getSession();

		try {
			if ("add".equals(action)) {
				addDish(request, session);
				// ========================================
				// アプリケーションdishMapを更新（NEW!）
				// ========================================
				refreshApplicationDishMap();

			} else if ("edit".equals(action)) {
				updateDish(request, session);
				// アプリケーションdishMapを更新（NEW!）
				refreshApplicationDishMap();

			} else if ("toggle".equals(action)) {
				toggleAvailability(request, session);
				// アプリケーションdishMapを更新（NEW!）
				refreshApplicationDishMap();

			} else if ("delete".equals(action)) {
				deleteDish(request, session);
				// アプリケーションdishMapを更新（NEW!）
				refreshApplicationDishMap();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			session.setAttribute("error", "エラーが発生しました: " + e.getMessage());
		}

		response.sendRedirect(request.getContextPath() + "/admin/dish-manage");
	}

	// ==================== 表示系メソッド ====================

	private void showList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {

		String category = request.getParameter("category");
		String keyword = request.getParameter("keyword");

		List<String> allCategories = dishDAO.getAllCategoryNames();
		List<Dish> displayDishes;

		if (keyword != null && !keyword.trim().isEmpty()) {
			displayDishes = dishDAO.findByKeyword(keyword);
		} else if (category != null && !category.isEmpty() && !"すべて".equals(category)) {
			displayDishes = dishDAO.findByKeyword(category);
		} else {
			displayDishes = dishDAO.findAll();
		}

		request.setAttribute("fullCategories", allCategories);
		request.setAttribute("dishes", displayDishes);
		request.getRequestDispatcher("/WEB-INF/admin/dish-list.jsp").forward(request, response);
	}

	private void showAddForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		String nextId = dishDAO.generateNextId();
		request.setAttribute("nextId", nextId);
		request.setAttribute("mode", "add");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/dish-form.jsp");
		dispatcher.forward(request, response);
	}

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		String dishId = request.getParameter("id");
		if (dishId == null) {
			dishId = request.getParameter("dishId");
		}

		Dish dish = dishDAO.findById(dishId);
		if (dish == null) {
			response.sendRedirect(request.getContextPath() + "/admin/dish-manage");
			return;
		}

		request.setAttribute("dish", dish);
		request.setAttribute("mode", "edit");
		request.getRequestDispatcher("/WEB-INF/admin/dish-form.jsp").forward(request, response);
	}

	// ==================== 更新系メソッド ====================

	private void addDish(HttpServletRequest request, HttpSession session)
			throws SQLException, ServletException, IOException {

		// 画像処理（ここで保存ロジックが動きます）
		String uploadedFileName = handleFileUpload(request);

		String dishId = request.getParameter("dishId");
		String name = request.getParameter("dishName");
		int price = Integer.parseInt(request.getParameter("dishPrice"));
		String category = request.getParameter("dishCategory");
		boolean available = request.getParameter("available") != null;

		if (uploadedFileName == null) {
			uploadedFileName = "no-image.jpg";
		}

		Dish dish = new Dish();
		dish.setDishId(dishId);
		dish.setName(name);
		dish.setPrice(price);
		dish.setCategory(category);
		dish.setPhoto(uploadedFileName);
		dish.setAvailable(available);

		dishDAO.insert(dish);
		session.setAttribute("message", "料理「" + name + "」を追加しました");
	}

	private void updateDish(HttpServletRequest request, HttpSession session)
			throws SQLException, ServletException, IOException {

		String dishId = request.getParameter("dishId");
		String name = request.getParameter("dishName");
		int price = Integer.parseInt(request.getParameter("dishPrice"));
		String category = request.getParameter("dishCategory");
		boolean available = request.getParameter("available") != null;

		// 画像処理（更新時もここが動くのでバックアップもされます）
		String uploadedFileName = handleFileUpload(request);

		// 元の画像ファイル名
		String oldPhotoName = request.getParameter("dishPhoto");

		// 新しい画像があればそれ、なければ元のまま
		String finalPhotoName;
		if (uploadedFileName != null) {
			finalPhotoName = uploadedFileName;
		} else {
			finalPhotoName = oldPhotoName;
		}

		Dish dish = new Dish();
		dish.setDishId(dishId);
		dish.setName(name);
		dish.setPrice(price);
		dish.setCategory(category);
		dish.setPhoto(finalPhotoName);
		dish.setAvailable(available);

		dishDAO.update(dish);
		session.setAttribute("message", "料理「" + name + "」を更新しました");
	}

	private void toggleAvailability(HttpServletRequest request, HttpSession session) throws SQLException {
		String dishId = request.getParameter("dishId");
		if (dishId != null) {
			dishDAO.toggleAvailability(dishId);
			session.setAttribute("message", "状態を変更しました");
		}
	}

	private void deleteDish(HttpServletRequest request, HttpSession session) throws SQLException {
		String dishId = request.getParameter("dishId");
		if (dishId != null) {
			dishDAO.hardDelete(dishId);
			session.setAttribute("message", "削除しました");
		}
	}

	/**
	 * 画像アップロード処理
	 * サーバー（一時保存）とソースコード（永続保存）の両方に対応
	 */
	private String handleFileUpload(HttpServletRequest request) throws ServletException, IOException {
		Part filePart = request.getPart("fileInput");

		if (filePart != null && filePart.getSize() > 0) {
			String fileName = filePart.getSubmittedFileName();

			// ---------------------------------------------------------
			// ① 【必須】サーバーの一時フォルダに保存（今すぐ画面に表示するため）
			// ---------------------------------------------------------
			String serverPath = getServletContext().getRealPath("/images");
			File serverDir = new File(serverPath);
			if (!serverDir.exists())
				serverDir.mkdirs();

			// サーバー側へ書き込み
			filePart.write(serverPath + File.separator + fileName);
			System.out.println("【表示用保存】成功: " + serverPath + File.separator + fileName);

			// ---------------------------------------------------------
			// ② 【開発用】JSPで指定されたソースフォルダに保存（永続化のため）
			// ---------------------------------------------------------
			//			String sourcePathInput = request.getParameter("sourcePath"); // JSPの入力を取得
			//
			//			if (sourcePathInput != null && !sourcePathInput.trim().isEmpty()) {
			//				try {
			//					File localDir = new File(sourcePathInput);
			//
			//					// 入力されたフォルダが実在する場合のみコピーを実行
			//					if (localDir.exists() && localDir.isDirectory()) {
			//						Path source = Path.of(serverPath, fileName); // コピー元
			//						Path target = Path.of(sourcePathInput, fileName); // コピー先
			//
			//						// 上書きコピーを実行
			//						Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			//						System.out.println("【永続化保存】成功: " + target);
			//						System.out.println("※Eclipseでプロジェクトを更新(F5)すると反映されます");
			//					} else {
			//						System.out.println("【永続化スキップ】指定パスが見つかりません: " + sourcePathInput);
			//					}
			//				} catch (Exception e) {
			//					// 先生のPC等でエラーになっても止まらないようにする
			//					System.out.println("【永続化エラー】スキップしました: " + e.getMessage());
			//				}
			//			}

			return fileName;
		}
		return null;
	}

	// ==================== アプリケーションスコープ更新（NEW!）====================

	/**
	 * アプリケーションスコープのdishMapを更新
	 * 
	 * 呼び出しタイミング:
	 * - 料理の追加後
	 * - 料理の更新後
	 * - 料理の削除後
	 * - 料理の有効化・無効化後
	 * 
	 * 効果:
	 * - 管理画面での変更が即座に全顧客のメニューに反映される
	 * 
	 * 作成日: 2026-02-09（方法A実装）
	 */
	private void refreshApplicationDishMap() {
		ServletContext context = getServletContext();

		System.out.println("====================================");
		System.out.println("アプリケーションdishMap更新:");

		try {
			DishDAO dishDAO = new DishDAO();
			List<Dish> dishes = dishDAO.findAvailable(); // IS_ACTIVE = 1 のみ

			// Map化（高速アクセス用）
			Map<String, Dish> dishMap = new HashMap<>();
			for (Dish dish : dishes) {
				dishMap.put(dish.getDishId(), dish);
			}

			// アプリケーションスコープに保存（全ユーザーに反映）
			context.setAttribute("dishMap", dishMap);

			System.out.println("  ✅ 更新完了: " + dishes.size() + "件（有効のみ）");
			System.out.println("  全顧客のメニューに即座に反映されます");
			System.out.println("====================================");

		} catch (SQLException e) {
			System.err.println("  ❌ 更新エラー");
			e.printStackTrace();
			System.out.println("====================================");
		}
	}
}
