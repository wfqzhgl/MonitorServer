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

	public String lpop(String key) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return null;
		}
		ShardedJedis sj = null;
		String o = null;
		try {
			sj = pool.getResource();
			o = sj.lpop(key);
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
	
	public String rpop(String key) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return null;
		}
		ShardedJedis sj = null;
		String o = null;
		try {
			sj = pool.getResource();
			o = sj.rpop(key);
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
	
	
	public String lget(String key) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return null;
		}
		ShardedJedis sj = null;
		String o = null;
		try {
			sj = pool.getResource();
			o = sj.lindex(key, 0);
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
	
	public long llen(String key) {
		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return 0;
		}
		ShardedJedis sj = null;
		long o = 0;
		try {
			sj = pool.getResource();
			o = sj.llen(key);
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
	
	public void lpush(String key, String value){

		ShardedJedisPool pool = redisConfig.getInstance().getPool();
		if (pool == null) {
			return;
		}
		ShardedJedis sj = null;
		try {
			sj = pool.getResource();
			sj.lpush(key, value);
			pool.returnResource(sj);
		} catch (Exception e) {
			if (sj != null) {
				pool.returnBrokenResource(sj);
			}
			e.printStackTrace();
		} finally {
			sj = null;
		}
	
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
