package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import manager.OrderManager;

/**
 * ResetServlet - リセット処理
 * テーブルのリセット（Session破棄 + Visit削除）
 */
@WebServlet("/reset")
public class ResetServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * リセット処理
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {
			String visitId = (String) session.getAttribute("visitId");

			if (visitId != null) {
				// Visitを削除（会計済みの場合のみ）
				manager.removeVisit(visitId);

				System.out.println("Visit削除: visitId=" + visitId);
			}

			// Session破棄
			session.invalidate();

			System.out.println("Session破棄");
		}

		// トップページへリダイレクト
		response.sendRedirect(request.getContextPath() + "/");
	}

	/**
	 * POSTも同じ処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
