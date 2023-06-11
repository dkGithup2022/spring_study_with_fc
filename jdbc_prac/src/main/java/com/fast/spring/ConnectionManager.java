package com.fast.spring;

import java.sql.Connection;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {

	private static final String DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem://localhost/~/jdbc-practice;MODE=MySQL;DB_CLOSE_DELAY=-1";
	private static final String USER_NAME = "sa";
	private static final String PASSWORD = "";

	private static final Integer MAX_CON_SIZE = 40;
	private static final Integer MIN_CON_SIZE = 10;
	private static DataSource ds;

	static {
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName(DRIVER);
		hikariDataSource.setJdbcUrl(DB_URL);
		hikariDataSource.setUsername(USER_NAME);
		hikariDataSource.setPassword(PASSWORD);
		hikariDataSource.setMaximumPoolSize(MAX_CON_SIZE);
		hikariDataSource.setMinimumIdle(MIN_CON_SIZE);
		ds = hikariDataSource;
	}

	public static DataSource getDataSource() {
		return ds;
	}

	public static Connection getConnection() {

		try {
			return ds.getConnection();
		} catch (Exception e) {
			throw new RuntimeException("CAN NOT GET CONNECTION");
		}
	}
}
