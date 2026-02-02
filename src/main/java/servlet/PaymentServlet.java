package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
 * 
 * 機能:
 * 1. 会計処理の実行
 * 2. Visit完了処理 (paymentTime設定)
 * 3. データベースへの保存（重複チェック付き）
 * 4. 会計完了画面への遷移
 * 
 * URL: /payment
 * 
 * 変更履歴:
 * 2026-02-02: 重複保存防止機能追加
 */
@WebServlet("/order/payment")
public class PaymentServlet extends HttpServlet {

	private OrderManager manager = OrderManager.getInstance();

	/**
	 * 会計画面表示 (GET)
	 * 
	 * 注意: 現在の実装では直接doPost()を呼び出しているため、
	 * 会計確認画面は表示されません。即座に会計処理が実行されます。
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String visitId = (String) session.getAttribute("visitId");

		// セッションチェック
		if (visitId == null) {
			System.out.println("エラー: visitIdがセッションにありません");
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// Visit存在チェック
		Visit visit = manager.getVisit(visitId);
		if (visit == null) {
			System.out.println("エラー: visitが見つかりません: " + visitId);
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 注文チェック
		if (visit.getOrderCount() == 0) {
			System.out.println("エラー: 注文がありません");
			session.setAttribute("error", "注文がありません。");
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		// 既に会計済みかチェック
		if (visit.getPaymentTime() != null) {
			System.out.println("警告: 既に会計済みです: " + visitId);

			// 会計完了画面へ直接遷移（再表示）
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);
			return;
		}

		// Visit情報をrequestに設定
		request.setAttribute("visit", visit);

		// 直接会計処理へ (会計確認画面をスキップ)
		doPost(request, response);
	}

	/**
	 * 会計処理実行 (POST)
	 * 
	 * 処理フロー:
	 * 1. visitId検証
	 * 2. Visit取得
	 * 3. 会計済みチェック（重複防止）
	 * 4. 会計完了処理 (completeVisit)
	 * 5. DB保存 (saveVisitWithAllOrders) - 重複エラー対応
	 * 6. セッションクリア
	 * 7. 会計完了画面へ遷移
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String visitId = (String) session.getAttribute("visitId");

		// セッションチェック
		if (visitId == null) {
			System.out.println("エラー: visitIdがセッションにありません");
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// Visit取得
		Visit visit = manager.getVisit(visitId);
		if (visit == null) {
			System.out.println("エラー: visitが見つかりません: " + visitId);
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 既に会計済みかチェック（重複防止）
		if (visit.getPaymentTime() != null) {
			System.out.println("====================================");
			System.out.println("警告: 既に会計済みです");
			System.out.println("  visitId: " + visitId);
			System.out.println("  会計時刻: " + visit.getPaymentTime());
			System.out.println("  合計金額: ¥" + visit.getTotalAmount());
			System.out.println("====================================");

			// 会計完了画面へ遷移（再表示）
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);
			return;
		}

		try {
			// 会計時刻記録
			LocalDateTime paymentTime = LocalDateTime.now();

			// Visit完了処理
			manager.completeVisit(visitId, paymentTime);

			System.out.println("====================================");
			System.out.println("会計完了処理:");
			System.out.println("  visitId: " + visitId);
			System.out.println("  テーブル番号: " + visit.getTableNum());
			System.out.println("  合計金額: ¥" + visit.getTotalAmount());
			System.out.println("  注文件数: " + visit.getOrderCount());
			System.out.println("  会計時刻: " + paymentTime);
			System.out.println("====================================");

			// データベース保存（重複エラー処理付き）
			try {
				manager.saveVisitWithAllOrders(visitId);
				System.out.println("✅ DB保存完了: visitId=" + visitId);

			} catch (SQLIntegrityConstraintViolationException e) {
				// PRIMARY KEY重複エラー（既にDB保存済み）
				System.out.println("⚠️ DB保存スキップ: 既に保存済みです");
				System.out.println("  visitId: " + visitId);
				System.out.println("  エラー詳細: " + e.getMessage());

				// エラーログは出すが、処理は継続
				// （会計完了画面は正常に表示）
			}

			// 会計完了情報をrequestに設定
			// 重要: payment-complete.jspはrequest属性から取得
			request.setAttribute("visit", visit);
			request.setAttribute("totalAmount", visit.getTotalAmount());
			request.setAttribute("tableNum", visit.getTableNum());

			// セッションクリア（次の来店のため）
			session.removeAttribute("visitId");
			session.removeAttribute("tableNum");
			session.removeAttribute("cart");
			session.removeAttribute("cartTotal");
			session.removeAttribute("cartCount");

			System.out.println("🔄 セッションクリア完了");

			// 会計完了画面へ転送
			request.getRequestDispatcher("/WEB-INF/payment-complete.jsp")
					.forward(request, response);

		} catch (SQLException e) {
			// その他のDB保存エラー
			System.err.println("❌ DB保存エラー: " + e.getMessage());
			e.printStackTrace();

			// エラー画面へ遷移
			request.setAttribute("errorMessage",
					"データベース保存中にエラーが発生しました。システム管理者に連絡してください。");
			request.setAttribute("errorDetail", e.getMessage());

			// エラーページへ転送（または適切なエラーハンドリング）
			throw new ServletException("データベース保存中にエラーが発生しました", e);
		}
	}
}
