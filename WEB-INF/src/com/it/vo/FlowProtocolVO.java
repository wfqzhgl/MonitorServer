package com.it.vo;


public class FlowProtocolVO {
	private String code;
	//类型名称
	private String name;
	
	
	
	public FlowProtocolVO() {
		super();
		// TODO Auto-generated constructor stub
	}



	public FlowProtocolVO(String code, String name) {
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
