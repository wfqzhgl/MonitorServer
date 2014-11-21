package com.it.vo;

public class Province {
	private int id;
	private String name;
	private String code;
	private Country country;
	
	
	
	public Province(int id, String name, String code, Country country) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.country = country;
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
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	
	
	
}
