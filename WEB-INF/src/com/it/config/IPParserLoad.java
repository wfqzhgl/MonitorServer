package com.it.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.it.config.csvip.CsvIPParser;
import com.it.config.csvip.GeoInfo;
import com.it.config.load.ConfigLoad;
import com.it.util.Utils;
import com.it.vo.City;
import com.it.vo.Country;
import com.it.vo.IpInfo;
import com.it.vo.Province;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Subdivision;

public class IPParserLoad extends ConfigLoad {

	private static final IPParserLoad instance = new IPParserLoad();
	private static boolean debug = false;

	public static IPParserLoad getInstance() {
		if (debug) {
			ip_data_file = "WEB-INF/17monipdb.dat";
			country_file = "WEB-INF/country.txt";
			ip_data_maxmind = "WEB-INF/GeoLite2-City.mmdb";
			ip_data_custom = "WEB-INF/ipdata.csv";
			instance.load();
		}
		return instance;
	}

	// 自定义优先级最高
	private static CsvIPParser customIPParser = CsvIPParser.getInstance();
	private static String ip_data_custom = null;
	private static String ip_data_file = null;
	private static String country_file = null;
	private static String ip_data_maxmind = null;
	private static DatabaseReader reader = null;

	private static int offset;
	private static int[] index = new int[256];
	private static ByteBuffer dataBuffer;
	private static ByteBuffer indexBuffer;
	private static ReentrantLock lock = new ReentrantLock();

	private static Map<String, String> country_code_map = new HashMap<String, String>();

	@Override
	public void load() {
		logger.debug("IPParserLoad Load Start...");
		// load custom
		customIPParser.load();

		// load other
		File ipFile = new File(ip_data_file);
		FileInputStream fin = null;
		try {
			dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length())
					.intValue());
			fin = new FileInputStream(ipFile);
			int readBytesLength;
			byte[] chunk = new byte[4096];
			while (fin.available() > 0) {
				readBytesLength = fin.read(chunk);
				dataBuffer.put(chunk, 0, readBytesLength);
			}
			dataBuffer.position(0);
			int indexLength = dataBuffer.getInt();
			byte[] indexBytes = new byte[indexLength];
			dataBuffer.get(indexBytes, 0, indexLength - 4);
			indexBuffer = ByteBuffer.wrap(indexBytes);
			indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
			offset = indexLength;

			int loop = 0;
			while (loop++ < 256) {
				index[loop - 1] = indexBuffer.getInt();
			}
			indexBuffer.order(ByteOrder.BIG_ENDIAN);

			String content = FileUtils.readFileToString(new File(
					this.country_file));
			JSONArray arr = JSONArray.fromObject(content);
			for (Object tmp : arr.toArray()) {
				JSONObject obj = JSONObject.fromObject(tmp);
				country_code_map.put(obj.get("c").toString(), obj.get("i")
						.toString());
			}

			File database = new File(ip_data_maxmind);

			// This creates the DatabaseReader object, which should be reused
			// across
			// lookups.
			try {
				reader = new DatabaseReader.Builder(database).build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("!!!IPParserLoad error." + e.getMessage());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!IPParserLoad error." + e.getMessage());
		}

		logger.debug("IPParserLoad Load end...");
	}

	public String getIp_data_file() {
		return this.ip_data_file;
	}

	public void setIp_data_file(String ip_data_file) {
		this.ip_data_file = ip_data_file;
	}

	public String getCountry_file() {
		return country_file;
	}

	public void setCountry_file(String country_file) {
		this.country_file = country_file;
	}

	public String getIp_data_maxmind() {
		return ip_data_maxmind;
	}

	public void setIp_data_maxmind(String ip_data_maxmind) {
		this.ip_data_maxmind = ip_data_maxmind;
	}

	private static long getbytesTolong(byte[] bytes, int offerSet, int size,
			ByteOrder byteOrder) {
		if ((offerSet + size) > bytes.length || size <= 0) {
			return -1;
		}
		byte[] b = new byte[size];
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes[offerSet + i];
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(b);
		byteBuffer.order(byteOrder);

		long temp = -1;
		if (byteBuffer.hasRemaining()) {
			temp = byteBuffer.getInt();
		}
		return temp;
	}

	private static int str2Ip(String ip) {
		String[] ss = ip.split("\\.");
		int a, b, c, d;
		a = Integer.parseInt(ss[0]);
		b = Integer.parseInt(ss[1]);
		c = Integer.parseInt(ss[2]);
		d = Integer.parseInt(ss[3]);
		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	public static long ip2long(String ip) {
		return int2long(str2Ip(ip));
	}

	private static long int2long(int i) {
		long l = i & 0x7fffffffL;
		if (i < 0) {
			l |= 0x080000000L;
		}
		return l;
	}

	private static long bytesToLong(byte a, byte b, byte c, byte d) {
		return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16)
				| ((c & 0xff) << 8) | (d & 0xff)));
	}

	public static IpInfo parseIP(String IP) {
		if (IP == null || IP.isEmpty()) {
			return new IpInfo(IP, "未知(无IP)", "", "", "", "", "", "");
		}

		if (!IP.contains(".")) {
			IP = Utils.longToIp(Long.parseLong(IP));
		}

		String[] res = null;
		// 匹配自定义ip库
		GeoInfo geoinfo = customIPParser.parseIP(IP);
		if (geoinfo != null) {
			logger.debug("----parse ip from custom:" + IP);
			// 外国
			if (!geoinfo.getCountry().equals("中国")) {
				Country country = GeoLoad.getInstance().getCountryByName(
						geoinfo.getCountry());
				String country_code = country == null ? "" : country.getId()
						+ "";
				return new IpInfo(IP, geoinfo.getCountry(), country_code,
						geoinfo.getRegion() == null ? "" : geoinfo.getRegion(),
						"", "", "", "");
			}
			// 中国
			if (geoinfo.getCity() != null && !geoinfo.getCity().isEmpty()) {
				res = new String[] { geoinfo.getCountry(), geoinfo.getRegion(),
						geoinfo.getCity() };
			} else {
				res = new String[] { geoinfo.getCountry(), geoinfo.getRegion() };
			}

		}

		if (res == null) {
			res = find(IP);
		}
		if (res.length == 2) {
			// 外国
			if (res[0].equalsIgnoreCase(res[1])) {
				Country country = GeoLoad.getInstance()
						.getCountryByName(res[0]);
				String country_code = country == null ? "" : country.getId()
						+ "";
				// read from maxmind
				String[] ipStr = IP.split("\\.");
				byte[] ipBuf = new byte[4];
				for (int i = 0; i < 4; i++) {
					ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
				}
				try {
					InetAddress ipAddress = InetAddress.getByAddress(IP, ipBuf);
					CityResponse response = reader.city(ipAddress);
					com.maxmind.geoip2.record.Country cMax = response
							.getCountry();
					Subdivision subdivision = response
							.getMostSpecificSubdivision();
					String ccodeMax = cMax.getIsoCode();
					String pMax = subdivision.getName();
					if (ccodeMax.equalsIgnoreCase("US")) {
						country = GeoLoad.getInstance().getCountryByName("美国");
						country_code = country == null ? "" : country.getId()
								+ "";
						Province province = null;
						if (pMax != null) {

							province = GeoLoad.getInstance().getProvinceByName(
									country,
									pMax.replaceAll("\\s+", "").toLowerCase());
						}
						String province_code = province == null ? "" : province
								.getId() + "";
						return new IpInfo(IP, res[0], country_code,
								province.getName(), province_code, "", "", "");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error("=============ip parse error:"
							+ e.getMessage() + ",ip=" + IP);
				}

				return new IpInfo(IP, res[0],
						country_code_map.containsKey(res[0]) ? country_code_map
								.get(res[0]) : "", "", "", "", "", "");
			}
			// 中国 直辖市/港澳台
			else {
				Country country = GeoLoad.getInstance()
						.getCountryByName(res[0]);
				if (res[1].equalsIgnoreCase("台湾")) {
					res[1] = "台湾省";
				}
				if (res[1].equalsIgnoreCase("台湾省")
						|| res[1].equalsIgnoreCase("台湾")
						|| res[1].equalsIgnoreCase("香港")
						|| res[1].equalsIgnoreCase("澳门")) {

					country = GeoLoad.getInstance().getCountryByName(res[1]);
				}
				String country_code = country == null ? "" : country.getId()
						+ "";
				Province province = GeoLoad.getInstance().getProvinceByName(
						country, res[1]);
				String province_code = province == null ? "" : province.getId()
						+ "";
				return new IpInfo(IP, res[0], country_code, res[1],
						province_code, "", "", "");
			}

		} else if (res.length >= 3) {
			String province_code;
			String city_code;
			String country_code;
			Country country = GeoLoad.getInstance().getCountryByName(res[0]);
			if (country == null) {
				country_code = province_code = city_code = "";
			} else {
				country_code = country.getId() + "";
				Province province = GeoLoad.getInstance().getProvinceByName(
						country, res[1]);
				if (province == null) {
					province_code = city_code = "";
				} else {
					province_code = province.getId() + "";
					City city = GeoLoad.getInstance().getCityByName(province,
							res[2]);
					city_code = city == null ? "" : city.getId() + "";
				}
			}
			return new IpInfo(IP, res[0], country_code, res[1], province_code,
					res[2], city_code, "");

		} else {
			return new IpInfo(IP, "未知", "", "", "", "", "", "");
		}

	}

	/**
	 * @param address
	 * @return
	 */
	/**
	 * @param address
	 * @return
	 * @throws
	 */
	public static String[] find(String ip) {
		int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
		long ip2long_value = ip2long(ip);
		int start = index[ip_prefix_value];
		int max_comp_len = offset - 1028;
		long index_offset = -1;
		int index_length = -1;
		byte b = 0;
		for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
			if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
				index_offset = bytesToLong(b, indexBuffer.get(start + 6),
						indexBuffer.get(start + 5), indexBuffer.get(start + 4));
				index_length = 0xFF & indexBuffer.get(start + 7);
				break;
			}
		}

		byte[] areaBytes;
		lock.lock();
		try {
			dataBuffer.position(offset + (int) index_offset - 1024);
			areaBytes = new byte[index_length];
			dataBuffer.get(areaBytes, 0, index_length);
		} finally {
			lock.unlock();
		}

		return new String(areaBytes).split("\t");
	}

	public String getIp_data_custom() {
		return ip_data_custom;
	}

	public void setIp_data_custom(String ip_data_custom) {
		this.ip_data_custom = ip_data_custom;
		customIPParser.setIp_data_file(ip_data_custom);
	}

	public static void main(String args[]) {
		// int a= Integer.parseInt("3589707579");
		// String s = "illegal ip data";
		// System.out.println(s.split("\\t").length);

		try {
			String IP = "222.222.24.31";
			System.out.println(ip2long(IP));

			IpInfo info = IPParserLoad.getInstance().parseIP(IP);
			System.out.println(info.getCountry() + info.getProvince()
					+ info.getCity()+info.getCountry_code());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}