package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Visit;

/**
 * VisitDAO（来店記録 - データアクセス）
 * Visit情報のCRUD操作
 */
public class VisitDAO {
    
    private static final String URL = "jdbc:mysql://10.64.144.5:3306/24jy0228";
    private static final String USER = "24jy0228";
    private static final String PASSWORD = "24jy0228";
    
    // staticイニシャライザでドライバをロード
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * DB接続を取得
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // ==================== CREATE ====================
    
    /**
     * Visit保存
     * ★ 会計時のみ使用
     */
    public void save(Connection conn, Visit visit) throws SQLException {
        String sql = "INSERT INTO VISITS " +
                     "(VISIT_ID, TABLE_NUM, ARRIVAL_TIME, " +
                     "TOTAL_AMOUNT, PAYMENT_TIME) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, visit.getVisitId());
            pstmt.setInt(2, visit.getTableNum());
            pstmt.setTimestamp(3, Timestamp.valueOf(visit.getArrivalTime()));
            pstmt.setInt(4, visit.getTotalAmount());
            
            if (visit.getPaymentTime() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(visit.getPaymentTime()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }
            
            pstmt.executeUpdate();
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Visit IDで取得
     */
    public Visit findById(String visitId) throws SQLException {
        String sql = "SELECT VISIT_ID, TABLE_NUM, ARRIVAL_TIME, " +
                     "TOTAL_AMOUNT, PAYMENT_TIME " +
                     "FROM VISITS " +
                     "WHERE VISIT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, visitId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVisit(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 日付別の来店記録を取得
     * ★ 注文履歴確認用
     */
    public List<Visit> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT VISIT_ID, TABLE_NUM, ARRIVAL_TIME, " +
                     "TOTAL_AMOUNT, PAYMENT_TIME " +
                     "FROM VISITS " +
                     "WHERE DATE(ARRIVAL_TIME) = ? " +
                     "ORDER BY ARRIVAL_TIME DESC";
        
        List<Visit> visits = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    visits.add(mapResultSetToVisit(rs));
                }
            }
        }
        
        return visits;
    }
    
    /**
     * 期間指定で来店記録を取得
     * ★ 売上分析用
     */
    public List<Visit> findByPeriod(LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        
        String sql = "SELECT VISIT_ID, TABLE_NUM, ARRIVAL_TIME, " +
                     "TOTAL_AMOUNT, PAYMENT_TIME " +
                     "FROM VISITS " +
                     "WHERE DATE(ARRIVAL_TIME) BETWEEN ? AND ? " +
                     "ORDER BY ARRIVAL_TIME DESC";
        
        List<Visit> visits = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    visits.add(mapResultSetToVisit(rs));
                }
            }
        }
        
        return visits;
    }
    
    /**
     * 全Visit取得
     */
    public List<Visit> findAll() throws SQLException {
        String sql = "SELECT VISIT_ID, TABLE_NUM, ARRIVAL_TIME, " +
                     "TOTAL_AMOUNT, PAYMENT_TIME " +
                     "FROM VISITS " +
                     "ORDER BY ARRIVAL_TIME DESC";
        
        List<Visit> visits = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                visits.add(mapResultSetToVisit(rs));
            }
        }
        
        return visits;
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Visit更新
     */
    public void update(Visit visit) throws SQLException {
        String sql = "UPDATE VISITS SET " +
                     "TABLE_NUM = ?, " +
                     "ARRIVAL_TIME = ?, " +
                     "TOTAL_AMOUNT = ?, " +
                     "PAYMENT_TIME = ? " +
                     "WHERE VISIT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, visit.getTableNum());
            pstmt.setTimestamp(2, Timestamp.valueOf(visit.getArrivalTime()));
            pstmt.setInt(3, visit.getTotalAmount());
            
            if (visit.getPaymentTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(visit.getPaymentTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            pstmt.setString(5, visit.getVisitId());
            
            pstmt.executeUpdate();
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * Visit削除
     */
    public void delete(String visitId) throws SQLException {
        String sql = "DELETE FROM VISITS WHERE VISIT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, visitId);
            pstmt.executeUpdate();
        }
    }
    
    // ==================== ヘルパーメソッド ====================
    
    /**
     * ResultSetからVisitオブジェクトを生成
     */
    private Visit mapResultSetToVisit(ResultSet rs) throws SQLException {
        Visit visit = new Visit();
        visit.setVisitId(rs.getString("VISIT_ID"));
        visit.setTableNum(rs.getInt("TABLE_NUM"));
        visit.setArrivalTime(rs.getTimestamp("ARRIVAL_TIME").toLocalDateTime());
        visit.setTotalAmount(rs.getInt("TOTAL_AMOUNT"));
        
        Timestamp paymentTime = rs.getTimestamp("PAYMENT_TIME");
        if (paymentTime != null) {
            visit.setPaymentTime(paymentTime.toLocalDateTime());
        }
        
        return visit;
    }
}