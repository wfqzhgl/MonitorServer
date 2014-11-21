package com.it.cache;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.it.config.RedisConfig;

public class JedisUtils {
	private static final JedisUtils instance = new JedisUtils();
	private static final RedisConfig redisConfig = RedisConfig.getInstance();

	public static JedisUtils getInstance() {
		return instance;
	}

	public String get(String key) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return null;
		}
		ShardedJedis sj = null;
		String o = null;
		try {
			sj = pool.getResource();
			o = sj.get(key);
			pool.returnResource(sj);
		} catch (Exception e) {
			if (sj != null) {
				pool.returnBrokenResource(sj);
			}
			e.printStackTrace();
		} finally {
			sj = null;
		}
		return o;
	}

	public String set(String key, String value) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return null;
		}
		ShardedJedis sj = null;
		String o = null;
		try {
			sj = pool.getResource();
			o = sj.set(key, value);
			pool.returnResource(sj);
		} catch (Exception e) {
			if (sj != null) {
				pool.returnBrokenResource(sj);
			}
			e.printStackTrace();
		} finally {
			sj = null;
		}
		return o;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
