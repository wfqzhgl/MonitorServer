package com.it.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.jdbc.core.RowMapper;

import com.it.config.load.ConfigLoad;
import com.it.hadoop.HbaseBaseOP;
import com.it.util.Constant;
import com.it.util.JsonReader;
import com.it.vo.DeviceVO;

public class DeviceConfigLoad extends ConfigLoad{
	
	private static final DeviceConfigLoad instance =new DeviceConfigLoad();
	
	public static DeviceConfigLoad getInstance(){
		return instance;
	}
	private Map<String,DeviceVO> cache =new HashMap<String, DeviceVO>();
	
	public DeviceVO getDeviceByID(String id){
		return this.cache.get(id);
	}
	
	
	public List<String> getLogHtableNames(){
		List<String> names = new ArrayList<String>();
		for(String key:this.cache.keySet()){
			names.add(key+"_log");
		}
		return names;
	}
	
	public Set<String> getDeviceSet(){
		return this.cache.keySet();
	}
	
	public Collection<DeviceVO> getDevices(){
		return cache.values();
	}
	
	
	public List<Object> getDeviceList(){
		return new ArrayList(cache.values());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
        logger.debug("DeviceConfigLoad Load Start...");
        
        String fromdb = BaseConfigLoad.getInstance().getConfigData(Constant.DEVICE_LIST_FROM_DB, "false");
        final Map<String, DeviceVO> map = new HashMap<String, DeviceVO>();
        if(fromdb.equalsIgnoreCase("true")){
        	String sql ="select * from mac";
            jdbcTemplate.query(sql, new RowMapper<byte[]>() {
                public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
                	DeviceVO config = new DeviceVO();
                    config.setId(rs.getString("id"));
                    config.setName(rs.getString("name"));
                    config.setGuid(rs.getString("guid"));
                    config.setIp(rs.getString("ip"));
                    config.setPid(rs.getString("pid"));
                    config.setDetail(rs.getString("detail"));
                    
                    if(HbaseBaseOP.getDataFromHbase()){
                    	if(!HbaseBaseOP.getInstance().isTableExist(config.getGuid()+"_log")){
                    		logger.info("========device not in hbase:"+config.getGuid());
                    	}else{
                    		logger.info("-----------device in hbase:"+config.getGuid());
                    	}
                    }
                    
                    map.put(config.getGuid(), config);
                    return null;
                }
            });        	
        }else{
        	String url = BaseConfigLoad.getInstance().getConfigData(Constant.DEVICE_LIST_URL, "");
    		try {
    			JSONArray res = (JSONArray) JsonReader.readJsonFromUrl(true, url,
    					null);
    			for (int i = 0; i < res.size(); i++) {
    				JSONObject rs = res.getJSONObject(i);
    				DeviceVO config = new DeviceVO();
    				config.setId(rs.getString("id"));
                    config.setName(rs.getString("name"));
                    config.setGuid(rs.getString("guid"));
                    config.setIp(rs.getString("ip"));
                    config.setPid(rs.getString("pid"));
                    config.setDetail(rs.getString("detail"));
                    map.put(config.getGuid(), config);
    			}

    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }

        
        synchronized(cache) {
            this.cache = map;
        }

        logger.debug("DeviceConfigLoad Load End..." +  this.cache);
    }

	
	public Set<String> get_device_list_by_connection(Connection conn) {
		Set<String> list = new HashSet<String>();
        logger.debug("get_device_list_by_connection  Start...");
        
        String fromdb = BaseConfigLoad.getInstance().getConfigData(Constant.DEVICE_LIST_FROM_DB, "true");
        final Map<String, DeviceVO> map = new HashMap<String, DeviceVO>();
        if(fromdb.equalsIgnoreCase("true")){
        	String sql ="select * from mac";
        	try {
				PreparedStatement pstmt1  = conn.prepareStatement(sql);
				ResultSet rs = pstmt1.executeQuery();
				while(rs.next()){
					list.add(rs.getString("guid"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	String url = BaseConfigLoad.getInstance().getConfigData(Constant.DEVICE_LIST_URL, "");
    		try {
    			JSONArray res = (JSONArray) JsonReader.readJsonFromUrl(true, url,
    					null);
    			for (int i = 0; i < res.size(); i++) {
    				JSONObject rs = res.getJSONObject(i);
    				list.add(rs.getString("guid"));
    			}

    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }

        logger.debug("get_device_list_by_connection  End...");
        return  list;
    }
}
