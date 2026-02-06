package manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import dao.OrderDAO;
import dao.VisitDAO;
import model.CartItem;
import model.Order;
import model.OrderItem;
import model.OrderItemWithDetails;
import model.TableStatus;
import model.Visit;

/**
 * OrderManager（注文管理マネージャー）
 * シングルトンパターンで全てのVisit/Order/OrderItemをメモリ管理
 */
public class OrderManager {

	// シングルトンインスタンス
	private static final OrderManager instance = new OrderManager();

	// Visit管理（visitId → Visit）
	private final ConcurrentHashMap<String, Visit> visits;

	// ランダム文字セット
	private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * プライベートコンストラクタ
	 */
	private OrderManager() {
		this.visits = new ConcurrentHashMap<>();
	}

	/**
	 * シングルトンインスタンスを取得
	 */
	public static OrderManager getInstance() {
		return instance;
	}

	// ==================== ID生成 ====================

	/**
	 * VISIT_ID生成
	 * フォーマット: VISYYMMDDHHMMss
	 * 例: VIS260116103045
	 * 長さ: 15文字
	 */
	public String generateVisitId() {
		LocalDateTime now = LocalDateTime.now();

		// VIS + 年月日時分秒（YYMMDDHHMMss）
		String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

		return "VIS" + timestamp;
	}

	/**
	 * ORDER_ID生成
	 * フォーマット: ORDYYMMDDHHMMss
	 * 例: ORD260116103045
	 * 長さ: 15文字
	 */
	public String generateOrderId() {
		LocalDateTime now = LocalDateTime.now();

		// ORD + 年月日時分秒（YYMMDDHHMMss）
		String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

		return "ORD" + timestamp;
	}

	/**
	 * ORDER_ITEM_ID生成
	 * フォーマット: ITMYYMMDDHHMMssRRR
	 * 例: ITM26011610304ABC
	 * 長さ: 18文字（最後の3文字はランダム）
	 */
	public String generateOrderItemId() {
		LocalDateTime now = LocalDateTime.now();

		// ITM + 年月日時分秒（YYMMDDHHMMss） + ランダム3文字
		String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		String randomChars = generateRandomString(3);

		return "ITM" + timestamp + randomChars;
	}

	/**
	 * ランダム英数文字列生成
	 */
	private String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(RANDOM_CHARS.length());
			sb.append(RANDOM_CHARS.charAt(index));
		}

		return sb.toString();
	}

	// ==================== Visit管理 ====================

	/**
	 * Visit生成
	 */
	public Visit createVisit(int tableNum) {
		Visit visit = new Visit();
		visit.setVisitId(generateVisitId());
		visit.setTableNum(tableNum);
		visit.setArrivalTime(LocalDateTime.now());
		visit.setTotalAmount(0);

		visits.put(visit.getVisitId(), visit);

		System.out.println("Visit作成: visitId=" + visit.getVisitId()
				+ ", table=" + tableNum
				+ ", time=" + visit.getArrivalTime());

		return visit;
	}

	/**
	 * Visit取得
	 */
	public Visit getVisit(String visitId) {
		return visits.get(visitId);
	}

	/**
	 * 全Visit取得
	 */
	public Collection<Visit> getAllVisits() {
		return visits.values();
	}

	/**
	 * Visit削除
	 */
	public void removeVisit(String visitId) {
		Visit removed = visits.remove(visitId);
		if (removed != null) {
			System.out.println("Visit削除: visitId=" + visitId);
		}
	}

	/**
	 * 全Visit削除（リセット用）
	 */
	public void removeAllVisits() {
		visits.clear();
		System.out.println("全Visit削除");
	}

	// ==================== Order管理 ====================

	/**
	 * カートから注文作成
	 */
	public Order createOrderFromCart(String visitId, List<CartItem> cartList) {
		Visit visit = visits.get(visitId);
		if (visit == null) {
			throw new IllegalArgumentException("Visit not found: " + visitId);
		}

		Order order = new Order();
		order.setOrderId(generateOrderId());
		order.setVisitId(visitId);
		order.setOrderTime(LocalDateTime.now());

		// CartItem → OrderItem変換
		for (CartItem cart : cartList) {
			OrderItem item = new OrderItem();
			item.setOrderItemId(generateOrderItemId());
			item.setOrderId(order.getOrderId());
			item.setDishId(cart.getDishId());
			item.setDishName(cart.getName());
			item.setPrice(cart.getPrice());
			item.setQuantity(cart.getQuantity());
			item.setItemStatus(0); // 0 = 注文

			order.addOrderItem(item);

			// 合計金額を更新
			synchronized (visit) {
				int subtotal = cart.getPrice() * cart.getQuantity();
				visit.setTotalAmount(visit.getTotalAmount() + subtotal);
			}
		}

		visit.addOrder(order);

		System.out.println("注文作成: orderId=" + order.getOrderId()
				+ ", items=" + order.getItemCount()
				+ ", total=" + order.calculateTotal());

		return order;
	}

	// ==================== OrderItem管理 ====================

	/**
	 * OrderItem検索（全Visit/Order内から）
	 */
	private OrderItem findOrderItem(String orderItemId) {
		for (Visit visit : visits.values()) {
			for (Order order : visit.getOrders()) {
				for (OrderItem item : order.getOrderItems()) {
					if (item.getOrderItemId().equals(orderItemId)) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * OrderItemの調理状態を更新
	 */
	public boolean updateItemStatus(String orderItemId, int newStatus) {
		OrderItem item = findOrderItem(orderItemId);

		if (item != null) {
			int oldStatus = item.getItemStatus();
			item.setItemStatus(newStatus);

			System.out.println("状態更新: itemId=" + orderItemId
					+ ", " + oldStatus + " → " + newStatus
					+ " (" + item.getStatusText() + ")");
			return true;
		}

		return false;
	}

	// ==================== キッチン/ホール画面用 ====================

	/**
	 * 全OrderItemWithDetailsを取得（キッチン画面用）
	 * 状態でフィルタリング可能
	 */
	public List<OrderItemWithDetails> getAllOrderItemsWithDetails(int... statusFilter) {
		List<OrderItemWithDetails> result = new ArrayList<>();
		Set<Integer> filterSet = new HashSet<>();

		for (int status : statusFilter) {
			filterSet.add(status);
		}

		for (Visit visit : visits.values()) {
			// 会計済みはスキップ
			if (visit.isPaid()) {
				continue;
			}

			for (Order order : visit.getOrders()) {
				for (OrderItem item : order.getOrderItems()) {
					// フィルタリング
					if (!filterSet.isEmpty() && !filterSet.contains(item.getItemStatus())) {
						continue;
					}

					OrderItemWithDetails detail = new OrderItemWithDetails(
							item,
							visit.getTableNum(),
							order.getOrderTime(),
							visit.getVisitId());

					result.add(detail);
				}
			}
		}

		// 注文時刻でソート（古い順）
		result.sort(Comparator.comparing(OrderItemWithDetails::getOrderTime));

		return result;
	}

	/**
	 * キッチン画面用：未完了のOrderItem取得（状態0,1）
	 */
	public List<OrderItemWithDetails> getKitchenItems() {
		return getAllOrderItemsWithDetails(0, 1); // 注文、調理中
	}

	/**
	 * ホール画面用：提供待ちのOrderItem取得（状態2）
	 */
	public List<OrderItemWithDetails> getHallItems() {
		return getAllOrderItemsWithDetails(2); // 完了（提供待ち）
	}

	// ==================== テーブル状態管理 ====================

	/**
	 * テーブル状態リストを取得
	 */
	public List<TableStatus> getTableStatusList() {
		Map<Integer, TableStatus> statusMap = new HashMap<>();

		for (Visit visit : visits.values()) {
			// 会計済みはスキップ
			if (visit.isPaid()) {
				continue;
			}

			int tableNum = visit.getTableNum();

			// 既存のStatusがあれば取得、なければ新規作成
			TableStatus status = statusMap.getOrDefault(
					tableNum,
					new TableStatus(tableNum, "使用中", visit.getVisitId()));

			// 合計金額を更新
			status.setTotalAmount(visit.getTotalAmount());

			// 来店時刻を設定
			status.setArrivalTime(visit.getArrivalTime());

			statusMap.put(tableNum, status);
		}

		List<TableStatus> result = new ArrayList<>(statusMap.values());
		result.sort(Comparator.comparingInt(TableStatus::getTableNum));

		return result;
	}

	// ==================== 会計処理 ====================

	/**
	 * 会計完了
	 */
	public void completeVisit(String visitId, LocalDateTime paymentTime) {
		Visit visit = visits.get(visitId);
		if (visit == null) {
			throw new IllegalArgumentException("Visit not found: " + visitId);
		}

		visit.setPaymentTime(paymentTime);

		System.out.println("会計完了: visitId=" + visitId
				+ ", total=" + visit.getTotalAmount()
				+ ", time=" + paymentTime);
	}

	/**
	 * DB保存（特定の Order の OrderItem のみ）
	 */
	public void saveOrderItems(Order order) throws SQLException {
		if (order == null) {
			throw new IllegalArgumentException("Order is null");
		}

		String visitId = order.getVisitId();
		Visit visit = visits.get(visitId);

		if (visit == null) {
			throw new IllegalArgumentException("Visit not found: " + visitId);
		}

		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;

		try {
			// トランザクション開始
			conn = orderDAO.getConnection();
			conn.setAutoCommit(false);

			// Visit が DB に存在しない場合は保存
			VisitDAO visitDAO = new VisitDAO();
			try {
				visitDAO.save(conn, visit);
				System.out.println("Visit保存: " + visitId);
			} catch (SQLException e) {
				// 既に存在する場合は無視（または UPDATE）
				System.out.println("Visit既存: " + visitId);
			}

			// OrderItem 保存
			int itemCount = 0;
			for (OrderItem item : order.getOrderItems()) {
				orderDAO.saveItemDirect(conn, visitId, item);
				itemCount++;
			}
			System.out.println("OrderItem保存: " + itemCount + "件");

			// コミット
			conn.commit();
			System.out.println("DB保存完了: orderId=" + order.getOrderId());

		} catch (SQLException e) {
			// ロールバック
			if (conn != null) {
				try {
					conn.rollback();
					System.out.println("ロールバック実行");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw e;

		} finally {
			// 接続クローズ
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * DB保存（Visit + 全OrderItem）
	 */
	public void saveVisitWithAllOrders(String visitId) throws SQLException {
		Visit visit = visits.get(visitId);
		if (visit == null) {
			throw new IllegalArgumentException("Visit not found: " + visitId);
		}

		VisitDAO visitDAO = new VisitDAO();
		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;

		try {
			// トランザクション開始
			conn = visitDAO.getConnection();
			conn.setAutoCommit(false);

			// Visit保存
			visitDAO.save(conn, visit);
			System.out.println("Visit保存: " + visitId);

			// OrderItem直接保存（ORDERSテーブルなし）
			int itemCount = 0;
			for (Order order : visit.getOrders()) {
				for (OrderItem item : order.getOrderItems()) {
					orderDAO.saveItemDirect(conn, visitId, item);
					itemCount++;
				}
			}
			System.out.println("OrderItem保存: " + itemCount + "件");

			// コミット
			conn.commit();
			System.out.println("DB保存完了: visitId=" + visitId);

		} catch (SQLException e) {
			// ロールバック
			if (conn != null) {
				try {
					conn.rollback();
					System.out.println("ロールバック実行");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw e;

		} finally {
			// 接続クローズ
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ==================== デバッグ用 ====================

	/**
	 * visits のサイズを取得（デバッグ用）
	 */
	public int getVisitsCount() {
		return visits.size();
	}

	/**
	 * 全ての Visit の情報を出力（デバッグ用）
	 */
	public void printAllVisits() {
		System.out.println("=== visits の内容 ===");
		System.out.println("visits.size(): " + visits.size());

		if (visits.isEmpty()) {
			System.out.println("  (空 - データがありません)");
		} else {
			for (java.util.Map.Entry<String, Visit> entry : visits.entrySet()) {
				String visitId = entry.getKey();
				Visit visit = entry.getValue();

				System.out.println("Visit: " + visitId);
				System.out.println("  tableNum: " + visit.getTableNum());
				System.out.println("  orders: " + visit.getOrders().size());

				for (Order order : visit.getOrders()) {
					System.out.println("    Order: " + order.getOrderId());
					System.out.println("      items: " + order.getItemCount());

					for (OrderItem item : order.getOrderItems()) {
						System.out.println("        - " + item.getDishName()
								+ " (status=" + item.getItemStatus()
								+ ", qty=" + item.getQuantity() + ")");
					}
				}
			}
		}
		System.out.println("=====================");
	}

	/**
	 * 現在の状態を表示
	 */
	public void printStatus() {
		System.out.println("========== OrderManager Status ==========");
		System.out.println("Total Visits: " + visits.size());

		for (Visit visit : visits.values()) {
			System.out.println("  Visit: " + visit.getVisitId()
					+ ", Table: " + visit.getTableNum()
					+ ", Orders: " + visit.getOrderCount()
					+ ", Total: ¥" + visit.getTotalAmount()
					+ ", Paid: " + visit.isPaid());

			for (Order order : visit.getOrders()) {
				System.out.println("    Order: " + order.getOrderId()
						+ ", Items: " + order.getItemCount()
						+ ", Time: " + order.getOrderTime());

				for (OrderItem item : order.getOrderItems()) {
					System.out.println("      Item: " + item.getDishName()
							+ " x" + item.getQuantity()
							+ ", Status: " + item.getStatusText());
				}
			}
		}
		System.out.println("==========================================");
	}

	/**
	 * 年間の売上マトリックスを取得
	 */
	public Map<Integer, Map<Integer, Integer>> getYearlySalesMatrix(int year) {
		OrderDAO dao = new OrderDAO();
		try {
			// DAOからデータを取得（初期化済みのMapが返ってくる）
			return dao.getYearlySalesData(year);
		} catch (SQLException e) {
			e.printStackTrace();
			// エラー時は空の構造を返す
			Map<Integer, Map<Integer, Integer>> emptyMap = new HashMap<>();
			for (int i = 1; i <= 12; i++)
				emptyMap.put(i, new HashMap<>());
			return emptyMap;
		}
	}

	/**
	 * 年間の総合計金額を計算
	 */
	public long calculateYearlyTotal(Map<Integer, Map<Integer, Integer>> matrix) {
		long total = 0;
		for (Map<Integer, Integer> monthMap : matrix.values()) {
			for (Integer dailyAmount : monthMap.values()) {
				if (dailyAmount != null) {
					total += dailyAmount;
				}
			}
		}
		return total;
	}

	// ==================== OrderManager.java に追加するメソッド ====================

	/**
	 * ホール画面用：全進捗のOrderItem取得（状態0,1,2）
	 * 
	 * 改善内容:
	 * - 旧版: status=2（完了）のみ表示
	 * - 新版: status=0,1,2（注文、調理中、完了）すべて表示
	 * 
	 * 用途: ホールスタッフが注文全体の進捗を把握するため
	 * 
	 * @return 全進捗のOrderItemリスト（注文時刻でソート）
	 */
	public List<OrderItemWithDetails> getAllProgressItems() {
		return getAllOrderItemsWithDetails(0, 1, 2); // 注文、調理中、完了
	}

	// ==================== 使用方法 ====================
	// HallServlet.java で以下のように変更:
	//
	// 【旧】
	// List<OrderItemWithDetails> hallItems = manager.getHallItems();
	//
	// 【新】
	// List<OrderItemWithDetails> allProgressItems = manager.getAllProgressItems();
}
