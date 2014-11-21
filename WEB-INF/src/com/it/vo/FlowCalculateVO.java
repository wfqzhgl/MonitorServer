package com.it.vo;


public class FlowCalculateVO {
	private String code;
	private double sumFlow;
	private int count=0;
	
	
	
	public FlowCalculateVO() {
		super();
		// TODO Auto-generated constructor stub
	}


	public double getAvgFlow(){
		if(count<=0){
			return 0;
		}
		return sumFlow/count;
	}

	public FlowCalculateVO(String code, double sumFlow, int count) {
		super();
		this.code = code;
		this.sumFlow = sumFlow;
		this.count = count;
	}



	public String getCode() {
		return code;
	}



	public double getSumFlow() {
		return sumFlow;
	}


	public void setSumFlow(double sumFlow) {
		this.sumFlow = sumFlow;
	}


	public void setCode(String code) {
		this.code = code;
	}





	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}


}
