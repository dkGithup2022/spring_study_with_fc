package com.spring.fast;

public class PasswordValidator {

	private final String INVALID_PASSWORD_MESSAGE = "비밀번호는 8자 이상, 16자 이하";

	public  void validate(String password)  {
		if (password.length() < 8 || password.length() > 16)
			throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
	}

	public void validate2 (PasswordGeneratePolicy pp){
		String password = pp.generatePassword();

		if (password.length() < 8 || password.length() > 16)
			throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);

	}
}
