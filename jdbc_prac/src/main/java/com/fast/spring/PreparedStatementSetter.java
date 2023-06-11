package com.fast.spring;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementSetter {
	public void setter (PreparedStatement pstm) throws SQLException;
}
