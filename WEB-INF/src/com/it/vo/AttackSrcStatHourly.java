package com.it.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import com.it.util.Utils;

/**
 * 
 * @author wufaqing
 * attack_srcstat_hourly
 * attack_dststat_hourly
 *
 */
public class AttackSrcStatHourly {
	private Integer year;
	private Integer month;
	private Integer week;
	private Date date;
	private Integer hour;
	private Integer datehour;
	private String sip;
	private Long lip;
	private String country;
	private String province;
	private String city;
	private String type;
	private long count;
	
	
	
	public Integer getDatehour() {
		return datehour;
	}
	public void setDatehour(Integer datehour) {
		this.datehour = datehour;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getWeek() {
		return week;
	}
	public void setWeek(Integer week) {
		this.week = week;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Integer getHour() {
		return hour;
	}
	public void setHour(Integer hour) {
		this.hour = hour;
	}
	public String getSip() {
		return sip;
	}
	public void setSip(String sip) {
		this.sip = sip;
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
	
	
	public Long getLip() {
		return lip;
	}
	public void setLip(Long lip) {
		this.lip = lip;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public static void main(String args[]){
//		System.out.println(Utils.getCalendarByDiffHourFromNow(-2).getTime());
//		System.out.println(Utils.getCalendarBySpecific(2014, 9, 3, 3).getTime());
//		Calendar a =Calendar.getInstance();
//		Calendar b = Calendar.getInstance();;
//		b.add(b.HOUR_OF_DAY, 1);
//		a.setTimeInMillis(b.getTimeInMillis());
//		System.out.println(a.getTime());
//		System.out.println(b.getTime());
	}
}
