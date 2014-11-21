package com.it.vo;

public class TypeAttackVO {
	public TypeAttackVO() {
		super();
	}



	private String ip;
	private String address;
	private int  count;
	
	
	
	public TypeAttackVO(String ip, String address, int count) {
		super();
		this.ip= ip;
		this.address = address;
		this.count = count;
	}



	public String getIp() {
		return ip;
	}



	public void setIp(String ip) {
		this.ip = ip;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}
	
	
	
	
}
