package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Visit（来店記録）
 * 1つの来店に対して複数の注文（Order）が紐づく
 * 
 * 変更履歴:
 * 2026-02-02: calculateTotalAmount()メソッド追加（削除機能対応、計算統一）
 */
public class Visit {
    
    private String visitId;              // 来店ID（例: 20260116-1030-XY7K9）
    private int tableNum;                // テーブル番号
    private LocalDateTime arrivalTime;   // 来店時刻
    private int totalAmount;             // 合計金額
    private LocalDateTime paymentTime;   // 会計時刻（NULL = 会計前）
    private List<Order> orders;          // 注文リスト（追加注文対応）
    
    /**
     * デフォルトコンストラクタ
     */
    public Visit() {
        this.orders = new ArrayList<>();
    }
    
    /**
     * コンストラクタ（基本情報）
     */
    public Visit(String visitId, int tableNum, LocalDateTime arrivalTime) {
        this.visitId = visitId;
        this.tableNum = tableNum;
        this.arrivalTime = arrivalTime;
        this.totalAmount = 0;
        this.orders = new ArrayList<>();
    }
    
    // ==================== Getters and Setters ====================
    
    public String getVisitId() {
        return visitId;
    }
    
    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }
    
    public int getTableNum() {
        return tableNum;
    }
    
    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public int getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    // ==================== ビジネスロジック ====================
    
    /**
     * 注文を追加
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }
    
    /**
     * 会計済みかチェック
     */
    public boolean isPaid() {
        return paymentTime != null;
    }
    
    /**
     * 注文数を取得
     */
    public int getOrderCount() {
        return orders.size();
    }
    
    /**
     * 全OrderItemを取得（全注文の全明細）
     */
    public List<OrderItem> getAllOrderItems() {
        List<OrderItem> allItems = new ArrayList<>();
        for (Order order : orders) {
            allItems.addAll(order.getOrderItems());
        }
        return allItems;
    }
    
    /**
     * 合計金額を再計算
     * すべての注文明細の小計を合算してtotalAmountを更新
     * 
     * 使用場面:
     * - 注文追加時（計算統一）
     * - 注文明細の削除時
     * - 注文明細の数量変更時
     * - 会計時の最終確認
     */
    public void calculateTotalAmount() {
        int total = 0;
        
        // すべてのOrderを巡回
        for (Order order : orders) {
            // 各OrderのすべてのOrderItemを巡回
            for (OrderItem item : order.getOrderItems()) {
                // 各OrderItemの小計を合算
                total += item.getSubtotal();
            }
        }
        
        // totalAmountフィールドを更新
        this.totalAmount = total;
        
        System.out.println("Visit合計金額再計算: visitId=" + visitId + ", total=¥" + total);
    }
    
    @Override
    public String toString() {
        return "Visit{" +
               "visitId='" + visitId + '\'' +
               ", tableNum=" + tableNum +
               ", arrivalTime=" + arrivalTime +
               ", totalAmount=" + totalAmount +
               ", paymentTime=" + paymentTime +
               ", orders=" + orders.size() + " orders" +
               '}';
    }
}
