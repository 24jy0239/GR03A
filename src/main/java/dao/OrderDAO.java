package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.OrderItem;

/**
 * OrderDAO（注文明細 - データアクセス）
 * OrderItem情報のCRUD操作
 * 
 * 重要: ORDERSテーブルは使用しない
 * ORDER_ITEMSを直接VISITに紐づける
 */
public class OrderDAO {

	private static final String URL = "jdbc:mysql://10.64.144.5:3306/24jy0228";
	private static final String USER = "24jy0228";
	private static final String PASSWORD = "24jy0228";

	/**
	 * DB接続を取得
	 */
	public Connection getConnection() throws SQLException {
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
	 * 料理別の売上集計
	 * ★ 売上分析用
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
