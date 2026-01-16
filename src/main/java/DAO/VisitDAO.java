package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Visit;



public class VisitDAO {

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

		public ArrayList<Visit> findAll() throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ArrayList<Visit> list = new ArrayList<Visit>();
			try {
				if (con != null) {
					String sql = "SELECT ID, TABLE_ID, VISIT_TIME, TOTAL_AMOUNT FROM VISIT";
					pstmt = con.prepareStatement(sql);
					rs = pstmt.executeQuery();
					while (rs.next()) {

					    int visitId = Integer.parseInt(rs.getString("ID"));
					    int tableId = Integer.parseInt(rs.getString("TABLE_ID"));
					    LocalDateTime paymentTime =rs.getTimestamp("PAYMENT_TIME").toLocalDateTime();
					    int totalAmount = rs.getInt("TOTAL_AMOUNT");

					    Visit cust = new Visit(visitId,tableId,totalAmount,paymentTime);
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
			}
			closeConnection(con);
			return list;
		}

		public ArrayList<Visit> findById(int visitId) throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ArrayList<Visit> list = new ArrayList<Visit>();
			
			 try {
			       if (con != null) {
			            String sql = "SELECT ID, TABLE_ID, PAYMENT_TIME, TOTAL_AMOUNT "
			                       + "FROM VISIT WHERE ID = ?";
			            pstmt = con.prepareStatement(sql);
			            pstmt.setString(1, String.valueOf(visitId));
			            rs = pstmt.executeQuery();

			            while (rs.next()) {
			                int vId = Integer.parseInt(rs.getString("ID"));
			                int tableId = Integer.parseInt(rs.getString("TABLE_ID"));
			                LocalDateTime paymentTime =rs.getTimestamp("PAYMENT_TIME").toLocalDateTime();
			                int totalAmount = rs.getInt("TOTAL_AMOUNT");

			                Visit cust = new Visit(vId, tableId, totalAmount, paymentTime);
			                list.add(cust);
			            }
			        }
			    }catch (SQLException e) {
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

		public ArrayList<Visit> findByTableId(int tableId) throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ArrayList<Visit> list = new ArrayList<Visit>();
			try {

			    if (con != null) {
			        String sql = "SELECT ID, TABLE_ID, PAYMENT_TIME, TOTAL_AMOUNT "
			                   + "FROM VISIT WHERE TABLE_ID = ?";
			        pstmt = con.prepareStatement(sql);
			        pstmt.setString(1, String.valueOf(tableId));

			        rs = pstmt.executeQuery();

			        while (rs.next()) {
			            int visitId = Integer.parseInt(rs.getString("ID"));
			            int tId = Integer.parseInt(rs.getString("TABLE_ID"));
			            LocalDateTime paymentTime =rs.getTimestamp("PAYMENT_TIME").toLocalDateTime();
			            int totalAmount = rs.getInt("TOTAL_AMOUNT");

			            Visit cust = new Visit(visitId, tId, totalAmount, paymentTime);
			            list.add(cust);
			        }
			    }
			}catch (SQLException e) {
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
		
		public void insert(Visit visit) throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				 if (con != null) {
				        String sql = "INSERT INTO VISIT (ID, TABLE_ID, PAYMENT_TIME, TOTAL_AMOUNT) "
				                   + "VALUES (?, ?, ?, ?)";
				        pstmt = con.prepareStatement(sql);

				        pstmt.setString(1, String.valueOf(visit.getVisitId()));
				        pstmt.setString(2, String.valueOf(visit.getTableId()));

				        // LocalDateTime → Timestamp
				        pstmt.setTimestamp(3, Timestamp.valueOf(visit.getVisitTime()));

				        pstmt.setInt(4, visit.getTotalAmount());

				        pstmt.executeUpdate();
				    }
		}catch (SQLException e) {
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
			
		}
		
		public void update(Visit visit) throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				 if (con != null) {
				        String sql = "UPDATE VISIT "
				                   + "SET TABLE_ID = ?, PAYMENT_TIME = ?, TOTAL_AMOUNT = ? "
				                   + "WHERE ID = ?";
				        pstmt = con.prepareStatement(sql);

				        pstmt.setString(1, String.valueOf(visit.getTableId()));
				        pstmt.setTimestamp(2, Timestamp.valueOf(visit.getVisitTime()));
				        pstmt.setInt(3, visit.getTotalAmount());

				        pstmt.setString(4, String.valueOf(visit.getVisitId()));

				        pstmt.executeUpdate();
				    }
		}catch (SQLException e) {
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
			
		}
		
		public void delecte(int visitid) throws Exception {
			Connection con = createConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				 if (con != null) {
				        String sql = "DELETE FROM VISIT WHERE ID = ?";
				        pstmt = con.prepareStatement(sql);
				        pstmt.setString(1, String.valueOf(visitid));
				        pstmt.executeUpdate();
				    }
		}catch (SQLException e) {
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
		}
		
	}


