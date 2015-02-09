package com.it.hadoop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import com.it.cache.JedisUtils;
import com.it.config.BaseConfigLoad;
import com.it.config.load.ConfigLoad;
import com.it.util.Constant;

public class MapDatatoRedis extends ConfigLoad {

	private static final MapDatatoRedis instance = new MapDatatoRedis();

	private MapDatatoRedis() {
	}

	public static MapDatatoRedis getInstance() {
		return instance;
	}

	@Override
	public void load() {
		logger.info("-------MapDatatoRedis Load Start...");

		try {

			if (getMap_data_from_redis()) {
				// 24小时内
				long begin = System.currentTimeMillis() - 3600 * 24 * 1000;
				List<Map<String, String>> listmap = HbaseBaseOP.getInstance()
						.get_space_origin_list_forcache(begin, 500);

				// to redis
				if (listmap == null || listmap.isEmpty()) {
					logger.debug("----listmap is empty!!!");
					return;
				}
				JSONArray o = JSONArray.fromObject(listmap);
				String json = o.toString();
				JedisUtils.getInstance().set(Constant.MAP_DATA_FROM_REDIS_KEY,
						json);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		logger.info("---------MapDatatoRedis Load End...");
	}

	public boolean getMap_data_from_redis() {

		if (!HbaseBaseOP.getDataFromHbase()) {
			logger.debug("----from hbase false.");
			return false;
		}

		String s = BaseConfigLoad.getInstance().getConfigData(
				"map_data_from_redis", "false");
		logger.debug("----map_data_from_redis:" + s);
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}
}
