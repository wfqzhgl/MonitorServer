package com.it.vo;

public class EventTypeVO {
	private String id;
	private String code;//code
	private String name;//name
	private String detail;
	private String level;
	
	
	
	
	
	public EventTypeVO(String id, String code, String name, String detail) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.detail = detail;
	}
	public EventTypeVO(String code, String name, String detail) {
		super();
		this.code = code;
		this.name = name;
		this.detail = detail;
	}
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	
}
