package model;

import java.util.List;

/**
 * VisitWithDetails - 来店情報＋注文明細＋画面表示用データ
 * 
 * 用途:
 * - 日別/週別/月別の売上詳細画面
 * - Servlet → JSP へのデータ転送
 * 
 * 設計パターン: DTO (Data Transfer Object)
 * - ビジネスロジックを持たない
 * - 画面表示用のデータを保持
 * - Servletでフォーマット済みデータを設定
 */
public class VisitWithDetails {

	private Visit visit; // 来店情報
	private List<OrderItem> orderItems; // 注文明細一覧
	private String arrivalTimeFormatted; // フォーマット済み来店時刻（例: "12:30"）
	private String paymentTimeFormatted; // フォーマット済み会計時刻（例: "13:15" or "未会計"）

	/**
	 * デフォルトコンストラクタ
	 */
	public VisitWithDetails() {
	}

	/**
	 * コンストラクタ（全フィールド）
	 */
	public VisitWithDetails(Visit visit, List<OrderItem> orderItems,
			String arrivalTimeFormatted, String paymentTimeFormatted) {
		this.visit = visit;
		this.orderItems = orderItems;
		this.arrivalTimeFormatted = arrivalTimeFormatted;
		this.paymentTimeFormatted = paymentTimeFormatted;
	}

	// ==================== Getters and Setters ====================

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public String getArrivalTimeFormatted() {
		return arrivalTimeFormatted;
	}

	public void setArrivalTimeFormatted(String arrivalTimeFormatted) {
		this.arrivalTimeFormatted = arrivalTimeFormatted;
	}

	public String getPaymentTimeFormatted() {
		return paymentTimeFormatted;
	}

	public void setPaymentTimeFormatted(String paymentTimeFormatted) {
		this.paymentTimeFormatted = paymentTimeFormatted;
	}

	// ==================== ビジネスロジック ====================

	/**
	 * 注文数を取得
	 */
	public int getOrderItemCount() {
		return (orderItems != null) ? orderItems.size() : 0;
	}

	/**
	 * 合計金額を取得
	 */
	public int getTotalAmount() {
		return (visit != null) ? visit.getTotalAmount() : 0;
	}

	@Override
	public String toString() {
		return "VisitWithDetails{" +
				"visitId='" + (visit != null ? visit.getVisitId() : "null") + '\'' +
				", tableNum=" + (visit != null ? visit.getTableNum() : 0) +
				", arrivalTime='" + arrivalTimeFormatted + '\'' +
				", paymentTime='" + paymentTimeFormatted + '\'' +
				", orderItemCount=" + getOrderItemCount() +
				", totalAmount=" + getTotalAmount() +
				'}';
	}
}
