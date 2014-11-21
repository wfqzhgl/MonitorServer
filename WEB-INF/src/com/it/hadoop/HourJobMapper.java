package com.it.hadoop;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;

import com.it.util.Constant;
import com.it.util.Utils;

public class HourJobMapper extends TableMapper<Text, MapWritable> {
	private static final Log logger = LogFactory.getLog(HourJobMapper.class);
	private StringBuilder outputKey = new StringBuilder(100);
	private MapWritable outputValue = new MapWritable();

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// get date from key
		Map<String, String> time_map = Utils.parse_date_from_hbase_key(key
				.toString());
		String year = time_map.get("year");
		String month = time_map.get("month");
		String week = time_map.get("week");
		String date = time_map.get("date");
		String hour = time_map.get("hour");
		
		String sip = new String(value.getValue(Bytes.toBytes("log"),"src_ip".getBytes()));
//		IpInfo s_info = IPParserLoad.parseIP(sip);
//		String s_country=s_info.getCountry();
//		String s_province = s_info.getProvince();
//		String s_city =s_info.getCity();
		
		String dip = new String(value.getValue(Bytes.toBytes("log"),"dst_ip".getBytes()));
//		IpInfo d_info = IPParserLoad.parseIP(dip);
//		String d_country=d_info.getCountry();
//		String d_province = d_info.getProvince();
//		String d_city =d_info.getCity();
		
		String type = new String(value.getValue(Bytes.toBytes("log"),"type".getBytes()));
		
		//write src
		outputKey.setLength(0);
		outputKey.append(Constant.HBASE_PREFIX_MS_SRC).append(sip).append(date).append(hour).append(type);
		outputValue.clear();
		outputValue.put(Constant.MAP_KEY_SIP, new Text(sip));
		outputValue.put(Constant.MAP_KEY_DATE, new Text(date));
		outputValue.put(Constant.MAP_KEY_HOUR, new Text(hour));
		outputValue.put(Constant.MAP_KEY_WEEK, new Text(week));
		outputValue.put(Constant.MAP_KEY_MONTH, new Text(month));
		outputValue.put(Constant.MAP_KEY_YEAR, new Text(year));
		outputValue.put(Constant.MAP_KEY_EVENT_TYPE, new Text(type));
		context.write(new Text(outputKey.toString()), outputValue);
		
		//write dst
		outputKey.setLength(0);
		outputKey.append(Constant.HBASE_PREFIX_MS_DST).append(dip).append(date).append(hour).append(type);
		outputValue.clear();
		outputValue.put(Constant.MAP_KEY_DIP, new Text(dip));
		outputValue.put(Constant.MAP_KEY_DATE, new Text(date));
		outputValue.put(Constant.MAP_KEY_HOUR, new Text(hour));
		outputValue.put(Constant.MAP_KEY_WEEK, new Text(week));
		outputValue.put(Constant.MAP_KEY_MONTH, new Text(month));
		outputValue.put(Constant.MAP_KEY_YEAR, new Text(year));
		outputValue.put(Constant.MAP_KEY_EVENT_TYPE, new Text(type));
		context.write(new Text(outputKey.toString()), outputValue);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
