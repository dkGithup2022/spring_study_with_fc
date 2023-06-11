package com.fast.spring;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {
	public static DataSource getDataSource(){
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName("org.h2.Driver");
		hikariDataSource.setJdbcUrl("jdbc:h2:mem://localhost/~/jdbc-practice;MODE=MySQL;DB_CLOSE_DELAY=-1");
		hikariDataSource.setUsername("sa");
		hikariDataSource.setPassword("");
		return hikariDataSource;
	}
}
