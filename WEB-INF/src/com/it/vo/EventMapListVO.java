package com.it.vo;

import java.util.List;
import java.util.Map;

public class EventMapListVO {
	private String type; // "1","2","3"
	private List<Map<String,String>> msglist;
	private List<Map<String,String>> datalist;
	
	



	public EventMapListVO() {
		super();
		// TODO Auto-generated constructor stub
	}





	public EventMapListVO(String type, List<Map<String, String>> msglist,
			List<Map<String, String>> datalist) {
		super();
		this.type = type;
		this.msglist = msglist;
		this.datalist = datalist;
	}





	public String getType() {
		return type;
	}





	public void setType(String type) {
		this.type = type;
	}





	public List<Map<String, String>> getMsglist() {
		return msglist;
	}





	public void setMsglist(List<Map<String, String>> msglist) {
		this.msglist = msglist;
	}





	public List<Map<String, String>> getDatalist() {
		return datalist;
	}





	public void setDatalist(List<Map<String, String>> datalist) {
		this.datalist = datalist;
	}





	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
