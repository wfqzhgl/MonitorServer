package com.it.hadoop.test;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.it.hadoop.HbaseBaseOP;
import com.it.util.Utils;

public class HbaseTest {
	private final static  HbaseTest instance = new HbaseTest();
	
	private  Configuration conf = null;
	private HBaseAdmin admin = null;
	private static Random random = new Random();

	
	public static HbaseTest getInstance() {
		return instance;
	}
	

	
	
	public HbaseTest() {
		super();
		try {
			init();
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		if (admin != null) {
			admin.close();
		}
	}
	
	private void init() throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		if (this.conf == null) {
			this.conf = HBaseConfiguration.create();
		}
		
		conf.set("hbase.zookeeper.quorum", "bj21-v03.oupeng.in");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
//		conf.set("hbase.zookeeper.quorum", "datanode91");
//		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
		// conf.setInt("timeout", 10000);
		// conf.set("hbase.zookeeper.quorum", "datanode-1.g122.zx");
		// conf.set("hbase.zookeeper.property.clientPort", "2181");
//		 conf.set("zookeeper.znode.parent", "/hbase-unsecure");
		
		if (this.admin == null) {
			this.admin = new HBaseAdmin(conf);
		}
		
		
		
	}

	// 创建表
	public  void createTable(String tableName, String[] columnFamilys)
			throws Exception {
		if (admin.tableExists(tableName)) {
			System.out.println("表已经存在");
			return;

		} else {
			HTableDescriptor tableDesc = new HTableDescriptor(
					TableName.valueOf(tableName));
			for (String clf : columnFamilys) {
				HColumnDescriptor tmp = new HColumnDescriptor(clf);
				tmp.setMaxVersions(3);
				tableDesc.addFamily(tmp);
			}
			admin.createTable(tableDesc);
			System.out.println("创建表成功");
		}
//		admin.close();
	}

	// 删除表
	public  void deleteTable(String tableName) throws Exception {
		if (admin.tableExists(tableName)) {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
			System.out.println("done.");
		} else {
			System.out.println("表不存在");
			System.exit(0);
		}
//		admin.close();
	}

	// 添加数据
	
	public  void addRowByTable(HTable table, String row,
			String columnFamily, String column, String value) throws Exception {
//		HTable table = new HTable(conf, tableName);
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
				Bytes.toBytes(value));
		table.put(put);
		table.flushCommits();
		System.out.println("done.");
	}
	
	public  void addRow(String tableName, String row,
			String columnFamily, String column, String value) throws Exception {
		HTable table = new HTable(conf, tableName);
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
				Bytes.toBytes(value));
		table.put(put);
		table.flushCommits();
		System.out.println("done.");
		table.close();
	}

	public  void addRow(String tableName, String row,
			String columnFamily, String column, String value, long ts)
			throws Exception {
		HTable table = new HTable(conf, tableName);
		Put put = new Put(Bytes.toBytes(row), ts);
		put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
				Bytes.toBytes(value));
		table.put(put);
		System.out.println(" ts done.");
		table.close();
	}

	// 获取数据
	public  void getRow(String tableName, String row) throws Exception {
		HTable table = new HTable(conf, tableName);
		Get get = new Get(Bytes.toBytes(row));
		Result result = table.get(get);
		for (Cell cell : result.rawCells()) {
			System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
			System.out.print(new String(CellUtil.cloneFamily(cell)) + " ");
			System.out.print(new String(CellUtil.cloneQualifier(cell)) + " ");
			System.out.print(new String(CellUtil.cloneValue(cell)) + " ");
			System.out.println(" ts:" + cell.getTimestamp());
		}
		table.close();
	}

	// 获取所有数据
	public  void getAllRows(String tableName) throws Exception {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		scan.setCaching(0);
		// scan.setCacheBlocks(false);
		ResultScanner results = table.getScanner(scan);

		for (Result result : results) {
			for (Cell cell : result.rawCells()) {
				System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
				System.out.print(new String(CellUtil.cloneFamily(cell)) + " ");
				System.out.print(new String(CellUtil.cloneQualifier(cell))
						+ " ");
				System.out.print(new String(CellUtil.cloneValue(cell)) + " ");
				System.out.println(" ts:" + cell.getTimestamp());
			}
		}
		table.close();

	}

	public  void getVersions(String tableName, String row, String family,
			String qualifier, long timestamp) throws IOException {

		HTable table = new HTable(conf, tableName);
		Get get = new Get(Bytes.toBytes(row));
		get.addColumn(family.getBytes(), qualifier.getBytes());
		// get.setMaxVersions();
		get.setTimeRange(0, System.currentTimeMillis());
		Result result = table.get(get);
		for (Cell cell : result.rawCells()) {
			System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
			System.out.print(new String(CellUtil.cloneFamily(cell)) + " ");
			System.out.print(new String(CellUtil.cloneQualifier(cell)) + " ");
			System.out.print(new String(CellUtil.cloneValue(cell)) + " ");
			System.out.println(" ts:" + cell.getTimestamp());
		}
		table.close();

	}

	//
	public  void filter_value(String tableName) throws Exception {

		FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
				"cf1".getBytes(), "c1".getBytes(), CompareOp.EQUAL,
				Bytes.toBytes("23"));
		list.addFilter(filter1);

		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		scan.setFilter(list);
		// scan.setFilter(new FirstKeyOnlyFilter());

		scan.setCaching(500);
		outputs(table.getScanner(scan));

		table.close();

	}

	// filter start stop
	public  void filter_start_stop(String tableName, String min,
			String max) throws Exception {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		// scan.setFilter(new FirstKeyOnlyFilter());
		scan.setStartRow(min.getBytes());
		scan.setStopRow(max.getBytes());

		scan.setCaching(500);
		outputs(table.getScanner(scan));

		table.close();

	}

	public  void outputs(ResultScanner results) {
		for (Result result : results) {
			for (Cell cell : result.rawCells()) {
				System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
				System.out.print(new String(CellUtil.cloneFamily(cell)) + " ");
				System.out.print(new String(CellUtil.cloneQualifier(cell))
						+ " ");
				System.out.print(new String(CellUtil.cloneValue(cell)) + " ");
				System.out.println(" ts:" + cell.getTimestamp());
			}
		}

	}

	// 过滤器，获取所有数据
	public  void filterRows(String tableName) throws Exception {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		// scan.setFilter(new FirstKeyOnlyFilter());
		scan.setTimeRange(1405102444, 140532780955L);
		scan.setCaching(500);
		ResultScanner results = table.getScanner(scan);

		for (Result result : results) {
			for (Cell cell : result.rawCells()) {
				System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
				System.out.print(new String(CellUtil.cloneFamily(cell)) + " ");
				System.out.print(new String(CellUtil.cloneQualifier(cell))
						+ " ");
				System.out.print(new String(CellUtil.cloneValue(cell)) + " ");
				System.out.println(" ts:" + cell.getTimestamp());
			}
		}
		table.close();

	}

	public  void add_datas(String tableName, String row, int count,
			String type, long ts) throws Exception {
		if (type == "log") {
			for (int i = 0; i < count; i++) {

				addRow(tableName, row, "log", "dst_ip", "192.168.1.2", ts);
				addRow(tableName, row, "log", "dst_port", "80", ts);
				addRow(tableName, row, "log", "level", "1", ts);
				addRow(tableName, row, "log", "logstr",
						"Win.TR.Startpage-1827", ts);
				addRow(tableName,
						row,
						"log",
						"packet_pcap",
						"any_20140702_380f92df-afda-4d35-aebd-72d0b6876261_2099573",
						ts);

				addRow(tableName, row, "log", "protocol_type", "", ts);
				addRow(tableName, row, "log", "src_ip", "192.168.1.1", ts);
				addRow(tableName, row, "log", "src_port", "51135", ts);
				addRow(tableName,
						row,
						"log",
						"type",
						"\\xE6\\x81\\xB6\\xE6\\x84\\x8F\\xE6\\x94\\xBB\\xE5\\x87\\xBB",
						ts);
			}
		} else {
			for (int i = 0; i < count; i++) {
				addRow(tableName, row, "tk", "adisk", "922526", ts);
				addRow(tableName, row, "tk", "aflow", "0", ts);
				addRow(tableName, row, "tk", "wflow", "10", ts);
				addRow(tableName, row, "tk", "amem", "7755", ts);
				addRow(tableName, row, "tk", "cpu", "3", ts);
				addRow(tableName, row, "tk", "fdisk", "917484", ts);
				addRow(tableName, row, "tk", "ip", "10.10.1.22", ts);
				addRow(tableName, row, "tk", "umem", "1137", ts);
			}
		}

	}

	public  void add_datas(String tableName, int count, String type)
			throws Exception {

		String sams[] = new String[] { "协议异常", "系统漏洞攻击", "后门传输", "WEB漏洞攻击",
				"蠕虫攻击", "恶意代码" };

		HTable table = new HTable(conf, tableName);
		if (type.equalsIgnoreCase("log")) {
			for (int i = 0; i < count; i++) {
				String row = Utils.getHbaseKeyByTimeStamp(System
						.currentTimeMillis() - 3600000 * i);

				// String row = "65525921424639" + random.nextInt(999);
				addRowByTable(table, row, type, "dst_ip",
						"64.177.64." + random.nextInt(20));
				addRowByTable(table, row, type, "dst_port", "80");
				addRowByTable(table, row, type, "level", "" + random.nextInt(4));
				addRowByTable(table, row, type, "logstr", "Win.TR.Startpage-1827");
				addRowByTable(table, row, type, "packet_pcap",
						"any_20140702_380f92df-afda-4d35-aebd-72d0b6876261_2099573");

				addRowByTable(table, row, type, "protocol_type", "");
				addRowByTable(table, row, type, "src_ip",
						"47.154.255." + random.nextInt(20));
				addRowByTable(table, row, type, "src_port",
						"" + random.nextInt(65534));
				addRowByTable(table, row, type, "type", sams[random.nextInt(4)]);
			}
		} else {
			for (int i = 0; i < count; i++) {
				String row = Utils.getHbaseKeyByTimeStamp(System
						.currentTimeMillis() - 3600000 * i);
				System.out.println(row);
				addRowByTable(table, row, type, "adisk",
						"" + random.nextInt(2635456));
				addRowByTable(table, row, type, "aflow", "0");
				addRowByTable(table, row, type, "wflow", "10");
				addRowByTable(table, row, type, "amem", "7755");
				addRowByTable(table, row, type, "cpu", "3");
				addRowByTable(table, row, type, "fdisk", "917484");
				addRowByTable(table, row, type, "ip", "10.10.1.22");
				addRowByTable(table, row, type, "umem", "1137");
			}
		}
		
		table.close();

	}

	public  void listTables() throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor[] res = admin.listTables();
		for (HTableDescriptor t : res) {
			System.out.println(t.getNameAsString());
		}
	}

	public  void initTables(boolean create, long ts) throws Exception {
		// 3 devices
		if (create) {

			createTable("32e9448f724a8cbe_log", new String[] { "log" });
			createTable("81f828a9587b10a2_log", new String[] { "log" });
			createTable("140a4d25974218DE_log", new String[] { "log" });

//			 createTable("32e9448f724a8cbe_tk", new String[] { "tk" });
//			 createTable("81f828a9587b10a2_tk", new String[] { "tk" });
//			 createTable("140a4d25974218DE_tk", new String[] { "tk" });
		}

		//
		add_datas("32e9448f724a8cbe_log", 2, "log");
		add_datas("81f828a9587b10a2_log", 2, "log");
		add_datas("140a4d25974218DE_log", 2, "log");

//		add_datas("32e9448f724a8cbe_tk", 2, "tk");
//		add_datas("81f828a9587b10a2_tk", 2, "tk");
//		add_datas("140a4d25974218DE_tk", 2, "tk");

	}

	public static void main(String[] args) {

		// TODO Auto-generated method stub
		try {

			HbaseTest cls = HbaseTest.getInstance();
			
			
			cls.listTables();
			// String tableName ="test02_max";
			// 32e9448f724a8cbe_tk
			// 81f828a9587b10a2_tk
			// 140a4d25974218DE_tk
//			String tableName = "32e9448f724a8cbe_tk";
			// deleteTable(tableName);
			//
			// createTable(tableName,new String[]{"cf1","cf2"});

//			 getAllRows(tableName);


			// filterRows(tableName);

			// getVersions(tableName,"k1","cf1","c1",System.currentTimeMillis());

//			cls.initTables(false, 0);

			// filter_start_stop("table1","row1","row3");
			// filter_value("table1");

			
			cls.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
