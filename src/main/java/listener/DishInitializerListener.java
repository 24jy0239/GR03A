package listener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import dao.DishDAO;
import model.Dish;

/**
 * DishInitializerListener - アプリケーション起動時の初期化
 * 
 * 機能:
 * - アプリケーション起動時にdishMapを読み込み
 * - アプリケーションスコープに保存（全ユーザーで共有）
 * 
 * メリット:
 * - メモリ効率: セッションごとではなく1つのdishMapを共有
 * - 管理画面での変更が全ユーザーに即座に反映
 * - セッション管理が不要
 * 
 * 作成日: 2026-02-09
 */
@WebListener
public class DishInitializerListener implements ServletContextListener {

	/**
	 * アプリケーション起動時の処理
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		System.out.println("====================================");
		System.out.println("アプリケーション起動:");
		System.out.println("  dishMap初期化開始");

		try {
			// データベースから有効な料理を取得
			DishDAO dishDAO = new DishDAO();
			List<Dish> dishes = dishDAO.findAvailable(); // IS_ACTIVE = 1 のみ

			// Map化（高速アクセス用）
			Map<String, Dish> dishMap = new HashMap<>();
			for (Dish dish : dishes) {
				dishMap.put(dish.getDishId(), dish);
			}

			// アプリケーションスコープに保存
			context.setAttribute("dishMap", dishMap);

			System.out.println("  ✅ dishMap初期化完了: " + dishes.size() + "件");
			System.out.println("  メモリスコープ: アプリケーション（全ユーザー共有）");
			System.out.println("====================================");

		} catch (SQLException e) {
			System.err.println("  ❌ dishMap初期化エラー");
			e.printStackTrace();
			System.out.println("====================================");
		}
	}

	/**
	 * アプリケーション終了時の処理
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("====================================");
		System.out.println("アプリケーション終了:");
		System.out.println("  dishMapクリーンアップ");
		System.out.println("====================================");
	}
}
