package com.it.config;

/************************************************************************
 *1.fix ip to  int overflow issue
 *2.reduce memory use
 *3.clean the code ,make int more shorter and simple
 *4.default limit  size of ip data 17monipdb.dat is 2GB（not test yet）。  
 ************************************************************************/

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

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

	public static IPParserLoad getInstance() {
		return instance;
	}

	// private static String ip_data_file = "WEB-INF/17monipdb.dat";
	// private static String country_file = "WEB-INF/country.txt";
	private String ip_data_maxmind = null;
	private String ip_data_file = null;
	private String country_file = null;
	private static DatabaseReader reader = null;

	private static DataInputStream inputStream = null;
	private static long fileLength = -1;
	private static int dataLength = -1;
	private static Map<String, String> cacheMap = null;
	private static byte[] allData = null;

	private static Map<String, String> country_code_map = new HashMap<String, String>();

	@Override
	public void load() {
		logger.debug("IPParserLoad Load Start...");

		File file = new File(this.ip_data_file);
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
			fileLength = file.length();
			cacheMap = new HashMap<String, String>();
			if (fileLength > Integer.MAX_VALUE) {
				throw new Exception("the filelength over 2GB");
			}

			dataLength = (int) fileLength;
			allData = new byte[dataLength];
			inputStream.read(allData, 0, dataLength);
			dataLength = (int) getbytesTolong(allData, 0, 4,
					ByteOrder.BIG_ENDIAN);

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

			// for (Map.Entry<String, String> ent : country_code_map.entrySet())
			// {
			// logger.debug(ent.getKey() + ":" + ent.getValue());
			// }

			// logger.debug("106.39.23.67:"+IPParserLoad.parseIP("106.39.23.67"));
			// logger.debug("58.192.191.255:"+IPParserLoad.parseIP("58.192.191.255"));
			// logger.debug("218.56.0.202:"+IPParserLoad.findGeography("218.56.0.202"));
			// logger.debug("60.171.139.143:"+IPParserLoad.findGeography("60.171.139.143"));
			// logger.debug("222.23.243.122:"+IPParserLoad.findGeography("222.23.243.122"));
			// logger.debug("48.255.255.2:"+IPParserLoad.findGeography("48.255.255.2"));
			// logger.debug("114.80.68.2:"+IPParserLoad.findGeography("114.80.68.2"));
			// logger.debug("114.80.68.2:"+IPParserLoad.findGeography("192.168.1.1"));

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

	private static long ip2long(String ip) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(ip);
		byte[] bytes = address.getAddress();
		long reslut = getbytesTolong(bytes, 0, 4, ByteOrder.BIG_ENDIAN);
		return reslut;
	}

	private static int getIntByBytes(byte[] b, int offSet) {
		if (b == null || (b.length < (offSet + 3))) {
			return -1;
		}

		byte[] bytes = Arrays.copyOfRange(allData, offSet, offSet + 3);
		byte[] bs = new byte[4];
		bs[3] = 0;
		for (int i = 0; i < 3; i++) {
			bs[i] = bytes[i];
		}

		return (int) getbytesTolong(bs, 0, 4, ByteOrder.LITTLE_ENDIAN);
	}

	public static IpInfo parseIP(String IP) {
		if (IP == null || IP.isEmpty()) {
			return new IpInfo(IP, "未知(无IP)", "", "", "", "", "", "");
		}

		if (!IP.contains(".")) {
			IP = Utils.longToIp(Long.parseLong(IP));
		}
		String res[] = findGeography(IP).split("\\t");
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
					logger.error("=============ip parse error:"+e.getMessage()+",ip="+IP);
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
	 */
	public static String findGeography(String address) {
		if (StringUtils.isBlank(address)) {
			return "illegal address";
		}

		if (dataLength < 4 || allData == null) {
			return "illegal ip data";
		}

		String ip = address;
		logger.debug("findGeography=========ip=" + ip);
		// String ip = "127.0.0.1";
		// try {
		// ip = Inet4Address.getByName(address).getHostAddress();
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }
		String[] ipArray = StringUtils.split(ip, ".");
		long ipHeadValue = Long.parseLong(ipArray[0]);
		if (ipArray.length != 4 || ipHeadValue < 0 || ipHeadValue > 255) {
			return "illegal ip";
		}

		if (cacheMap.containsKey(ip)) {
			return cacheMap.get(ip);
		}

		long numIp = 1;
		try {
			numIp = ip2long(address);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		int tempOffSet = (int) ipHeadValue * 4 + 4;
		long start = getbytesTolong(allData, tempOffSet, 4,
				ByteOrder.LITTLE_ENDIAN);
		int max_len = dataLength - 1028;
		long resultOffSet = 0;
		int resultSize = 0;

		for (start = start * 8 + 1024; start < max_len; start += 8) {
			if (getbytesTolong(allData, (int) start + 4, 4,
					ByteOrder.BIG_ENDIAN) >= numIp) {
				resultOffSet = getIntByBytes(allData, (int) (start + 4 + 4));
				resultSize = (char) allData[(int) start + 7 + 4];
				break;
			}
		}

		if (resultOffSet <= 0) {
			return "resultOffSet too small";
		}

		byte[] add = Arrays.copyOfRange(allData, (int) (dataLength
				+ resultOffSet - 1024),
				(int) (dataLength + resultOffSet - 1024 + resultSize));
		try {
			if (add == null) {
				cacheMap.put(ip, new String("no data found!!"));
			} else {
				cacheMap.put(ip, new String(add, "UTF-8"));
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return cacheMap.get(ip);
	}

	public static void main(String args[]) {
		// int a= Integer.parseInt("3589707579");
		// String s = "illegal ip data";
		// System.out.println(s.split("\\t").length);

		try {
			System.out.println(ip2long("200.12.2.4"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}