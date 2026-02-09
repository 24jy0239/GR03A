package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AdminServlet - 管理画面トップ
 * URL: /admin
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

	/**
	 * 管理画面トップ表示
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("==========================================");
		System.out.println("【AdminServlet】管理トップ表示");
		System.out.println("==========================================");

		// 管理トップ画面へ
		request.getRequestDispatcher("/administration.jsp").forward(request, response);
	}
}
