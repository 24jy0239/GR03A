package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import model.Order;
import model.Visit;

public class OrderDAO {

	private Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://10.64.144.5:3306/24jy0228",
					"24jy0228",
					"24jy0228");

		} catch (Exception e) {
			System.out.println("DBアクセス時にエラーが発生しました。");
			e.printStackTrace();
			throw new Exception("DB接続処理に失敗しました！管理者に連絡してください。");
		}
		return con;
	}

	private void closeConnection(Connection con) throws Exception {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("DB切断時にエラーが発生しました。");
			e.printStackTrace();
			throw new Exception("DB接続処理に失敗しました！管理者に連絡してください。");
		}
	}

	public ArrayList<Order> findAll() throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Order> list = new ArrayList<Order>();
		try {
			if (con != null) {
			    String sql = "SELECT ID, TABLE_ID, PAYMENT_TIME, TOTAL_AMOUNT FROM ORDER_HISTORY";
			    pstmt = con.prepareStatement(sql);
			    rs = pstmt.executeQuery();

			    while (rs.next()) {
			        String orderId = rs.getString("ID");        // CHAR(12)
			        String tableId = rs.getString("TABLE_ID");  // CHAR(3)
			        Timestamp paymentTime = rs.getTimestamp("PAYMENT_TIME");
			        int totalAmount = rs.getInt("TOTAL_AMOUNT");

//			        // 根据你的实体类来改构造函数
//			        Visit visit = new Visit(
//			                orderId,
//			                tableId,
//			                paymentTime,
//			                totalAmount
//			        );
//
//			        list.add(order);
			    }
			}

		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（商品検索）。");
			e.printStackTrace();
			throw new Exception("顧客情報検索処理に失敗しました！管理者に連絡してください。");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				System.out.println("DB切断時にエラーが発生しました。");
				e.printStackTrace();
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				System.out.println("DB切断時にエラーが発生しました。");
				e.printStackTrace();
			}
		}
		closeConnection(con);
		return list;
	}

	public ArrayList<Visit> findById(int ordeId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Visit> list = new ArrayList<Visit>();
//		
		closeConnection(con);
		return list;
	}

	public ArrayList<Visit> findByVisitId(int visitid) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Visit> list = new ArrayList<Visit>();
//		
		closeConnection(con);
		return list;
	}
	
	public void insert(Order order) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Visit> list = new ArrayList<Visit>();
//		try {
//			if (con != null) {
//				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
//						"FROM Visit WHERE TEL = ?;";
//				pstmt = con.prepareStatement(sql);
//				pstmt.setString(1, tel);
//				rs = pstmt.executeQuery();
//				while (rs.next() == true) {
//					int finid = rs.getInt("CUSTID");
//					String finname = rs.getString("CUSTNAME");
//					String finkana = rs.getString("KANA");
//					String fintel = rs.getString("TEL");
//					String finadd = rs.getString("ADDRESS");
//
//					Visit cust = new Visit(finid, finname, finkana, fintel, finadd);
//					list.add(cust);
//				}
//			}
//		} catch (SQLException e) {
//			System.out.println(
//					"DB切断時にエラーが発生しました（商品検索）。");
//			e.printStackTrace();
//			throw new Exception("顧客情報検索処理に失敗しました！管理者に連絡してください。");
//		} finally {
//			try {
//				if (rs != null) {
//					rs.close();
//				}
//			} catch (SQLException e) {
//				System.out.println("DB切断時にエラーが発生しました。");
//				e.printStackTrace();
//			}
//			try {
//				if (pstmt != null) {
//					pstmt.close();
//				}
//			} catch (SQLException e) {
//				System.out.println("DB切断時にエラーが発生しました。");
//				e.printStackTrace();
//			}
//		}
		closeConnection(con);
		
	}
	
	public void update(Order order) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Visit> list = new ArrayList<Visit>();
//		
		closeConnection(con);
		
	}
	
	public void delecte(int orderid) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Visit> list = new ArrayList<Visit>();
//		
		closeConnection(con);
		
	}
	
}
