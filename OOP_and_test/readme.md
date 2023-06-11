
## 테스트와 객체 지향

### 테스트 목적

- 원활한 리팩토링 
  - 기존 기능이 정상동작한다는 심리적 안정감 제공
- document 의 역할 
    - 우리는 어디까지 신경쓰고 경계값은 어디인지 볼 수 있음. 
  
### 테스트와 인터페이스

- 인터페이스의 사용은 테스트를 용이하게 만듬 .
- 다른 말로, 낮은 결합도의 코드는 테스트를 용이하게 만듬 

아래의 코드는 구현부가 높은 결합도를 가지고 있음.

```java
	public void validate2 (){
	        PasswordGenerator  pg = new PasswordGenrator();
		String password = pg.generatePassword();

		if (password.length() < 8 || password.length() > 16)
			throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);

	}
```

VALIDATE2의 함수를 테스트하기 위해선 PasswordGenerator 의 생성, 기능에 의존함

이것은 두가지 문제의 원인임.
1. 테스트하기 힘들다. 
2. 기능 변경 시, 원본 코드( validate2 의 클래스)를 건드려야 한다

따라서 좋은 설계라고 볼 수 없다. 인터페이스를 변경이 있을 수 있는 부분에 둠으로써 낮은 결합과 용이한 테스트를 가진 코드를 짤 수 있다.



</br>

1. 구현부 
```java
	public void validate2 (PasswordGeneratePolicy pp){
		String password = pp.generatePassword();

		if (password.length() < 8 || password.length() > 16)
			throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);

	}

```

2. 인터페이스 
```java
public interface PasswordGeneratePolicy {
	String generatePassword();
}

```

##### 테스트 예시 

```java
	@Test
	public void 비밀번호_생성_과_검증_default_policy() {
		assertThatCode(() -> passwordValidator.validate2(new RandomPasswordGenerator()))
			.doesNotThrowAnyException();
	}

	@Test
	public void 비밀번호_생성과_검증_짧은_비밀번호() {
		assertThatCode(() -> passwordValidator.validate2(() -> {
			return "a";
		})).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 비밀번호_생성과_검증_긴_비밀번호() {
		assertThatCode(() -> passwordValidator.validate2(() -> {
			return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		})).isInstanceOf(IllegalArgumentException.class);
	}

```
