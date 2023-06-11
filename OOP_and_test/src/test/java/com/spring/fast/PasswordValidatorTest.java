package com.spring.fast;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PasswordValidatorTest {

	PasswordValidator passwordValidator = new PasswordValidator();

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




	@ParameterizedTest
	@ValueSource(strings = {"123456789", "aaaaaaaaaaaaaa"})
	public void 비밀번호_검증_성공(String pw) {
		assertThatCode(() -> passwordValidator.validate(pw))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(strings = {"12345678", "aaaaaaaaaaaaaaaa"})
	public void 비밀번호_검증_성공_경계값(String pw) {
		assertThatCode(() -> passwordValidator.validate(pw))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(strings = {"1234567", "aaaaaaaaaaaㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁaaaaa"})
	public void 비밀번호_검증_실패_크거나_작은_인풋(String pw) {
		assertThatCode(() -> passwordValidator.validate(pw))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("비밀번호는 8자 이상, 16자 이하");
	}

}
