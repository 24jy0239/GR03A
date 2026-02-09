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

	// ==================== 1. createOrderFromCartメソッドを修正（177行目付近）====================

	/**
	 * カートから注文を作成
	 * 
	 * 変更履歴:
	 * 2026-02-02: calculateTotalAmount()メソッド使用に統一
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

			// ========================================
			// 旧コード（削除）:
			// synchronized (visit) {
			//     int subtotal = cart.getPrice() * cart.getQuantity();
			//     visit.setTotalAmount(visit.getTotalAmount() + subtotal);
			// }
			// ========================================
		}

		visit.addOrder(order);

		// ========================================
		// 新コード: calculateTotalAmount()使用（統一！）
		// ========================================
		visit.calculateTotalAmount();

		System.out.println("注文作成: orderId=" + order.getOrderId()
				+ ", items=" + order.getItemCount()
				+ ", visitTotal=¥" + visit.getTotalAmount());

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

	// ==================== OrderManager.java の getTableStatusList メソッド修正版 ====================
	// 317行目付近を以下に置き換え

	/**
	 * テーブル状態リストを取得
	 * 
	 * 修正内容:
	 * - 同じテーブル番号で複数のVisitがある場合、注文のあるVisitを優先
	 * - 注文数が同じ場合は、より新しいVisit（来店時刻が遅い）を優先
	 */
	public List<TableStatus> getTableStatusList() {
		Map<Integer, TableStatus> statusMap = new HashMap<>();

		for (Visit visit : visits.values()) {
			// 会計済みはスキップ
			if (visit.isPaid()) {
				continue;
			}

			int tableNum = visit.getTableNum();

			// 既存のStatusを確認
			TableStatus existingStatus = statusMap.get(tableNum);

			// 既存のStatusがない、または既存より優先度が高い場合に更新
			if (existingStatus == null || shouldReplaceVisit(existingStatus.getVisitId(), visit.getVisitId())) {
				TableStatus status = new TableStatus(tableNum, "使用中", visit.getVisitId());
				status.setTotalAmount(visit.getTotalAmount());
				status.setArrivalTime(visit.getArrivalTime());
				statusMap.put(tableNum, status);

				System.out.println("TableStatus更新: table=" + tableNum
						+ ", visitId=" + visit.getVisitId()
						+ ", orders=" + visit.getOrderCount()
						+ ", amount=¥" + visit.getTotalAmount());
			}
		}

		List<TableStatus> result = new ArrayList<>(statusMap.values());
		result.sort(Comparator.comparingInt(TableStatus::getTableNum));

		return result;
	}

	/**
	 * Visitの置き換え判定
	 * 
	 * 優先順位:
	 * 1. 注文数が多いVisit
	 * 2. 注文数が同じ場合、来店時刻が新しいVisit
	 * 
	 * @param currentVisitId 現在のvisitId
	 * @param newVisitId 新しいvisitId
	 * @return 新しいVisitで置き換えるべきならtrue
	 */
	private boolean shouldReplaceVisit(String currentVisitId, String newVisitId) {
		Visit currentVisit = visits.get(currentVisitId);
		Visit newVisit = visits.get(newVisitId);

		if (currentVisit == null) {
			return true; // 現在のVisitがnullなら置き換え
		}
		if (newVisit == null) {
			return false; // 新しいVisitがnullなら置き換えない
		}

		// 1. 注文数で比較
		int currentOrders = currentVisit.getOrderCount();
		int newOrders = newVisit.getOrderCount();

		if (newOrders > currentOrders) {
			System.out.println("  → 注文数で置き換え: " + currentOrders + " → " + newOrders);
			return true; // 新しいVisitの方が注文が多い
		}
		if (newOrders < currentOrders) {
			return false; // 現在のVisitの方が注文が多い
		}

		// 2. 注文数が同じ場合、来店時刻で比較（新しい方を優先）
		if (newVisit.getArrivalTime() != null && currentVisit.getArrivalTime() != null) {
			boolean isNewer = newVisit.getArrivalTime().isAfter(currentVisit.getArrivalTime());
			if (isNewer) {
				System.out.println("  → 来店時刻で置き換え");
			}
			return isNewer;
		}

		return false;
	}

	// ==================== 統合方法 ====================
	// 
	// 1. OrderManager.java の getTableStatusList() メソッド（317行目付近）を上記に置き換え
	// 2. shouldReplaceVisit() メソッドを getTableStatusList() の後に追加

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

	// ==================== OrderManager.java に追加するメソッド ====================

	/**
	 * 注文明細を削除（取り消し）
	 * 
	 * 制限:
	 * - status=0（注文）の場合のみ削除可能
	 * - status=1,2,3（調理中、完了、配膳済）は削除不可
	 * 
	 * @param orderItemId 削除する注文明細のID
	 * @return 削除成功ならtrue、失敗ならfalse
	 */
	public boolean deleteOrderItem(String orderItemId) {
		System.out.println("====================================");
		System.out.println("注文明細削除:");
		System.out.println("  orderItemId: " + orderItemId);

		// 全visitを検索
		for (Visit visit : visits.values()) {
			for (Order order : visit.getOrders()) {
				List<OrderItem> items = order.getOrderItems();

				for (int i = 0; i < items.size(); i++) {
					OrderItem item = items.get(i);

					if (item.getOrderItemId().equals(orderItemId)) {
						// status=0（注文）のみ削除可能
						if (item.getItemStatus() != 0) {
							System.out.println("  ❌ 削除不可: status=" + item.getItemStatus() + "（調理開始済み）");
							System.out.println("====================================");
							return false;
						}

						// 削除前の情報をログ出力
						System.out.println("  削除対象:");
						System.out.println("    料理名: " + item.getDishName());
						System.out.println("    数量: " + item.getQuantity());
						System.out.println("    単価: ¥" + item.getPrice());
						System.out.println("    小計: ¥" + item.getSubtotal());

						// 削除
						items.remove(i);

						// Visit合計金額を再計算
						visit.calculateTotalAmount();

						System.out.println("  ✅ 削除成功");
						System.out.println("  更新後の訪問合計: ¥" + visit.getTotalAmount());
						System.out.println("====================================");
						return true;
					}
				}
			}
		}

		System.out.println("  ❌ 削除失敗: 注文明細が見つかりません");
		System.out.println("====================================");
		return false;
	}

	/**
	 * 注文明細の削除可否をチェック
	 * 
	 * @param orderItemId チェックする注文明細のID
	 * @return 削除可能ならtrue、不可ならfalse
	 */
	public boolean canDeleteOrderItem(String orderItemId) {
		for (Visit visit : visits.values()) {
			for (Order order : visit.getOrders()) {
				for (OrderItem item : order.getOrderItems()) {
					if (item.getOrderItemId().equals(orderItemId)) {
						// status=0（注文）のみ削除可能
						return item.getItemStatus() == 0;
					}
				}
			}
		}
		return false;
	}

	// ==================== 使用例 ====================
	// TableStatusServlet.java で以下のように使用:
	//
	// String orderItemId = request.getParameter("orderItemId");
	// boolean success = manager.deleteOrderItem(orderItemId);
	//
	// if (success) {
	//	     System.out.println("注文明細を削除しました");
	// } else {
	//	     System.out.println("削除できませんでした（調理開始済み）");
	// }

	// ==================== OrderManager.java に追加するメソッド ====================
	// deleteOrderItem()メソッドの後に追加してください

	/**
	 * 注文明細を作り直し（提供済み→未制作に変更）
	 * 
	 * 用途:
	 * - 料理の制作不備対応
	 * - 顧客からのクレーム対応
	 * 
	 * 制限:
	 * - status=3（配膳済）の場合のみ作り直し可能
	 * - status=0,1,2は作り直し不可
	 * 
	 * 処理:
	 * - status=3 → status=0 に変更
	 * - キッチン画面に再表示される
	 * - ホール画面で再度配膳可能になる
	 * 
	 * @param orderItemId 作り直しする注文明細のID
	 * @return 作り直し成功ならtrue、失敗ならfalse
	 */
	public boolean resetOrderItem(String orderItemId) {
		System.out.println("====================================");
		System.out.println("注文明細作り直し:");
		System.out.println("  orderItemId: " + orderItemId);

		for (Visit visit : visits.values()) {
			for (Order order : visit.getOrders()) {
				List<OrderItem> items = order.getOrderItems();

				for (OrderItem item : items) {
					if (item.getOrderItemId().equals(orderItemId)) {
						// status=3（配膳済）のみ作り直し可能
						if (item.getItemStatus() != 3) {
							System.out.println("  ❌ 作り直し不可: status=" + item.getItemStatus() + "（配膳済みではありません）");
							System.out.println("====================================");
							return false;
						}

						// 作り直し前の情報をログ出力
						System.out.println("  作り直し対象:");
						System.out.println("    料理名: " + item.getDishName());
						System.out.println("    数量: " + item.getQuantity());
						System.out.println("    単価: ¥" + item.getPrice());
						System.out.println("    現在のステータス: 3（配膳済）");

						// status を 0（未制作）に変更
						item.setItemStatus(0);

						System.out.println("  ✅ 作り直し成功");
						System.out.println("  新しいステータス: 0（未制作）");
						System.out.println("  → キッチン画面に表示されます");
						System.out.println("====================================");
						return true;
					}
				}
			}
		}

		System.out.println("  ❌ 作り直し失敗: 注文明細が見つかりません");
		System.out.println("====================================");
		return false;
	}

	/**
	 * 注文明細の作り直し可否をチェック
	 * 
	 * @param orderItemId チェックする注文明細のID
	 * @return 作り直し可能ならtrue、不可ならfalse
	 */
	public boolean canResetOrderItem(String orderItemId) {
		for (Visit visit : visits.values()) {
			for (Order order : visit.getOrders()) {
				for (OrderItem item : order.getOrderItems()) {
					if (item.getOrderItemId().equals(orderItemId)) {
						// status=3（配膳済）のみ作り直し可能
						return item.getItemStatus() == 3;
					}
				}
			}
		}
		return false;
	}

	// ==================== 使用例 ====================
	// TableStatusServlet.java で以下のように使用:
	//
	// String orderItemId = request.getParameter("orderItemId");
	// boolean success = manager.resetOrderItem(orderItemId);
	//
	// if (success) {
	//	     System.out.println("料理を作り直します");
	// } else {
	//	     System.out.println("作り直しできませんでした（配膳済みではありません）");
	// }

}
