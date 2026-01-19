package model;

/**
 * Category（カテゴリマスタ）
 * 料理カテゴリの基本情報
 */
public class Category {
    
    private String categoryId;    // カテゴリID（例: CAT001）
    private String categoryName;  // カテゴリ名（例: 麺類）
    
    /**
     * デフォルトコンストラクタ
     */
    public Category() {
    }
    
    /**
     * コンストラクタ（全フィールド）
     */
    public Category(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    
    // ==================== Getters and Setters ====================
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @Override
    public String toString() {
        return "Category{" +
               "categoryId='" + categoryId + '\'' +
               ", categoryName='" + categoryName + '\'' +
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Category category = (Category) o;
        
        return categoryId != null ? categoryId.equals(category.categoryId) : category.categoryId == null;
    }
    
    @Override
    public int hashCode() {
        return categoryId != null ? categoryId.hashCode() : 0;
    }
}
