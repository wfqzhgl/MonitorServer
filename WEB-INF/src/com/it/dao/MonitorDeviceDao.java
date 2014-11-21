package com.it.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.it.config.DeviceConfigLoad;
import com.it.hadoop.HbaseBaseOP;
import com.it.vo.DeviceStatusVO;

@Repository
public class MonitorDeviceDao extends BaseDao {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<Object> get_device_status_list(String device_id,boolean fromHbase) {
		Random r = new Random();

		List<Object> list = new ArrayList<Object>();
		Set<String> devices = new HashSet<String>();
		if (device_id == null || device_id.isEmpty()) {
			devices = DeviceConfigLoad.getInstance().getDeviceSet();
		} else {
			for (String id : device_id.split(",")) {
				if (id.isEmpty()) {
					continue;
				}
				devices.add(id);
			}
		}

		if (fromHbase) {
			list = HbaseBaseOP.getInstance().get_device_status_list(devices);
		} else {
			DeviceStatusVO vo;
			for (String id : devices) {
				vo = new DeviceStatusVO(id, "192.168.1.1", String.valueOf(r
						.nextInt(100)), "2048",
						String.valueOf(r.nextInt(2048)), "4096",
						String.valueOf(r.nextInt(2048)), String.valueOf(r
								.nextInt(2048)),
						String.valueOf(r.nextInt(2048)), String.valueOf(System
								.currentTimeMillis()));
				list.add(vo);
			}

		}

		return list;
	}

	public List<Object> get_device_list() {
		// TODO Auto-generated method stub
		return DeviceConfigLoad.getInstance().getDeviceList();
	}

}
