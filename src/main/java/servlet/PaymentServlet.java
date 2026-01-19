package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import manager.OrderManager;
import model.Visit;

/**
 * PaymentServlet - 会計処理
 * 会計完了 + DB保存
 */
@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * 会計画面表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String visitId = (String) session.getAttribute("visitId");

		if (visitId == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		Visit visit = manager.getVisit(visitId);

		if (visit == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 注文がない場合
		if (visit.getOrderCount() == 0) {
			session.setAttribute("error", "注文がありません。");
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		request.setAttribute("visit", visit);

		// 会計画面は不要、直接POSTへ
		doPost(request, response);
	}

	/**
	 * 会計処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String visitId = (String) session.getAttribute("visitId");

		if (visitId == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		Visit visit = manager.getVisit(visitId);

		if (visit == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		try {
			// 会計完了
			LocalDateTime paymentTime = LocalDateTime.now();
			manager.completeVisit(visitId, paymentTime);

			System.out.println("会計完了: visitId=" + visitId
					+ ", total=¥" + visit.getTotalAmount()
					+ ", time=" + paymentTime);

			// DB保存
			manager.saveVisitWithAllOrders(visitId);

			System.out.println("DB保存完了: visitId=" + visitId);

			// 会計完了情報をrequestに設定
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			// 会計完了画面へ
			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServletException("DB保存エラー", e);
		}
	}
}
