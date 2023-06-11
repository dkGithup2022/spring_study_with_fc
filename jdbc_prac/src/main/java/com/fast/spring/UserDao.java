package com.fast.spring;

import java.sql.SQLException;

public class UserDao {

	public void create(User user) throws SQLException {

		String sql = "insert into users values(?,?,?,?)";
		JdbcTemplate.execute(sql, pstm -> {
				pstm.setString(1, user.getUserId());
				pstm.setString(2, user.getPassword());
				pstm.setString(3, user.getName());
				pstm.setString(4, user.getEmail());
			}
		);

	}

	public User findById(String userId) throws SQLException {
		String sql = "select * from users where userId = ?";
		return (User)JdbcTemplate.findById(userId, sql,
			pstm -> {
				pstm.setString(1, userId);
			}, rs -> new User(
				rs.getString("userId"),
				rs.getString("password"),
				rs.getString("name"),
				rs.getString("email")
			));
	}


}
