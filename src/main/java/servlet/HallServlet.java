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
 * HallServlet - ホール画面
 * 提供待ちの注文明細を表示、配膳完了を更新
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

		// 提供待ちのアイテムを取得（状態2）
		List<OrderItemWithDetails> hallItems = manager.getHallItems();

		request.setAttribute("hallItems", hallItems);
		request.setAttribute("totalCount", hallItems.size());

		// ホール画面へ
		request.getRequestDispatcher("/WEB-INF/admin/hall.jsp")
				.forward(request, response);
	}

	/**
	 * 配膳完了更新
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String orderItemId = request.getParameter("orderItemId");

		if (orderItemId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/hall");
			return;
		}

		boolean updated = false;

		if ("serve".equals(action)) {
			// 配膳完了: 2 → 3
			updated = manager.updateItemStatus(orderItemId, 3);
		}

		if (updated) {
			System.out.println("ホール: 配膳完了 - " + orderItemId);
		} else {
			System.out.println("ホール: 配膳完了失敗 - " + orderItemId);
		}

		// ホール画面へリダイレクト
		response.sendRedirect(request.getContextPath() + "/admin/hall");
	}
}
