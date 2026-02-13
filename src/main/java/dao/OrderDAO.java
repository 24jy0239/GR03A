package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.OrderItem;

/**
 * OrderDAO（注文明細 - データアクセス）
 * OrderItem情報のCRUD操作
 * 
 * 重要: ORDERSテーブルは使用しない
 * ORDER_ITEMSを直接VISITに紐づける
 * 
 * 変更履歴:
 * 2026-02-09: 売上分析用メソッドに会計済みフィルター追加
 */
public class OrderDAO {

	private static final String URL = "jdbc:mysql://10.64.144.5:3306/24jy0228";
	private static final String USER = "24jy0228";
	private static final String PASSWORD = "24jy0228";

	// staticイニシャライザを一時的にコメントアウト
	/*
	static {
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("MySQL JDBC Driver not found", e);
	    }
	}
	*/

	/**
	 * DB接続を取得
	 */
	public Connection getConnection() throws SQLException {
		// 接続時に明示的にドライバをロード
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	// ==================== CREATE ====================

	/**
	 * OrderItemを直接Visitに紐づけて保存
	 * ★ 会計時のみ使用
	 * ★ ORDERSテーブルには保存しない
	 */
	public void saveItemDirect(Connection conn, String visitId, OrderItem item)
			throws SQLException {

		String sql = "INSERT INTO ORDER_ITEMS " +
				"(ORDER_ITEM_ID, VISIT_ID, ORDER_ID, DISH_ID, " +
				"DISH_NAME, PRICE, QUANTITY) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, item.getOrderItemId());
			pstmt.setString(2, visitId); // ★ visitIdに直接紐づける
			pstmt.setString(3, item.getOrderId()); // ★ 参考情報として保存
			pstmt.setString(4, item.getDishId());
			pstmt.setString(5, item.getDishName());
			pstmt.setInt(6, item.getPrice());
			pstmt.setInt(7, item.getQuantity());

			pstmt.executeUpdate();
		}
	}

	// ==================== READ ====================

	/**
	 * Visit IDで注文明細を取得
	 * ★ 注文履歴確認用
	 */
	public List<OrderItem> findByVisitId(String visitId) throws SQLException {
		String sql = "SELECT ORDER_ITEM_ID, VISIT_ID, ORDER_ID, DISH_ID, " +
				"DISH_NAME, PRICE, QUANTITY " +
				"FROM ORDER_ITEMS " +
				"WHERE VISIT_ID = ? " +
				"ORDER BY ORDER_ITEM_ID";

		List<OrderItem> items = new ArrayList<>();

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, visitId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					items.add(mapResultSetToOrderItem(rs));
				}
			}
		}

		return items;
	}

	/**
	 * Order IDで注文明細を取得
	 */
	public List<OrderItem> findByOrderId(String orderId) throws SQLException {
		String sql = "SELECT ORDER_ITEM_ID, VISIT_ID, ORDER_ID, DISH_ID, " +
				"DISH_NAME, PRICE, QUANTITY " +
				"FROM ORDER_ITEMS " +
				"WHERE ORDER_ID = ? " +
				"ORDER BY ORDER_ITEM_ID";

		List<OrderItem> items = new ArrayList<>();

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, orderId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					items.add(mapResultSetToOrderItem(rs));
				}
			}
		}

		return items;
	}

	/**
	 * OrderItem IDで取得
	 */
	public OrderItem findById(String orderItemId) throws SQLException {
		String sql = "SELECT ORDER_ITEM_ID, VISIT_ID, ORDER_ID, DISH_ID, " +
				"DISH_NAME, PRICE, QUANTITY " +
				"FROM ORDER_ITEMS " +
				"WHERE ORDER_ITEM_ID = ?";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, orderItemId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToOrderItem(rs);
				}
			}
		}

		return null;
	}

	/**
	 * 全OrderItem取得
	 */
	public List<OrderItem> findAll() throws SQLException {
		String sql = "SELECT ORDER_ITEM_ID, VISIT_ID, ORDER_ID, DISH_ID, " +
				"DISH_NAME, PRICE, QUANTITY " +
				"FROM ORDER_ITEMS " +
				"ORDER BY ORDER_ITEM_ID";

		List<OrderItem> items = new ArrayList<>();

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				items.add(mapResultSetToOrderItem(rs));
			}
		}

		return items;
	}

	// ==================== UPDATE ====================

	/**
	 * OrderItem更新
	 */
	public void update(OrderItem item) throws SQLException {
		String sql = "UPDATE ORDER_ITEMS SET " +
				"DISH_ID = ?, " +
				"DISH_NAME = ?, " +
				"PRICE = ?, " +
				"QUANTITY = ? " +
				"WHERE ORDER_ITEM_ID = ?";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, item.getDishId());
			pstmt.setString(2, item.getDishName());
			pstmt.setInt(3, item.getPrice());
			pstmt.setInt(4, item.getQuantity());
			pstmt.setString(5, item.getOrderItemId());

			pstmt.executeUpdate();
		}
	}

	// ==================== DELETE ====================

	/**
	 * OrderItem削除
	 */
	public void delete(String orderItemId) throws SQLException {
		String sql = "DELETE FROM ORDER_ITEMS WHERE ORDER_ITEM_ID = ?";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, orderItemId);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Visit IDで全OrderItem削除
	 */
	public void deleteByVisitId(String visitId) throws SQLException {
		String sql = "DELETE FROM ORDER_ITEMS WHERE VISIT_ID = ?";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, visitId);
			pstmt.executeUpdate();
		}
	}

	// ==================== 統計・分析用 ====================

	/**
	 * 料理別の売上集計（会計済みのみ）
	 * 
	 * 用途:
	 * - 売上分析
	 * 
	 * 変更履歴:
	 * 2026-02-09: 会計済み（PAYMENT_TIME IS NOT NULL）のみ集計
	 */
	public List<DishSalesData> getDishSalesData(java.time.LocalDate startDate,
			java.time.LocalDate endDate)
			throws SQLException {

		String sql = "SELECT oi.DISH_NAME, " +
				"SUM(oi.QUANTITY) as total_quantity, " +
				"SUM(oi.PRICE * oi.QUANTITY) as total_sales " +
				"FROM ORDER_ITEMS oi " +
				"JOIN VISITS v ON oi.VISIT_ID = v.VISIT_ID " +
				"WHERE DATE(v.ARRIVAL_TIME) BETWEEN ? AND ? " +
				"AND v.PAYMENT_TIME IS NOT NULL " + // ← 追加: 会計済みのみ
				"GROUP BY oi.DISH_NAME " +
				"ORDER BY total_sales DESC";

		List<DishSalesData> dataList = new ArrayList<>();

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDate(1, Date.valueOf(startDate));
			pstmt.setDate(2, Date.valueOf(endDate));

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String dishName = rs.getString("DISH_NAME");
					int quantity = rs.getInt("total_quantity");
					int sales = rs.getInt("total_sales");

					dataList.add(new DishSalesData(dishName, quantity, sales));
				}
			}
		}

		System.out.println("料理別売上集計: " + startDate + " ~ " + endDate
				+ ", 種類=" + dataList.size() + "（会計済みのみ）");

		return dataList;
	}

	// ==================== ヘルパーメソッド ====================

	/**
	 * ResultSetからOrderItemオブジェクトを生成
	 */
	private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
		OrderItem item = new OrderItem();
		item.setOrderItemId(rs.getString("ORDER_ITEM_ID"));
		item.setOrderId(rs.getString("ORDER_ID"));
		item.setDishId(rs.getString("DISH_ID"));
		item.setDishName(rs.getString("DISH_NAME"));
		item.setPrice(rs.getInt("PRICE"));
		item.setQuantity(rs.getInt("QUANTITY"));
		// itemStatusはDBに保存しないのでデフォルト値（0）

		return item;
	}

	/**
	 * 获取指定年份的每日销售总额（会計済みのみ）
	 * 返回结构：Map<月份, Map<日期, 当日总额>>
	 * 
	 * 変更履歴:
	 * 2026-02-09: 会計済み（PAYMENT_TIME IS NOT NULL）のみ集計
	 */
	public Map<Integer, Map<Integer, Integer>> getYearlySalesData(int year) throws SQLException {
		String sql = "SELECT MONTH(v.ARRIVAL_TIME) as m, DAY(v.ARRIVAL_TIME) as d, " +
				"SUM(oi.PRICE * oi.QUANTITY) as daily_sum " +
				"FROM ORDER_ITEMS oi " +
				"JOIN VISITS v ON oi.VISIT_ID = v.VISIT_ID " +
				"WHERE YEAR(v.ARRIVAL_TIME) = ? " +
				"AND v.PAYMENT_TIME IS NOT NULL " + // ← 追加: 会計済みのみ
				"GROUP BY MONTH(v.ARRIVAL_TIME), DAY(v.ARRIVAL_TIME)";

		Map<Integer, Map<Integer, Integer>> salesData = new HashMap<>();

		// 初始化 1-12 月的 Map，防止 JSP 出现空指针
		for (int i = 1; i <= 12; i++) {
			salesData.put(i, new HashMap<>());
		}

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, year);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					int month = rs.getInt("m");
					int day = rs.getInt("d");
					int sum = rs.getInt("daily_sum");
					salesData.get(month).put(day, sum);
				}
			}
		}

		System.out.println("年間売上データ取得: year=" + year + "（会計済みのみ）");

		return salesData;
	}
}

/**
 * 料理別売上データ（内部クラス）
 */
class DishSalesData {
	private String dishName;
	private int quantity;
	private int sales;

	public DishSalesData(String dishName, int quantity, int sales) {
		this.dishName = dishName;
		this.quantity = quantity;
		this.sales = sales;
	}

	public String getDishName() {
		return dishName;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getSales() {
		return sales;
	}
}
