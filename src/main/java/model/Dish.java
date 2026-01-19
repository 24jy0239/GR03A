package model;

/**
 * Dish（料理マスタ）
 * 料理の基本情報
 */
public class Dish {
    
    private String dishId;       // 料理ID（例: DIS001）
    private String name;         // 料理名
    private int price;           // 価格
    private String category;     // カテゴリID
    private String photo;        // 写真ファイル名
    private boolean available;   // 利用可能フラグ
    
    /**
     * デフォルトコンストラクタ
     */
    public Dish() {
    }
    
    /**
     * コンストラクタ（全フィールド）
     */
    public Dish(String dishId, String name, int price, String category, 
                String photo, boolean available) {
        this.dishId = dishId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.photo = photo;
        this.available = available;
    }
    
    // ==================== Getters and Setters ====================
    
    public String getDishId() {
        return dishId;
    }
    
    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPhoto() {
        return photo;
    }
    
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // ==================== ビジネスロジック ====================
    
    /**
     * 価格をフォーマット（¥1,000形式）
     */
    public String getFormattedPrice() {
        return String.format("¥%,d", price);
    }
    
    /**
     * 写真のパスを取得
     */
    public String getPhotoPath() {
        if (photo == null || photo.isEmpty()) {
            return "images/no-image.jpg";
        }
        return "images/" + photo;
    }
    
    @Override
    public String toString() {
        return "Dish{" +
               "dishId='" + dishId + '\'' +
               ", name='" + name + '\'' +
               ", price=" + price +
               ", category='" + category + '\'' +
               ", photo='" + photo + '\'' +
               ", available=" + available +
               '}';
    }
}

