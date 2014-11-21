package com.it.config;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.jdbc.core.RowMapper;

import com.it.config.load.ConfigLoad;
import com.it.util.Constant;
import com.it.util.JsonReader;
import com.it.vo.EventTypeVO;

public class TypeListLoad extends ConfigLoad {

	private static final TypeListLoad instance = new TypeListLoad();

	public static TypeListLoad getInstance() {
		return instance;
	}

	private Map<String, EventTypeVO> cache = new HashMap<String, EventTypeVO>();
	private Map<String, String> cache_level = new HashMap<String, String>();

	public EventTypeVO getTypeByID(String id) {
		return this.cache.get(id);
	}
	
	public String getNameByID(String code){
		EventTypeVO vo = this.cache.get(code);
		if(vo==null){
			return null;
		}
		return vo.getName();
	}
	
	public EventTypeVO getTypeByName(String name){
		return this.getTypeByID(this.getCodeByName(name));
	}
	
	public String getCodeByName(String name){
		for(EventTypeVO vo:cache.values()){
			if(vo.getName().equalsIgnoreCase(name)){
				return vo.getCode();
			}
		}
		return "";
	}

	public List<Object> getTypeList() {
		List<Object> res = new ArrayList<Object>();
		//全部放第一个
		EventTypeVO all=null;
		for (Map.Entry<String, EventTypeVO> en : cache.entrySet()) {
			EventTypeVO tmp = en.getValue();
			if(tmp.getName().equalsIgnoreCase("全部")){
				all=tmp;
				continue;
			}
			res.add(tmp);
		}
		if(all!=null){
			res.add(0, all);
		}
		return res;
	}

	 
	public Map<String, EventTypeVO> getTypeMap(){
		return  cache;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		logger.debug("TypeListLoad Load Start...");

		if (this.propertiesfilename == null
				|| this.propertiesfilename.isEmpty()) {
			logger.error("level file name empty!!!!!!!!!!");
		}

		try {
			Configuration configuration = new PropertiesConfiguration(
					propertiesfilename);
			Iterator<String> it = configuration.getKeys();
			while (it.hasNext()) {
				String key = it.next();
				cache_level.put(key, configuration.getString(key));
			}

		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String fromdb = BaseConfigLoad.getInstance().getConfigData(
				Constant.TYPE_LIST_FROM_DB, "false");
		final Map<String, EventTypeVO> map = new HashMap<String, EventTypeVO>();
		if (fromdb.equalsIgnoreCase("true")) {
			String sql = "select id,type,name,detail from event_type";
			jdbcTemplate.query(sql, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					EventTypeVO config = new EventTypeVO(rs.getString("id"), rs
							.getString("type"), rs.getString("name"), rs
							.getString("detail"));
					String lev = cache_level.containsKey(rs.getString("name")) ? cache_level
							.get(rs.getString("name")) : "1";
							config.setLevel(lev);
					map.put(config.getCode(), config);
					return null;
				}
			});
		} else {
			String url = BaseConfigLoad.getInstance().getConfigData(
					Constant.TYPE_LIST_URL, "");
			try {
				JSONArray res = (JSONArray) JsonReader.readJsonFromUrl(true,
						url, null);
				for (int i = 0; i < res.size(); i++) {
					JSONObject o = res.getJSONObject(i);
					EventTypeVO config = new EventTypeVO(
							o.getString("id") == null ? "" : o.getString("id"),
							o.getString("type"), o.getString("name"),
							o.getString("detail") == null ? "" : o
									.getString("detail"));
					String lev = cache_level.containsKey(o.getString("name")) ? cache_level
							.get(o.getString("name")) : "1";
							config.setLevel(lev);
					map.put(config.getCode(), config);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		synchronized (cache) {
			this.cache = map;
		}

		logger.debug("TypeListLoad Load End..." + this.cache);
	}

}
