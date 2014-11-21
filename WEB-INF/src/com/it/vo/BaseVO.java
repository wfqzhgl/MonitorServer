package com.it.vo;

import java.util.List;

import net.sf.json.JSONObject;

public  class BaseVO {
	private int code;
	private List<Object> value;
	private int page=0;
	private int pages = 0;
	private int total=0;

	
	
	
	public BaseVO(int code, List<Object> value) {
		super();
		this.code = code;
		this.value = value;
	}
	
	

	public BaseVO(int code, List<Object> value, int page, int pages, int total) {
		super();
		this.code = code;
		this.value = value;
		this.page = page;
		this.pages = pages;
		this.total = total;
	}



	public String toString() {

		JSONObject jo = JSONObject.fromObject(this);
		return jo.toString();

	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Object> getValue() {
		return value;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
