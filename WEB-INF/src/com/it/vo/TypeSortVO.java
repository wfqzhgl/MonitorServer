package com.it.vo;

public class TypeSortVO {
	private String code;
	private String type;
	private int  count;
	
	
	
	public TypeSortVO(String code, String type, int count) {
		super();
		this.code= code;
		this.type = type;
		this.count = count;
	}
	
	
	public TypeSortVO() {
		// TODO Auto-generated constructor stub
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
