package com.it.vo;

import net.sf.json.JSONObject;



public class AuthVO {
	private int code;
	private String msg;
	private int admin=0;

	
	
	public AuthVO(int code, String msg, int admin) {
		super();
		this.code = code;
		this.msg = msg;
		this.admin = admin;
	}


	public String toString() {

		JSONObject jo = JSONObject.fromObject(this);
		return jo.toString();

	}
	
	public int getCode() {
		return code;
	}



	public void setCode(int code) {
		this.code = code;
	}



	public String getMsg() {
		return msg;
	}



	public void setMsg(String msg) {
		this.msg = msg;
	}



	public int getAdmin() {
		return admin;
	}



	public void setAdmin(int admin) {
		this.admin = admin;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
