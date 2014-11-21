package com.it.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.it.config.load.ConfigLoad;
import com.it.util.Utils;
import com.it.vo.FlowPortVO;
import com.it.vo.FlowProtocolVO;

public class FlowDeviceConfig extends ConfigLoad {

	private static final FlowDeviceConfig instance = new FlowDeviceConfig();
	private List<FlowPortVO> cache_port = new ArrayList<FlowPortVO>();
	private List<FlowProtocolVO> cache_protocol = new ArrayList<FlowProtocolVO>();
	private String deviceId_protocols = null;
	private String deviceId_ports = null;
	private static final String separator = "\\|";

	public static FlowDeviceConfig getInstance() {
		return instance;
	}

	
	public String getNameByPortcode(String code){
		for(FlowPortVO vo:cache_port){
			if(vo.getCode().equalsIgnoreCase(code)){
				return vo.getName();
			}
		}
		
		return code;
	}
	
	public String getProtocolNameByIndex(int i){
		if(i>=cache_protocol.size()){
			return String.valueOf(i);
		}
		return cache_protocol.get(i).getCode();
	}
	
	public String getDeviceId_protocols() {
		return deviceId_protocols;
	}



	public void setDeviceId_protocols(String deviceId_protocols) {
		this.deviceId_protocols = deviceId_protocols;
	}



	public String getDeviceId_ports() {
		return deviceId_ports;
	}



	public void setDeviceId_ports(String deviceId_ports) {
		this.deviceId_ports = deviceId_ports;
	}



	public String getHTableNameProtocols() {
		return deviceId_protocols+"_tk";
	}
	
	public String getHTableNamePorts() {
		return deviceId_ports+"_tk";
	}
	

	public List<Object> getAllProtocols() {
		List res = new ArrayList();
		for (FlowProtocolVO en : cache_protocol) {
			res.add(en);
		}
		return res;
	}
	
	public List<Object> getAllPorts() {
		List res = new ArrayList();
		for (FlowPortVO en : cache_port) {
			res.add(en);
		}
		return res;
	}

	@Override
	public void load(){
		logger.debug("FlowDeviceConfig Load Start...");

		final List<FlowPortVO> map_port = new ArrayList<FlowPortVO>();
		final List<FlowProtocolVO> map_protocol = new ArrayList<FlowProtocolVO>();
		
		
		if (this.propertiesfilename == null
				|| this.propertiesfilename.isEmpty()) {
			logger.error("========file name empty!!!!!!!!!!");
			return;

		}

		Configuration configuration;
		try {
			configuration = new PropertiesConfiguration(
					propertiesfilename);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("=====configuration open error.");
			return;
		}
		
		this.deviceId_protocols = configuration.getString("deviceId_protocols");
		this.deviceId_ports = configuration.getString("deviceId_ports");
		String protocols = configuration.getString("protocols");
		String ports = configuration.getString("ports");
		if(protocols!=null&&!protocols.isEmpty()){
			protocols=Utils.trimStr(protocols, separator);
			for(String pro:protocols.split(separator)){
				map_protocol.add(new FlowProtocolVO(pro,pro));
			}
		}
		
		if(ports!=null&&!ports.isEmpty()){
			ports=Utils.trimStr(ports, separator);
			for(String port:ports.split(separator)){
				if(port.contains(":")){
					map_port.add(new FlowPortVO(port.split(":")[0],port.split(":")[1]));
				}
			}
		}
		
		
		
		synchronized (cache_port) {
			this.cache_port = map_port;
		}
		synchronized (cache_protocol) {
			this.cache_protocol = map_protocol;
		}

		logger.debug("FlowDeviceConfig Load End..." );
	}

}
