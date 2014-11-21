package com.it.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.it.config.load.ConfigLoad;

public class BaseConfigLoad extends ConfigLoad {

	private static final BaseConfigLoad instance = new BaseConfigLoad();

	public static BaseConfigLoad getInstance() {
		return instance;
	}

	private Map<String, String> cache = new HashMap<String, String>();

	public String getConfigData(String key, String defaultValue) {
		String value = cache.get(key);
		if (value == null || value.isEmpty()) {
			value = defaultValue;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load() {
		try {
			logger.debug("BaseConfig Load properties begin...");

			Configuration configuration = new PropertiesConfiguration(
					propertiesfilename);
			Iterator<String> it = configuration.getKeys();
			while (it.hasNext()) {
				String key = it.next();
				cache.put(key, configuration.getString(key));
			}
			logger.debug("BaseConfig Load properties end ...");
		} catch (ConfigurationException e) {
			logger.debug("BaseConfig Load properties Error...");
			e.printStackTrace();
		}

	}

}
