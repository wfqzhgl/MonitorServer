package com.it.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public interface Constant {
	String CONSTANT_TEST ="test";
	String LIMIT_KEY = "limit";
	String LIMIT_DEFAULT = "20";
	String PARAMETER_NAME_MODEL ="m";
	String PARAMETER_NAME_FUNCTION ="f";
	String PARAMETER_NAME_USERNAME ="u";
	String PARAMETER_NAME_PASSWD ="p";
	String PARAMETER_NAME_PASSWD1 ="p1";
	String PARAMETER_NAME_PASSWD2 ="p2";
	String PARAMETER_MAPLIST_SHOWTYPE="showtype";
	String PARAMETER_MAPLIST_EVENTTYPE="eventtype";
	String MAP_DATA_URL="MAP_DATA_URL";
	String TYPE_LIST_URL="TYPE_LIST_URL";
	String DEVICE_LIST_URL = "DEVICE_LIST_URL";
	String TYPE_LIST_FROM_DB = "TYPE_LIST_FROM_DB";
	String MAP_DATA_FROM_FILE = "MAP_DATA_FROM_FILE";
	String DEVICE_LIST_FROM_DB = "DEVICE_LIST_FROM_DB";
	String SESSIONID = "sessionid";
	DateFormat DEFAULT_YEAR_FORMAT = new SimpleDateFormat("yyyy");
	DateFormat DEFAULT_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
	DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat DEFAULT_DATE_HOUR_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH");
	DateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat DEFAULT_WEEK_FORMAT = new SimpleDateFormat("yyyy-ww");
	String HBASE_PREFIX_MS_SRC = "HBASE_PREFIX_MS_SRC";
	String HBASE_PREFIX_MS_DST = "HBASE_PREFIX_MS_DST";
	Text MAP_KEY_SIP = new Text("MAP_KEY_SIP");
	Text MAP_KEY_DIP = new Text("MAP_KEY_DIP");
	Text MAP_KEY_DATE = new Text("MAP_KEY_DATE");
	Text MAP_KEY_HOUR = new Text("MAP_KEY_HOUR");
	Text MAP_KEY_WEEK = new Text("MAP_KEY_WEEK");
	Text MAP_KEY_MONTH = new Text("MAP_KEY_MONTH");
	Text MAP_KEY_YEAR = new Text("MAP_KEY_YEAR");
	Text MAP_KEY_EVENT_TYPE = new Text("MAP_KEY_EVENT_TYPE");
	
	String SRC_ADDRESS = "src_address";
	String DST_ADDRESS = "dst_address";
	String DATA_FROM_HBASE = "DATA_FROM_HBASE";
	String MONITOR_DATA_FROM_HBASE = "MONITOR_DATA_FROM_HBASE";
	String DEVICE_ID_KEY = "device_id";
	String TYPE_ID_KEY = "type";
	String SRC_IP_KEY = "src_ip";
	String DST_IP_KEY = "dst_ip";
	String PARAMETER_MAPLIST_BEGINTIME = "begintime";
	String QUERY_BEGIN = "begin";
	String QUERY_END = "end";
	String QUERY_PROTOCOLS = "protocols";
	String QUERY_RANGE = "range";
	String QUERY_PORTS = "ports";
	String QUERY_X = "x";
	String QUERY_IP = "ip";
	String QUERY_ADDRESS_ID = "address";
	String QUERY_PAGE = "page";
	String QUERY_ROWS = "rows";
	String QUERY_LEVEL = "level";
	String PIE_SHOW_COUNT = "pie_show_count";
	String PARAMETER_NAME_IS_ADMIN = "admin";
	
	static final String SQL_DEL_SRC_HOURLY = "DELETE FROM `attack_srcstat_hourly` WHERE date=? and hour=?;";
	static final String SQL_DEL_DST_HOURLY = "DELETE FROM `attack_dststat_hourly` WHERE date=? and hour=?;";
	static final String SQL_DEL_PROTOCOL_HOURLY = "DELETE FROM `flow_protocol_hourly` WHERE date=? and hour=?;";
	static final String SQL_DEL_PORT_HOURLY = "DELETE FROM `flow_port_hourly` WHERE date=? and hour=?;";
	static final String SQL_SRC_HOURLY = "INSERT INTO `attack_srcstat_hourly` ( "
			+ "`year` , `month` , `week` , `date` , `hour` ,`datehour`,`lip`, `sip` , "
			+ "`country`, `province`, "
			+ "`city`, `type`,`code`,`count`) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	static final String SQL_DST_HOURLY = "INSERT INTO `attack_dststat_hourly` ( "
			+ "`year` , `month` , `week` , `date` , `hour` ,`datehour`, `lip`, `dip` , "
			+ "`country`, `province`, "
			+ "`city`, `type`,`code`,`count`) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	static final String SQL_PROTOCOL_HOURLY = "INSERT INTO `flow_protocol_hourly` ( "
			+ "`year` , `month` , `week` , `date` , `hour` ,`datehour`, `protocol`, `flow` , "
			+ "`updateTime`) "
			+ "VALUES(?,?,?,?,?,?,?,?,now());";
	static final String SQL_PORT_HOURLY = "INSERT INTO `flow_port_hourly` ( "
			+ "`year` , `month` , `week` , `date` , `hour` ,`datehour`, `port`, `flow` , "
			+ "`updateTime`) "
			+ "VALUES(?,?,?,?,?,?,?,?,now());";
	

}
