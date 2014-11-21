package com.it.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it.dao.EventGlobalDao;
import com.it.dao.EventSpaceDao;
import com.it.dao.EventTimeDao;
import com.it.dao.EventTypeDao;
import com.it.dao.MonitorDeviceDao;
import com.it.dao.MonitorFlowDao;

@Service("monitorService")
public class MonitorService {

	private static final MonitorService instance = new MonitorService();

	public static MonitorService getInstance() {
		return instance;
	}

	private MonitorDeviceDao monitorDeviceDao;
	private MonitorFlowDao monitorFlowDao;


	public MonitorDeviceDao getMonitorDeviceDao() {
		return monitorDeviceDao;
	}

	public void setMonitorDeviceDao(MonitorDeviceDao monitorDeviceDao) {
		this.monitorDeviceDao = monitorDeviceDao;
	}

	public MonitorFlowDao getMonitorFlowDao() {
		return monitorFlowDao;
	}

	public void setMonitorFlowDao(MonitorFlowDao monitorFlowDao) {
		this.monitorFlowDao = monitorFlowDao;
	}


	public List<Object> get_device_status_list(String device_id,String fromHbase) {
		// TODO Auto-generated method stub
		return monitorDeviceDao.get_device_status_list(device_id,fromHbase.equalsIgnoreCase("false")?false:true);
	}

	public List<Object> get_flow_sum_list(String range) {
		// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
		// TODO Auto-generated method stub
		return monitorFlowDao.get_flow_sum_list(range);
	}
	
	public List<Object> get_flow_protocol_list(String range, String protocols) {
		// TODO Auto-generated method stub
		return monitorFlowDao.get_flow_protocol_list(range,protocols);
	}
	
	public List<Object> get_flow_port_list(String range, String ports) {
		// TODO Auto-generated method stub
		return monitorFlowDao.get_flow_port_list(range,ports);
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public List<Object> get_port_list() {
		// TODO Auto-generated method stub
		return monitorFlowDao.get_port_list();
	}

	public List<Object> get_protocol_list() {
		// TODO Auto-generated method stub
		return monitorFlowDao.get_protocol_list();
	}

	public List<Object> get_device_list() {
		// TODO Auto-generated method stub
		return monitorDeviceDao.get_device_list();
	}

}
