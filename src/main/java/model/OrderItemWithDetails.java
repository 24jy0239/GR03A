package model;

import java.time.LocalDateTime;

/**
 * OrderItemWithDetails（注文明細+詳細情報）
 * キッチン画面・ホール画面で使用
 * OrderItemに追加情報（テーブル番号、注文時刻など）を持たせたクラス
 */
public class OrderItemWithDetails extends OrderItem {
    
    private int tableNum;              // テーブル番号
    private LocalDateTime orderTime;   // 注文時刻
    private String visitId;            // 来店ID
    
    /**
     * デフォルトコンストラクタ
     */
    public OrderItemWithDetails() {
        super();
    }
    
    /**
     * コンストラクタ（OrderItemから生成）
     */
    public OrderItemWithDetails(OrderItem item, int tableNum, 
                                LocalDateTime orderTime, String visitId) {
        // OrderItemの情報をコピー
        this.setOrderItemId(item.getOrderItemId());
        this.setOrderId(item.getOrderId());
        this.setDishId(item.getDishId());
        this.setDishName(item.getDishName());
        this.setPrice(item.getPrice());
        this.setQuantity(item.getQuantity());
        this.setItemStatus(item.getItemStatus());
        
        // 追加情報
        this.tableNum = tableNum;
        this.orderTime = orderTime;
        this.visitId = visitId;
    }
    
    // ==================== Getters and Setters ====================
    
    public int getTableNum() {
        return tableNum;
    }
    
    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }
    
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
    
    public String getVisitId() {
        return visitId;
    }
    
    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }
    
    // ==================== ビジネスロジック ====================
    
    /**
     * 注文時刻をフォーマット（HH:mm形式）
     */
    public String getFormattedOrderTime() {
        if (orderTime == null) {
            return "";
        }
        return String.format("%02d:%02d", orderTime.getHour(), orderTime.getMinute());
    }
    
    /**
     * 経過時間を計算（分）
     */
    public long getElapsedMinutes() {
        if (orderTime == null) {
            return 0;
        }
        return java.time.Duration.between(orderTime, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * 経過時間の文字列を取得
     */
    public String getElapsedTimeText() {
        long minutes = getElapsedMinutes();
        
        if (minutes < 60) {
            return minutes + "分前";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + "時間" + remainingMinutes + "分前";
        }
    }
    
    /**
     * 遅延しているかチェック（15分以上）
     */
    public boolean isDelayed() {
        return getElapsedMinutes() >= 15;
    }
    
    /**
     * 優先度を取得（経過時間ベース）
     */
    public String getPriority() {
        long minutes = getElapsedMinutes();
        
        if (minutes >= 20) {
            return "urgent";      // 緊急（赤）
        } else if (minutes >= 15) {
            return "warning";     // 警告（黄）
        } else {
            return "normal";      // 通常（緑）
        }
    }
    
    @Override
    public String toString() {
        return "OrderItemWithDetails{" +
               "tableNum=" + tableNum +
               ", orderTime=" + orderTime +
               ", visitId='" + visitId + '\'' +
               ", " + super.toString() +
               ", elapsed=" + getElapsedMinutes() + "分" +
               '}';
    }
}
