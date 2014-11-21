package com.it.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.it.config.BaseConfigLoad;
import com.it.config.TypeListLoad;
import com.it.hadoop.HbaseBaseOP;
import com.it.util.Constant;
import com.it.vo.EventGlobalVO;
import com.it.vo.TypeGlobalVO;

@Repository
public class EventGlobalDao extends BaseDao {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<Object> get_global_list(String type_id, String level,
			String src_ip, String dst_ip, String begin, String end, long limit,boolean fromHbase) {
		List<Object> list = new ArrayList<Object>();

		if (fromHbase) {
			List<String> type_list = null;
			if (!type_id.isEmpty()) {
				type_list = new ArrayList<String>();
				for (String tid : type_id.split(",")) {
					// 根据类型id获取名称,hbase存储的是名称
					type_list.add(TypeListLoad.getInstance().getTypeByID(tid)
							.getName());
				}
			}

			List<String> src_ip_list = null;
			if (!src_ip.isEmpty()) {
				src_ip_list = Arrays.asList(src_ip.split(","));
			}

			List<String> dst_ip_list = null;
			if (!dst_ip.isEmpty()) {
				dst_ip_list = Arrays.asList(dst_ip.split(","));
			}

			long lbegin = System.currentTimeMillis() - 3600000;
			long lend = System.currentTimeMillis();
			if (!begin.isEmpty()) {
				try {
					lbegin = Constant.DEFAULT_DATE_HOUR_FORMAT.parse(begin)
							.getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!end.isEmpty()) {
				try {
					lend = Constant.DEFAULT_DATE_HOUR_FORMAT.parse(end)
							.getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				list = HbaseBaseOP.getInstance().get_global_list(type_list, level, src_ip_list,
						dst_ip_list, lbegin, lend, limit);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

			TypeGlobalVO vo = new TypeGlobalVO("1", "协议异常", "协议异常", "1",
					"192.168.1.1", "8280", "美国", "192.168.1.2", "80", "中国 北京",
					"未处理", "2014-09-09 09:09:09",2);
			vo.setDeviceName("设备1");
			list.add(vo);
			vo = new TypeGlobalVO("8", "web漏洞攻击", "web漏洞攻击", "2",
					"192.168.1.1", "8280", "美国", "192.168.1.2", "80", "中国 天津",
					"未处理", "2014-09-09 09:09:09", 2);
			vo.setDeviceName("设备2");
			list.add(vo);
			vo = new TypeGlobalVO("3", "恶意攻击", "恶意攻击", "3", "192.168.1.5",
					"8280", "日本", "192.168.1.2", "80", "中国 河北", "已处理", "2014-09-09 09:09:09", 2);
			vo.setDeviceName("设备3");
			list.add(vo);

			vo = new TypeGlobalVO("4", "蠕虫攻击", "蠕虫攻击", "2", "192.168.1.1",
					"8280", "英国", "192.168.1.2", "80", "中国  安徽", "未处理", "2014-09-09 09:09:09", 2);
			list.add(vo);
			vo = new TypeGlobalVO("3", "恶意攻击", "恶意攻击", "3", "192.168.1.5",
					"8280", "日本", "192.168.1.2", "80", "中国 河北", "已处理", "2014-09-09 09:09:09", 2);
			list.add(vo);
			vo = new TypeGlobalVO("2", "web漏洞攻击", "web漏洞攻击", "2",
					"192.168.1.1", "8280", "美国", "192.168.1.2", "80", "中国 北京",
					"未处理",  "2014-09-09 09:09:09",2);
			list.add(vo);
			vo = new TypeGlobalVO("3", "恶意攻击", "恶意攻击", "3", "192.168.1.5",
					"8280", "日本", "192.168.1.2", "80", "中国 河北", "已处理", "2014-09-09 09:09:09", 2);
			list.add(vo);
			vo = new TypeGlobalVO("9", "加密传输", "加密传输", "2", "192.168.1.1",
					"8280", "美国", "192.168.1.2", "80", "中国 天津", "未处理", "2014-09-09 09:09:09", 2);
			list.add(vo);
			vo = new TypeGlobalVO("5", "DDOS攻击", "DDOS攻击", "3", "192.168.1.5",
					"8280", "日本", "192.168.1.2", "80", "中国 江西", "已处理", "2014-09-09 09:09:09", 2);
			vo.setDeviceName("设备5");
			list.add(vo);
		}
		return list;

	}

	public List<Object> get_global_flow_list() {
		List<Object> list = new ArrayList<Object>();

		return list;
	}
	
	public boolean getDataFromHbase() {
		String s = BaseConfigLoad.getInstance().getConfigData(Constant.DATA_FROM_HBASE, "false");
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}

}
