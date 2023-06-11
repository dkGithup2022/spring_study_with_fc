package com.fast.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcTemplate {

	public static void execute(String sql, PreparedStatementSetter pss) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = ConnectionManager.getConnection();
			pstm = con.prepareStatement(sql);
			pss.setter(pstm);

			pstm.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Object findById(String id, String sql, PreparedStatementSetter pss, RowMapper rom) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = ConnectionManager.getConnection();
			pstm = con.prepareStatement(sql);
			pss.setter(pstm);
			ResultSet rs = pstm.executeQuery();

			if (!rs.next())
				return null;

			return rom.map(rs);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
