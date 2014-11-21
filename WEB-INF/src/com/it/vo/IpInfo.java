package com.it.vo;

import java.io.Serializable;

public class IpInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8036446791886243585L;
	private Object ip;
	private String country;
	private String country_code;
	private String province;
	private String province_code;
	private String city;
	private String city_code;
	private String isp;

	
	
	public IpInfo(Object ip, String country, String country_code,
			String province, String province_code, String city,
			String city_code, String isp) {
		super();
		this.ip = ip;
		this.country = country;
		this.country_code = country_code;
		this.province = province;
		this.province_code = province_code;
		this.city = city;
		this.city_code = city_code;
		this.isp = isp;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ip=").append(ip).append(": country=").append(country)
				.append("; country_code=").append(country_code).append("; province=")
				.append(province).append("; city=").append(city);
		return sb.toString();
	}

	
	public String get_country_province_city_string(){
		StringBuilder sb = new StringBuilder();
		sb.append(country);
		if(province!=null&&!province.isEmpty()){
			sb.append("-").append(province);
		}
		if(city!=null&&!city.isEmpty()){
			sb.append("-").append(city);
		}
		
		return sb.toString();
		
	}
	public Object getIp() {
		return ip;
	}

	public void setIp(Object ip) {
		this.ip = ip;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public String getProvince_code() {
		return province_code;
	}

	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

}
