package com.it.service;

import org.springframework.stereotype.Service;

import com.it.dao.AuthDao;
import com.it.vo.AuthVO;

@Service("AuthService")
public class AuthService {

	private static final AuthService instance = new AuthService();

	public static AuthService getInstance() {
		return instance;
	}

	private AuthDao authDao;

	public AuthDao getAuthDao() {
		return authDao;
	}

	public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public AuthVO serve(String model, String username, String passwd,
			String passwd1, String passwd2, String admin) {
		if (model.equalsIgnoreCase("login")) {
			return authDao.login(username, passwd);

		} else if (model.equalsIgnoreCase("new")) {
			return authDao.addUser(username, passwd1, passwd2, admin);

		} else if (model.equalsIgnoreCase("change")) {
			return authDao.changePasswd(username, passwd, passwd1, passwd2);
		}
		return null;
	}

}
