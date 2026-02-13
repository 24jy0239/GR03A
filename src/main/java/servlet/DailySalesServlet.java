package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.OrderDAO;
import dao.VisitDAO;
import model.OrderItem;
import model.Visit;
import model.VisitWithDetails; // ← modelパッケージから使用

/**
 * DailySalesServlet - 日別売上詳細画面
 * 
 * 機能:
 * - 指定日の来店一覧表示
 * - 各来店の注文明細表示
 * - 日別集計表示
 * 
 * URL: /admin/daily-sales?year=2026&month=2&day=9
 */
@WebServlet("/admin/daily-sales")
public class DailySalesServlet extends HttpServlet {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	/**
	 * 日別詳細画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// パラメータ取得
		String yearStr = request.getParameter("year");
		String monthStr = request.getParameter("month");
		String dayStr = request.getParameter("day");

		// デフォルト値設定
		int year = (yearStr != null) ? Integer.parseInt(yearStr) : 2026;
		int month = (monthStr != null) ? Integer.parseInt(monthStr) : 2;
		int day = (dayStr != null) ? Integer.parseInt(dayStr) : 9;

		LocalDate targetDate = LocalDate.of(year, month, day);

		System.out.println("====================================");
		System.out.println("日別売上詳細画面表示:");
		System.out.println("  対象日: " + targetDate);

		try {
			// ========================================
			// データベースから日別データ取得
			// ========================================
			VisitDAO visitDAO = new VisitDAO();
			OrderDAO orderDAO = new OrderDAO();

			// 指定日の来店一覧を取得
			List<Visit> visits = visitDAO.findByDate(targetDate);

			System.out.println("  来店数: " + visits.size());

			// 各来店の注文明細を取得
			List<VisitWithDetails> visitDetails = new ArrayList<>();
			int dailyTotal = 0;
			int totalOrderCount = 0;

			for (Visit visit : visits) {
				// 注文明細を取得
				List<OrderItem> orderItems = orderDAO.findByVisitId(visit.getVisitId());

				// ========================================
				// VisitWithDetailsを作成（modelパッケージ）
				// ========================================
				VisitWithDetails detail = new VisitWithDetails();
				detail.setVisit(visit);
				detail.setOrderItems(orderItems);

				// 日時をフォーマット（JSP用）
				detail.setArrivalTimeFormatted(visit.getArrivalTime().format(TIME_FORMATTER));

				if (visit.getPaymentTime() != null) {
					detail.setPaymentTimeFormatted(visit.getPaymentTime().format(TIME_FORMATTER));
				} else {
					detail.setPaymentTimeFormatted("未会計");
				}

				visitDetails.add(detail);

				// 集計
				dailyTotal += visit.getTotalAmount();
				totalOrderCount += orderItems.size();
			}

			System.out.println("  日別合計: ¥" + dailyTotal);
			System.out.println("  総注文数: " + totalOrderCount);
			System.out.println("====================================");

			// ========================================
			// JSPに渡すデータ設定
			// ========================================
			request.setAttribute("targetDate", targetDate);
			request.setAttribute("year", year);
			request.setAttribute("month", month);
			request.setAttribute("day", day);
			request.setAttribute("visitDetails", visitDetails);
			request.setAttribute("visitCount", visits.size());
			request.setAttribute("dailyTotal", dailyTotal);
			request.setAttribute("totalOrderCount", totalOrderCount);

			// ========================================
			// View（JSP）に転送
			// ========================================
			request.getRequestDispatcher("/WEB-INF/admin/daily-sales.jsp")
					.forward(request, response);

		} catch (SQLException e) {
			System.err.println("❌ DB取得エラー: " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("データベース取得エラー", e);
		}
	}
}
