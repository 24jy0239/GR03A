package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.OrderItem;

public class OrderItemDAO {
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

	public OrderItem findById(int itemId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		OrderItem orderItem = new OrderItem();
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
		return orderItem;
	}
	
	public ArrayList<OrderItem> findByOrderId(int orderId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<OrderItem> list = new ArrayList<>();
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
		return list;
	}
	
	public ArrayList<OrderItem> findAll() throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<OrderItem> list = new ArrayList<>();
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
		return list;
	}
	
	public void insert(OrderItem orderItem) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
	}
	
	public void update(OrderItem orderItem) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
	}
	
	public void delete(int itemId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
	}
	
	public void updateStatus(int itemId, int status) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		/*try {
			if (con != null) {
				String sql = "SELECT CUSTID, CUSTNAME, KANA, TEL, ADDRESS " +
						"FROM customer WHERE TEL = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, tel);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("CUSTID");
					String finname = rs.getString("CUSTNAME");
					String finkana = rs.getString("KANA");
					String fintel = rs.getString("TEL");
					String finadd = rs.getString("ADDRESS");
		
					Customer cust = new Customer(finid, finname, finkana, fintel, finadd);
					list.add(cust);
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
		}*/
		closeConnection(con);
	}
}
