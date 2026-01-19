package model;

import java.time.LocalDateTime;

/**
 * TableStatus（テーブル状態）
 * テーブル状態管理画面で使用
 */
public class TableStatus {

	private int tableNum; // テーブル番号
	private String status; // 状態（空席/使用中）
	private String visitId; // 来店ID
	private int totalAmount; // 合計金額
	private LocalDateTime arrivalTime; // 来店時刻

	/**
	 * デフォルトコンストラクタ
	 */
	public TableStatus() {
	}

	/**
	 * コンストラクタ（基本情報）
	 */
	public TableStatus(int tableNum, String status, String visitId) {
		this.tableNum = tableNum;
		this.status = status;
		this.visitId = visitId;
		this.totalAmount = 0;
	}

	// ==================== Getters and Setters ====================

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	// ==================== ビジネスロジック ====================

	/**
	 * 使用中かチェック
	 */
	public boolean isOccupied() {
		return "使用中".equals(status);
	}

	/**
	 * 合計金額をフォーマット
	 */
	public String getFormattedTotal() {
		return String.format("¥%,d", totalAmount);
	}

	/**
	 * 来店時刻をフォーマット
	 */
	public String getFormattedArrivalTime() {
		if (arrivalTime == null) {
			return "";
		}
		return String.format("%02d:%02d", arrivalTime.getHour(), arrivalTime.getMinute());
	}

	/**
	 * 滞在時間を計算（分）
	 */
	public long getStayMinutes() {
		if (arrivalTime == null) {
			return 0;
		}
		return java.time.Duration.between(arrivalTime, LocalDateTime.now()).toMinutes();
	}

	/**
	 * 滞在時間をフォーマット
	 */
	public String getFormattedStayTime() {
		long minutes = getStayMinutes();

		if (minutes < 60) {
			return minutes + "分";
		} else {
			long hours = minutes / 60;
			long remainingMinutes = minutes % 60;
			return hours + "時間" + remainingMinutes + "分";
		}
	}

	@Override
	public String toString() {
		return "TableStatus{" +
				"tableNum=" + tableNum +
				", status='" + status + '\'' +
				", visitId='" + visitId + '\'' +
				", totalAmount=" + totalAmount +
				", arrivalTime=" + arrivalTime +
				'}';
	}
}
