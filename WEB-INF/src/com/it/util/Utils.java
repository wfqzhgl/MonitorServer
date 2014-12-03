package com.it.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.it.vo.IpRange;

public class Utils {

	public static String[] get_hbase_connection_string(String configFile)
			throws ConfigurationException {
		Configuration configuration = new PropertiesConfiguration(configFile);
		String host = configuration.getString("hbase_zookeeper_quorum_host");
		String port = configuration.getString("hbase_zookeeper_quorum_port");
		if (host == null || port == null || host.isEmpty() || port.isEmpty()) {
			return null;
		}
		return new String[] { host, port };
	}

	public static Connection get_mysql_conn(String jdbcFile)
			throws SQLException, ConfigurationException {
		String url, user, password = null;
		Configuration configuration = new PropertiesConfiguration(jdbcFile);
		url = configuration.getString("it.main.jdbc.url");
		user = configuration.getString("it.main.jdbc.username");
		password = configuration.getString("it.main.jdbc.password");
		Connection conn = DriverManager.getConnection(url, user, password);
		conn.setAutoCommit(false);
		return conn;
	}

	
	public static long get_timestamp_from_hbase_key(String key){
		long max_seconds = 7956886942222L;
		long key_seconds = Long.parseLong(key.substring(0, 13));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(max_seconds - key_seconds);
		return  cal.getTimeInMillis();
	}
	
	public static String get_date_string_from_hbase_key(String key){
		long max_seconds = 7956886942222L;
		long key_seconds = Long.parseLong(key.substring(0, 13));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(max_seconds - key_seconds);
		return  Constant.STANDARD_DATE_FORMAT.format(cal.getTime());
	}
	
	public static Map<String, String> parse_date_from_hbase_key(String key) {
		Map<String, String> map = new HashMap<String, String>();
		long max_seconds = 7956886942222L;
		long key_seconds = Long.parseLong(key.substring(0, 13));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(max_seconds - key_seconds);
		System.out.println(cal.getTimeInMillis());
		map.put("year", String.valueOf(cal.get(cal.YEAR)));
		map.put("month", String.valueOf(cal.get(cal.MONTH) + 1));
		map.put("date", Constant.DEFAULT_DATE_FORMAT.format(cal.getTime()));
		map.put("week", String.valueOf(cal.get(cal.WEEK_OF_YEAR)));
		map.put("hour", String.valueOf(cal.get(cal.HOUR_OF_DAY)));

		return map;

	}

	public static Calendar getCalendarByDiffHourFromNow(int diffHour) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, diffHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Calendar getCalendarBySpecific(int year, int month, int date,
			int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static String getHbaseKeyByTimeStamp(long cal) throws ParseException {
		String maxDateStr = "2222-02-22 22:22:22.222";
		long maxDateMico = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
				.parse(maxDateStr).getTime();
//		System.out.println("max mico=" + maxDateMico);
		return String.valueOf((maxDateMico - cal) * 10000);
	}

	public static String getHbaseKeyByCalendar(Calendar cal)
			throws ParseException {
		String maxDateStr = "2222-02-22 22:22:22.222";
		long maxDateMico = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
				.parse(maxDateStr).getTime();
//		System.out.println("max mico=" + maxDateMico);
		return String.valueOf((maxDateMico - cal.getTimeInMillis()) * 10000);
	}

	public static long ipToLong(String ip) throws UnknownHostException {
		byte[] address = InetAddress.getByName(ip).getAddress();
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

	public static String longToIp(long longIp) {
		// 将十进制整数形式转换成127.0.0.1形式的ip地址
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	public static boolean IpInRanges(String ip, List<String> ips) {
		long lip;

		if (ip.contains(".")) {

			try {
				lip = ipToLong(ip);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			lip = Long.parseLong(ip);
		}
		for (String tmp : ips) {
			IpRange ir = new IpRange(tmp);
			if (lip >= ir.getBegin() && lip <= ir.getEnd()) {
				return true;
			}

		}
		return false;

	}


	public static String getSqlByIps(String ipfield, List<String> ips) {
		String sql = "";
		if (ips == null || ips.isEmpty()) {
			return sql;
		}
		for (String ip : ips) {
			IpRange ir = new IpRange(ip);
			sql += " (" + ipfield + " between " + ir.getBegin() + " and "
					+ ir.getEnd() + ") or";
		}
		if (!sql.isEmpty()) {
			sql = trimStr(sql, "or");
			sql = " ( " + sql + " ) ";
		}
		return sql;

	}

	public static String trimStr(String srcs, String trims) {
		srcs = srcs.trim();
		if (srcs.endsWith(trims)) {
			return srcs.substring(0, srcs.length() - trims.length());
		}
		return srcs;
	}

	
	public static void test1(Map<String,String> map){
		map.put("t1", "1");
	}
	
	public static void test2(Map<String,String> map){
		map.put("t2", "2");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			Map<String,String> map = new HashMap();
			map.put("123", "123");
			map.put("ssdfs", "中国");
			JSONObject o = JSONObject.fromObject(map);
			String json = o.toString();
			System.out.println(json);
			
			JSONObject oo = JSONObject.fromObject(json);
			Map<String, String> mm = (Map<String, String>) JSONObject.toBean(oo,
					Map.class);
			System.out.println(mm.get("123"));
			
//			System.out.println(get_date_string_from_hbase_key("65459173422220000"));
			//
			// Calendar cal = Calendar.getInstance();
			// System.out.println(getHbaseKeyByCalendar(cal));

			// System.out.println(ipToLong("200.12.2.4"));

			// System.out.println(IpInRanges("192.168.1.17",
			// Arrays.asList("192.168.1.15","192.168.1.16/29")));
			// List<String> list = Arrays.asList("你好,类型2".split(","));
			// System.out.println(list.contains("你好"));
			// System.out.println(list.contains("类型2"));
			// System.out.println(list.contains("你好2"));

//			System.out.println(ipToLong("210.130.202.122"));
//
//			System.out.println(longToIp(1099511627703L));
//
//			System.out.println(getSqlByIps("lip", Arrays.asList(new String[] {
//					"192.168.1.1", "192.168.2.3" })));
//
//			System.out.println(trimStr("addfsfs99a99", "99"));
			
//			System.out.println("as.f|123".contains("|"));
//			System.out.println(Arrays.toString("TOTAL".split("\\|")));
//			System.out.println(Arrays.toString("TOTAL|TCP|UDP|HTTP|FTP|ICMP|OTHER".split("\\|")));
			
			
//			System.out.println(String.format("asddfs%ssfsf", "1111"));
//			System.out.println(
//			Utils.getHbaseKeyByTimeStamp(System
//					.currentTimeMillis() - 1800000 * 1));
			
//			System.out.println("1000,12,13".contains("1000"));
			
//			System.out.println(System.currentTimeMillis());
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
