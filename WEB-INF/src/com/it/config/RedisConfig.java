package com.it.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import com.it.config.load.ConfigLoad;

public class RedisConfig extends ConfigLoad {

	private static final int REDIS_CONNECTION_TIMEOUT = 10000;

	private static final RedisConfig instance = new RedisConfig();

	private RedisConfig() {
	}

	public static RedisConfig getInstance() {
		return instance;
	}

	private ShardedJedisPool pool = null;

	@Override
	public void load() {
		logger.info("RedisConfig Load Start...");

		if (pool != null) {
			this.pool.destroy();
		}
		try {
			this.pool = loadFromFile();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("RedisConfig Load End...");
	}

	private ShardedJedisPool loadFromFile() throws ConfigurationException {
		if (this.propertiesfilename == null
				|| this.propertiesfilename.isEmpty()) {
			logger.error("file name empty!!!!!!!!!!");
			return null;

		}

		Configuration configuration = new PropertiesConfiguration(
				propertiesfilename);

		List<JedisShardInfo> lst = new ArrayList<JedisShardInfo>();
		try {

			Iterator<String> it = configuration.getKeys();
			while (it.hasNext()) {
				String key = it.next();
				String port = configuration.getString(key);
				lst.add(new JedisShardInfo(key, Integer.parseInt(port),
						REDIS_CONNECTION_TIMEOUT, key + ":" + port));
				logger.debug("load redis from file:" + key + ":" + port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Config poolConfig = getSharededJedisPoolConfig();

		ShardedJedisPool shardedJedisPool = new ShardedJedisPool(poolConfig,
				lst);

		return shardedJedisPool;
	}

	private Config getSharededJedisPoolConfig() {
		// redis池中的待分配redis链接
		Config poolConfig = new Config();
		// controls the maximum number of objects that can be allocated by the
		// pool (checked out to clients, or idle awaiting checkout) at a given
		// time.When non-positive, there is no limit to the number of objects
		// that can be managed by the pool at one time. When maxActive is
		// reached, the pool is said to be exhausted.
		poolConfig.maxActive = 128;
		// controls the maximum number of objects that can sit idle in the pool
		// at any time. When negative, there is no limit to the number of
		// objects that may be idle at one time.
		poolConfig.maxIdle = 32;
		// borrowObject() will block (invoke Object.wait()) until a new or idle
		// object is available. If a positive maxWait value is supplied, then
		// borrowObject() will block for at most that many milliseconds, after
		// which a NoSuchElementException will be thrown. If maxWait is
		// non-positive, the borrowObject() method will block indefinitely.
		poolConfig.maxWait = 180000;
		poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		// indicates how long the eviction thread should sleep before "runs" of
		// examining idle objects. When non-positive, no eviction thread will be
		// launched.
		poolConfig.timeBetweenEvictionRunsMillis = 7000;
		// specifies the minimum amount of time that an object may sit idle in
		// the pool before it is eligible for eviction due to idle time. When
		// non-positive, no object will be dropped from the pool due to idle
		// time alone. This setting has no effect unless
		// timeBetweenEvictionRunsMillis > 0.
		poolConfig.minEvictableIdleTimeMillis = 14000;
		// determines the number of objects examined in each run of the idle
		// object evictor.
		poolConfig.numTestsPerEvictionRun = 8;
		// indicates whether or not idle objects should be validated using the
		// factory's PoolableObjectFactory.validateObject(java.lang.Object)
		// method. Objects that fail to validate will be dropped from the pool.
		// This setting has no effect unless timeBetweenEvictionRunsMillis > 0.
		// The default setting for this parameter is false.
		poolConfig.testWhileIdle = true;// false;// true;
		// the pool will attempt to validate each object before it is returned
		// from the borrowObject() method.Objects that fail to validate will be
		// dropped from the pool, and a different object will be borrowed.
		poolConfig.testOnBorrow = false;// false;// true;
		// the pool will attempt to validate each object before it is returned
		// to the pool in the returnObject(java.lang.Object) method.Objects that
		// fail to validate will be dropped from the pool.
		poolConfig.testOnReturn = false;// true;
		return poolConfig;
	}

	public ShardedJedisPool getPool() {
		return pool;
	}

	public void setPool(ShardedJedisPool pool) {
		this.pool = pool;
	}

}
