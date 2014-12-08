package com.it.config.csvip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.it.config.load.ConfigLoad;

public class CsvIPParser extends ConfigLoad {

	private static final CsvIPParser instance = new CsvIPParser();

	public static CsvIPParser getInstance() {
		return instance;
	}

	private LookupIP lookupIp = null;
	private String ip_data_file = "WEB-INF/ipdata.csv";

	public void load() {
		logger.info("---------begin load CsvIPParser");
		if (ip_data_file == null || ip_data_file.isEmpty()) {
			logger.error("===file is null:" + ip_data_file);
			return;
		}
		Map<GeoInfo, Integer> geoInfoMap = new HashMap<GeoInfo, Integer>();
		lookupIp = new LookupIP(LoadIPFile.loadFile(
				Arrays.asList(ip_data_file.split("[,;]")), geoInfoMap),
				geoInfoMap);
		logger.info("---------end load CsvIPParser");
	}

	public GeoInfo parseIP(String ip) {
		try {
			return lookupIp.getLocation(ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			logger.error("======CsvIPParser error:"+e.getMessage());
		}
		return null;
	}

	
	public String getIp_data_file() {
		return ip_data_file;
	}

	public void setIp_data_file(String ip_data_file) {
		this.ip_data_file = ip_data_file;
	}

	public static void main(String args[]) {
		CsvIPParser parser = new CsvIPParser();
		parser.load();
		String IP = "204.3.255.25";
		GeoInfo res = parser.parseIP(IP);
		System.out.println("res=" + res);
		if (res != null) {
			System.out.println(res.getCountry() + res.getRegion()
					+ res.getCity());
		}

	}

}
