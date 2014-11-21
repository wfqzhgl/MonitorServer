package com.it.hadoop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import com.it.config.FlowDeviceConfig;
import com.it.config.load.ConfigLoad;
import com.it.util.Constant;

public class HourlyFlowJob extends ConfigLoad {

	private static final HourlyFlowJob instance = new HourlyFlowJob();

	public static HourlyFlowJob getInstance() {
		return instance;
	}

	private void saveToDB(Calendar cal) {
		if(!HbaseBaseOP.getDataFromHbase()){
			logger.info("getDataFromHbase = false, exiting.");
			return;
		}
		
		execHourlyFlow(cal);

	}

	private void execHourlyFlow(Calendar cal) {
		PreparedStatement pstmt1 = null;
		Connection conn = null;

		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// delete protocol first
		try {
			pstmt1 = conn.prepareStatement(Constant.SQL_DEL_PROTOCOL_HOURLY);
			pstmt1.setString(1, Constant.DEFAULT_DATE_FORMAT.format(cal.getTime()));
			pstmt1.setInt(2, cal.get(Calendar.HOUR_OF_DAY));
			pstmt1.execute();
			conn.commit();
		} catch (SQLException e) {
			logger.error("SQL_DEL_PROTOCOL_HOURLY SQLException: ", e);
		} finally {
			try {
				pstmt1.close();
			} catch (SQLException e) {
				logger.error("SQLException: ", e);
			}
		}

		// delete port first
		try {
			pstmt1 = conn.prepareStatement(Constant.SQL_DEL_PORT_HOURLY);
			pstmt1.setString(1, Constant.DEFAULT_DATE_FORMAT.format(cal.getTime()));
			pstmt1.setInt(2, cal.get(Calendar.HOUR_OF_DAY));
			pstmt1.execute();
			conn.commit();
		} catch (SQLException e) {
			logger.error("SQL_DEL_PORT_HOURLY SQLException: ", e);
		} finally {
			try {
				pstmt1.close();
			} catch (SQLException e) {
				logger.error("SQLException: ", e);
			}
		}

		// insert data
		PreparedStatement pstmt_protocol = null;
		PreparedStatement pstmt_port = null;
		try {
			pstmt_protocol = conn.prepareStatement(Constant.SQL_PROTOCOL_HOURLY);
			pstmt_port = conn.prepareStatement(Constant.SQL_PORT_HOURLY);

			HbaseBaseOP.getInstance().getFlowTableResult(cal,
					FlowDeviceConfig.getInstance().getHTableNameProtocols(),FlowDeviceConfig.getInstance().getHTableNamePorts(), pstmt_protocol, pstmt_port);

			pstmt_protocol.executeBatch();
			pstmt_port.executeBatch();
			conn.commit();
			
		} catch (SQLException e) {
			logger.error("SQLException: ", e);
		} catch (Exception e) {
			logger.error("IOException: ", e);
		} finally {
			try {
				pstmt_protocol.close();
				pstmt_port.close();
				conn.close();
			} catch (SQLException e) {
				logger.error("SQLException: ", e);
			}
		}
	}

	@Override
	public void load() {
		logger.debug("HourlyFlowJob Load Start...");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		
		// 上一小时
		cal.add(Calendar.HOUR_OF_DAY, -1);
		saveToDB(cal);
		logger.debug("HourlyFlowJob Load End...");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
