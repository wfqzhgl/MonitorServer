package com.it.dao;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.it.cache.JedisUtils;
import com.it.config.AddressConfig;
import com.it.config.BaseConfigLoad;
import com.it.config.GeoLoad;
import com.it.config.IPParserLoad;
import com.it.config.TypeListLoad;
import com.it.hadoop.HbaseBaseOP;
import com.it.util.Constant;
import com.it.util.JsonReader;
import com.it.vo.AddressVO;
import com.it.vo.Country;
import com.it.vo.EventGlobalVO;
import com.it.vo.EventMapListVO;
import com.it.vo.IpInfo;

@Repository
public class EventSpaceDao extends BaseDao {
	private BaseConfigLoad config;

	private static Logger logger = Logger.getLogger(EventSpaceDao.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private String get_time_from_redis(String key) {
		String begintime = JedisUtils.getInstance().get(key);
		logger.debug("====begintime from redis:"
				+ (begintime == null ? "null" : begintime));
		if (begintime != null) {
			return String.valueOf(Long.parseLong(begintime) - 20);
		}
		return begintime;

	}

	private Map<String, String> get_last_data_from_redis(String key) {
		try {
			String data = JedisUtils.getInstance().lget(key);
			if (data == null || data.isEmpty()) {
				return null;
			}
			Map<String, String> map = null;
			JSONObject o = JSONObject.fromObject(data);

			map = (Map<String, String>) JSONObject.toBean((JSONObject) o,
					Map.class);
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void set_last_data_to_redis(String key, Map<String, String> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		try {
			JSONObject o = JSONObject.fromObject(data);
			long len = JedisUtils.getInstance().llen(key);
			if (len > 0) {
				JedisUtils.getInstance().rpop(key);
			}
			JedisUtils.getInstance().lpush(key, o.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void set_to_redis(String key, String v) {
		JedisUtils.getInstance().set(key, v);
		logger.debug("==== set to redis: key=" + key + ",v=" + v);

	}

	public List<Object> get_space_map_list_from_hbase(String sessionid,
			Map<String, String> params) throws IOException,
			NumberFormatException, ParseException {
		String key_last_data = "key_last_data";
		List<Object> list = new ArrayList<Object>();
		// config key:map_data_from_hbase_reverse
		boolean reverse = this.getIsReverseMapData();

		// get last begintime from cache JedisUtils
		String timekey = Constant.PARAMETER_MAPLIST_BEGINTIME + "_" + sessionid;
		String begintime = null;
		if (params.containsKey(Constant.PARAMETER_MAPLIST_BEGINTIME)) {
			logger.debug("====begintime  in  url:"
					+ params.get(Constant.PARAMETER_MAPLIST_BEGINTIME));
			begintime = params.get(Constant.PARAMETER_MAPLIST_BEGINTIME);
		} else {
			begintime = get_time_from_redis(timekey);
			if (begintime != null && !begintime.isEmpty()) {
				params.put(Constant.PARAMETER_MAPLIST_BEGINTIME, begintime);
			} else {
				begintime = String
						.valueOf(System.currentTimeMillis() / 1000 - 3600);
			}
		}

		String begin = null;
		String end = null;
		if (params.containsKey(Constant.QUERY_BEGIN)) {
			begin = params.get(Constant.QUERY_BEGIN);
		}
		if (params.containsKey(Constant.QUERY_END)) {
			end = params.get(Constant.QUERY_END);
		}
		// 自定义日期查询
		long begins = 0;
		long ends = 0;
		if (begin != null && !begin.isEmpty() && end != null && !end.isEmpty()) {
			Date bb = Constant.DEFAULT_DATE_FORMAT.parse(begin);
			Date ee = Constant.DEFAULT_DATE_FORMAT.parse(end);
			begins = bb.getTime();
			ends = ee.getTime();
		}

		// 1单点/2多点/3散列
		String showtype = params.get(Constant.PARAMETER_MAPLIST_SHOWTYPE);

		String eventtype = params.get(Constant.PARAMETER_MAPLIST_EVENTTYPE);
		List<String> elist = new ArrayList<String>();
		if (eventtype != null && !eventtype.isEmpty()) {
			// 转换成中文名
			elist.add(TypeListLoad.getInstance().getTypeByID(eventtype)
					.getName());

		}

		String limit = params.get(Constant.LIMIT_KEY);

		if (this.getMapDataFromFile()) {
			return get_space_map_list(sessionid, params);
		}

		List<Map<String, String>> hlist = null;
		if (begins > 0 && ends > 0) {
			logger.info("------------data from custom time-------");
			hlist = HbaseBaseOP.getInstance().get_space_origin_list(begins,
					ends, this.getCustomMapDataSize(), elist);
		} else {
			logger.info("------------data from auto time-------");
			hlist = HbaseBaseOP.getInstance().get_space_origin_list(
					Long.parseLong(begintime), 0, Long.parseLong(limit), elist);
		}

		EventMapListVO vo = null;

		Map<String, String> map_last_data = new HashMap<String, String>();

		for (Map<String, String> map : hlist) {
			vo = new EventMapListVO();
			vo.setType(showtype);
			List<Map<String, String>> msglist = new ArrayList<Map<String, String>>();
			List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
			Map<String, String> tmpmap = new HashMap<String, String>();

			if (reverse && map.get("s_country_name").equalsIgnoreCase("中国")
					&& !map.get("d_country_name").equalsIgnoreCase("中国")) {
				
				String titile = map.get("time")+" "+map.get("dip") + "(" + map.get("d_country_name")+ ")"
						+ "->" +map.get("sip")+ "("+ map.get("s_country_name")+ ") "+map.get("msg_data") ;
				
				tmpmap.put("msg_title", titile);
				tmpmap.put("msg_data", map.get("msg_data"));
				msglist.add(tmpmap);

				tmpmap = new HashMap<String, String>();
				
				tmpmap.put("sip", map.get("dip"));
				tmpmap.put("s_country_code", map.get("d_country_code"));
				tmpmap.put("s_country_name", map.get("d_country_name"));
				tmpmap.put("s_province_code", map.get("d_province_code"));
				tmpmap.put("s_province_name", map.get("d_province_name"));
				tmpmap.put("s_city_code", map.get("d_city_code"));
				tmpmap.put("s_city_name", map.get("d_city_name"));

				tmpmap.put("dip", map.get("sip"));
				tmpmap.put("d_country_code", map.get("s_country_code"));
				tmpmap.put("d_country_name", map.get("s_country_name"));
				tmpmap.put("d_province_code", map.get("s_province_code"));
				tmpmap.put("d_province_name", map.get("s_province_name"));
				tmpmap.put("d_city_code", map.get("s_city_code"));
				tmpmap.put("d_city_name", map.get("s_city_name"));
				tmpmap.put("time", map.get("time"));

			} else {
				tmpmap.put("msg_title", map.get("msg_title"));
				tmpmap.put("msg_data", map.get("msg_data"));
				msglist.add(tmpmap);

				tmpmap = new HashMap<String, String>();
				tmpmap.put("sip", map.get("sip"));
				tmpmap.put("s_country_code", map.get("s_country_code"));
				tmpmap.put("s_country_name", map.get("s_country_name"));
				tmpmap.put("s_province_code", map.get("s_province_code"));
				tmpmap.put("s_province_name", map.get("s_province_name"));
				tmpmap.put("s_city_code", map.get("s_city_code"));
				tmpmap.put("s_city_name", map.get("s_city_name"));

				tmpmap.put("dip", map.get("dip"));
				tmpmap.put("d_country_code", map.get("d_country_code"));
				tmpmap.put("d_country_name", map.get("d_country_name"));
				tmpmap.put("d_province_code", map.get("d_province_code"));
				tmpmap.put("d_province_name", map.get("d_province_name"));
				tmpmap.put("d_city_code", map.get("d_city_code"));
				tmpmap.put("d_city_name", map.get("d_city_name"));
				tmpmap.put("time", map.get("time"));
			}
			tmpmap.put("count",
					map.get("count") == null ? "1" : map.get("count"));
			if (tmpmap.get("d_country_name").equalsIgnoreCase("中国")
					&& !tmpmap.get("s_country_name").equalsIgnoreCase("中国")) {
				map_last_data = tmpmap;
				String titile = tmpmap.get("time")+" "+tmpmap.get("sip") + "(" + tmpmap.get("s_country_name")+ ")"
						+ "->" +tmpmap.get("dip")+ "("+ tmpmap.get("d_country_name")+ ") "+map.get("msg_data") ;
				
				map_last_data.put("msg_title", titile);
				map_last_data.put("msg_data", map.get("msg_data"));

			}

			datalist.add(tmpmap);

			vo.setMsglist(msglist);
			vo.setDatalist(datalist);
			list.add(vo);
		}

		// 非自定义日期
		if (begins == 0 || ends == 0) {
			// update time
			set_to_redis(timekey,
					String.valueOf(System.currentTimeMillis() / 1000 - 20));

			// update last data
			if (!map_last_data.isEmpty()) {
				set_last_data_to_redis(key_last_data, map_last_data);
			}
		}

		if (list.isEmpty()) {
			Map<String, String> map = get_last_data_from_redis(key_last_data);
			if (map != null && !map.isEmpty()) {
				vo = new EventMapListVO();
				vo.setType(showtype);
				List<Map<String, String>> msglist = new ArrayList<Map<String, String>>();
				List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
				Map<String, String> tmpmap = new HashMap<String, String>();
				tmpmap.put("msg_title", map.get("msg_title"));
				tmpmap.put("msg_data", map.get("msg_data"));
				msglist.add(tmpmap);
				datalist.add(map);
				vo.setMsglist(msglist);
				vo.setDatalist(datalist);
				list.add(vo);
			}
		}

		return list;

	}

	public List<Object> get_space_map_list(String sessionid,
			Map<String, String> params) throws IOException {

		List<Object> list = new ArrayList<Object>();
		JSONObject res = null;

		// get last begintime from cache JedisUtils
		String timekey = Constant.PARAMETER_MAPLIST_BEGINTIME + "_" + sessionid;
		String begintime;
		if (params.containsKey(Constant.PARAMETER_MAPLIST_BEGINTIME)) {
			logger.debug("====begintime  in  url:"
					+ params.get(Constant.PARAMETER_MAPLIST_BEGINTIME));
		} else {
			begintime = get_time_from_redis(timekey);
			if (begintime != null && !begintime.isEmpty()) {
				params.put(Constant.PARAMETER_MAPLIST_BEGINTIME, begintime);
			}
		}

		// //// for test
		if (this.getMapDataFromFile()) {
			String showtype = params.get("showtype");
			String content = null;
			if (showtype.equalsIgnoreCase("1")) {
				content = FileUtils.readFileToString(new File(
						"/data/soft/MonitorServer/WEB-INF/单点.json"));
			} else if (showtype.equalsIgnoreCase("2")) {
				content = FileUtils.readFileToString(new File(
						"/data/soft/MonitorServer/WEB-INF/多点.json"));
			} else {
				content = FileUtils.readFileToString(new File(
						"/data/soft/MonitorServer/WEB-INF/散列.json"));
			}
			res = JSONObject.fromObject(content);
		} else {
			String url = this.getMapDataUrl();
			res = (JSONObject) JsonReader.readJsonFromUrl(false, url, params);
		}

		// ----------------------------------------------------
		if (!res.getString("status").equalsIgnoreCase("ok")) {
			return null;
		}

		if (res.containsKey(Constant.PARAMETER_MAPLIST_BEGINTIME)) {
			begintime = res.getString(Constant.PARAMETER_MAPLIST_BEGINTIME);
			// update begintime for this sessionid
			if (begintime != null && !begintime.isEmpty()) {
				set_to_redis(timekey, begintime);
			}
			// ...........................
		}

		JSONArray valueList = res.getJSONArray("data");
		String[] rans = new String[] { "1", "6", "20", "100" };
		for (Object o : valueList) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setRootClass(EventMapListVO.class);
			Map<String, Class> classMap = new HashMap<String, Class>();
			classMap.put("datalist", Map.class);
			jsonConfig.setClassMap(classMap);

			EventMapListVO vo = (EventMapListVO) JSONObject.toBean(
					(JSONObject) o, jsonConfig);
			List<Map<String, String>> datalist = vo.getDatalist();
			for (Map<String, String> map : datalist) {
				String sip = map.get("sip");
				IpInfo info = IPParserLoad.parseIP(sip);
				map.put("s_country_name", info.getCountry());
				map.put("s_country_code", info.getCountry_code());
				map.put("s_province_code", info.getProvince_code());
				map.put("s_province_name", info.getProvince());
				map.put("s_city_code", info.getCity_code());
				map.put("s_city_name", info.getCity());

				String dip = map.get("dip");
				info = IPParserLoad.parseIP(dip);
				map.put("d_country_name", info.getCountry());
				map.put("d_country_code", info.getCountry_code());
				map.put("d_province_name", info.getProvince());
				map.put("d_province_code", info.getProvince_code());
				map.put("d_city_name", info.getCity());
				map.put("d_city_code", info.getCity_code());
				// test
				Random r = new Random();
				map.put("count", rans[r.nextInt(4)]);
			}

			list.add(vo);

		}

		return list;
	}

	public List<Object> get_space_global_list(String sessionid,
			String fromTime, long limit, String fromHbase) throws Exception {

		boolean fromhbase;
		if (fromHbase != null && !fromHbase.isEmpty()) {
			fromhbase = fromHbase.equalsIgnoreCase("false") ? false : true;
		} else {
			fromhbase = this.getDataFromHbase();
		}

		List<Object> list = new ArrayList<Object>();

		long now = Calendar.getInstance().getTimeInMillis();
		long begin = now - 24 * 3600 * 1000;
		String timekey = "space_global" + "_" + sessionid;
		if (fromTime == null || fromTime.isEmpty()) {
			fromTime = get_time_from_redis(timekey);
		}
		if (fromTime != null && !fromTime.isEmpty()) {
			begin = Long.parseLong(fromTime);
		}

		// // TODO Auto-generated method stub
		if (fromhbase) {

			list = HbaseBaseOP.getInstance()
					.get_space_global_logs(begin, limit);
		} else {
			EventGlobalVO vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国上海", "36", "192.168.1.1", "80",
					"美国", "182", "14033841f4c72455", "1024");
			vo.setDeviceName("设备1");
			list.add(vo);
			vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国北京", "36", "192.168.1.1", "80",
					"日本", "82", "14033841f4c72411", "1024");
			vo.setDeviceName("设备2");
			list.add(vo);

			return list;
		}

		set_to_redis(timekey, String.valueOf(now));
		return list;
	}

	public List<Object> get_address_list() {
		List<Object> list = AddressConfig.getInstance().getAllAddressTarget();

		return list;
	}

	public List<Object> get_address_source_list() {
		List<Object> list = new ArrayList();

		Collection<Country> cs = GeoLoad.getInstance().getAllCountries();
		AddressVO config = null;
		for (Country c : cs) {
			config = new AddressVO();
			config.setCode(c.getName());
			config.setName(c.getName());
			list.add(config);
		}

		return list;
	}

	public List<Object> get_type_list() {

		List<Object> list = TypeListLoad.getInstance().getTypeList();

		return list;
	}

	/**
	 * 
	 * @param limit
	 * @param fromTime
	 * @param src_address
	 *            源单位id
	 * @param dst_address
	 *            目标单位id
	 * @return
	 * @throws Exception
	 */
	public List<Object> get_space_part_list(String sessionid, String fromTime,
			long limit, String src_country, String dst_address, String fromHbase)
			throws Exception {
		List<Object> list = new ArrayList<Object>();

		boolean fromhbase;
		if (fromHbase != null && !fromHbase.isEmpty()) {
			fromhbase = fromHbase.equalsIgnoreCase("false") ? false : true;
		} else {
			fromhbase = this.getDataFromHbase();
		}

		long now = Calendar.getInstance().getTimeInMillis();
		long begin = now - 24 * 3600 * 1000;
		String timekey = "space_part" + "_" + sessionid;
		if (fromTime == null || fromTime.isEmpty()) {
			fromTime = get_time_from_redis(timekey);
		}
		if (fromTime != null && !fromTime.isEmpty()) {
			begin = Long.parseLong(fromTime);
		}

		// ---------------------test
		if (fromhbase) {
			AddressVO avo = null;
			if (dst_address != null && !dst_address.isEmpty()) {
				avo = AddressConfig.getInstance().getAddressTargetByCode(
						dst_address);
			}
			List<String> ips = null;
			if (avo != null) {
				ips = avo.getIps();
			}
			list = HbaseBaseOP.getInstance().get_space_part_list(begin, limit,
					src_country, ips);

		} else {

			EventGlobalVO vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国上海", "36", "192.168.1.1", "80",
					"美国", "182", "14033841f4c72455", "1024");
			vo.setDeviceName("设备1");

			list.add(vo);
			vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国北京", "36", "192.168.1.1", "80",
					"日本", "82", "14033841f4c72411", "1024");
			vo.setDeviceName("设备2");
			list.add(vo);
		}

		set_to_redis(timekey, String.valueOf(now));
		return list;
	}

	public List<Object> get_space_key_list(String sessionid, String fromTime,
			long limit, String device_id, String type_ids, String src_ip,
			String dst_ip, String fromHbase) throws Exception {
		List<Object> list = new ArrayList<Object>();

		boolean fromhbase;
		if (fromHbase != null && !fromHbase.isEmpty()) {
			fromhbase = fromHbase.equalsIgnoreCase("false") ? false : true;
		} else {
			fromhbase = this.getDataFromHbase();
		}

		long now = Calendar.getInstance().getTimeInMillis();
		long begin = now - 24 * 3600 * 1000;
		String timekey = "space_key" + "_" + sessionid;
		if (fromTime == null || fromTime.isEmpty()) {
			fromTime = get_time_from_redis(timekey);
		}
		if (fromTime != null && !fromTime.isEmpty()) {
			begin = Long.parseLong(fromTime);
		}

		List<String> typenames = new ArrayList<String>();
		if (type_ids != null && !type_ids.isEmpty()) {
			for (String tp : type_ids.trim().split(",")) {
				typenames.add(TypeListLoad.getInstance().getTypeByID(tp)
						.getName());
			}
		}

		if (fromhbase) {
			list = HbaseBaseOP.getInstance().get_space_key_list(begin, limit,
					device_id, typenames, src_ip, dst_ip);
		} else {

			EventGlobalVO vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国上海", "36", "192.168.1.1", "80",
					"美国", "182", "14033841f4c72455", "1024");
			vo.setDeviceName("设备1");

			list.add(vo);
			vo = new EventGlobalVO(
					Constant.STANDARD_DATE_FORMAT.format(Calendar.getInstance()
							.getTime()), "协议异常", "1", "Win.TR.Startpage-1827",
					"192.168.2.0", "51135", "中国北京", "36", "192.168.1.1", "80",
					"日本", "82", "14033841f4c72411", "1024");
			vo.setDeviceName("设备2");
			list.add(vo);
		}
		set_to_redis(timekey, String.valueOf(now));
		return list;
	}

	public String getMapDataUrl() {

		return this.config.getConfigData(Constant.MAP_DATA_URL, "");
	}

	public boolean getDataFromHbase() {
		String s = this.config.getConfigData(Constant.DATA_FROM_HBASE, "false");
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean getMapDataFromFile() {

		String s = this.config.getConfigData(Constant.MAP_DATA_FROM_FILE,
				"false");
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean getIsReverseMapData() {

		String s = this.config.getConfigData("map_data_from_hbase_reverse",
				"true");
		logger.debug("----map_data_from_hbase_reverse:" + s);
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}

	public long getCustomMapDataSize() {

		String s = this.config.getConfigData("map_data_custom_size", "200");
		logger.debug("----map_data_custom_size:" + s);
		return Long.parseLong(s);
	}

	public BaseConfigLoad getConfig() {
		return config;
	}

	public void setConfig(BaseConfigLoad config) {
		this.config = config;
	}

}
