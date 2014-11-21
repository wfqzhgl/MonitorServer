package com.it.hadoop;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseManager {

	private static final String HTABLE_EVENT_SRC = "ms_result_src";
	private static final String HTABLE_EVENT_DST = "ms_result_dst";
	public static final byte[] FAMILY_RESULT_DATA = Bytes.toBytes("data");

	public static final byte[] QUALIFIER_YEAR = Bytes.toBytes("year");
	public static final byte[] QUALIFIER_MONTH = Bytes.toBytes("month");
	public static final byte[] QUALIFIER_WEEK = Bytes.toBytes("week");
	public static final byte[] QUALIFIER_DATE = Bytes.toBytes("date");
	public static final byte[] QUALIFIER_HOUR = Bytes.toBytes("hour");
	public static final byte[] QUALIFIER_SIP = Bytes.toBytes("sip");
	public static final byte[] QUALIFIER_DIP = Bytes.toBytes("dip");
	public static final byte[] QUALIFIER_COUNTRY = Bytes.toBytes("country");
	public static final byte[] QUALIFIER_PROVINCE = Bytes.toBytes("province");
	public static final byte[] QUALIFIER_CITY = Bytes.toBytes("city");
	public static final byte[] QUALIFIER_TYPE = Bytes.toBytes("type");
	public static final byte[] QUALIFIER_COUNT = Bytes.toBytes("count");

	public HTable eventSrcTable = null;
	public HTable eventDstTable = null;

	private HBaseAdmin admin = null;

	public HbaseManager(Configuration conf) throws IOException {
		init(conf);
	}

	private void init(Configuration conf) throws IOException {
		Configuration config = HBaseConfiguration.create(conf);

		admin = new HBaseAdmin(config);
		createTable(HTABLE_EVENT_SRC, -1, Bytes.toString(FAMILY_RESULT_DATA));
		createTable(HTABLE_EVENT_DST, -1, Bytes.toString(FAMILY_RESULT_DATA));
		eventSrcTable = new HTable(config, HTABLE_EVENT_SRC);
		eventDstTable = new HTable(config, HTABLE_EVENT_DST);
	}

	public void close() throws IOException {
		eventSrcTable.close();
		eventDstTable.close();
	}

	public ResultScanner getEventDstTableResult(String rowPrefix)
			throws IOException {
		Scan s = new Scan();
		s.setFilter(new PrefixFilter(rowPrefix.getBytes()));
		return eventDstTable.getScanner(s);
	}

	public ResultScanner getEventSrcTableResult(String rowPrefix)
			throws IOException {
		Scan s = new Scan();
		s.setFilter(new PrefixFilter(rowPrefix.getBytes()));
		return eventSrcTable.getScanner(s);
	}

	public void updateEventSrc(String row, String year, String month,
			String week, String date, String hour, String sip, String country,
			String province, String city, String type, long count) throws IOException {

		Put put = new Put(Bytes.toBytes(row));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_YEAR, Bytes.toBytes(year));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_MONTH, Bytes.toBytes(month));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_WEEK,
				Bytes.toBytes(week));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_DATE, Bytes.toBytes(date));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_HOUR, Bytes.toBytes(hour));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_SIP,
				Bytes.toBytes(sip));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_COUNTRY, Bytes.toBytes(country));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_PROVINCE,
				Bytes.toBytes(province));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_CITY, Bytes.toBytes(city));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_TYPE, Bytes.toBytes(type));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_COUNT, Bytes.toBytes(String.valueOf(count)));

		eventSrcTable.put(put);
	}

	
	public void updateEventDst(String row, String year, String month,
			String week, String date, String hour, String dip, String country,
			String province, String city, String type, String count) throws IOException {

		Put put = new Put(Bytes.toBytes(row));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_YEAR, Bytes.toBytes(year));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_MONTH, Bytes.toBytes(month));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_WEEK,
				Bytes.toBytes(week));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_DATE, Bytes.toBytes(date));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_HOUR, Bytes.toBytes(hour));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_DIP,
				Bytes.toBytes(dip));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_COUNTRY, Bytes.toBytes(country));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_PROVINCE,
				Bytes.toBytes(province));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_CITY, Bytes.toBytes(city));
		put.add(FAMILY_RESULT_DATA, QUALIFIER_TYPE, Bytes.toBytes(type));

		put.add(FAMILY_RESULT_DATA, QUALIFIER_COUNT, Bytes.toBytes(count));

		eventDstTable.put(put);
	}
	
	
	private void createTable(String table, int timeToLive, String... colfams)
			throws IOException {

		if (admin.tableExists(table)) {
			return;
		}

		HTableDescriptor desc = new HTableDescriptor(table);
		for (String cf : colfams) {
			HColumnDescriptor coldef = new HColumnDescriptor(cf);
			if (timeToLive > 0) {
				coldef.setTimeToLive(timeToLive);
			}
			desc.addFamily(coldef);
		}
		admin.createTable(desc);
	}

}
