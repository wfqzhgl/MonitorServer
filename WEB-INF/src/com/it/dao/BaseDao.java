package com.it.dao;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public  class  BaseDao {
	public static Logger logger = Logger.getLogger(BaseDao.class);
	
	//hbase query
	protected JdbcTemplate jdbcTemplate;
	

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	
}
