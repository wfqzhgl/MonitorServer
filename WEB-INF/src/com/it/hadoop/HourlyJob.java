package com.it.hadoop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.it.config.DeviceConfigLoad;
import com.it.util.Constant;
import com.it.util.Utils;

public class HourlyJob extends Configured implements Tool {

	private static Log log = LogFactory.getLog(HourlyJob.class);
	private static Connection conn = null;

	public static void main(String[] args) throws Exception {
		log.info("job beigin.............");
		log.debug("---args:len="+args.length+","+Arrays.toString(args));
		conn = Utils.get_mysql_conn(args[0]);

		ToolRunner.run(new Configuration(), new HourlyJob(), args);

		//
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e1) {
				log.error("SQLException: ", e1);
			} finally {
				conn = null;
			}
		}

	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = HBaseConfiguration.create();
		String[] host_port = Utils
				.get_hbase_connection_string("/data/soft/MonitorServer/WEB-INF/config.properties");

		conf.set("hbase.zookeeper.quorum", host_port[0]);
		conf.set("hbase.zookeeper.property.clientPort", host_port[1]);
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		log.debug("---otherArgs:len="+otherArgs.length+","+Arrays.toString(otherArgs));
		if (otherArgs.length != 2 && otherArgs.length != 5) {
			System.err.println("Usage1: com.it.hadoop.HourlyJob  jdbc.properties diffhour");
			System.err
					.println("Usage2: com.it.hadoop.HourlyJob  jdbc.properties year month date hour");
			System.exit(2);
		}

		Calendar begin = null;
		Calendar end = Calendar.getInstance();
		if (otherArgs.length == 2) {
			begin = Utils.getCalendarByDiffHourFromNow(Integer
					.parseInt(otherArgs[1]));
			end = Utils.getCalendarByDiffHourFromNow(Integer
					.parseInt(otherArgs[1]) + 1);
		} else if (otherArgs.length == 5) {
			begin = Utils.getCalendarBySpecific(Integer.parseInt(otherArgs[0]),
					Integer.parseInt(otherArgs[1]),
					Integer.parseInt(otherArgs[2]),
					Integer.parseInt(otherArgs[3]));
			end.setTimeInMillis(begin.getTimeInMillis());
			end.add(Calendar.HOUR_OF_DAY, 1);
		} else {
			System.out.println("params  error...exiting...");
			System.exit(2);
		}

		String stopkey = Utils.getHbaseKeyByCalendar(begin);
		String startkey = Utils.getHbaseKeyByCalendar(end);
		log.info("------begin:" + begin.getTime().toString() + ", end:"
				+ end.getTime().toString());
		log.info("------startkey:" + startkey + ", stopkey:" + stopkey);
		List<Scan> scans = new ArrayList<Scan>();
		Job job = new Job(conf, "MS hourly job");
		job.setJarByClass(HourlyJob.class);
		Scan scanTmp = null;
		String tableNameTmp = null;
		Set<String> table_set = DeviceConfigLoad.getInstance().get_device_list_by_connection(conn);;
		System.out.println("---table_set:len="+table_set.size()+","+table_set);
		log.debug("---table_set:len="+table_set.size()+","+table_set);
		for (String device_id : table_set) {
			scanTmp = new Scan();
			// scanTmp.setTimeRange(minStamp, maxStamp);
			scanTmp.setStartRow(startkey.getBytes());
			scanTmp.setStopRow(stopkey.getBytes());
			tableNameTmp = device_id + "_log";
			if (!HbaseBaseOP.getInstance().isTableExist(tableNameTmp)) {
				log.info("================htable  not exist:" + tableNameTmp);
				continue;
			}
			scanTmp.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME,
					tableNameTmp.getBytes());
			scans.add(scanTmp);
		}

		TableMapReduceUtil.initTableMapperJob(scans, HourJobMapper.class,
				Text.class, MapWritable.class, job);
		job.setReducerClass(HourJobReducer.class);
		// job.setNumReduceTasks(2);
		job.setOutputFormatClass(NullOutputFormat.class);

		boolean tf = false;
		try {
			tf = job.waitForCompletion(true);
		} catch (InterruptedException e) {
			log.error("Run hourly MFPCollectJob InterruptedException:", e);
		} catch (ClassNotFoundException e) {
			log.error("Run hourly MFPCollectJob ClassNotFoundException:", e);
		}

		// Connection conn = Utils.get_mysql_conn(args[1]);

		HbaseManager hbase = new HbaseManager(conf);
		// 写入数据库
		if (tf) {
			String currentDay = Constant.DEFAULT_DATE_FORMAT.format(begin
					.getTime());
			int currentHour = begin.get(Calendar.HOUR_OF_DAY);
			saveToDB(hbase, currentDay, currentHour);
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e1) {
				log.error("SQLException: ", e1);
			} finally {
				conn = null;
			}
		}
		return 0;
	}

	private void saveToDB(HbaseManager hbase, String currentDay, int currentHour) {
		if (conn == null) {
			log.error("Without mysql connection!");
			return;
		}
		log.info("------------save to db start!");
		execSrc(hbase, currentDay, currentHour);
		execDst(hbase, currentDay, currentHour);
		log.info("------------save to db end!");

	}

	private void execSrc(HbaseManager hbase, String currentDay, int currentHour) {
		PreparedStatement pstmt1 = null;
		try {
			pstmt1 = conn.prepareStatement(Constant.SQL_DEL_SRC_HOURLY);
			pstmt1.setString(1, currentDay);
			pstmt1.setInt(2, currentHour);
			pstmt1.execute();
			conn.commit();
		} catch (SQLException e) {
			log.error("SQL_DEL_SRC_HOURLY SQLException: ", e);
		} finally {
			try {
				pstmt1.close();
			} catch (SQLException e) {
				log.error("SQLException: ", e);
			}
		}
		PreparedStatement pstmt = null;
		try {
			log.info("===save data to sql_src_hourly start");
			pstmt = conn.prepareStatement(Constant.SQL_SRC_HOURLY);
			ResultScanner rs = hbase
					.getEventSrcTableResult(Constant.HBASE_PREFIX_MS_SRC
							+ currentDay + currentHour);
			for (Result r : rs) {
				log.debug(Constant.HBASE_PREFIX_MS_SRC + r.toString());
				addSrcHourlyBatch(pstmt, r);
			}
			pstmt.executeBatch();
			conn.commit();
			log.info("===save data to sql_src_hourly end");

			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
		} catch (IOException e) {
			log.error("IOException: ", e);
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				log.error("SQLException: ", e);
			}
		}
	}

	private void execDst(HbaseManager hbase, String currentDay, int currentHour) {
		PreparedStatement pstmt1 = null;
		try {
			pstmt1 = conn.prepareStatement(Constant.SQL_DEL_DST_HOURLY);
			pstmt1.setString(1, currentDay);
			pstmt1.setInt(2, currentHour);
			pstmt1.execute();
			conn.commit();
		} catch (SQLException e) {
			log.error("SQL_DEL_DST_HOURLY SQLException: ", e);
		} finally {
			try {
				pstmt1.close();
			} catch (SQLException e) {
				log.error("SQLException: ", e);
			}
		}
		PreparedStatement pstmt = null;
		try {
			log.info("===save data to sql_dst_hourly start");
			pstmt = conn.prepareStatement(Constant.SQL_DST_HOURLY);
			ResultScanner rs = hbase
					.getEventDstTableResult(Constant.HBASE_PREFIX_MS_DST
							+ currentDay + currentHour);
			for (Result r : rs) {
				log.debug(Constant.HBASE_PREFIX_MS_DST + r.toString());
				addDstHourlyBatch(pstmt, r);
			}
			pstmt.executeBatch();
			conn.commit();
			log.info("===save data to sql_dst_hourly end");

			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
		} catch (IOException e) {
			log.error("IOException: ", e);
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				log.error("SQLException: ", e);
			}
		}
	}

	private void addSrcHourlyBatch(PreparedStatement pstmt, Result r) {

		String year = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_YEAR));
		String date = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_DATE));
		String hour = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_HOUR));
		String week = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_WEEK));
		String month = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_MONTH));

		String datehour = date.replaceAll("-", "")
				+ (hour.length() <= 1 ? ("0" + hour) : hour);
		// String country = Bytes.toString(r
		// .getValue(HbaseManager.FAMILY_RESULT_DATA,
		// HbaseManager.QUALIFIER_COUNTRY));
		// String province = Bytes.toString(r.getValue(
		// HbaseManager.FAMILY_RESULT_DATA,
		// HbaseManager.QUALIFIER_PROVINCE));
		String lsip = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_SIP));
		String event_type = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_TYPE));
		String count = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_COUNT));

		try {

			if (lsip.contains(".")) {
				log.info("==========sip format error:" + lsip);
				return;
			}
			pstmt.setInt(1, Integer.parseInt(year));
			pstmt.setInt(2, Integer.parseInt(month));
			pstmt.setInt(3, Integer.parseInt(week));
			pstmt.setInt(4, Integer.parseInt(date));
			pstmt.setInt(5, Integer.parseInt(hour));
			pstmt.setInt(6, Integer.parseInt(datehour));

			pstmt.setString(7, lsip);
			try {
				pstmt.setString(8, Utils.longToIp(Long.parseLong(lsip)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				log.info("==========sip format error:" + lsip);
				return;
			}

			pstmt.setString(9, null);
			pstmt.setString(10, null);
			pstmt.setString(11, null);
			pstmt.setString(12, event_type);
			pstmt.setLong(13, Long.parseLong(count));

			pstmt.addBatch();
			return;
		} catch (Exception e) {
			log.error("handle db addSrcHourlyBatch error. ", e);
		}
	}

	private void addDstHourlyBatch(PreparedStatement pstmt, Result r) {

		String year = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_YEAR));
		String date = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_DATE));
		String hour = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_HOUR));
		String week = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_WEEK));
		String month = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_MONTH));
		String datehour = date.replaceAll("-", "")
				+ (hour.length() <= 1 ? ("0" + hour) : hour);
		// String country = Bytes.toString(r
		// .getValue(HbaseManager.FAMILY_RESULT_DATA,
		// HbaseManager.QUALIFIER_COUNTRY));
		// String province = Bytes.toString(r.getValue(
		// HbaseManager.FAMILY_RESULT_DATA,
		// HbaseManager.QUALIFIER_PROVINCE));
		String ldip = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_DIP));
		String event_type = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_TYPE));
		String count = Bytes.toString(r.getValue(
				HbaseManager.FAMILY_RESULT_DATA, HbaseManager.QUALIFIER_COUNT));

		try {
			if (ldip.contains(".")) {
				log.info("==========dip format error:" + ldip);
				return;
			}

			pstmt.setInt(1, Integer.parseInt(year));
			pstmt.setInt(2, Integer.parseInt(month));
			pstmt.setInt(3, Integer.parseInt(week));
			pstmt.setInt(4, Integer.parseInt(date));
			pstmt.setInt(5, Integer.parseInt(hour));
			pstmt.setInt(6, Integer.parseInt(datehour));
			pstmt.setString(7, ldip);
			try {
				pstmt.setString(8, Utils.longToIp(Long.parseLong(ldip)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				log.info("==========dip format error:" + ldip);
				return;
			}
			pstmt.setString(9, null);
			pstmt.setString(10, null);
			pstmt.setString(11, null);
			pstmt.setString(12, event_type);
			pstmt.setLong(13, Long.parseLong(count));

			pstmt.addBatch();
			return;
		} catch (Exception e) {
			log.error("handle db addDstHourlyBatch error. ", e);
		}
	}

}
