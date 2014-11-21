package com.it.vo;

public class TypeGlobalVO {
	
	private String code;
	private String name;
	private String logstr;
	private String level;
	
	private  String src_ip;
	private  String src_port;
	//源地域名称
	private  String src_name;
	
	private  String dst_ip;
	private  String dst_port;
	//目标地域
	private  String dst_name;
	
	//处理状态data_bin_des为空未处理
	private String status;
	private String deviceName;
	private String date;
	private  int  count;
	
	
	

	public TypeGlobalVO() {
		super();
	}





	public TypeGlobalVO(String code, String name, String logstr, String level,
			String src_ip, String src_port, String src_name, String dst_ip,
			String dst_port, String dst_name, String status, String date,
			int count) {
		super();
		this.code = code;
		this.name = name;
		this.logstr = logstr;
		this.level = level;
		this.src_ip = src_ip;
		this.src_port = src_port;
		this.src_name = src_name;
		this.dst_ip = dst_ip;
		this.dst_port = dst_port;
		this.dst_name = dst_name;
		this.status = status;
		this.date = date;
		this.count = count;
	}









	public String getDate() {
		return date;
	}




	public void setDate(String date) {
		this.date = date;
	}




	public String getDeviceName() {
		return deviceName;
	}





	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
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



	public String getLogstr() {
		return logstr;
	}





	public void setLogstr(String logstr) {
		this.logstr = logstr;
	}





	public String getLevel() {
		return level;
	}




	public void setLevel(String level) {
		this.level = level;
	}




	public String getSrc_ip() {
		return src_ip;
	}




	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}




	public String getSrc_port() {
		return src_port;
	}




	public void setSrc_port(String src_port) {
		this.src_port = src_port;
	}




	public String getSrc_name() {
		return src_name;
	}




	public void setSrc_name(String src_name) {
		this.src_name = src_name;
	}




	public String getDst_ip() {
		return dst_ip;
	}




	public void setDst_ip(String dst_ip) {
		this.dst_ip = dst_ip;
	}




	public String getDst_port() {
		return dst_port;
	}




	public void setDst_port(String dst_port) {
		this.dst_port = dst_port;
	}




	public String getDst_name() {
		return dst_name;
	}




	public void setDst_name(String dst_name) {
		this.dst_name = dst_name;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public int getCount() {
		return count;
	}




	public void setCount(int count) {
		this.count = count;
	}



}
