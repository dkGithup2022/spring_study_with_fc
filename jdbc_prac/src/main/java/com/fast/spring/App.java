package com.fast.spring;

public class App {
}

/* TODO
1. 커넥션 관리는 singleton Connection manager 로 위임
2. (1) 에서 hikaricp  를 사용하도록 설정
3. UserDao 에서 아래의 방법으로 코드 깔끔하게
	1. connection 획득의 로직은 connection manager 에서
	2. JdbcTemplate 로 공통적인 jdbc 수행 로직 옮김
	3. preparedState 의 인자가 그때그때 바뀜
		-> 인터페이스 정의 후, 각 구현부에서 람다로 받도록 수정
	4. read 요청에 대해 각 결과로 typecasting 할 수 있는 mapper 클래스 생성.
		이것도 인터페이스로 구현 후 람다로 바꾸도록 한다 .

 */