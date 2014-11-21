package com.it.vo;

import java.util.List;

public class AddressVO {
	private String id;
	private String code;
	private String name;
	private List<String> ips;
	
	
	public AddressVO(String id,String code, String name, List<String> ips) {
		super();
		this.id=id;
		this.code = code;
		this.name = name;
		this.ips = ips;
	}
	

	public AddressVO() {
		// TODO Auto-generated constructor stub
	}


	public String getCode() {
		return code;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
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
	public List<String> getIps() {
		return ips;
	}
	public void setIps(List<String> ips) {
		this.ips = ips;
	}
	
	
	

}
