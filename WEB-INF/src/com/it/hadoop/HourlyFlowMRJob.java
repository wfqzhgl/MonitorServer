package com.it.hadoop;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.it.config.DeviceConfigLoad;
import com.it.util.Utils;

public class HourlyFlowMRJob extends Configured implements Tool {

	private static Log log = LogFactory.getLog(HourlyFlowMRJob.class);
	private Connection conn = null;

	public static void main(String[] args) throws Exception {
		log.info("HourlyFlowJob job beigin.............");
		Connection conn = Utils.get_mysql_conn(args[1]);

		ToolRunner.run(new Configuration(), new HourlyFlowMRJob(), args);

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
		conf.set("hbase.zookeeper.quorum", "bj21-v03.oupeng.in");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 1 || otherArgs.length != 4) {
			System.err.println("Usage1: com.it.hadoop.HourlyFlowJob  diffhour");
			System.err
					.println("Usage2: com.it.hadoop.HourlyFlowJob  year month date hour");
			System.exit(2);
		}

		Calendar begin = null;
		Calendar end = Calendar.getInstance();
		if (otherArgs.length == 1) {
			begin = Utils.getCalendarByDiffHourFromNow(Integer
					.parseInt(otherArgs[0]));
			end = Utils.getCalendarByDiffHourFromNow(Integer
					.parseInt(otherArgs[0]) + 1);
		} else if (otherArgs.length == 4) {
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
		Job job = new Job(conf, "MS HourlyFlowJob job");
		job.setJarByClass(HourlyFlowMRJob.class);
		Scan scanTmp = null;
		String tableNameTmp = null;
		Set<String> table_set = DeviceConfigLoad.getInstance().getDeviceSet();
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
			log.error("Run HourlyFlowJob MFPCollectJob InterruptedException:", e);
		} catch (ClassNotFoundException e) {
			log.error("Run HourlyFlowJob MFPCollectJob ClassNotFoundException:", e);
		}
		return 0;
	}
}
