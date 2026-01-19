package model;

/**
 * OrderItem（注文明細）
 * 1つの料理の注文情報
 */
public class OrderItem {
    
    private String orderItemId;   // 注文明細ID（例: 20260116-1005-M3P8Q）
    private String orderId;       // 注文ID
    private String dishId;        // 料理ID
    private String dishName;      // 料理名（注文時の名前）
    private int price;            // 単価（注文時の価格）
    private int quantity;         // 数量
    private int itemStatus;       // 調理状態（0:注文 1:調理中 2:完了 3:配膳）
    
    /**
     * デフォルトコンストラクタ
     */
    public OrderItem() {
        this.itemStatus = 0;  // デフォルト: 注文状態
    }
    
    /**
     * コンストラクタ（基本情報）
     */
    public OrderItem(String orderItemId, String orderId, String dishId, 
                     String dishName, int price, int quantity) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.dishId = dishId;
        this.dishName = dishName;
        this.price = price;
        this.quantity = quantity;
        this.itemStatus = 0;
    }
    
    // ==================== Getters and Setters ====================
    
    public String getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getDishId() {
        return dishId;
    }
    
    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
    
    public String getDishName() {
        return dishName;
    }
    
    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getItemStatus() {
        return itemStatus;
    }
    
    public void setItemStatus(int itemStatus) {
        this.itemStatus = itemStatus;
    }
    
    // ==================== ビジネスロジック ====================
    
    /**
     * 小計を計算
     */
    public int getSubtotal() {
        return price * quantity;
    }
    
    /**
     * 調理状態の文字列を取得
     */
    public String getStatusText() {
        switch (itemStatus) {
            case 0: return "注文";
            case 1: return "調理中";
            case 2: return "完了";
            case 3: return "配膳";
            default: return "不明";
        }
    }
    
    /**
     * 調理状態のCSS クラスを取得（画面表示用）
     */
    public String getStatusClass() {
        switch (itemStatus) {
            case 0: return "status-ordered";
            case 1: return "status-cooking";
            case 2: return "status-ready";
            case 3: return "status-served";
            default: return "status-unknown";
        }
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
               "orderItemId='" + orderItemId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", dishId='" + dishId + '\'' +
               ", dishName='" + dishName + '\'' +
               ", price=" + price +
               ", quantity=" + quantity +
               ", itemStatus=" + itemStatus + " (" + getStatusText() + ")" +
               ", subtotal=" + getSubtotal() +
               '}';
    }
}
