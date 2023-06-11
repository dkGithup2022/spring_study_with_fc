package com.fast.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper {
	public Object map(ResultSet rs) throws SQLException;
}
