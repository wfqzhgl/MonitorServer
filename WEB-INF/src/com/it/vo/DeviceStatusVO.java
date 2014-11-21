package com.it.vo;

public class DeviceStatusVO {
	private String deviceID;
	private String ip;
	private String cpu;//单位 :%
	private String amem;//内存总量MB
	private String umem;//已用内存
	private String adisk;//磁盘总量MB
	private String fdisk;//剩余磁盘
	private String aflow;//输入的业务流 KB
	private String wflow;//处理的业务流KB
	private String time;//时间戳
	
	
	
	public DeviceStatusVO(String deviceID, String ip, String cpu, String amem,
			String umem, String adisk, String fdisk, String aflow,
			String wflow, String time) {
		super();
		this.deviceID = deviceID;
		this.ip = ip;
		this.cpu = cpu;
		this.amem = amem;
		this.umem = umem;
		this.adisk = adisk;
		this.fdisk = fdisk;
		this.aflow = aflow;
		this.wflow = wflow;
		this.time = time;
	}



	public DeviceStatusVO() {
		// TODO Auto-generated constructor stub
	}



	public String getDeviceID() {
		return deviceID;
	}



	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}



	public String getIp() {
		return ip;
	}



	public void setIp(String ip) {
		this.ip = ip;
	}



	public String getCpu() {
		return cpu;
	}



	public void setCpu(String cpu) {
		this.cpu = cpu;
	}



	public String getAmem() {
		return amem;
	}



	public void setAmem(String amem) {
		this.amem = amem;
	}



	public String getUmem() {
		return umem;
	}



	public void setUmem(String umem) {
		this.umem = umem;
	}



	public String getAdisk() {
		return adisk;
	}



	public void setAdisk(String adisk) {
		this.adisk = adisk;
	}



	public String getFdisk() {
		return fdisk;
	}



	public void setFdisk(String fdisk) {
		this.fdisk = fdisk;
	}



	public String getAflow() {
		return aflow;
	}



	public void setAflow(String aflow) {
		this.aflow = aflow;
	}



	public String getWflow() {
		return wflow;
	}



	public void setWflow(String wflow) {
		this.wflow = wflow;
	}



	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
