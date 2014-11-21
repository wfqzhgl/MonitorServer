package com.it.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class MRLog {
	private static final Log logger = LogFactory.getLog(MRLog.class);

	public static class CountMapper extends TableMapper<Text, IntWritable> {
		private final IntWritable ONE = new IntWritable(1);
		private Text text = new Text();

		public void map(ImmutableBytesWritable row, Result value,
				Context context) throws IOException, InterruptedException {
			String val = new String(value.getValue(Bytes.toBytes("log"),
					"src_ip".getBytes()));
			text.set(val);
			context.write(text, ONE);
		}
	}

	public static class CountReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", "bj21-v03.oupeng.in");
		config.set("hbase.zookeeper.property.clientPort", "2181");
	    String[] otherArgs = new GenericOptionsParser(config, args).getRemainingArgs();
	    if (otherArgs.length != 1) {
	      System.err.println("Usage: com.it.hadoop.MRLog  <out>");
	      System.exit(2);
	    }
	    
		Job job = new Job(config, "count test");
		job.setJarByClass(MRLog.class);

		List scans = new ArrayList();
		Scan scanTmp = null;
		String tableNameTmp = null;
		Set<String> table_set=new HashSet<String>();
		table_set.add("14095e6541599F4E");
		table_set.add("14033841f4c72455");
		table_set.add("140a4d314b4E683B");
		for (String device_id : table_set) {
			scanTmp = new Scan();
			// scantmp.setTimeRange(minStamp, maxStamp);
			// scantmp.setStartRow(startRow);
			// scantmp.setStopRow(stopRow);]
			tableNameTmp = device_id + "_log";
			scanTmp.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME,
					tableNameTmp.getBytes());
			scans.add(scanTmp);
		}

		TableMapReduceUtil.initTableMapperJob(scans, CountMapper.class,
				Text.class, IntWritable.class, job);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(2);
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[0]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
