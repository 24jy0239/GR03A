package servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import manager.OrderManager;

/**
 * SalesServlet - 売上分析画面
 * 
 * MVCパターン:
 * - Model: OrderManager（データ取得）
 * - View: sales.jsp（表示）
 * - Controller: このServlet（制御）
 */
@WebServlet("/admin/sales")
public class SalesServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * 売上分析画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 年度パラメータ取得（デフォルト：2026年）
		String yearStr = request.getParameter("year");
		int selectedYear = (yearStr == null || yearStr.isEmpty()) ? 2026 : Integer.parseInt(yearStr);

		System.out.println("====================================");
		System.out.println("売上分析画面表示:");
		System.out.println("  選択年度: " + selectedYear + "年");

		// ========================================
		// ビジネスロジック（Model）
		// ========================================

		// 年間売上マトリックス取得（月×日）
		Map<Integer, Map<Integer, Integer>> salesData = manager.getYearlySalesMatrix(selectedYear);

		// 年間合計計算
		long yearlyTotal = manager.calculateYearlyTotal(salesData);

		System.out.println("  年間合計: ¥" + yearlyTotal);
		System.out.println("====================================");

		// ========================================
		// JSPに渡すデータ設定
		// ========================================
		request.setAttribute("salesData", salesData);
		request.setAttribute("selectedYear", selectedYear);
		request.setAttribute("yearlyTotal", yearlyTotal);

		// ========================================
		// View（JSP）に転送
		// ========================================
		request.getRequestDispatcher("/WEB-INF/admin/sales.jsp")
				.forward(request, response);
	}
}
