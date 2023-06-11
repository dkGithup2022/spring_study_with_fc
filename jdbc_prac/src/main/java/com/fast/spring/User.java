package com.fast.spring;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
public class User {

	private String userId;
	private String name;
	private String password;
	private String email;

}
