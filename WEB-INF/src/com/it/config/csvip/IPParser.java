package com.it.config.csvip;

import java.net.InetAddress;

import com.it.vo.IpInfo;



public interface IPParser {
	public void load() throws Exception;

	public IpInfo getIpInfo(String ip);

	public IpInfo getIpInfo(long ip);

	public IpInfo getIpInfo(InetAddress ip);
}
