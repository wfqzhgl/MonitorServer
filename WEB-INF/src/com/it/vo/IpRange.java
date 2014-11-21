package com.it.vo;

import java.net.UnknownHostException;

import org.apache.commons.net.util.SubnetUtils;

import com.it.util.Utils;

public class IpRange {
	private String ips;
	private long begin;
	private long end;

	public IpRange(String ips) {
		super();
		this.ips = ips;
		// ip/ip段/ip组
		// to do ..
		if (ips.contains("-")) {
			String[] tmp = ips.split("-");
			try {
				begin = Utils.ipToLong(tmp[0]
						+ tmp[1].substring(tmp[1].indexOf(".") == -1 ? tmp[1]
								.length() : tmp[1].indexOf(".")));
				end = Utils.ipToLong(tmp[0].substring(0, tmp[0]
						.lastIndexOf(".") == -1 ? 0
						: tmp[0].lastIndexOf(".") + 1)
						+ tmp[1]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (ips.contains("/")) { //192.0.2.16/29
			SubnetUtils utils = new SubnetUtils(ips);
			try {
				begin = Utils.ipToLong(utils.getInfo().getLowAddress());
				end = Utils.ipToLong(utils.getInfo().getHighAddress());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			


		} else {
			try {
				begin = end = Utils.ipToLong(ips);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public static void main(String[] args) {
		String ips = "192.168.2.1-23";
		String ips1 = "16-90.3.192.3";
		String ips2 = "192.12-34.3.3";
//		String a = ips2.split("-")[0];
//		String b = ips2.split("-")[1];
//		System.out.println(a
//				+ b.substring(b.indexOf(".") == -1 ? b.length() : b
//						.indexOf(".")));
//		System.out.println(a.substring(0,
//				a.lastIndexOf(".") == -1 ? 0 : a.lastIndexOf(".") + 1)
//				+ b);
		
		IpRange  ir = new IpRange(ips);
		System.out.println(ir.getBegin()+":"+ir.getEnd());
		try {
//			System.out.println(Utils.ipToLong("192.168.2.1"));
//			System.out.println(Utils.ipToLong("192.168.2.23"));
			
			SubnetUtils utils = new SubnetUtils("192.0.2.16/29");
			
			System.out.println(utils.getInfo().getLowAddress());
			System.out.println(utils.getInfo().getHighAddress());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
