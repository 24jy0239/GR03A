package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Dish;

public class DishDAO {

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

	public ArrayList<Dish> findAll() throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Dish> list = new ArrayList<>();
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "SELECT * " +
						"FROM dish;";
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("DISH_ID");
					String finname = rs.getString("DISH_NAME");
					int finprice = rs.getInt("DISH_PRICE");
					String fincategory = rs.getString("DISH_CATEGORY");
					String finphoto = rs.getString("DISH_PHOTO");
					boolean finavailable = rs.getInt("DISH_AVAILABLE") != 0;
					Dish dish = new Dish(finid, finname, finprice, fincategory, finphoto, finavailable);
					list.add(dish);
				}
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー検索）。");
			e.printStackTrace();
			throw new Exception("メニュー検索処理に失敗しました！管理者に連絡してください。");
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
	
	public Dish findById(int dishId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Dish dish = new Dish();
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "SELECT * " +
						"FROM dish WHERE DISH_ID = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, dishId);
				rs = pstmt.executeQuery();
				int finid = rs.getInt("DISH_ID");
				String finname = rs.getString("DISH_NAME");
				int finprice = rs.getInt("DISH_PRICE");
				String fincategory = rs.getString("DISH_CATEGORY");
				String finphoto = rs.getString("DISH_PHOTO");
				boolean finavailable = rs.getInt("DISH_AVAILABLE") != 0;
				dish.setDishId(finid);
				dish.setName(finname);;
				dish.setPrice(finprice);
				dish.setCategory(fincategory);
				dish.setPhoto(finphoto);
				dish.setAvailable(finavailable);
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー検索）。");
			e.printStackTrace();
			throw new Exception("メニュー検索処理に失敗しました！管理者に連絡してください。");
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
		return dish;
	}
	
	public ArrayList<Dish> findByCategory(String category) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Dish> list = new ArrayList<>();
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "SELECT * " +
						"FROM customer WHERE DISH_CATEGORY = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, category);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("DISH_ID");
					String finname = rs.getString("DISH_NAME");
					int finprice = rs.getInt("DISH_PRICE");
					String fincategory = rs.getString("DISH_CATEGORY");
					String finphoto = rs.getString("DISH_PHOTO");
					boolean finavailable = rs.getInt("DISH_AVAILABLE") != 0;
					Dish dish = new Dish(finid, finname, finprice, fincategory, finphoto, finavailable);
					list.add(dish);
				}
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー検索）。");
			e.printStackTrace();
			throw new Exception("メニュー検索処理に失敗しました！管理者に連絡してください。");
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
	
	public ArrayList<Dish> findAvailable() throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Dish> list = new ArrayList<>();
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "SELECT * " +
						"FROM customer WHERE DISHAVAILABLE = 1;";
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next() == true) {
					int finid = rs.getInt("DISH_ID");
					String finname = rs.getString("DISH_NAME");
					int finprice = rs.getInt("DISH_PRICE");
					String fincategory = rs.getString("DISH_CATEGORY");
					String finphoto = rs.getString("DISH_PHOTO");
					boolean finavailable = rs.getInt("DISH_AVAILABLE") != 0;
					Dish dish = new Dish(finid, finname, finprice, fincategory, finphoto, finavailable);
					list.add(dish);
				}
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー検索）。");
			e.printStackTrace();
			throw new Exception("メニュー検索処理に失敗しました！管理者に連絡してください。");
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
	
	public void insert(Dish dish) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "INSERT INTO dish (DISH_NAME, DISH_PRICE, DISH_CATEGORY, DISH_PHOTO " +
						"VALUES (?, ?, ?, ?);";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, dish.getName());
				pstmt.setInt(2, dish.getPrice());
				pstmt.setString(3, dish.getCategory());
				pstmt.setString(4, dish.getPhoto());
				rs = pstmt.executeQuery();
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー追加）。");
			e.printStackTrace();
			throw new Exception("メニュー追加処理に失敗しました！管理者に連絡してください。");
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
	
	public void update(Dish dish) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "UPDATE　dish " +
						"SET DISH_NAME = ? " + "SET DISH_PRICE = ? " + "SET DISH_CATEGORY = ? " + "SET DISH_PHOTO = ? " + 
						"WHERE DISH_ID = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, dish.getName());
				pstmt.setInt(2, dish.getPrice());
				pstmt.setString(3, dish.getCategory());
				pstmt.setString(4, dish.getPhoto());
				pstmt.setInt(5, dish.getDishId());
				rs = pstmt.executeQuery();
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー更新）。");
			e.printStackTrace();
			throw new Exception("メニュー更新処理に失敗しました！管理者に連絡してください。");
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
	
	public void delete(int dishId) throws Exception {
		Connection con = createConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//下のコメントアウトのところに書き直す
		try {
			if (con != null) {
				String sql = "DELETE FROM　dish " +
						"WHERE DISH_ID = ?;";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, dishId);
				rs = pstmt.executeQuery();
			}
		} catch (SQLException e) {
			System.out.println(
					"DB切断時にエラーが発生しました（メニュー削除）。");
			e.printStackTrace();
			throw new Exception("メニュー削除処理に失敗しました！管理者に連絡してください。");
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
