package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Dish;

/**
 * DishDAO（料理マスタ - データアクセス）
 * 料理情報のCRUD操作
 */
public class DishDAO {
    
	private static final String URL = "jdbc:mysql://localhost:3306/24ｊｙ0228";
    private static final String USER = "24ｊｙ0228";
    private static final String PASSWORD = "24ｊｙ0228";
    
    /**
     * DB接続を取得
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // ==================== READ ====================
    
    /**
     * 利用可能な料理を全て取得
     * ★ メニュー表示用（最も重要）
     */
    public List<Dish> findAvailable() throws SQLException {
        String sql = "SELECT DISH_ID, DISH_NAME, DISH_PRICE, " +
                     "DISH_CATEGORY, DISH_PHOTO, DISH_AVAILABLE " +
                     "FROM DISH " +
                     "WHERE DISH_AVAILABLE = 1 " +
                     "ORDER BY DISH_CATEGORY, DISH_ID";
        
        return executeQuery(sql);
    }
    
    /**
     * 全ての料理を取得（管理画面用）
     */
    public List<Dish> findAll() throws SQLException {
        String sql = "SELECT DISH_ID, DISH_NAME, DISH_PRICE, " +
                     "DISH_CATEGORY, DISH_PHOTO, DISH_AVAILABLE " +
                     "FROM DISH " +
                     "ORDER BY DISH_CATEGORY, DISH_ID";
        
        return executeQuery(sql);
    }
    
    /**
     * IDで料理を取得
     */
    public Dish findById(String dishId) throws SQLException {
        String sql = "SELECT DISH_ID, DISH_NAME, DISH_PRICE, " +
                     "DISH_CATEGORY, DISH_PHOTO, DISH_AVAILABLE " +
                     "FROM DISH " +
                     "WHERE DISH_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dishId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDish(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * カテゴリ別に料理を取得
     */
    public List<Dish> findByCategory(String categoryId) throws SQLException {
        String sql = "SELECT DISH_ID, DISH_NAME, DISH_PRICE, " +
                     "DISH_CATEGORY, DISH_PHOTO, DISH_AVAILABLE " +
                     "FROM DISH " +
                     "WHERE DISH_CATEGORY = ? AND DISH_AVAILABLE = 1 " +
                     "ORDER BY DISH_ID";
        
        List<Dish> dishes = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dishes.add(mapResultSetToDish(rs));
                }
            }
        }
        
        return dishes;
    }
    
    // ==================== CREATE ====================
    
    /**
     * 料理を新規登録
     */
    public void insert(Dish dish) throws SQLException {
        String sql = "INSERT INTO DISH " +
                     "(DISH_ID, DISH_NAME, DISH_PRICE, DISH_CATEGORY, " +
                     "DISH_PHOTO, DISH_AVAILABLE) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dish.getDishId());
            pstmt.setString(2, dish.getName());
            pstmt.setInt(3, dish.getPrice());
            pstmt.setString(4, dish.getCategory());
            pstmt.setString(5, dish.getPhoto());
            pstmt.setInt(6, dish.isAvailable() ? 1 : 0);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 次のDISH_IDを生成
     */
    public String generateNextId() throws SQLException {
        String sql = "SELECT MAX(DISH_ID) as max_id FROM DISH";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                
                if (maxId == null) {
                    return "DIS001";
                }
                
                // DIS001 → 001 → 1 → 2 → 002 → DIS002
                int num = Integer.parseInt(maxId.substring(3));
                num++;
                
                return String.format("DIS%03d", num);
            }
        }
        
        return "DIS001";
    }
    
    // ==================== UPDATE ====================
    
    /**
     * 料理情報を更新
     */
    public void update(Dish dish) throws SQLException {
        String sql = "UPDATE DISH SET " +
                     "DISH_NAME = ?, " +
                     "DISH_PRICE = ?, " +
                     "DISH_CATEGORY = ?, " +
                     "DISH_PHOTO = ?, " +
                     "DISH_AVAILABLE = ? " +
                     "WHERE DISH_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dish.getName());
            pstmt.setInt(2, dish.getPrice());
            pstmt.setString(3, dish.getCategory());
            pstmt.setString(4, dish.getPhoto());
            pstmt.setInt(5, dish.isAvailable() ? 1 : 0);
            pstmt.setString(6, dish.getDishId());
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 料理の利用可否を切り替え
     */
    public void toggleAvailability(String dishId) throws SQLException {
        String sql = "UPDATE DISH SET " +
                     "DISH_AVAILABLE = 1 - DISH_AVAILABLE " +
                     "WHERE DISH_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dishId);
            pstmt.executeUpdate();
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * 論理削除（推奨）
     */
    public void softDelete(String dishId) throws SQLException {
        String sql = "UPDATE DISH SET DISH_AVAILABLE = 0 WHERE DISH_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dishId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 物理削除（非推奨）
     */
    public void hardDelete(String dishId) throws SQLException {
        String sql = "DELETE FROM DISH WHERE DISH_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dishId);
            pstmt.executeUpdate();
        }
    }
    
    // ==================== ヘルパーメソッド ====================
    
    /**
     * SQLクエリを実行してDishリストを取得
     */
    private List<Dish> executeQuery(String sql) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                dishes.add(mapResultSetToDish(rs));
            }
        }
        
        return dishes;
    }
    
    /**
     * ResultSetからDishオブジェクトを生成
     */
    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setDishId(rs.getString("DISH_ID"));
        dish.setName(rs.getString("DISH_NAME"));
        dish.setPrice(rs.getInt("DISH_PRICE"));
        dish.setCategory(rs.getString("DISH_CATEGORY"));
        dish.setPhoto(rs.getString("DISH_PHOTO"));
        dish.setAvailable(rs.getInt("DISH_AVAILABLE") == 1);
        
        return dish;
    }
}
