package com.it.config.load;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class ConfigLoad {
	protected static Logger logger = Logger.getLogger(ConfigLoad.class);
	protected JdbcTemplate jdbcTemplate;
	protected String propertiesfilename;

	public String getPropertiesfilename() {
		return propertiesfilename;
	}

	public void setPropertiesfilename(String propertiesfilename) {
		this.propertiesfilename = propertiesfilename;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public abstract void load();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
