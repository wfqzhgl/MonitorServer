package com.it.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.it.config.load.ConfigLoad;
import com.it.vo.AddressVO;

public class AddressConfig extends ConfigLoad {

	private static final AddressConfig instance = new AddressConfig();
	private Map<String, AddressVO> cache_target = new HashMap<String, AddressVO>();

	public static AddressConfig getInstance() {
		return instance;
	}


	public AddressVO getAddressTargetByCode(String code) {
		return cache_target.get(code);
	}


	public List<Object> getAllAddressTarget() {
		List<Object> res = new ArrayList<Object>();
		for (Map.Entry<String, AddressVO> en : cache_target.entrySet()) {
			AddressVO tmp = en.getValue();
			tmp.setIps(null);
			res.add(tmp);
		}
		return res;

	}

	@Override
	public void load() {
		logger.debug("AddressConfig Load Start------------");

		final Map<String, AddressVO> map_target = new HashMap<String, AddressVO>();

		String sql1 = "select * from address_target";
		jdbcTemplate.query(sql1, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				AddressVO config = null;
				if (!map_target.containsKey(rs.getString("code"))) {
					config = new AddressVO();
					config.setId(rs.getString("id"));
					config.setCode(rs.getString("code"));
					config.setName(rs.getString("name"));
					List<String> tmp = new ArrayList<String>();
					tmp.addAll(Arrays.asList(rs.getString("ips").split(",")));
					config.setIps(tmp);
					map_target.put(config.getCode(), config);
					// logger.debug(config.getCode() + "," + config.getName());
					return null;
				}

				config = map_target.get(rs.getString("code"));
				config.getIps().addAll(
						Arrays.asList(rs.getString("ips").split(",")));
				return null;

			}
		});

		synchronized (cache_target) {
			this.cache_target = map_target;
		}
		logger.debug("cache_target size= " + this.cache_target.size());
		logger.debug("AddressConfig Load End--------------------");
	}

}
