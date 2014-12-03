package com.it.hadoop;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
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
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jredis.ri.alphazero.support.Log;

import com.it.config.BaseConfigLoad;
import com.it.config.DeviceConfigLoad;
import com.it.config.FlowDeviceConfig;
import com.it.config.IPParserLoad;
import com.it.config.TypeListLoad;
import com.it.util.Constant;
import com.it.util.Utils;
import com.it.vo.DeviceStatusVO;
import com.it.vo.DeviceVO;
import com.it.vo.EventGlobalVO;
import com.it.vo.EventTypeVO;
import com.it.vo.HourlyLogVO;
import com.it.vo.IpInfo;
import com.it.vo.TypeGlobalVO;

public class HbaseBaseOP {
	private final static HbaseBaseOP instance = new HbaseBaseOP();
	static Logger logger = Logger.getLogger(HbaseBaseOP.class);

	private HBaseAdmin admin = null;
	private Configuration conf = null;

	public static HbaseBaseOP getInstance() {
		return instance;
	}

	public HbaseBaseOP() {
		super();
		try {
			init();
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
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

	private void init() throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException, ConfigurationException {
		if (this.conf == null) {
			this.conf = HBaseConfiguration.create();
		}

		String[] host_port = Utils
				.get_hbase_connection_string("/data/soft/MonitorServer/WEB-INF/config.properties");
		System.out.println("==============get hbase connstr:" + host_port[0]
				+ host_port[1]);
		// conf.set("hbase.zookeeper.quorum", "s31-v03.oupeng.in");
		conf.set("hbase.zookeeper.quorum", host_port[0]);
		conf.set("hbase.zookeeper.property.clientPort", host_port[1]);
		// conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("zookeeper.znode.parent", "/hbase-unsecure");

		if (this.admin == null) {
			this.admin = new HBaseAdmin(conf);
		}
	}

	public void htest() throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		logger.debug("-----------------------------------hbase test  begin--------------------------");

		HTableDescriptor[] res = admin.listTables();
		for (HTableDescriptor t : res) {
			System.out.println(t.getNameAsString());
			logger.debug("-----------------------------------hbase test--------------------------");
			logger.debug(t.getNameAsString());
		}

		logger.debug("-----------------------------------hbase test  end--------------------------");
	}

	public boolean isTableExist(String tableName) {
		try {
			return admin.tableExists(tableName);
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
		return false;
	}

	// 创建表
	public void createTable(String tableName, String[] columnFamilys)
			throws Exception {
		if (admin.tableExists(tableName)) {
			System.out.println("表已经存在");
			System.exit(0);

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
		admin.close();
	}

	// 获取数据
	public void getRow(String tableName, String row) throws Exception {
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

	// 流量设备信息入库
	public void getFlowTableResult(Calendar cal, String tableNameProtocols,
			String tableNamePorts, PreparedStatement pstmt_protocol,
			PreparedStatement pstmt_port) {
		logger.debug("--------table list:" + tableNameProtocols + ","
				+ tableNamePorts);
		HTable flowTableProtocols = null;
		HTable flowTablePorts = null;
		ResultScanner rs1 = null;
		ResultScanner rs2 = null;

		try {
			flowTableProtocols = new HTable(conf, tableNameProtocols);
			Scan s1 = new Scan();
			Scan s2 = new Scan();
			// 过滤当前小时
			String begin = Utils
					.getHbaseKeyByTimeStamp(cal.getTimeInMillis() + 3600000);
			String end = Utils.getHbaseKeyByTimeStamp(cal.getTimeInMillis());
			s1.setStartRow(begin.getBytes());
			s1.setStopRow(end.getBytes());
			s2.setStartRow(begin.getBytes());
			s2.setStopRow(end.getBytes());

			rs1 = flowTableProtocols.getScanner(s1);
			addFlowHourlyBatch(cal, "protocols", pstmt_protocol, rs1);

			flowTablePorts = new HTable(conf, tableNamePorts);
			rs2 = flowTablePorts.getScanner(s2);
			addFlowHourlyBatch(cal, "ports", pstmt_port, rs2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("=========getFlowTableResult:" + e.getMessage());

		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (rs2 != null) {
				try {
					rs2.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (flowTableProtocols != null) {
				try {
					flowTableProtocols.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (flowTablePorts != null) {
				try {
					flowTablePorts.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void addFlowHourlyBatch(Calendar cal, String tag,
			PreparedStatement pstmt, ResultScanner rs) {

		Map<String, List<Double>> map_res = new HashMap<String, List<Double>>();

		String year = Constant.DEFAULT_YEAR_FORMAT.format(cal.getTime());
		String date = Constant.DEFAULT_DATE_FORMAT.format(cal.getTime());
		String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		String week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);

		String datehour = date.replaceAll("-", "")
				+ (hour.length() <= 1 ? ("0" + hour) : hour);

		logger.info("-----addFlowHourlyBatch,tag=" + tag);

		if (tag.equalsIgnoreCase("protocols")) {
			for (Result r : rs) {

				// TOTAL|TCP|UDP|HTTP|FTP|ICMP|OTHER
				String aflow = Bytes.toString(r.getValue(Bytes.toBytes("tk"),
						Bytes.toBytes("aflow")));
				if (aflow == null && aflow.isEmpty()) {
					continue;
				}
				//
				try {
					String[] lAflow = aflow.split("\\|");
					logger.debug("----- aflow len = " + lAflow.length
							+ ",aflow=" + aflow);
					for (int i = 0; i < lAflow.length; i++) {
						String k = FlowDeviceConfig.getInstance()
								.getProtocolNameByIndex(i);
						double v = Double.valueOf(lAflow[i]);
						if (map_res.containsKey(k)) {
							map_res.get(k).add(v);
						} else {
							List<Double> vl = new ArrayList<Double>();
							vl.add(v);
							map_res.put(k, vl);
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
			// TOTAL|TCP|UDP|HTTP|FTP|ICMP|OTHER
			if (!map_res.isEmpty()) {
				for (Map.Entry<String, List<Double>> en : map_res.entrySet()) {
					double aflow = 0.0;
					for (double d : en.getValue()) {
						aflow += d;
					}
					aflow = aflow / en.getValue().size();

					updateFlowStatement(pstmt, year, month, week, date, hour,
							datehour, en.getKey(), aflow);

				}
			}
		} else {
			logger.debug("----22222222222222222222-");
			for (Result r : rs) {
				boolean hasWflow = r.containsColumn(Bytes.toBytes("tk"),
						Bytes.toBytes("wflow"));

				if (!hasWflow) {
					logger.error("=====222 no wflow data !!!");
					continue;
				}
				// 2:0|4:0|5:0
				String wflow = Bytes.toString(r.getValue(Bytes.toBytes("tk"),
						Bytes.toBytes("wflow")));
				if (wflow == null && wflow.isEmpty()) {
					logger.error("====222 wflow is empty!!!");
					continue;
				}
				String[] lWflow = wflow.split("\\|");
				logger.debug("-----222 wflow len = " + lWflow.length);
				for (int i = 0; i < lWflow.length; i++) {
					if (!lWflow[i].contains(":")) {
						logger.debug("----- wflow no ':' ");
						continue;
					}
					String k = lWflow[i].split(":")[0];
					double v = Double.valueOf(lWflow[i].split(":")[1]);
					if (map_res.containsKey(k)) {
						map_res.get(k).add(v);
					} else {
						List<Double> vl = new ArrayList<Double>();
						vl.add(v);
						map_res.put(k, vl);
					}

				}
			}
			logger.debug("----22222222222222222222-------------end-");
			if (!map_res.isEmpty()) {
				for (Map.Entry<String, List<Double>> en : map_res.entrySet()) {
					double aflow = 0.0;
					for (double d : en.getValue()) {
						aflow += d;
					}
					aflow = aflow / en.getValue().size();
					updateFlowStatement(pstmt, year, month, week, date, hour,
							datehour, en.getKey(), aflow);
				}
			}
		}

	}

	private static void updateFlowStatement(PreparedStatement pstmt,
			String year, String month, String week, String date, String hour,
			String datehour, String type, double aflow) {
		try {
			pstmt.setInt(1, Integer.parseInt(year));
			pstmt.setInt(2, Integer.parseInt(month));
			pstmt.setInt(3, Integer.parseInt(week));
			pstmt.setString(4, date);
			pstmt.setInt(5, Integer.parseInt(hour));
			pstmt.setInt(6, Integer.parseInt(datehour));
			pstmt.setString(7, type);
			pstmt.setDouble(8, aflow);

			pstmt.addBatch();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取所有数据
	public void getAllRows(String tableName) throws Exception {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		scan.setCaching(200);
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

	// 过滤器，获取所有数据
	public void filterRows(String tableName) throws Exception {
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

	public void getLogStat(Map<String, HourlyLogVO> mapsrc,
			Map<String, HourlyLogVO> mapdst, List<String> tables, Calendar cal) {
		HTable table;
		Scan scan;
		for (String tname : tables) {
			try {
				if (!isTableExist(tname)) {
					logger.info("!!!!!!table not exist:" + tname);
					continue;
				}

				logger.debug("-----getLogStat: begin reading table " + tname);

				table = new HTable(conf, tname);
				scan = new Scan();
				scan.setCaching(500);
				String datehour = Constant.DEFAULT_DATE_HOUR_FORMAT.format(
						cal.getTime()).replace("-", "");
				// 过滤当前小时
				String begin = Utils.getHbaseKeyByTimeStamp(cal
						.getTimeInMillis() + 3600000);
				String end = Utils
						.getHbaseKeyByTimeStamp(cal.getTimeInMillis());
				scan.setStartRow(begin.getBytes());
				scan.setStopRow(end.getBytes());
				ResultScanner results = table.getScanner(scan);
				for (Result result : results) {
					String row = new String(new String(result.getRow()));
					String dip = "";
					if (result.containsColumn("log".getBytes(),
							"dst_ip".getBytes())) {
						dip = new String(result.getValue("log".getBytes(),
								"dst_ip".getBytes()));
					}

					String sip = "";
					if (result.containsColumn("log".getBytes(),
							"src_ip".getBytes())) {
						sip = new String(result.getValue("log".getBytes(),
								"src_ip".getBytes()));
					}

					if (sip.isEmpty() || dip.isEmpty() || sip.contains(".")
							|| dip.contains(".")) {
						logger.info("!!!! ip format :sip=" + sip + ",dip="
								+ dip);
						continue;
					}
					Long sip_l = Long.parseLong(sip);
					Long dip_l = Long.parseLong(dip);
					String sip_s = Utils.longToIp(sip_l);
					String dip_s = Utils.longToIp(dip_l);
					logger.info("=============sip,dip,sip_s,dip_s=" + sip + ","
							+ dip + "," + sip_s + "," + dip_s);
					String type = getTypeName(new String(result.getValue(
							"log".getBytes(), "type".getBytes())));
					String code = TypeListLoad.getInstance()
							.getCodeByName(type);

					String key_src = datehour + ":" + sip + ":" + code;
					String key_dst = datehour + ":" + dip + ":" + code;
					if (!mapsrc.containsKey(key_src)) {
						HourlyLogVO vo = new HourlyLogVO(datehour, sip_s,
								sip_l, type, code, 1L);
						mapsrc.put(key_src, vo);
					} else {
						HourlyLogVO vo = mapsrc.get(key_src);
						vo.setCount(vo.getCount() + 1);
					}

					if (!mapdst.containsKey(key_dst)) {
						HourlyLogVO vo = new HourlyLogVO(datehour, dip_s,
								dip_l, type, code, 1L);
						mapdst.put(key_dst, vo);
					} else {
						HourlyLogVO vo = mapdst.get(key_dst);
						vo.setCount(vo.getCount() + 1);
					}
				}

				table.close();
				logger.debug("-----getLogStat: end reading table " + tname);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("!!!!!!!ParseException:exit.");
				return;
			}

		}

	}

	public static String getTypeName(String type) {
		String code = TypeListLoad.getInstance().getCodeByName(type);
		if (code.isEmpty()) {
			String name = TypeListLoad.getInstance().getNameByID(type);
			if (name == null || name.isEmpty()) {
				return type;
			} else {
				return name;
			}
		} else {
			return type;
		}
	}

	public static String getTypeCode(String type) {
		String code = TypeListLoad.getInstance().getCodeByName(type);
		if (code.isEmpty()) {
			String name = TypeListLoad.getInstance().getNameByID(type);
			if (name == null || name.isEmpty()) {
				return "";
			} else {
				return type;
			}
		} else {
			return code;
		}
	}

	public List<Map<String, String>> get_space_origin_list(long timeFrom,
			long limit, List<String> type_names) throws IOException,
			ParseException {

		if (String.valueOf(timeFrom).length() == 10) {
			timeFrom = timeFrom * 1000;
		}
		logger.info("-------get request origin list:" + timeFrom + ",limit="
				+ limit + ",type=" + Arrays.toString(type_names.toArray()));

		List<Map<String, String>> res = new ArrayList<Map<String, String>>();
		Collection<DeviceVO> devices = DeviceConfigLoad.getInstance()
				.getDevices();

		HTable table;
		Scan scan;
		Map<String, String> tmp;
		int tip = 0;
		for (DeviceVO dev : devices) {
			logger.info("------- at device " + tip);
			String tname = dev.getGuid() + "_log";
			// if (!isTableExist(tname)) {
			// logger.info("===htable  not exist:" + tname);
			// continue;
			// }
			table = new HTable(conf, tname);
			scan = new Scan();
			scan.setCaching(500);
			String begin = Utils.getHbaseKeyByTimeStamp(System
					.currentTimeMillis() - 20000);
			String end = Utils.getHbaseKeyByTimeStamp(timeFrom);
			scan.setStartRow(begin.getBytes());
			scan.setStopRow(end.getBytes());
			ResultScanner results = table.getScanner(scan);
			for (Result result : results) {
				tmp = new HashMap<String, String>();
				// String row = new String(new String(result.getRow()));
				String dip = "";
				if (result
						.containsColumn("log".getBytes(), "dst_ip".getBytes())) {
					dip = new String(result.getValue("log".getBytes(),
							"dst_ip".getBytes()));
				}

				String sip = "";
				if (result
						.containsColumn("log".getBytes(), "src_ip".getBytes())) {
					sip = new String(result.getValue("log".getBytes(),
							"src_ip".getBytes()));
				}

				String msg_data = "";
				if (result
						.containsColumn("log".getBytes(), "logstr".getBytes())) {
					msg_data = new String(result.getValue("log".getBytes(),
							"logstr".getBytes()));
				}

				String event_type = getTypeName(new String(result.getValue(
						"log".getBytes(), "type".getBytes())));

				// 类型过滤type_names
				if (type_names != null && !type_names.isEmpty()) {
					if (!type_names.contains(event_type)) {
						continue;
					}
				}

				IpInfo sinfo = IPParserLoad.getInstance().parseIP(sip);
				IpInfo dinfo = IPParserLoad.getInstance().parseIP(dip);

				tmp.put("dip", dinfo.getIp().toString());
				tmp.put("sip", sinfo.getIp().toString());
				tmp.put("msg_title", event_type + "(" + sinfo.getCountry()
						+ "->" + dinfo.getCountry() + ")");
				tmp.put("msg_data", msg_data);
				tmp.put("d_country_code", dinfo.getCountry_code());
				tmp.put("d_country_name", dinfo.getCountry());
				tmp.put("d_province_code", dinfo.getProvince_code());
				tmp.put("d_province_name", dinfo.getProvince());
				tmp.put("d_city_code", dinfo.getCity_code());
				tmp.put("d_city_name", dinfo.getCity());
				tmp.put("s_country_code", sinfo.getCountry_code());
				tmp.put("s_country_name", sinfo.getCountry());
				tmp.put("s_province_code", sinfo.getProvince_code());
				tmp.put("s_province_name", sinfo.getProvince());
				tmp.put("s_city_code", sinfo.getCity_code());
				tmp.put("s_city_name", sinfo.getCity());

				
				String row = new String(new String(result.getRow()));
				String time = Utils.get_date_string_from_hbase_key(row);
				
				logger.info("------- got record:" +"time:"+time+",sip:"+sip+",dip:"+dip+sinfo.getCountry()
						+ sinfo.getProvince() + "-->" + dinfo.getCountry()
						+ dinfo.getProvince() + "," + event_type);
				res.add(tmp);

				if (res.size() >= limit) {
					logger.info("------- limit got,exiting.");
					break;
				}
			}
			table.close();
			tip++;
		}

		logger.debug("---------size of get_space_origin_list:" + res.size());
		return res;
	}

	public List<Object> get_space_key_list(long timeFrom, long limit,
			String device_ids, List<String> type_names, String src_ips,
			String dst_ips) throws Exception {
		if (String.valueOf(timeFrom).length() == 10) {
			timeFrom = timeFrom * 1000;
		}
		List<Object> res = new ArrayList<Object>();
		Collection<DeviceVO> devices;
		if (device_ids != null && !device_ids.isEmpty()) {
			String[] ids = device_ids.trim().split(",");
			devices = new HashSet<DeviceVO>();
			for (String tmp : ids) {
				DeviceVO vvo = DeviceConfigLoad.getInstance()
						.getDeviceByID(tmp);
				if (vvo != null) {
					devices.add(vvo);
				}
			}
		} else {
			devices = DeviceConfigLoad.getInstance().getDevices();
		}

		HTable table;
		Scan scan;
		EventGlobalVO tmp;
		for (DeviceVO dev : devices) {
			String tname = dev.getGuid() + "_log";
			// if (!isTableExist(tname)) {
			// logger.info("===htable  not exist:" + tname);
			// continue;
			// }
			table = new HTable(conf, tname);
			scan = new Scan();
			scan.setCaching(500);
			String begin = Utils.getHbaseKeyByTimeStamp(System
					.currentTimeMillis());
			String end = Utils.getHbaseKeyByTimeStamp(timeFrom);
			scan.setStartRow(begin.getBytes());
			scan.setStopRow(end.getBytes());
			ResultScanner results = table.getScanner(scan);
			for (Result result : results) {
				tmp = new EventGlobalVO();
				tmp.setDeviceName(dev.getName());
				String row = new String(new String(result.getRow()));
				String time = Utils.get_date_string_from_hbase_key(row);
				tmp.setDate(time);

				String dip = "";
				if (result
						.containsColumn("log".getBytes(), "dst_ip".getBytes())) {
					dip = new String(result.getValue("log".getBytes(),
							"dst_ip".getBytes()));
				}

				String sip = "";
				if (result
						.containsColumn("log".getBytes(), "src_ip".getBytes())) {
					sip = new String(result.getValue("log".getBytes(),
							"src_ip".getBytes()));
				}

				String event_type = getTypeName(new String(result.getValue(
						"log".getBytes(), "type".getBytes())));
				// 源ip过滤
				if (src_ips != null && !src_ips.isEmpty()) {
					List<String> src_ip_list = Arrays.asList(src_ips.trim()
							.split(","));
					if (sip.isEmpty() || !Utils.IpInRanges(sip, src_ip_list)) {
						continue;
					}
				}

				// 目标ip过滤
				if (dst_ips != null && !dst_ips.isEmpty()) {
					List<String> dst_ip_list = Arrays.asList(dst_ips.trim()
							.split(","));
					if (dip.isEmpty() || !Utils.IpInRanges(dip, dst_ip_list)) {
						continue;
					}
				}

				// 类型过滤type_names
				if (type_names != null && !type_names.isEmpty()) {
					if (!type_names.contains(event_type)) {
						continue;
					}
				}

				String dst_port = "";
				if (result.containsColumn("log".getBytes(),
						"dst_port".getBytes())) {
					dst_port = new String(result.getValue("log".getBytes(),
							"dst_port".getBytes()));
				}

				String src_port = "";
				if (result.containsColumn("log".getBytes(),
						"src_port".getBytes())) {
					src_port = new String(result.getValue("log".getBytes(),
							"src_port".getBytes()));
				}

				String protocol_type = "";
				if (result.containsColumn("log".getBytes(),
						"protocol_type".getBytes())) {
					protocol_type = new String(result.getValue(
							"log".getBytes(), "protocol_type".getBytes()));
				}

				tmp.setDst_port(dst_port);

				tmp.setLevel(new String(result.getValue("log".getBytes(),
						"level".getBytes())));
				tmp.setLogstr(new String(result.getValue("log".getBytes(),
						"logstr".getBytes())));
				tmp.setProtocol_type(protocol_type);

				tmp.setSrc_port(src_port);
				tmp.setType(event_type);
				tmp.setName(event_type);
				IpInfo sinfo = IPParserLoad.getInstance().parseIP(sip);
				IpInfo dinfo = IPParserLoad.getInstance().parseIP(dip);

				tmp.setSrc_ip(sinfo.getIp().toString());
				tmp.setDst_ip(dinfo.getIp().toString());

				tmp.setSrc_name(sinfo.getCountry() + "/" + sinfo.getProvince());
				tmp.setDst_name(dinfo.getCountry() + "/" + dinfo.getProvince());
				res.add(tmp);

				if (res.size() >= limit) {
					break;
				}
			}
			table.close();
		}

		logger.debug("---------size of get_space_key_list:" + res.size());
		return res;
	}

	public List<Object> get_global_list(List<String> types, String level,
			List<String> src_ips, List<String> dst_ips, Long lbegin, Long lend,
			long limit) throws IOException, ParseException {

		// key=tmp.getCode()+"_"+tmp.getSrc_ip()+"_"+tmp.getDst_ip();
		Map<String, TypeGlobalVO> res_map = new HashMap<String, TypeGlobalVO>();

		Collection<DeviceVO> devices = DeviceConfigLoad.getInstance()
				.getDevices();
		HTable table;
		Scan scan;
		TypeGlobalVO tmp;

		FilterList list = null;
		if (level != null && !level.isEmpty()) {
			list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
			SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
					"log".getBytes(), "level".getBytes(), CompareOp.EQUAL,
					Bytes.toBytes(level));
			list.addFilter(filter1);
		}

		if (types != null && !types.isEmpty()) {
			String temp = types.get(0);
			if (!temp.equalsIgnoreCase("1000")) {
				if (list == null) {
					list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
				}
				SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
						"log".getBytes(), "type".getBytes(), CompareOp.EQUAL,
						Bytes.toBytes(temp));
				list.addFilter(filter1);
			}
		}

		if (list == null) {
			logger.debug("-----------FilterList is null.");
		} else {
			logger.debug("-----------FilterList not null.");
		}

		for (DeviceVO dev : devices) {
			String tname = dev.getGuid() + "_log";
			// if (!isTableExist(tname)) {
			// logger.info("===htable  not exist:" + tname);
			// continue;
			// }
			table = new HTable(conf, tname);
			scan = new Scan();

			if (list != null) {
				scan.setFilter(list);
			}

			scan.setCaching(500);
			String begin = Utils.getHbaseKeyByTimeStamp(lend);
			String end = Utils.getHbaseKeyByTimeStamp(lbegin);
			logger.debug("----get_global_list:setStartRow=" + begin
					+ ",setStopRow=" + end);
			scan.setStartRow(begin.getBytes());
			scan.setStopRow(end.getBytes());
			ResultScanner results = table.getScanner(scan);
			for (Result result : results) {
				logger.debug("--reading data...");
				tmp = new TypeGlobalVO();
				tmp.setDeviceName(dev.getName());
				String row = new String(result.getRow());
				tmp.setDate(Utils.get_date_string_from_hbase_key(row));

				String sip = "";
				try {
					sip = new String(result.getValue("log".getBytes(),
							"src_ip".getBytes()));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				}

				if (src_ips != null && !src_ips.isEmpty()) {
					if (!Utils.IpInRanges(sip, src_ips)) {
						continue;
					}
				}

				String dip = "";
				try {
					dip = new String(result.getValue("log".getBytes(),
							"dst_ip".getBytes()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				if (dst_ips != null && !dst_ips.isEmpty()) {
					if (!Utils.IpInRanges(dip, dst_ips)) {
						continue;
					}
				}

				// String code, String name, String detail, String level,
				// String src_ip, String src_port, String src_name, String
				// dst_ip,
				// String dst_port, String dst_name, String status, int count

				String name = getTypeName(new String(result.getValue(
						"log".getBytes(), "type".getBytes())));
				EventTypeVO vo = TypeListLoad.getInstance().getTypeByName(name);
				if (vo == null) {
					logger.info("========get type name not exist:" + name);
					continue;
				}

				tmp.setCode(vo.getCode());
				tmp.setName(name);
				tmp.setLevel(vo.getLevel());

				String dst_port = "";
				if (result.containsColumn("log".getBytes(),
						"dst_port".getBytes())) {
					dst_port = new String(result.getValue("log".getBytes(),
							"dst_port".getBytes()));
				}

				String src_port = "";
				if (result.containsColumn("log".getBytes(),
						"src_port".getBytes())) {
					src_port = new String(result.getValue("log".getBytes(),
							"src_port".getBytes()));
				}

				String protocol_type = "";
				if (result.containsColumn("log".getBytes(),
						"protocol_type".getBytes())) {
					protocol_type = new String(result.getValue(
							"log".getBytes(), "protocol_type".getBytes()));
				}

				tmp.setDst_port(dst_port);

				tmp.setLevel(new String(result.getValue("log".getBytes(),
						"level".getBytes())));
				tmp.setSrc_port(src_port);

				tmp.setLogstr(new String(result.getValue("log".getBytes(),
						"logstr".getBytes())));

				IpInfo sinfo = IPParserLoad.getInstance().parseIP(sip);
				IpInfo dinfo = IPParserLoad.getInstance().parseIP(dip);

				tmp.setSrc_ip(sinfo.getIp().toString());
				tmp.setDst_ip(dinfo.getIp().toString());

				tmp.setSrc_name(sinfo.getCountry() + " " + sinfo.getProvince()
						+ " " + sinfo.getCity());
				tmp.setDst_name(dinfo.getCountry() + " " + dinfo.getProvince()
						+ " " + dinfo.getCity());
				tmp.setCount(1);
				tmp.setStatus("");

				String key = tmp.getCode() + "_" + tmp.getSrc_ip() + "_"
						+ tmp.getDst_ip();
				if (res_map.containsKey(key)) {
					res_map.get(key).setCount(res_map.get(key).getCount() + 1);
				} else {
					res_map.put(key, tmp);
				}

				if (res_map.size() >= limit) {
					break;
				}
			}
			table.close();
		}

		logger.debug("---------size of get_global_list:" + res_map.size());
		return new ArrayList(res_map.values());
	}

	/**
	 * 
	 * @param timeFrom
	 * @param limit
	 * @param src_country
	 *            来源国家名称
	 * @param ips
	 *            ip段/组范围字符串
	 * @return
	 * @throws Exception
	 */
	public List<Object> get_space_part_list(long timeFrom, long limit,
			String src_country, List<String> ips) throws Exception {
		List<Object> res = new ArrayList<Object>();
		Collection<DeviceVO> devices = DeviceConfigLoad.getInstance()
				.getDevices();
		HTable table;
		Scan scan;
		EventGlobalVO tmp;
		for (DeviceVO dev : devices) {
			String tname = dev.getGuid() + "_log";
			// if (!isTableExist(tname)) {
			// logger.info("===htable  not exist:" + tname);
			// continue;
			// }
			table = new HTable(conf, tname);
			scan = new Scan();
			scan.setCaching(500);
			String begin = Utils.getHbaseKeyByTimeStamp(System
					.currentTimeMillis());
			String end = Utils.getHbaseKeyByTimeStamp(timeFrom);
			scan.setStartRow(begin.getBytes());
			scan.setStopRow(end.getBytes());
			ResultScanner results = table.getScanner(scan);
			for (Result result : results) {
				tmp = new EventGlobalVO();
				tmp.setDeviceName(dev.getName());
				String row = new String(new String(result.getRow()));
				String time = Utils.get_date_string_from_hbase_key(row);
				tmp.setDate(time);

				String dip = "";
				if (result
						.containsColumn("log".getBytes(), "dst_ip".getBytes())) {
					dip = new String(result.getValue("log".getBytes(),
							"dst_ip".getBytes()));
				}

				String sip = "";
				if (result
						.containsColumn("log".getBytes(), "src_ip".getBytes())) {
					sip = new String(result.getValue("log".getBytes(),
							"src_ip".getBytes()));
				}

				if (src_country != null && !src_country.isEmpty()) {
					IpInfo info = IPParserLoad.getInstance().parseIP(sip);
					if (!src_country.equalsIgnoreCase(info.getCountry())) {
						continue;
					}
				}

				if (ips != null && !ips.isEmpty()) {
					if (dip.isEmpty() || !Utils.IpInRanges(dip, ips)) {
						continue;
					}
				}

				String dst_port = "";
				if (result.containsColumn("log".getBytes(),
						"dst_port".getBytes())) {
					dst_port = new String(result.getValue("log".getBytes(),
							"dst_port".getBytes()));
				}

				String src_port = "";
				if (result.containsColumn("log".getBytes(),
						"src_port".getBytes())) {
					src_port = new String(result.getValue("log".getBytes(),
							"src_port".getBytes()));
				}

				String protocol_type = "";
				if (result.containsColumn("log".getBytes(),
						"protocol_type".getBytes())) {
					protocol_type = new String(result.getValue(
							"log".getBytes(), "protocol_type".getBytes()));
				}

				tmp.setDst_port(dst_port);

				tmp.setLevel(new String(result.getValue("log".getBytes(),
						"level".getBytes())));
				tmp.setLogstr(new String(result.getValue("log".getBytes(),
						"logstr".getBytes())));
				tmp.setProtocol_type(protocol_type);
				tmp.setSrc_port(src_port);
				tmp.setType(getTypeName(new String(result.getValue(
						"log".getBytes(), "type".getBytes()))));
				tmp.setName(tmp.getType());

				IpInfo sinfo = IPParserLoad.getInstance().parseIP(sip);
				IpInfo dinfo = IPParserLoad.getInstance().parseIP(dip);

				tmp.setSrc_ip(sinfo.getIp().toString());
				tmp.setDst_ip(dinfo.getIp().toString());

				tmp.setSrc_name(sinfo.getCountry() + " " + sinfo.getProvince()
						+ " " + sinfo.getCity());
				tmp.setDst_name(dinfo.getCountry() + " " + dinfo.getProvince()
						+ " " + dinfo.getCity());
				res.add(tmp);

				if (res.size() >= limit) {
					break;
				}
			}
			table.close();
		}

		logger.debug("---------size of get_space_part_list:" + res.size());
		return res;
	}

	// 给定时间，数量限制，获取记录
	public List<Object> get_space_global_logs(long timeFrom, long limit)
			throws Exception {

		logger.debug("------------get_space_global_logs:timeFrom=" + timeFrom
				+ ",limit=" + limit);
		List<Object> res = new ArrayList<Object>();
		Collection<DeviceVO> devices = DeviceConfigLoad.getInstance()
				.getDevices();
		HTable table;
		Scan scan;
		EventGlobalVO tmp;
		logger.debug("------devices size = " + devices.size() + ":" + devices);
		for (DeviceVO dev : devices) {
			String tname = dev.getGuid() + "_log";
			// if (!isTableExist(tname)) {
			// logger.info("===htable  not exist:" + tname);
			// continue;
			// }
			table = new HTable(conf, tname);
			scan = new Scan();
			scan.setCaching(500);
			String begin = Utils.getHbaseKeyByTimeStamp(System
					.currentTimeMillis());
			String end = Utils.getHbaseKeyByTimeStamp(timeFrom);

			logger.debug("----setStartRow=" + begin + ",setStopRow=" + end);

			scan.setStartRow(begin.getBytes());
			scan.setStopRow(end.getBytes());
			ResultScanner results = table.getScanner(scan);
			for (Result result : results) {
				tmp = new EventGlobalVO();
				String row = new String(new String(result.getRow()));
				String time = Utils.get_date_string_from_hbase_key(row);
				tmp.setDate(time);
				tmp.setDeviceName(dev.getName());

				String dip = "";
				if (result
						.containsColumn("log".getBytes(), "dst_ip".getBytes())) {
					dip = new String(result.getValue("log".getBytes(),
							"dst_ip".getBytes()));
				}

				String sip = "";
				if (result
						.containsColumn("log".getBytes(), "src_ip".getBytes())) {
					sip = new String(result.getValue("log".getBytes(),
							"src_ip".getBytes()));
				}

				String dst_port = "";
				if (result.containsColumn("log".getBytes(),
						"dst_port".getBytes())) {
					dst_port = new String(result.getValue("log".getBytes(),
							"dst_port".getBytes()));
				}

				String src_port = "";
				if (result.containsColumn("log".getBytes(),
						"src_port".getBytes())) {
					src_port = new String(result.getValue("log".getBytes(),
							"src_port".getBytes()));
				}

				String protocol_type = "";
				if (result.containsColumn("log".getBytes(),
						"protocol_type".getBytes())) {
					protocol_type = new String(result.getValue(
							"log".getBytes(), "protocol_type".getBytes()));
				}

				tmp.setDst_port(dst_port);

				tmp.setLevel(new String(result.getValue("log".getBytes(),
						"level".getBytes())));
				tmp.setLogstr(new String(result.getValue("log".getBytes(),
						"logstr".getBytes())));
				tmp.setProtocol_type(protocol_type);
				tmp.setSrc_port(src_port);
				tmp.setType(getTypeName(new String(result.getValue(
						"log".getBytes(), "type".getBytes()))));
				tmp.setName(tmp.getType());

				IpInfo sinfo = IPParserLoad.getInstance().parseIP(sip);
				IpInfo dinfo = IPParserLoad.getInstance().parseIP(dip);

				tmp.setSrc_ip(sinfo.getIp().toString());
				tmp.setDst_ip(dinfo.getIp().toString());

				tmp.setSrc_name(sinfo.getCountry() + " " + sinfo.getProvince()
						+ " " + sinfo.getCity());
				tmp.setDst_name(dinfo.getCountry() + " " + dinfo.getProvince()
						+ " " + dinfo.getCity());
				res.add(tmp);

				if (res.size() >= limit) {
					break;
				}
			}
			table.close();
		}
		logger.debug("---------size of get_space_global_logs:" + res.size());
		return res;
	}

	public static boolean getDataFromHbase() {
		String s = BaseConfigLoad.getInstance().getConfigData(
				Constant.MONITOR_DATA_FROM_HBASE, "false");
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(String args[]) {
		System.out.println("---------------test-------------");
		HbaseBaseOP hop = HbaseBaseOP.getInstance();
	}

	public List<Object> get_device_status_list(Set<String> devices) {
		// TODO Auto-generated method stub
		List<Object> res = new ArrayList<Object>();
		HTable table = null;

		logger.debug("-----------get_device_status_list,devices=" + devices);
		for (String id : devices) {
			String table_name = id + "_tk";
			try {
				table = new HTable(conf, table_name);
				Scan scan = new Scan();
				scan.setCaching(1);
				logger.debug("-----------table_name=" + table_name);
				ResultScanner results = table.getScanner(scan);
				for (Result result : results) {
					logger.debug("------------------read device status------------");
					String row = new String(new String(result.getRow()));
					String time = String.valueOf(Utils
							.get_timestamp_from_hbase_key(row));
					String ip = new String(result.getValue("tk".getBytes(),
							"ip".getBytes()));

					String adisk = "0";
					if (result.containsColumn("tk".getBytes(),
							"adisk".getBytes())) {
						adisk = new String(result.getValue("tk".getBytes(),
								"adisk".getBytes()));
					}

					String fdisk = "0";
					if (result.containsColumn("tk".getBytes(),
							"fdisk".getBytes())) {
						fdisk = new String(result.getValue("tk".getBytes(),
								"fdisk".getBytes()));
					}

					String amem = "0";
					if (result.containsColumn("tk".getBytes(),
							"amem".getBytes())) {
						amem = new String(result.getValue("tk".getBytes(),
								"amem".getBytes()));
					}

					String umem = "0";
					if (result.containsColumn("tk".getBytes(),
							"umem".getBytes())) {
						umem = new String(result.getValue("tk".getBytes(),
								"umem".getBytes()));
					}

					String cpu = "0";
					if (result
							.containsColumn("tk".getBytes(), "cpu".getBytes())) {
						cpu = new String(result.getValue("tk".getBytes(),
								"cpu".getBytes()));
					}

					String aflow = "";
					if (result.containsColumn("tk".getBytes(),
							"aflow".getBytes())) {
						aflow = new String(result.getValue("tk".getBytes(),
								"aflow".getBytes()));
					}

					String wflow = "";
					if (result.containsColumn("tk".getBytes(),
							"wflow".getBytes())) {
						wflow = new String(result.getValue("tk".getBytes(),
								"wflow".getBytes()));
					}

					DeviceStatusVO vo = new DeviceStatusVO(id, ip, cpu, amem,
							umem, adisk, fdisk, aflow, wflow, time);
					res.add(vo);

					break;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("==========" + e.getMessage());
			} finally {
				try {
					table.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error("==========" + e.getMessage());
				}
			}

		}
		return res;
	}
}
