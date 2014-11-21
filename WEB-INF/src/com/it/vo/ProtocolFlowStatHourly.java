package com.it.vo;

import java.sql.Date;

/**
 * 
 * @author wufaqing
 * 
 *
 */
public class ProtocolFlowStatHourly {
	private Integer year;
	private Integer month;
	private Integer week;
	private Date date;
	private Integer hour;
	private Integer datehour;
	//TOTAL、TCP、UDP、HTTP、FTP、ICMP、OTHER
	private String protocol;
	//实时平均流量KB
	private Long flow;
	
	private Date updateTime;
	
	
	
	
	public ProtocolFlowStatHourly() {
		super();
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




	public Integer getDatehour() {
		return datehour;
	}




	public void setDatehour(Integer datehour) {
		this.datehour = datehour;
	}




	public String getProtocol() {
		return protocol;
	}




	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}




	public Long getFlow() {
		return flow;
	}




	public void setFlow(Long flow) {
		this.flow = flow;
	}




	public Date getUpdateTime() {
		return updateTime;
	}




	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
