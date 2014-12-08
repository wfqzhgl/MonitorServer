package com.it.hadoop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.it.config.DeviceConfigLoad;
import com.it.config.IPParserLoad;
import com.it.config.load.ConfigLoad;
import com.it.util.Constant;
import com.it.vo.HourlyLogVO;
import com.it.vo.IpInfo;

public class HourlyLogJob extends ConfigLoad {

	private static final HourlyLogJob instance = new HourlyLogJob();

	public static HourlyLogJob getInstance() {
		return instance;
	}

	private int pastHour = 1;

	private void saveToDB(Calendar cal) {
		if (!HbaseBaseOP.getDataFromHbase()) {
			logger.info("---HourlyLogJob,getDataFromHbase = false, exiting.");
			return;
		}

		Map<String, HourlyLogVO> mapsrc = new HashMap<String, HourlyLogVO>();
		Map<String, HourlyLogVO> mapdst = new HashMap<String, HourlyLogVO>();

		getLogStatFromHbase(cal, mapsrc, mapdst);

		execHourlyLog(cal, mapsrc, mapdst);

	}

	private void execHourlyLog(Calendar cal, Map<String, HourlyLogVO> mapsrc,
			Map<String, HourlyLogVO> mapdst) {

		PreparedStatement pstmt1 = null;
		Connection conn = null;

		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (mapsrc == null || mapsrc.isEmpty()) {
			logger.info("----------execHourlyLog: mapsrc is empty!!!");
		} else {
			logger.info("----------execHourlyLog: mapsrc size=" + mapsrc.size());
			// delete first
			try {
				pstmt1 = conn.prepareStatement(Constant.SQL_DEL_SRC_HOURLY);
				pstmt1.setString(1,
						Constant.DEFAULT_DATE_FORMAT.format(cal.getTime()));
				pstmt1.setInt(2, cal.get(Calendar.HOUR_OF_DAY));
				pstmt1.execute();
				conn.commit();
			} catch (SQLException e) {
				logger.error("SQL_DEL_SRC_HOURLY SQLException: ", e);
			} finally {
				try {
					pstmt1.close();
				} catch (SQLException e) {
					logger.error("SQLException: ", e);
				}
			}

			// insert data
			PreparedStatement pstmt = null;
			try {
				logger.info("---begin save mapsrc to db...");

				pstmt = conn.prepareStatement(Constant.SQL_SRC_HOURLY);

				for (Map.Entry<String, HourlyLogVO> en : mapsrc.entrySet()) {

					String year = Constant.DEFAULT_YEAR_FORMAT.format(cal
							.getTime());
					String date = Constant.DEFAULT_DATE_FORMAT.format(cal
							.getTime());
					String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
					String week = String
							.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
					String month = String.valueOf(cal.get(Calendar.MONTH));
					pstmt.setInt(1, Integer.parseInt(year));
					pstmt.setInt(2, Integer.parseInt(month));
					pstmt.setInt(3, Integer.parseInt(week));
					pstmt.setString(4, date);
					pstmt.setInt(5, Integer.parseInt(hour));
					pstmt.setInt(6,
							Integer.parseInt(en.getValue().getDatehour()));

					pstmt.setLong(7, en.getValue().getLip());
					pstmt.setString(8, en.getValue().getIp());
					IpInfo ipinfo = IPParserLoad.getInstance().parseIP(
							en.getValue().getIp());
					pstmt.setString(9, ipinfo.getCountry());
					pstmt.setString(10, ipinfo.getProvince());
					pstmt.setString(11, ipinfo.getCity());
					pstmt.setString(12, en.getValue().getTypeName());
					pstmt.setString(13, en.getValue().getTypeCode());
					pstmt.setLong(14, en.getValue().getCount());
					logger.info("--------check-----lip,ip="+en.getValue().getLip()+","+en.getValue().getIp());
					pstmt.addBatch();
				}

				pstmt.executeBatch();
				conn.commit();
				logger.info("---end save mapsrc to db...");
			} catch (SQLException e) {
				logger.error("SQLException: ", e);
			} catch (Exception e) {
				logger.error("IOException: ", e);
			} finally {
				try {
					pstmt.close();
					// conn.close();
				} catch (SQLException e) {
					logger.error("SQLException: ", e);
				}
			}

		}

		if (mapdst == null || mapdst.isEmpty()) {
			logger.info("----------execHourlyLog: mapdst is empty!!!");
		} else {
			logger.info("----------execHourlyLog: mapdst size=" + mapsrc.size());
			// delete first
			try {
				pstmt1 = conn.prepareStatement(Constant.SQL_DEL_DST_HOURLY);
				pstmt1.setString(1,
						Constant.DEFAULT_DATE_FORMAT.format(cal.getTime()));
				pstmt1.setInt(2, cal.get(Calendar.HOUR_OF_DAY));
				pstmt1.execute();
				conn.commit();
			} catch (SQLException e) {
				logger.error("SQL_DEL_DST_HOURLY SQLException: ", e);
			} finally {
				try {
					pstmt1.close();
				} catch (SQLException e) {
					logger.error("SQLException: ", e);
				}
			}

			// insert data
			PreparedStatement pstmt = null;
			try {
				logger.info("---begin save mapdst to db...");
				pstmt = conn.prepareStatement(Constant.SQL_DST_HOURLY);
				for (Map.Entry<String, HourlyLogVO> en : mapsrc.entrySet()) {

					String year = Constant.DEFAULT_YEAR_FORMAT.format(cal
							.getTime());
					String date = Constant.DEFAULT_DATE_FORMAT.format(cal
							.getTime());
					String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
					String week = String
							.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
					String month = String.valueOf(cal.get(Calendar.MONTH));
					pstmt.setInt(1, Integer.parseInt(year));
					pstmt.setInt(2, Integer.parseInt(month));
					pstmt.setInt(3, Integer.parseInt(week));
					pstmt.setString(4, date);
					pstmt.setInt(5, Integer.parseInt(hour));
					pstmt.setInt(6,
							Integer.parseInt(en.getValue().getDatehour()));

					pstmt.setLong(7, en.getValue().getLip());
					pstmt.setString(8, en.getValue().getIp());
					IpInfo ipinfo = IPParserLoad.getInstance().parseIP(
							en.getValue().getIp());
					pstmt.setString(9, ipinfo.getCountry());
					pstmt.setString(10, ipinfo.getProvince());
					pstmt.setString(11, ipinfo.getCity());
					pstmt.setString(12, en.getValue().getTypeName());
					pstmt.setString(13, en.getValue().getTypeCode());
					pstmt.setLong(14, en.getValue().getCount());

					pstmt.addBatch();
				}

				pstmt.executeBatch();
				conn.commit();

				logger.info("---end save mapdst to db...");

			} catch (SQLException e) {
				logger.error("SQLException: ", e);
			} catch (Exception e) {
				logger.error("IOException: ", e);
			} finally {
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error("SQLException: ", e);
				}
			}
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getLogStatFromHbase(Calendar cal,
			Map<String, HourlyLogVO> mapsrc, Map<String, HourlyLogVO> mapdst) {

		// get htable names
		List<String> tables = DeviceConfigLoad.getInstance()
				.getLogHtableNames();
		HbaseBaseOP.getInstance().getLogStat(mapsrc, mapdst, tables, cal);
	}

	@Override
	public void load() {
		logger.debug("----HourlyLogJob Load Start...");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// 上一小时
		cal.add(Calendar.HOUR_OF_DAY, -pastHour);
		saveToDB(cal);
		logger.debug("-----HourlyLogJob Load End...");
	}

	public int getPastHour() {
		return pastHour;
	}

	public void setPastHour(int pastHour) {
		this.pastHour = pastHour;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
