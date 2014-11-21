package com.it.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it.config.AddressConfig;
import com.it.config.TypeListLoad;
import com.it.dao.EventGlobalDao;
import com.it.dao.EventSpaceDao;
import com.it.dao.EventTimeDao;
import com.it.dao.EventTypeDao;
import com.it.hadoop.HbaseBaseOP;
import com.it.hadoop.test.HbaseTest;
import com.it.util.Utils;
import com.it.vo.AddressVO;

@Service("eventService")
public class EventService {

	private static final EventService instance = new EventService();

	public static EventService getInstance() {
		return instance;
	}

	@Autowired
	private EventSpaceDao eventSpaceDao;
	private EventTimeDao eventTimeDao;
	private EventTypeDao eventTypeDao;
	private EventGlobalDao eventGlobalDao;

	public EventGlobalDao getEventGlobalDao() {
		return eventGlobalDao;
	}

	public void setEventGlobalDao(EventGlobalDao eventGlobalDao) {
		this.eventGlobalDao = eventGlobalDao;
	}

	public EventSpaceDao getEventSpaceDao() {
		return eventSpaceDao;
	}

	public void setEventSpaceDao(EventSpaceDao eventSpaceDao) {
		this.eventSpaceDao = eventSpaceDao;
	}

	public EventTimeDao getEventTimeDao() {
		return eventTimeDao;
	}

	public void setEventTimeDao(EventTimeDao eventTimeDao) {
		this.eventTimeDao = eventTimeDao;
	}

	public EventTypeDao getEventTypeDao() {
		return eventTypeDao;
	}

	public void setEventTypeDao(EventTypeDao eventTypeDao) {
		this.eventTypeDao = eventTypeDao;
	}

	public List<Object> get_space_map_list(String sessionid,
			Map<String, String> params) throws IOException {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_space_map_list(sessionid, params);
	}

	public List<Object> get_space_global_list(String sessionid,String fromTime, long limit, String fromHbase)
			throws Exception {
		// TODO Auto-generated method stub
		
		return eventSpaceDao.get_space_global_list(sessionid,fromTime, limit,fromHbase.equalsIgnoreCase("false")?false:true);
	}

	public List<Object> get_address_list() {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_address_list();
	}

	public List<Object> get_address_source_list() {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_address_source_list();
	}
	
	public List<Object> get_type_list() {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_type_list();
	}

	public List<Object> get_space_part_list(String sessionid,String fromTime, long limit,
			String src_address, String dst_address,String fromHbase) throws Exception {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_space_part_list(sessionid,fromTime, limit, src_address,
				dst_address,fromHbase.equalsIgnoreCase("false")?false:true);
	}

	public List<Object> get_space_key_list(String sessionid,String fromTime, long limit,
			String device_id, String type_ids, String src_ip, String dst_ip,String fromHbase)
			throws Exception {
		// TODO Auto-generated method stub
		return eventSpaceDao.get_space_key_list(sessionid,fromTime, limit, device_id,
				type_ids, src_ip, dst_ip,fromHbase.equalsIgnoreCase("false")?false:true);
	}

	public List<Object> get_type_sorttype_list(String begin, String end,
			long limit) {
		// TODO Auto-generated method stub
		return eventTypeDao.get_type_sorttype_list(begin, end, limit);
	}

	public List<Object> get_type_sortdst_list(String begin, String end,
			long limit) {
		// TODO Auto-generated method stub
		return eventTypeDao.get_type_sortdst_list(begin, end, limit);
	}

	public List<Object> get_type_sortsrc_list(String begin, String end,
			long limit) {
		// TODO Auto-generated method stub
		return eventTypeDao.get_type_sortsrc_list(begin, end, limit);
	}

	public List<Object> get_time_list(String begin, String end, String x,
			String type_id, String address, String ip) {
		// TODO Auto-generated method stub
		return eventTimeDao.get_time_list(begin, end, x, type_id.trim()
				.isEmpty() ? null : Arrays.asList(type_id.trim().split(",")),
				margeIps(address, ip));
	}

	public List<Object> get_time_compare_list(String begin, String end,
			String x, String type_id, String address, String ip)
			throws ParseException {
		// TODO Auto-generated method stub
		return eventTimeDao.get_time_compare_list(begin, end, x, type_id.trim()
				.isEmpty() ? null : Arrays.asList(type_id.split(",")),
				margeIps(address, ip));
	}

	public List<Object> get_time_sample_list(String x, List<String> range,
			String type_id, String address, String ip) throws ParseException {
		// TODO Auto-generated method stub
		return eventTimeDao.get_time_sample_list(x, range, type_id.trim()
				.isEmpty() ? null : Arrays.asList(type_id.split(",")),
				margeIps(address, ip));
	}

	public List<Object> get_global_list(String type_id, String level,
			String src_ip, String dst_ip, String begin, String end, long limit,String fromHbase) {
		// TODO Auto-generated method stub
		src_ip = Utils.trimStr(src_ip, ",");
		dst_ip = Utils.trimStr(dst_ip, ",");
		type_id = Utils.trimStr(type_id, ",");
		
		return eventGlobalDao.get_global_list(type_id,level,src_ip,dst_ip,begin,end,limit,fromHbase.equalsIgnoreCase("false")?false:true);
	}

	public List<Object> get_global_flow_list() {
		// TODO Auto-generated method stub
		return eventGlobalDao.get_global_flow_list();
	}

	private List<String> margeIps(String address, String ip) {
		List<String> res = new ArrayList<String>();
		AddressVO vo = AddressConfig.getInstance().getAddressTargetByCode(address);
		if (vo != null) {
			res.addAll(vo.getIps());
		}
		if (ip != null && !ip.isEmpty()) {
			res.addAll(Arrays.asList(ip.split(",")));
		}
		return res;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Arrays.asList("".split(",")));
		System.out.println(Arrays.asList("".split(",")).isEmpty());
		System.out.println(Arrays.asList("".split(",")).size());
		System.out.println("".isEmpty());

	}

	public void testHbase() {
		// TODO Auto-generated method stub
		try {
			HbaseBaseOP.getInstance().htest();
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
