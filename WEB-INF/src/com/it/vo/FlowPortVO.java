package com.it.vo;


public class FlowPortVO {
	private String code;
	//类型名称
	private String name;
	
	
	
	public FlowPortVO() {
		super();
		// TODO Auto-generated constructor stub
	}



	public FlowPortVO(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



}
