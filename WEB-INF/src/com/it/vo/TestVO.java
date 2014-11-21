package com.it.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7850121747437045573L;
	private String id;
	private String name;
	private String quid;
	private String ip;
	private String detail;
	private List<Map<String,String>> list;
	
	
	
	public List<Map<String,String>> getList() {
		return list;
	}

	public void setList(List<Map<String,String>> list) {
		this.list = list;
	}

	public TestVO(){
		
	}
	
	public TestVO(String id, String ip) {
		super();
		this.id = id;
		this.ip = ip;
		this.list =new ArrayList();
		Map<String,String> m =new HashMap<String,String>();
		m.put("name", "name1");
		m.put("count", "2123");
		this.list.add(m);
		m =new HashMap<String,String>();
		m.put("name", "name2");
		m.put("count", "678");
		this.list.add(m);
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

	public String getQuid() {
		return quid;
	}

	public void setQuid(String quid) {
		this.quid = quid;
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
