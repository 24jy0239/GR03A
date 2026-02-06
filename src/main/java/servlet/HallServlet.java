package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import manager.OrderManager;
import model.OrderItemWithDetails;

/**
 * HallServlet - ホール管理画面（全進捗表示版）
 * 
 * 改善内容:
 * - 旧版: status=2（完了）のみ表示
 * - 新版: status=0,1,2（注文、調理中、完了）すべて表示
 * 
 * 変更履歴:
 * 2026-02-02: 全進捗表示対応
 */
@WebServlet("/admin/hall")
public class HallServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * ホール画面表示（全進捗版）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ========== デバッグログ開始 ==========
		System.out.println("==========================================");
		System.out.println("【HallServlet】画面表示（全進捗版）");
		System.out.println("------------------------------------------");
		System.out.println("OrderManager instance: " + System.identityHashCode(manager));
		System.out.println("visits count: " + manager.getVisitsCount());
		System.out.println("------------------------------------------");

		// visits の全内容を出力
		manager.printAllVisits();

		System.out.println("------------------------------------------");
		// ========== デバッグログ終了 ==========

		// ========================================
		// 改善: 全進捗のOrderItemを取得
		// status 0:注文, 1:調理中, 2:完了
		// ========================================
		List<OrderItemWithDetails> allProgressItems = manager.getAllProgressItems();

		// ステータス別にカウント
		Map<Integer, Integer> statusCounts = new HashMap<>();
		statusCounts.put(0, 0); // 注文
		statusCounts.put(1, 0); // 調理中
		statusCounts.put(2, 0); // 完了

		for (OrderItemWithDetails item : allProgressItems) {
			int status = item.getItemStatus();
			statusCounts.put(status, statusCounts.get(status) + 1);
		}

		// ========== デバッグログ開始 ==========
		System.out.println("getAllProgressItems() 結果:");
		System.out.println("  取得件数: " + allProgressItems.size());
		System.out.println("  注文（status=0）: " + statusCounts.get(0) + " 件");
		System.out.println("  調理中（status=1）: " + statusCounts.get(1) + " 件");
		System.out.println("  完了（status=2）: " + statusCounts.get(2) + " 件");

		if (allProgressItems.isEmpty()) {
			System.out.println("  ⚠️ ホール表示データが空です");
			System.out.println("  可能性:");
			System.out.println("    1. まだ注文が確定されていない");
			System.out.println("    2. 全て配膳完了済み（status=3）");
			System.out.println("    3. Tomcatを再起動してメモリがクリアされた");
		} else {
			for (OrderItemWithDetails item : allProgressItems) {
				String statusText = "";
				switch (item.getItemStatus()) {
				case 0:
					statusText = "注文";
					break;
				case 1:
					statusText = "調理中";
					break;
				case 2:
					statusText = "完了";
					break;
				}
				System.out.println("  - " + item.getDishName()
						+ " (table=" + item.getTableNum()
						+ ", status=" + item.getItemStatus() + ":" + statusText
						+ ", qty=" + item.getQuantity() + ")");
			}
		}
		System.out.println("==========================================");
		// ========== デバッグログ終了 ==========

		// JSPに渡す
		request.setAttribute("allProgressItems", allProgressItems);
		request.setAttribute("statusCounts", statusCounts);

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
