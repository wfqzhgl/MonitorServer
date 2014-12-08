package com.it.hadoop;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.it.util.Constant;

public class HourJobReducer extends
		Reducer<Text, MapWritable, NullWritable, NullWritable> {
	private static final Log log = LogFactory.getLog(HourJobReducer.class);
	private HbaseManager hbase;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);

		try {
			Configuration conf = context.getConfiguration();
			hbase = new HbaseManager(conf);
			hbase.eventSrcTable.setAutoFlush(false, true);
			hbase.eventDstTable.setAutoFlush(false, true);
		} catch (Exception e) {
			log.error("init hbase error. " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);

		if (hbase != null) {
			try {
				hbase.eventSrcTable.flushCommits();
				hbase.eventDstTable.flushCommits();
				hbase.close();
			} catch (IOException e) {
				log.error("close hbase error. " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected void reduce(Text key, Iterable<MapWritable> values,
			Context context) throws IOException, InterruptedException {

		Iterator<MapWritable> it = values.iterator();
		boolean isSrc = key.toString().startsWith(Constant.HBASE_PREFIX_MS_SRC);
		boolean isDst = key.toString().startsWith(Constant.HBASE_PREFIX_MS_DST);
		Text year = null;
		Text month = null;
		Text week = null;
		Text date = null;
		Text hour = null;
		Text sip = null;
		Text dip = null;
		Text eventType = null;
		long count = 0;
		while (it.hasNext()) {
			MapWritable record = it.next();
			if (year == null) {
				year = (Text) record.get(Constant.MAP_KEY_YEAR);
			}
			if (month == null) {
				month = (Text) record.get(Constant.MAP_KEY_MONTH);
			}
			if (week == null) {
				week = (Text) record.get(Constant.MAP_KEY_WEEK);
			}
			if (date == null) {
				date = (Text) record.get(Constant.MAP_KEY_DATE);
			}
			if (hour == null) {
				hour = (Text) record.get(Constant.MAP_KEY_HOUR);
			}
			if (eventType == null) {
				eventType = (Text) record.get(Constant.MAP_KEY_EVENT_TYPE);
			}
			if (isSrc) {
				if (sip == null) {
					sip = (Text) record.get(Constant.MAP_KEY_SIP);
				}
			}

			if (isDst) {
				if (dip == null) {
					dip = (Text) record.get(Constant.MAP_KEY_DIP);
				}
			}

			count += 1;
		}

		if (isSrc) {
			saveToSrcHBase(year.toString(), month.toString(), week.toString(),
					date.toString(), hour.toString(), sip.toString(),
					eventType.toString(), count);
		} else if (isDst) {
			saveToDstHBase(year.toString(), month.toString(), week.toString(),
					date.toString(), hour.toString(), dip.toString(),
					eventType.toString(), count);
		}

	}

	private void saveToSrcHBase(String year, String month, String week,
			String date, String hour, String sip, String eventType, long count) {
		try {
			StringBuffer row = new StringBuffer();
			row.append(Constant.HBASE_PREFIX_MS_SRC).append(date).append(hour)
					.append(",").append(eventType).append(",").append(sip);
			hbase.updateEventSrc(row.toString(), year, month, week, date, hour,
					sip, "", "", "", eventType, count);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("saveToSrcHBase error " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveToDstHBase(String year, String month, String week,
			String date, String hour, String dip, String eventType, long count) {
		try {
			StringBuffer row = new StringBuffer();
			row.append(Constant.HBASE_PREFIX_MS_DST).append(date).append(hour)
					.append(",").append(eventType).append(",").append(dip);
			hbase.updateEventSrc(row.toString(), year, month, week, date, hour,
					dip, "", "", "", eventType, count);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("saveToDstHBase error " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
