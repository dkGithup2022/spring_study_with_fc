package com.fast.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

	public void create(User user) throws SQLException {
		Connection con = null;
		PreparedStatement psmt = null;
		try {
			con = getConnection();
			String sql = "insert into users values(?,?,?,?)";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, user.getUserId());
			psmt.setString(2, user.getPassword());
			psmt.setString(3, user.getName());
			psmt.setString(4, user.getEmail());

			psmt.executeUpdate();
		} finally {

			if (con != null)
				con.close();

			if (psmt != null)
				psmt.close();
		}

	}

	public User findById(String userId) throws SQLException {
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			String sql = "select * from users where userId = ?";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, userId);

			rs = psmt.executeQuery();
			User user = null;

			if (rs.next())
				user = new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
					rs.getString("email"));

			return user;
		} finally {
			if (rs != null)
				rs.close();

			if (con != null)
				con.close();

			if (psmt != null)
				psmt.close();
		}

	}

	private Connection getConnection() {
		String url = "jdbc:h2:mem://localhost/~/jdbc-practice;MODE=MySQL;DB_CLOSE_DELAY=-1";
		String id = "sa";
		String pw = "";

		try {
			Class.forName("org.h2.Driver");
			return DriverManager.getConnection(url, id, pw);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
