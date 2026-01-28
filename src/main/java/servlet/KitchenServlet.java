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
 * KitchenServlet - キッチン管理画面
 * 調理待ち・調理中の注文を表示（デバッグログ付き）
 */
@WebServlet("/admin/kitchen")
public class KitchenServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * キッチン画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ========== デバッグログ開始 ==========
		System.out.println("==========================================");
		System.out.println("【KitchenServlet】画面表示");
		System.out.println("------------------------------------------");
		System.out.println("OrderManager instance: " + System.identityHashCode(manager));
		System.out.println("visits count: " + manager.getVisitsCount());
		System.out.println("------------------------------------------");

		// visits の全内容を出力
		manager.printAllVisits();

		System.out.println("------------------------------------------");
		// ========== デバッグログ終了 ==========

		// キッチン向けの注文項目を取得（status = 0 or 1）
		List<OrderItemWithDetails> kitchenItems = manager.getKitchenItems();

		// ========== デバッグログ開始 ==========
		System.out.println("getKitchenItems() 結果:");
		System.out.println("  取得件数: " + kitchenItems.size());

		if (kitchenItems.isEmpty()) {
			System.out.println("  ⚠️ キッチン表示データが空です");
			System.out.println("  可能性:");
			System.out.println("    1. まだ注文が確定されていない");
			System.out.println("    2. 全ての注文が調理完了済み（status=2）");
			System.out.println("    3. Tomcatを再起動してメモリがクリアされた");
		} else {
			for (OrderItemWithDetails item : kitchenItems) {
				System.out.println("  - " + item.getDishName()
						+ " (table=" + item.getTableNum()
						+ ", status=" + item.getItemStatus()
						+ ", qty=" + item.getQuantity() + ")");
			}
		}
		System.out.println("==========================================");
		// ========== デバッグログ終了 ==========

		// JSPに渡す
		request.setAttribute("kitchenItems", kitchenItems);

		// キッチン画面へ
		request.getRequestDispatcher("/WEB-INF/admin/kitchen.jsp").forward(request, response);
	}

	/**
	 * 調理状態の更新
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String orderItemId = request.getParameter("orderItemId");

		System.out.println("==========================================");
		System.out.println("【KitchenServlet】状態更新");
		System.out.println("action: " + action);
		System.out.println("orderItemId: " + orderItemId);

		if (orderItemId == null) {
			System.out.println("❌ orderItemId が null");
			System.out.println("==========================================");
			response.sendRedirect(request.getContextPath() + "/admin/kitchen");
			return;
		}

		boolean success = false;

		if ("start".equals(action)) {
			// 調理開始（status: 0 → 1）
			success = manager.updateItemStatus(orderItemId, 1);
			System.out.println("調理開始: " + (success ? "成功" : "失敗"));

		} else if ("finish".equals(action)) {
			// 調理完了（status: 1 → 2）
			success = manager.updateItemStatus(orderItemId, 2);
			System.out.println("調理完了: " + (success ? "成功" : "失敗"));
		}

		System.out.println("==========================================");

		// キッチン画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/kitchen");
	}
}
