## 강의 정리

### 개요

강의가 다루는 것
1. jdbc 를 이용한 기본적인 db 접근에 대해 다룹니다.
2. spring 에 의존적이지 않은 방식의 dao 클래스를 구현합니다
3. (2) 에서 작성한 내용을 리팩토링 합니다.
   - dao query 수행의 공통 부분을  connection manager, jdbc template 로 나눔
   - jdbc template 수행 중 type 별로 달라 지는 부분은 interface + 람다 의 조합으로 보기 좋게 관리 하는 예시 작성


</br> 

###  리팩토링 이전의 코드  - 기능만 보기 

##### 수행 확인을 위한 테스트 코드 생성 

테스트 코드 & sql 파일 

- DAO TEST 코드 
```agsl
public class UserDaoTest {
	@BeforeEach
	void setup() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("db_schema.sql"));
		DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
	}

	@Test
	void createTest() throws SQLException {
		UserDao userDao = new UserDao();

		userDao.create(new User("wizard", "pw", "name", "email"));

		User user = userDao.findById("wizard");
		assertThat(user).isEqualTo(new User("wizard", "pw", "name", "email"));
	}
}
```

</br>

- initialize sql 
```
drop table if exists users;


create table users (
    userId      varchar(12)     not null,
    password    varbinary(64)   not null,
    name        varchar(20)     not null,
    email       varchar(255),

    primary key (userId)
);
```

</br>

#### 기능만 수행하는 dao 코드


```java

public class UserDao {

    public Connection getConnection() {
        String url = "jdbc:h2:mem://localhost/~/jdbc-practice;MODE=MySQL;DB_CLOSE_DELAY=-1";
        String id = "sa";
        String pw = "";

        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(url, id, pw);
        } catch (Exception ex) {
            return null;
        }
    }

    public void create(User user) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }

            if (con != null) {
                con.close();
            }
        }
    }

    public User findByUserId(String userId) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT userId, password, name, email FROM USERS WHERE userid=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            User user = null;
            if (rs.next()) {
                user = new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
                        rs.getString("email"));
            }

            return user;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
}

```

##### 보면 알 수 있는 것

1. db 접근은 아래와 같은 순서가 필요하다
   1. 커낵션을 받아옴
    - 이 과정에서 위의 예시는 basic auth 기반의 인증을 거침
   2. query string 생성
   3. 파라미터 설정 
   4. sql 수행
   5. result set 결과를 리턴 값에 매핑
   6. 자원의 반납

    

##### 안좋은 점

1. get connection이 해당 dao 에 작성될 이유가 없음
   1. 코드 길어져서 가독성 해침
   2. auth 정보가 모든 dao 에 들어가게 됨
   3. pool 관리를 위해선 싱글턴으로 선언된 매니저 클래스가 있어야함
2. query 수행에 공통되는 부분이 많음 



</br>


### 리팩토링 내용 

위의 안좋은 점 때문에, 아래의 방식으로 리팩토링 한다.

1. 커넥션은 싱글턴으로 관리함. 그래서 커넥션 관련 로직을 한군데로 모으고 pool 설정도 할 수 있다.


```java


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

```


</br>

2. 모든 dao 에서 공통으로 쓰일 jdbc 함수 로직을 한군데로 모은다. 


```java

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


```


3. 타입별로 달라지는 연산부는 interface 로 선언한 뒤, dao 구현체에서 람다로 정의하면 보기 좋고 짧은 코드를 유지할 수 있다.

```java
public interface PreparedStatementSetter {
	public void setter (PreparedStatement pstm) throws SQLException;
}

```

```java
public interface RowMapper {
	public Object map(ResultSet rs) throws SQLException;
}

```

- 아래는  dao 함수 구현부

```java
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
```

</br>

4. 코드 수정을 마쳤으면 test 코드를 실행해서 이전의 요구 사항을 충족하는지 확인한다. 