package com.it.vo;


public class HourlyLogVO {
	private String datehour;
	private String ip;
	private Long lip;
	private String typeName;
	private String typeCode;
	private Long count;

	public HourlyLogVO() {
		super();
	}

	

	public HourlyLogVO(String datehour, String ip, Long lip, String typeName,
			String typeCode, Long count) {
		super();
		this.datehour = datehour;
		this.ip = ip;
		this.lip = lip;
		this.typeName = typeName;
		this.typeCode = typeCode;
		this.count = count;
	}



	public String getDatehour() {
		return datehour;
	}



	public void setDatehour(String datehour) {
		this.datehour = datehour;
	}



	public String getIp() {
		return ip;
	}



	public void setIp(String ip) {
		this.ip = ip;
	}



	public Long getLip() {
		return lip;
	}



	public void setLip(Long lip) {
		this.lip = lip;
	}



	public String getTypeName() {
		return typeName;
	}



	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}



	public String getTypeCode() {
		return typeCode;
	}



	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}



	public Long getCount() {
		return count;
	}



	public void setCount(Long count) {
		this.count = count;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
