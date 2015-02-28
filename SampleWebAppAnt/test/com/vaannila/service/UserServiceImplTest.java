package com.vaannila.service;

import com.vaannila.domain.User;

public class UserServiceImplTest implements UserService {

	@Override
	public void add(User user) {
		//Persist the user object here. 
		System.out.println("User added successfully");

	}

}
