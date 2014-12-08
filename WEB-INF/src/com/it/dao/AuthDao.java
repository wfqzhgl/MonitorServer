package com.it.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.it.vo.AuthVO;

@Repository
public class AuthDao extends BaseDao {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public AuthVO login(String username, String passwd) {
		username = username.replaceAll("\\s+", "");
		passwd = passwd.replaceAll("\\s+", "");
		String sql = "select username,passwd,isadmin from users where username= ? and passwd = ?";
		final Map<String, Integer> mapData = new HashMap<String, Integer>();
		Object[] params = new Object[2];
		params[0] = username;
		params[1] = passwd;
		jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				mapData.put(rs.getString("username"), rs.getInt("isadmin"));
				return null;
			}
		});
		if (mapData.isEmpty()) {
			return new AuthVO(1, "用户名或密码错误.", 0);
		}
		return new AuthVO(0, "登录成功.", mapData.get(username));
	}

	public AuthVO addUser(String username, String passwd1, String passwd2,
			String admin) {
		// TODO Auto-generated method stub
		username = username.replaceAll("\\s+", "");
		passwd1 = passwd1.replaceAll("\\s+", "");
		passwd2 = passwd2.replaceAll("\\s+", "");
		if (passwd1.isEmpty() || !passwd1.equalsIgnoreCase(passwd2)) {
			return new AuthVO(1, "密码为空或两次输入密码不相等.", 0);
		}
		String sql = "insert into users values(null,'%s','%s',%s,now())";
		try {
			jdbcTemplate.execute(String.format(sql, username, passwd1, admin));
			return new AuthVO(0, "添加成功.", 0);
		}catch (DuplicateKeyException e){
//			e.printStackTrace();
			return new AuthVO(1, "添加失败: 用户名已存在." , 0);
		} catch (Exception e) {
			e.printStackTrace();
			return new AuthVO(1, "添加失败:" + e.getMessage(), 0);
		}

	}

	public AuthVO changePasswd(String username, String passwd, String passwd1,
			String passwd2) {
		username = username.replaceAll("\\s+", "");
		passwd = passwd.replaceAll("\\s+", "");
		passwd1 = passwd1.replaceAll("\\s+", "");
		passwd2 = passwd2.replaceAll("\\s+", "");
		if (passwd1.isEmpty() || !passwd1.equalsIgnoreCase(passwd2)) {
			return new AuthVO(1, "密码为空或两次输入密码不相等.", 0);
		}
		String sql = "update users set passwd=? where username=? and passwd=?";
		int res = jdbcTemplate.update(sql, passwd1, username, passwd);
		if(res==1){
			return new AuthVO(0, "密码修改成功.", 0);
		}
		return new AuthVO(1, "密码修改失败.", 0);
	}

}
