package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import manager.OrderManager;
import model.OrderItemWithDetails;

/**
 * HallServlet - ホール管理画面
 * 提供待ちの注文を表示（デバッグログ付き）
 */
@WebServlet("/admin/hall")
public class HallServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * ホール画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ========== デバッグログ開始 ==========
		System.out.println("==========================================");
		System.out.println("【HallServlet】画面表示");
		System.out.println("------------------------------------------");
		System.out.println("OrderManager instance: " + System.identityHashCode(manager));
		System.out.println("visits count: " + manager.getVisitsCount());
		System.out.println("------------------------------------------");

		// visits の全内容を出力
		manager.printAllVisits();

		System.out.println("------------------------------------------");
		// ========== デバッグログ終了 ==========

		// ホール向けの注文項目を取得（status = 2）
		List<OrderItemWithDetails> hallItems = manager.getHallItems();

		// ========== デバッグログ開始 ==========
		System.out.println("getHallItems() 結果:");
		System.out.println("  取得件数: " + hallItems.size());

		if (hallItems.isEmpty()) {
			System.out.println("  ⚠️ ホール表示データが空です");
			System.out.println("  可能性:");
			System.out.println("    1. まだ調理完了していない（status≠2）");
			System.out.println("    2. 全て配膳完了済み（status=3）");
			System.out.println("    3. Tomcatを再起動してメモリがクリアされた");
		} else {
			for (OrderItemWithDetails item : hallItems) {
				System.out.println("  - " + item.getDishName()
						+ " (table=" + item.getTableNum()
						+ ", status=" + item.getItemStatus()
						+ ", qty=" + item.getQuantity() + ")");
			}
		}
		System.out.println("==========================================");
		// ========== デバッグログ終了 ==========

		// JSPに渡す
		request.setAttribute("hallItems", hallItems);

		// ホール画面へ
		request.getRequestDispatcher("/WEB-INF/admin/hall.jsp").forward(request, response);
	}

	/**
	 * 配膳完了
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String orderItemId = request.getParameter("orderItemId");

		System.out.println("==========================================");
		System.out.println("【HallServlet】状態更新");
		System.out.println("action: " + action);
		System.out.println("orderItemId: " + orderItemId);

		if (orderItemId == null) {
			System.out.println("❌ orderItemId が null");
			System.out.println("==========================================");
			response.sendRedirect(request.getContextPath() + "/admin/hall");
			return;
		}

		boolean success = false;

		if ("serve".equals(action)) {
			// 配膳完了（status: 2 → 3）
			success = manager.updateItemStatus(orderItemId, 3);
			System.out.println("配膳完了: " + (success ? "成功" : "失敗"));
		}

		System.out.println("==========================================");

		// ホール画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/hall");
	}
}
