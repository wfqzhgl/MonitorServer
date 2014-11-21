package com.it.vo;

import java.io.Serializable;

public class DeviceVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7850121747437045573L;
	private String id;
	private String name;
	private String guid;
	private String ip;
	private String pid;
	private String detail;
	
	public DeviceVO(){
		
	}
	
	public DeviceVO(String id, String ip) {
		super();
		this.id = id;
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
