package com.it.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.it.config.load.ConfigLoad;
import com.it.vo.City;
import com.it.vo.Country;
import com.it.vo.Province;

public class GeoLoad extends ConfigLoad {

	private static final GeoLoad instance = new GeoLoad();
	// key = name
	private Map<String, Country> cache_country = new HashMap<String, Country>();
	// key = country.id +"_"+name
	private Map<String, Province> cache_province = new HashMap<String, Province>();
	// key = province.id+"_"+name
	private Map<String, City> cache_city = new HashMap<String, City>();

	public static GeoLoad getInstance() {
		return instance;
	}

	
	public Collection<Country> getAllCountries(){
		return cache_country.values();
	}
	
	public Country getCountryByName(String name) {
		return cache_country.get(name);
	}

	public Province getProvinceByName(Country country, String name) {
		if(country==null){
			return  null;
		}
		return cache_province.get(country.getId() + "_" + name);
	}

	public City getCityByName(Province province, String name) {
		return cache_city.get(province.getId() + "_" + name);
	}

	@Override
	public void load() {
		logger.debug("GeoLoad Load Start...cache_country......");
		final Map<String, Country> map_co = new HashMap<String, Country>();
		String sql_co = "select * from country";
		jdbcTemplate.query(sql_co, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				Country config = new Country(rs.getInt("id"), rs
						.getString("name"), rs.getString("code"));
				map_co.put(config.getName(), config);

//				logger.debug(config.getId() + "," + config.getName());
				return null;
			}
		});
		synchronized (cache_country) {
			this.cache_country = map_co;
		}
		logger.debug("GeoLoad Load End...cache_country:"
				+ this.cache_country.size());

		//province
		logger.debug("GeoLoad Load Start...cache_province......");
		final Map<String, Province> map_p = new HashMap<String, Province>();
		String sql_p = "select a.id,a.name as name,a.code as code,b.name as cname from province a join country b on a.country_id=b.id";
		jdbcTemplate.query(sql_p, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				Country tmp = cache_country
						.get(rs.getString("cname"));
				Province config = new Province(rs.getInt("id"), rs
						.getString("name"), rs.getString("code"),tmp );
				if(tmp.getName().equalsIgnoreCase("中国")){
					map_p.put(tmp.getId()+"_"+config.getName(), config);	
				}else{
					map_p.put(tmp.getId()+"_"+(config.getCode().replaceAll("\\s+", "").toLowerCase()), config);
				}
				

//				logger.debug(config.getId() + "," + config.getName());
				return null;
			}
		});
		synchronized (cache_province) {
			this.cache_province = map_p;
		}
		logger.debug("GeoLoad Load End...cache_province:"
				+ this.cache_province.size());
		
		//city
		logger.debug("GeoLoad Load Start...cache_city......");
		final Map<String, City> map_c = new HashMap<String, City>();
		String sql_c = "select a.id,a.name,a.code,b.name as cname,b.country_id from city a join province b on a.province_id=b.id";
		jdbcTemplate.query(sql_c, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				Province tmp = cache_province
						.get(rs.getString("country_id")+"_"+rs.getString("cname"));
				City config = new City(rs.getInt("id"), rs
						.getString("name"), rs.getString("code"),tmp );
				map_c.put(tmp.getId()+"_"+config.getName(), config);

//				logger.debug(config.getId() + "," + config.getName());
				return null;
			}
		});
		synchronized (cache_city) {
			this.cache_city = map_c;
		}
		logger.debug("GeoLoad Load End...cache_city:"
				+ this.cache_city.size());


	}

}
