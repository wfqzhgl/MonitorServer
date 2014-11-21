package com.it.vo;

public class City {
	private int id;
	private String name;
	private String code;
	private Province province;
	
	
	public City(int id, String name, String code, Province province) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.province = province;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	
	
}
