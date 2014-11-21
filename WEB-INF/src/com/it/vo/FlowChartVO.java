package com.it.vo;

import java.util.List;

public class FlowChartVO {
	private String code;
	//类型名称
	private String name;
	//横坐标
	private List<String> x;
	//纵坐标-流量
	private List<Long> y;
	
	
	
	public FlowChartVO() {
		super();
		// TODO Auto-generated constructor stub
	}



	public FlowChartVO(String code, String name, List<String> x, List<Long> y) {
		super();
		this.code = code;
		this.name = name;
		this.x = x;
		this.y = y;
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



	public List<String> getX() {
		return x;
	}



	public void setX(List<String> x) {
		this.x = x;
	}



	public List<Long> getY() {
		return y;
	}



	public void setY(List<Long> y) {
		this.y = y;
	}
	

}
