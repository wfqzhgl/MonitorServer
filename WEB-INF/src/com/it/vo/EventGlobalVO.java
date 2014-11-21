package com.it.vo;


public class EventGlobalVO {
	private String date;
	private String name;
	private String type;
	private String level;
	private String logstr;
	private String src_ip;
	private String src_port;
	// 源地域名称
	private String src_name;
	// 源国家代码
	private String src_country;

	private String dst_ip;
	private String dst_port;
	// 目标地域
	private String dst_name;
	// 目标国家代码
	private String dst_country;
	private String deviceid;
	private String packet_pcap;
	private String protocol_type;
	private String deviceName;

	public EventGlobalVO() {
		super();
	}

	public EventGlobalVO(String date, String type, String level, String logstr,
			String src_ip, String src_port, String src_name,
			String src_country, String dst_ip, String dst_port,
			String dst_name, String dst_country, String deviceid,
			String packet_pcap) {
		super();
		this.date = date;
		this.type = type;
		this.name = type;
		this.level = level;
		this.logstr = logstr;
		this.src_ip = src_ip;
		this.src_port = src_port;
		this.src_name = src_name;
		this.src_country = src_country;
		this.dst_ip = dst_ip;
		this.dst_port = dst_port;
		this.dst_name = dst_name;
		this.dst_country = dst_country;
		this.deviceid = deviceid;
		this.packet_pcap = packet_pcap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProtocol_type() {
		return protocol_type;
	}

	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setProtocol_type(String protocol_type) {
		this.protocol_type = protocol_type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLogstr() {
		return logstr;
	}

	public void setLogstr(String logstr) {
		this.logstr = logstr;
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

	public String getSrc_country() {
		return src_country;
	}

	public void setSrc_country(String src_country) {
		this.src_country = src_country;
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

	public String getDst_country() {
		return dst_country;
	}

	public void setDst_country(String dst_country) {
		this.dst_country = dst_country;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getPacket_pcap() {
		return packet_pcap;
	}

	public void setPacket_pcap(String packet_pcap) {
		this.packet_pcap = packet_pcap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
