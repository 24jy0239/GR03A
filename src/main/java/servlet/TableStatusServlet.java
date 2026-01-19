package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import manager.OrderManager;
import model.TableStatus;
import model.Visit;

/**
 * TableStatusServlet - テーブル状態管理画面
 * 全テーブルの使用状況を表示
 */
@WebServlet("/admin/table-status")
public class TableStatusServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * テーブル状態画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// テーブル状態リストを取得
		List<TableStatus> tableStatusList = manager.getTableStatusList();

		// 統計情報を計算
		int occupiedCount = 0; // 使用中テーブル数
		int totalSales = 0; // 合計売上

		for (TableStatus status : tableStatusList) {
			if (status.isOccupied()) {
				occupiedCount++;
				totalSales += status.getTotalAmount();
			}
		}

		request.setAttribute("tableStatusList", tableStatusList);
		request.setAttribute("occupiedCount", occupiedCount);
		request.setAttribute("totalSales", totalSales);

		// テーブル状態画面へ
		request.getRequestDispatcher("/WEB-INF/admin/table-status.jsp")
				.forward(request, response);
	}

	/**
	 * Visit詳細表示（AJAX用）
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String visitId = request.getParameter("visitId");

		if (visitId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/table-status");
			return;
		}

		Visit visit = manager.getVisit(visitId);

		if (visit == null) {
			response.sendRedirect(request.getContextPath() + "/admin/table-status");
			return;
		}

		// Visit詳細を表示（今回は簡易版）
		request.setAttribute("visit", visit);

		// テーブル状態画面へ
		request.getRequestDispatcher("/WEB-INF/admin/table-status.jsp")
				.forward(request, response);
	}
}
