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
import model.Order;
import model.OrderItem;
import model.TableStatus;
import model.Visit;

/**
 * TableStatusServlet - テーブル状態管理画面（注文明細付き + 取り消し機能）
 * 
 * 機能:
 * 1. 全テーブルの使用状況を表示
 * 2. 各テーブルの注文明細を表示
 * 3. 各料理の注文時間を表示
 * 4. 未制作料理の取り消し機能（NEW!）
 * 
 * 変更履歴:
 * 2026-02-02: 注文明細表示追加
 * 2026-02-02: 取り消し機能追加
 */
@WebServlet("/admin/table-status")
public class TableStatusServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * テーブル状態画面表示（注文明細付き）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// テーブル状態リストを取得
		List<TableStatus> tableStatusList = manager.getTableStatusList();

		// 各visitの注文明細を取得
		Map<String, OrderDetailsInfo> orderDetailsByVisit = new HashMap<>();

		for (TableStatus status : tableStatusList) {
			if (status.isOccupied()) {
				String visitId = status.getVisitId();
				Visit visit = manager.getVisit(visitId);

				if (visit != null) {
					OrderDetailsInfo detailsInfo = new OrderDetailsInfo();
					detailsInfo.orders = visit.getOrders();
					detailsInfo.totalItems = 0;

					// ステータス別カウント
					detailsInfo.statusCounts = new HashMap<>();
					detailsInfo.statusCounts.put(0, 0); // 注文
					detailsInfo.statusCounts.put(1, 0); // 調理中
					detailsInfo.statusCounts.put(2, 0); // 完了
					detailsInfo.statusCounts.put(3, 0); // 配膳済み

					for (Order order : visit.getOrders()) {
						for (OrderItem item : order.getOrderItems()) {
							detailsInfo.totalItems++;
							int itemStatus = item.getItemStatus();
							detailsInfo.statusCounts.put(itemStatus,
									detailsInfo.statusCounts.get(itemStatus) + 1);
						}
					}

					orderDetailsByVisit.put(visitId, detailsInfo);
				}
			}
		}

		// 統計情報を計算
		int occupiedCount = 0; // 使用中テーブル数
		int totalSales = 0; // 合計売上
		int totalOrders = 0; // 総注文数

		for (TableStatus status : tableStatusList) {
			if (status.isOccupied()) {
				occupiedCount++;
				totalSales += status.getTotalAmount();

				OrderDetailsInfo info = orderDetailsByVisit.get(status.getVisitId());
				if (info != null) {
					totalOrders += info.totalItems;
				}
			}
		}

		System.out.println("====================================");
		System.out.println("テーブル状態画面表示:");
		System.out.println("  使用中テーブル数: " + occupiedCount);
		System.out.println("  総注文数: " + totalOrders);
		System.out.println("  合計売上: ¥" + totalSales);
		System.out.println("====================================");

		// JSPに渡す
		request.setAttribute("tableStatusList", tableStatusList);
		request.setAttribute("orderDetailsByVisit", orderDetailsByVisit);
		request.setAttribute("occupiedCount", occupiedCount);
		request.setAttribute("totalSales", totalSales);
		request.setAttribute("totalOrders", totalOrders);

		// テーブル状態画面へ
		request.getRequestDispatcher("/WEB-INF/admin/table-status.jsp")
				.forward(request, response);
	}

	/**
	 * 注文明細の操作（取り消し、詳細表示）
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String orderItemId = request.getParameter("orderItemId");

		System.out.println("====================================");
		System.out.println("【TableStatusServlet】注文操作");
		System.out.println("action: " + action);
		System.out.println("orderItemId: " + orderItemId);

		if (orderItemId == null) {
			System.out.println("❌ orderItemId が null");
			System.out.println("====================================");
			response.sendRedirect(request.getContextPath() + "/admin/table-status");
			return;
		}

		boolean success = false;
		String message = "";

		// ========================================
		// 取り消し処理（NEW!）
		// ========================================
		if ("cancel".equals(action)) {
			success = manager.deleteOrderItem(orderItemId);

			if (success) {
				message = "注文を取り消しました";
				System.out.println("✅ 取り消し成功");
			} else {
				message = "取り消しできませんでした（調理開始済み）";
				System.out.println("❌ 取り消し失敗");
			}
		}

		System.out.println("====================================");

		// メッセージをセッションに保存（表示用）
		if (!message.isEmpty()) {
			request.getSession().setAttribute("statusMessage", message);
		}

		// テーブル状態画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/table-status");
	}

	/**
	 * 注文明細情報を保持する内部クラス
	 */
	public static class OrderDetailsInfo {
		public List<Order> orders;
		public int totalItems;
		public Map<Integer, Integer> statusCounts;
	}
}
