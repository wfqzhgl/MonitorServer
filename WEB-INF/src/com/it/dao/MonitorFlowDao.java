package com.it.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.it.config.FlowDeviceConfig;
import com.it.util.Constant;
import com.it.vo.FlowChartVO;

@Repository
public class MonitorFlowDao extends BaseDao {

	private static Logger logger = Logger.getLogger(MonitorFlowDao.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<Object> get_flow_sum_list(String range) {
		// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
		List<Object> list = new ArrayList<Object>();
		Calendar calCurrent = Calendar.getInstance();
		calCurrent.set(Calendar.MINUTE, 0);
		calCurrent.set(Calendar.SECOND, 0);
		calCurrent.set(Calendar.MILLISECOND, 0);

		if (range.equalsIgnoreCase("0")) {
			Calendar calBegin = (Calendar) calCurrent.clone();
			Calendar calEnd = (Calendar) calCurrent.clone();
			calEnd.add(Calendar.HOUR_OF_DAY, -1);
			String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT.format(calEnd
					.getTime());
			int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));
			int intBeginHour = 0;
			calBegin.add(Calendar.HOUR_OF_DAY, -23);
			String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
					.format(calBegin.getTime());
			intBeginHour = Integer.parseInt(beginHour.replaceAll("-", ""));
			String sql = "SELECT datehour,sum(flow*3600) as s FROM view.flow_protocol_hourly "
					+ "where protocol='TOTAL' and datehour between  ? and ? "
					+ "group by hour  order by datehour asc;";
			Object[] params = new Object[2];
			params[0] = intBeginHour;
			params[1] = intEndHour;

			final Map<String, Long> mapData = new HashMap<String, Long>();
			jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					mapData.put(rs.getString("datehour"), rs.getLong("s"));
					return null;
				}
			});

			list.add(getFlowChartVO(calBegin, calEnd, range, mapData, "TOTAL",
					"总输入流量"));

		} else if (range.equalsIgnoreCase("1")) {
			Calendar calBegin = (Calendar) calCurrent.clone();
			Calendar calEnd = (Calendar) calCurrent.clone();
			calEnd.add(Calendar.DAY_OF_MONTH, -1);
			String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
					.getTime());
			calBegin.add(Calendar.DAY_OF_MONTH, -7);
			String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
					.getTime());
			String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
					+ "where protocol='TOTAL' and date between  ? and ? "
					+ "group by date  order by date asc;";
			Object[] params = new Object[2];
			params[0] = beginDate;
			params[1] = endDate;

			final Map<String, Long> mapData = new HashMap<String, Long>();
			jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					mapData.put(rs.getString("date"), rs.getLong("s"));
					return null;
				}
			});

			list.add(getFlowChartVO(calBegin, calEnd, range, mapData, "TOTAL",
					"总输入流量"));

		} else if (range.equalsIgnoreCase("2")) {
			Calendar calBegin = (Calendar) calCurrent.clone();
			Calendar calEnd = (Calendar) calCurrent.clone();
			calEnd.add(Calendar.DAY_OF_MONTH, -1);
			String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
					.getTime());
			calBegin.add(Calendar.DAY_OF_MONTH, -30);
			String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
					.getTime());
			String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
					+ "where protocol='TOTAL' and date between  ? and ? "
					+ "group by date  order by date asc;";
			Object[] params = new Object[2];
			params[0] = beginDate;
			params[1] = endDate;

			final Map<String, Long> mapData = new HashMap<String, Long>();
			jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					mapData.put(rs.getString("date"), rs.getLong("s"));
					return null;
				}
			});

			list.add(getFlowChartVO(calBegin, calEnd, range, mapData, "TOTAL",
					"总输入流量"));
		} else if (range.equalsIgnoreCase("3")) {
			Calendar calBegin = (Calendar) calCurrent.clone();
			Calendar calEnd = (Calendar) calCurrent.clone();
			calEnd.add(Calendar.DAY_OF_MONTH, -1);
			String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
					.getTime());
			calBegin.add(Calendar.DAY_OF_MONTH, -90);
			String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
					.getTime());
			String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
					+ "where protocol='TOTAL' and date between  ? and ? "
					+ "group by date  order by date asc;";
			Object[] params = new Object[2];
			params[0] = beginDate;
			params[1] = endDate;

			final Map<String, Long> mapData = new HashMap<String, Long>();
			jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					mapData.put(rs.getString("date"), rs.getLong("s"));
					return null;
				}
			});

			list.add(getFlowChartVO(calBegin, calEnd, range, mapData, "TOTAL",
					"总输入流量"));
		} else if (range.equalsIgnoreCase("4")) {
			Calendar calBegin = (Calendar) calCurrent.clone();
			calBegin.add(Calendar.MONTH, -5);

			Calendar calEnd = (Calendar) calCurrent.clone();
			calEnd.set(Calendar.DAY_OF_MONTH,
					calEnd.getMaximum(Calendar.DAY_OF_MONTH));
			calEnd.set(Calendar.HOUR_OF_DAY, 23);

			calBegin.set(Calendar.DAY_OF_MONTH, 1);
			calBegin.set(Calendar.HOUR_OF_DAY, 0);

			String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT.format(calEnd
					.getTime());
			int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));

			String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
					.format(calBegin.getTime());
			int intBeginHour = Integer.parseInt(beginHour.replaceAll("-", ""));

			String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
					+ "where protocol='TOTAL' and datehour between  ? and ? "
					+ "group by month  order by month asc;";
			Object[] params = new Object[2];
			params[0] = intBeginHour;
			params[1] = intEndHour;

			final Map<String, Long> mapData = new HashMap<String, Long>();
			jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
				public byte[] mapRow(ResultSet rs, int arg1)
						throws SQLException {
					mapData.put(rs.getString("date").split("-")[0] + "-"
							+ rs.getString("date").split("-")[1],
							rs.getLong("s"));
					return null;
				}
			});

			list.add(getFlowChartVO(calBegin, calEnd, range, mapData, "TOTAL",
					"总输入流量"));
		}

		return list;
	}

	// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
	private FlowChartVO getFlowChartVO(Calendar calBegin, Calendar calEnd,
			String range, Map<String, Long> mapData, String code, String name) {

		FlowChartVO vo = new FlowChartVO();
		vo.setCode(code);
		vo.setName(name);

		final List<String> xx = new ArrayList<String>();
		final List<Long> yy = new ArrayList<Long>();
		if (range.equalsIgnoreCase("0")) {

			while (!calBegin.after(calEnd)) {
				String tmpDH = Constant.DEFAULT_DATE_HOUR_FORMAT.format(
						calBegin.getTime()).replaceAll("-", "");

				xx.add(String.valueOf(calBegin.get(Calendar.HOUR_OF_DAY)));
				if (mapData.containsKey(tmpDH)) {
					yy.add(mapData.get(tmpDH));
				} else {
					yy.add(0L);
				}
				calBegin.add(Calendar.HOUR_OF_DAY, 1);
			}
		} else if (range.equalsIgnoreCase("1")) {
			while (!calBegin.after(calEnd)) {
				String tmpDH = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());

				xx.add(String.valueOf(calBegin.get(Calendar.DAY_OF_MONTH)));
				if (mapData.containsKey(tmpDH)) {
					yy.add(mapData.get(tmpDH));
				} else {
					yy.add(0L);
				}
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else if (range.equalsIgnoreCase("2")) {
			while (!calBegin.after(calEnd)) {
				String tmpDH = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());

				xx.add(String.valueOf(calBegin.get(Calendar.DAY_OF_MONTH)));
				if (mapData.containsKey(tmpDH)) {
					yy.add(mapData.get(tmpDH));
				} else {
					yy.add(0L);
				}
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else if (range.equalsIgnoreCase("3")) {
			while (!calBegin.after(calEnd)) {
				String tmpDH = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());

				xx.add(String.valueOf(calBegin.get(Calendar.DAY_OF_MONTH)));
				if (mapData.containsKey(tmpDH)) {
					yy.add(mapData.get(tmpDH));
				} else {
					yy.add(0L);
				}
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else if (range.equalsIgnoreCase("4")) {
			while (!calBegin.after(calEnd)) {
				String tmpDH = Constant.DEFAULT_MONTH_FORMAT.format(calBegin
						.getTime());

				xx.add(String.valueOf(calBegin.get(Calendar.MONTH) + 1));
				if (mapData.containsKey(tmpDH)) {
					yy.add(mapData.get(tmpDH));
				} else {
					yy.add(0L);
				}
				calBegin.add(Calendar.MONTH, 1);
			}
		}

		vo.setX(xx);
		vo.setY(yy);

		return vo;

	}

	public List<Object> get_flow_protocol_list(String range, String protocols) {

		// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
		List<Object> list = new ArrayList<Object>();
		Calendar calCurrent = Calendar.getInstance();
		calCurrent.set(Calendar.MINUTE, 0);
		calCurrent.set(Calendar.SECOND, 0);
		calCurrent.set(Calendar.MILLISECOND, 0);

		for (String protocolName : protocols.split(",")) {

			logger.debug("-----get_flow_protocol_list: protocol =" + protocolName);

			if (range.equalsIgnoreCase("0")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.HOUR_OF_DAY, -1);
				String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calEnd.getTime());
				int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));
				int intBeginHour = 0;
				calBegin.add(Calendar.HOUR_OF_DAY, -23);
				String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calBegin.getTime());
				intBeginHour = Integer.parseInt(beginHour.replaceAll("-", ""));
				String sql = "SELECT datehour,sum(flow*3600) as s FROM view.flow_protocol_hourly "
						+ "where protocol=? and datehour between  ? and ? "
						+ "group by hour  order by datehour asc;";
				Object[] params = new Object[3];
				params[0] = protocolName;
				params[1] = intBeginHour;
				params[2] = intEndHour;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("datehour"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, protocolName,
						protocolName));

			} else if (range.equalsIgnoreCase("1")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -7);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
						+ "where protocol=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = protocolName;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, protocolName,
						protocolName));

			} else if (range.equalsIgnoreCase("2")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -30);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
						+ "where protocol=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = protocolName;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, protocolName,
						protocolName));
			} else if (range.equalsIgnoreCase("3")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -90);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
						+ "where protocol=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = protocolName;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, protocolName,
						protocolName));
			} else if (range.equalsIgnoreCase("4")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				calBegin.add(Calendar.MONTH, -5);

				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.set(Calendar.DAY_OF_MONTH,
						calEnd.getMaximum(Calendar.DAY_OF_MONTH));
				calEnd.set(Calendar.HOUR_OF_DAY, 23);

				calBegin.set(Calendar.DAY_OF_MONTH, 1);
				calBegin.set(Calendar.HOUR_OF_DAY, 0);

				String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calEnd.getTime());
				int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));

				String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calBegin.getTime());
				int intBeginHour = Integer.parseInt(beginHour.replaceAll("-",
						""));

				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_protocol_hourly "
						+ "where protocol=? and datehour between  ? and ? "
						+ "group by month  order by month asc;";
				Object[] params = new Object[3];
				params[0] = protocolName;
				params[1] = intBeginHour;
				params[2] = intEndHour;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date").split("-")[0] + "-"
								+ rs.getString("date").split("-")[1],
								rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, protocolName,
						protocolName));
			}
		}
		return list;
	}

	public List<Object> get_flow_port_list(String range, String ports) {

		// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
		List<Object> list = new ArrayList<Object>();
		Calendar calCurrent = Calendar.getInstance();
		calCurrent.set(Calendar.MINUTE, 0);
		calCurrent.set(Calendar.SECOND, 0);
		calCurrent.set(Calendar.MILLISECOND, 0);

		for (String port : ports.split(",")) {
			String portName = FlowDeviceConfig.getInstance().getNameByPortcode(
					port);

			logger.debug("-----get_flow_port_list: port =" + portName);

			if (range.equalsIgnoreCase("0")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.HOUR_OF_DAY, -1);
				String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calEnd.getTime());
				int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));
				int intBeginHour = 0;
				calBegin.add(Calendar.HOUR_OF_DAY, -23);
				String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calBegin.getTime());
				intBeginHour = Integer.parseInt(beginHour.replaceAll("-", ""));
				String sql = "SELECT datehour,sum(flow*3600) as s FROM view.flow_port_hourly "
						+ "where port=? and datehour between  ? and ? "
						+ "group by hour  order by datehour asc;";
				Object[] params = new Object[3];
				params[0] = port;
				params[1] = intBeginHour;
				params[2] = intEndHour;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("datehour"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, port,
						portName));

			} else if (range.equalsIgnoreCase("1")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -7);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_port_hourly "
						+ "where port=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = port;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, port,
						portName));

			} else if (range.equalsIgnoreCase("2")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -30);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_port_hourly "
						+ "where port=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = port;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, port,
						portName));
			} else if (range.equalsIgnoreCase("3")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.add(Calendar.DAY_OF_MONTH, -1);
				String endDate = Constant.DEFAULT_DATE_FORMAT.format(calEnd
						.getTime());
				calBegin.add(Calendar.DAY_OF_MONTH, -90);
				String beginDate = Constant.DEFAULT_DATE_FORMAT.format(calBegin
						.getTime());
				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_port_hourly "
						+ "where port=? and date between  ? and ? "
						+ "group by date  order by date asc;";
				Object[] params = new Object[3];
				params[0] = port;
				params[1] = beginDate;
				params[2] = endDate;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date"), rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, port,
						portName));
			} else if (range.equalsIgnoreCase("4")) {
				Calendar calBegin = (Calendar) calCurrent.clone();
				calBegin.add(Calendar.MONTH, -5);

				Calendar calEnd = (Calendar) calCurrent.clone();
				calEnd.set(Calendar.DAY_OF_MONTH,
						calEnd.getMaximum(Calendar.DAY_OF_MONTH));
				calEnd.set(Calendar.HOUR_OF_DAY, 23);

				calBegin.set(Calendar.DAY_OF_MONTH, 1);
				calBegin.set(Calendar.HOUR_OF_DAY, 0);

				String endHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calEnd.getTime());
				int intEndHour = Integer.parseInt(endHour.replaceAll("-", ""));

				String beginHour = Constant.DEFAULT_DATE_HOUR_FORMAT
						.format(calBegin.getTime());
				int intBeginHour = Integer.parseInt(beginHour.replaceAll("-",
						""));

				String sql = "SELECT date,sum(flow*3600) as s FROM view.flow_port_hourly "
						+ "where port=? and datehour between  ? and ? "
						+ "group by month  order by month asc;";
				Object[] params = new Object[3];
				params[0] = port;
				params[1] = intBeginHour;
				params[2] = intEndHour;

				final Map<String, Long> mapData = new HashMap<String, Long>();
				jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1)
							throws SQLException {
						mapData.put(rs.getString("date").split("-")[0] + "-"
								+ rs.getString("date").split("-")[1],
								rs.getLong("s"));
						return null;
					}
				});

				list.add(getFlowChartVO(calBegin, calEnd, range, mapData, port,
						portName));
			}
		}
		return list;
	}

	public List<Object> get_port_list() {
		// TODO Auto-generated method stub
		return FlowDeviceConfig.getInstance().getAllPorts();
	}

	public List<Object> get_protocol_list() {
		// TODO Auto-generated method stub
		return FlowDeviceConfig.getInstance().getAllProtocols();
	}

	private Long[] get_yy_test(String range) {
		Random r = new Random();
		Long[] yy = null;

		if (range.equalsIgnoreCase("0")) {
			yy = new Long[] { (long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500) };
		} else if (range.equalsIgnoreCase("1")) {
			yy = new Long[] { (long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500) };
		} else if (range.equalsIgnoreCase("2")) {
			yy = new Long[] { (long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500) };
		} else if (range.equalsIgnoreCase("3")) {
			yy = new Long[] { (long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500) };
		} else if (range.equalsIgnoreCase("4")) {
			yy = new Long[] { (long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500),
					(long) r.nextInt(500), (long) r.nextInt(500) };
		}
		return yy;
	}

	private String[] get_xx_test(String range) {
		// 0:最近24小时, 1:最近一周 2:最近一月 3:最近3月 4:最近6月
		String xx[] = null;

		if (range.equalsIgnoreCase("0")) {
			xx = new String[] { "2014-09-01-1", "2014-09-01-2", "2014-09-01-3",
					"2014-09-01-4", "2014-09-01-5", "2014-09-01-6",
					"2014-09-01-7", "2014-09-01-8", "2014-09-01-9",
					"2014-09-01-10" };
		} else if (range.equalsIgnoreCase("1")) {
			xx = new String[] { "2014-08-01", "2014-08-02", "2014-08-03",
					"2014-08-04", "2014-08-05", "2014-08-06", "2014-08-07" };

		} else if (range.equalsIgnoreCase("2")) {
			xx = new String[] { "2014-08-01", "2014-08-02", "2014-08-03",
					"2014-08-04", "2014-08-05", "2014-08-06", "2014-08-07" };

		} else if (range.equalsIgnoreCase("3")) {
			xx = new String[] { "2014-07", "2014-08", "2014-09" };

		} else if (range.equalsIgnoreCase("4")) {
			xx = new String[] { "2014-04", "2014-05", "2014-06", "2014-07",
					"2014-08", "2014-09" };

		}
		return xx;
	}
}
