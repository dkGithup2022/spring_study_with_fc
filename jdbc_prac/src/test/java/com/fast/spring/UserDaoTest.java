package com.fast.spring;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class UserDaoTest {
	@BeforeEach
	void setup() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("db_schema.sql"));
		DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
	}

	@Test
	public void empty() {

	}

	@Test
	void createTest() throws SQLException {
		UserDao userDao = new UserDao();

		userDao.create(new User("wizard", "pw", "name", "email"));

		User user = userDao.findById("wizard");
		assertThat(user).isEqualTo(new User("wizard", "pw", "name", "email"));

		User noUser = userDao.findById("noone");
		assertThat(noUser).isNull();
		;
	}
}
