package com.it.config.csvip;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPCompare implements Comparable<IPCompare> {
	private long start;
	private long end;
	private int geoInfoIndex;

	@Override
	public int compareTo(IPCompare o) {
		if (o == null) {
			return -1;
		} else if (end < o.start) {
			return -1;
		} else if (o.end < start) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(start).append('\t').append(end).append('\t')
				.append(geoInfoIndex);
		return sb.toString();
	}

	private static long bytesToLong(byte[] address) {
		long ipnum = 0;
		for (int i = 0; i < 4; ++i) {
			long y = address[i];
			if (y < 0) {
				y += 256;
			}
			ipnum += y << ((3 - i) * 8);
		}
		return ipnum;
	}

	public IPCompare() {

	}

	private static byte[] getipBytes(String ip) {
		String[] ipStr = ip.split("\\.");
		byte[] ipBuf = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
		}
		return ipBuf;
	}

	public IPCompare(String ip) throws UnknownHostException {

		this(bytesToLong(InetAddress.getByAddress(ip, getipBytes(ip))
				.getAddress()));
	}

	public IPCompare(InetAddress ip) throws UnknownHostException {
		this(bytesToLong(ip.getAddress()));
	}

	public IPCompare(long ip) {
		this.start = ip;
		this.end = ip;
	}

	public IPCompare(long start, long end, int geoInfoIndex) {
		this.start = start;
		this.end = end;
		this.geoInfoIndex = geoInfoIndex;
	}

	public IPCompare(IPCompare other) {
		this.start = other.start;
		this.end = other.end;
		this.geoInfoIndex = other.geoInfoIndex;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getGeoInfoIndex() {
		return geoInfoIndex;
	}

	public void setGeoInfoIndex(int geoInfoIndex) {
		this.geoInfoIndex = geoInfoIndex;
	}

}
