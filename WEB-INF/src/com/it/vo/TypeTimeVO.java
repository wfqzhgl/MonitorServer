package com.it.vo;

import java.util.List;

public class TypeTimeVO {
	private String type;
	private String code;
	private List<String> x;
	private List<Long> y;
	
	
	
	public TypeTimeVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TypeTimeVO(String type, String code, List<String> x, List<Long> y) {
		super();
		this.type = type;
		this.code = code;
		this.x = x;
		this.y = y;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<String> getX() {
		return x;
	}
	public void setX(List<String> xlist) {
		this.x = xlist;
	}
	public List<Long> getY() {
		return y;
	}
	public void setY(List<Long> ylist) {
		this.y = ylist;
	}
	
	

}
