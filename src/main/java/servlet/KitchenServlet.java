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
 * KitchenServlet - キッチン画面
 * 未完了の注文明細を表示、調理状態を更新
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

		// 未完了のアイテムを取得（状態0,1）
		List<OrderItemWithDetails> kitchenItems = manager.getKitchenItems();

		// 状態別に分類
		int orderedCount = 0; // 状態0: 注文
		int cookingCount = 0; // 状態1: 調理中

		for (OrderItemWithDetails item : kitchenItems) {
			if (item.getItemStatus() == 0) {
				orderedCount++;
			} else if (item.getItemStatus() == 1) {
				cookingCount++;
			}
		}

		request.setAttribute("kitchenItems", kitchenItems);
		request.setAttribute("orderedCount", orderedCount);
		request.setAttribute("cookingCount", cookingCount);
		request.setAttribute("totalCount", kitchenItems.size());

		// キッチン画面へ
		request.getRequestDispatcher("/WEB-INF/admin/kitchen.jsp")
				.forward(request, response);
	}

	/**
	 * 調理状態更新
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String orderItemId = request.getParameter("orderItemId");

		if (orderItemId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/kitchen");
			return;
		}

		boolean updated = false;

		if ("start".equals(action)) {
			// 調理開始: 0 → 1
			updated = manager.updateItemStatus(orderItemId, 1);

		} else if ("complete".equals(action)) {
			// 調理完了: 1 → 2
			updated = manager.updateItemStatus(orderItemId, 2);
		}

		if (updated) {
			System.out.println("キッチン: 状態更新成功 - " + action + " - " + orderItemId);
		} else {
			System.out.println("キッチン: 状態更新失敗 - " + orderItemId);
		}

		// キッチン画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/kitchen");
	}
}
