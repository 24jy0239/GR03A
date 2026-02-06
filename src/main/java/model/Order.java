package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order（注文）
 * 1つの注文操作に対して複数の注文明細（OrderItem）が紐づく
 * メモリ内でのみ使用（DB保存時はOrderItemをVISITに直接紐づける）
 */
public class Order {

	private String orderId; // 注文ID（例: 20260116-1005-7RTNK）
	private String visitId; // 来店ID
	private LocalDateTime orderTime; // 注文時刻
	private List<OrderItem> orderItems; // 注文明細リスト

	/**
	 * デフォルトコンストラクタ
	 */
	public Order() {
		this.orderItems = new ArrayList<>();
	}

	/**
	 * コンストラクタ（基本情報）
	 */
	public Order(String orderId, String visitId, LocalDateTime orderTime) {
		this.orderId = orderId;
		this.visitId = visitId;
		this.orderTime = orderTime;
		this.orderItems = new ArrayList<>();
	}

	// ==================== Getters and Setters ====================

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public LocalDateTime getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	// ==================== ビジネスロジック ====================

	/**
	 * 注文明細を追加
	 */
	public void addOrderItem(OrderItem item) {
		this.orderItems.add(item);
	}

	/**
	 * 注文の合計金額を計算
	 */
	public int calculateTotal() {
		int total = 0;
		for (OrderItem item : orderItems) {
			total += item.getPrice() * item.getQuantity();
		}
		return total;
	}

	/**
	 * 注文明細数を取得
	 */
	public int getItemCount() {
		return orderItems.size();
	}

	/**
	 * 全て提供済みかチェック
	 */
	public boolean isAllServed() {
		for (OrderItem item : orderItems) {
			if (item.getItemStatus() != 3) { // 3 = 配膳完了
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderId='" + orderId + '\'' +
				", visitId='" + visitId + '\'' +
				", orderTime=" + orderTime +
				", orderItems=" + orderItems.size() + " items" +
				", total=" + calculateTotal() +
				'}';
	}
}
