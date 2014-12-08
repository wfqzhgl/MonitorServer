package com.it.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.it.config.TypeListLoad;
import com.it.util.Constant;
import com.it.util.Utils;
import com.it.vo.TypeTimeDetailVO;
import com.it.vo.TypeTimeVO;

@Repository
public class EventTimeDao extends BaseDao {

	@SuppressWarnings("deprecation")
	public List<Object> get_time_list(String begin, String end, String x,
			List<String> types, List<String> ips) {
		List<Object> list = new ArrayList<Object>();
		List<String> xxx = new ArrayList<String>();
		List<Long> yyy = new ArrayList<Long>();

		logger.debug("---get_time_list:begin=" + begin + ",end=" + end);
		String sql = "";
		String sql_w = "";

		// get where sql
		if (types != null && !types.isEmpty() && !types.contains("1000")) {
			String tmp = "type in ( ";
			for (String t : types) {
				String tname= TypeListLoad.getInstance().getNameByID(t);
				if(tname==null){
					continue;
				}
				tmp += "'" + tname + "',";
			}
			tmp = Utils.trimStr(tmp, ",");
			tmp += ") ";
			sql_w += tmp;
		}
		if (ips != null && !ips.isEmpty()) {
			String tmp = Utils.getSqlByIps("lip", ips);
			if (!tmp.isEmpty()) {
				if (!sql_w.isEmpty()) {
					sql_w += " and " + tmp;
				} else {
					sql_w += " " + tmp;
				}

			}

		}
		if (!sql_w.isEmpty()) {
			sql_w = " and " + sql_w;
		}
		int datehour_begin = Integer.parseInt(begin.replaceAll("-", ""));
		int datehour_end = Integer.parseInt(end.replaceAll("-", ""));

		Calendar calBegin = Calendar.getInstance();
		try {
			calBegin.setTime(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(begin));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calEnd = Calendar.getInstance();
		try {
			calEnd.setTime(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(end));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (x.equalsIgnoreCase("year")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "  group by  year,type order by year";

			calBegin.set(Calendar.MONTH, 0);
			calBegin.set(Calendar.DAY_OF_MONTH, 0);
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.MONTH, 0);
			calEnd.set(Calendar.DAY_OF_MONTH, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(String.valueOf(calBegin.get(Calendar.YEAR)));
				calBegin.add(Calendar.YEAR, 1);
			}

		} else if (x.equalsIgnoreCase("month")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  year,month,type order by year,month";
			calBegin.set(Calendar.DAY_OF_MONTH, 0);
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.DAY_OF_MONTH, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_MONTH_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.MONTH, 1);
			}

		} else if (x.equalsIgnoreCase("week")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  year,week,type order by year,week";
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_WEEK_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.WEEK_OF_YEAR, 1);
			}

		} else if (x.equalsIgnoreCase("day")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  `date`,type order by `date`";

			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_DATE_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
			}

		} else if (x.equalsIgnoreCase("hour")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  `date`,hour,type order by `date`,hour";
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_DATE_HOUR_FORMAT.format(calBegin
						.getTime()));
				calBegin.add(Calendar.HOUR_OF_DAY, 1);
			}
		} else {
			return list;
		}

		logger.debug("---get_time_list: sql =" + sql);

		Object[] params = new Object[2];
		params[0] = datehour_begin;
		params[1] = datehour_end;

		logger.debug("------params=" + params);

		final List<TypeTimeDetailVO> listtmp = new ArrayList<TypeTimeDetailVO>();
		jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				TypeTimeDetailVO config = new TypeTimeDetailVO();
				config.setYear(rs.getString("year"));
				config.setWeek(rs.getString("week"));
				config.setMonth(rs.getString("month"));
				config.setDate(rs.getString("date"));
				config.setHour(rs.getString("hour"));
				config.setTypeCode(TypeListLoad.getInstance().getCodeByName(
						rs.getString("type")));
				;
				config.setTypeName(rs.getString("type"));
				config.setCount(rs.getInt("count"));

				listtmp.add(config);
				return null;
			}
		});

		// 按攻击类型分组
		Map<String, List<TypeTimeDetailVO>> map = new HashMap<String, List<TypeTimeDetailVO>>();

		for (TypeTimeDetailVO vo : listtmp) {
			String code = vo.getTypeCode();
			if (map.containsKey(code)) {
				map.get(code).add(vo);
			} else {
				List<TypeTimeDetailVO> arr = new ArrayList<TypeTimeDetailVO>();
				arr.add(vo);
				map.put(code, arr);
			}
		}

		for (Map.Entry<String, List<TypeTimeDetailVO>> en : map.entrySet()) {
			TypeTimeVO vo = new TypeTimeVO();
			List<Long> yy = new ArrayList<Long>();

			vo.setX(xxx);
			vo.setType(en.getValue().get(0).getTypeName());
			vo.setCode(en.getValue().get(0).getTypeCode());

			if (x.equalsIgnoreCase("year")) {

				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.equalsIgnoreCase(voo.getYear())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("month")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.split("-")[0].equalsIgnoreCase(voo.getYear())
								&& Integer.parseInt(xx.split("-")[1]) == Integer
										.parseInt(voo.getMonth())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("week")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.split("-")[0].equalsIgnoreCase(voo.getYear())
								&& Integer.parseInt(xx.split("-")[1]) == Integer
										.parseInt(voo.getWeek())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("day")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						try {
							if (Constant.DEFAULT_DATE_FORMAT.parse(xx).compareTo(
									Constant.DEFAULT_DATE_FORMAT.parse(voo
											.getDate())) == 0) {
								yy.add(voo.getCount());
								got=true;
								break;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("hour")) {
				List<String> xtmp = new ArrayList<String>();
				for (String xx : xxx) {
					try {
						xtmp.add(String.valueOf(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(xx).getHours()));
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						xtmp.add(xx);
					}
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						try {
							if (Constant.DEFAULT_DATE_HOUR_FORMAT.parse(xx).compareTo(
									Constant.DEFAULT_DATE_HOUR_FORMAT.parse(voo
											.getDate()+"-"+voo.getHour())) == 0) {
								yy.add(voo.getCount());
								got=true;
								break;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}
				vo.setX(xtmp);
			}

			vo.setY(yy);
			list.add(vo);
		}

		return list;
	}

	public List<Object> get_time_compare_list(String begin, String end,
			String x, List<String> types, List<String> ips)
			throws ParseException {
		List<Object> list = new ArrayList<Object>();
		List<String> xxx = new ArrayList<String>();

		logger.debug("---get_time_compare_list:begin=" + begin + ",end=" + end);
		String sql = "";
		String sql_w = "";

		// get where sql
		if (types != null && !types.isEmpty() && !types.contains("1000")) {
			String tmp = "type in ( ";
			for (String t : types) {
				String tname= TypeListLoad.getInstance().getNameByID(t);
				if(tname==null){
					continue;
				}
				tmp += "'" + tname + "',";
			}
			tmp = Utils.trimStr(tmp, ",");
			tmp += ") ";
			sql_w += tmp;
		}
		if (ips != null && !ips.isEmpty()) {
			String tmp = Utils.getSqlByIps("lip", ips);
			if (!tmp.isEmpty()) {
				if (!sql_w.isEmpty()) {
					sql_w += " and " + tmp;
				} else {
					sql_w += " " + tmp;
				}

			}

		}
		if (!sql_w.isEmpty()) {
			sql_w = " and " + sql_w;
		}
		int datehour_begin = Integer.parseInt(begin.replaceAll("-", ""));
		int datehour_end = Integer.parseInt(end.replaceAll("-", ""));

		Calendar calBegin = Calendar.getInstance();
		try {
			calBegin.setTime(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(begin));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calEnd = Calendar.getInstance();
		try {
			calEnd.setTime(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(end));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (x.equalsIgnoreCase("year")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "  group by  year,type order by year";

			calBegin.set(Calendar.MONTH, 0);
			calBegin.set(Calendar.DAY_OF_MONTH, 0);
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.MONTH, 0);
			calEnd.set(Calendar.DAY_OF_MONTH, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(String.valueOf(calBegin.get(Calendar.YEAR)));
				calBegin.add(Calendar.YEAR, 1);
			}

		} else if (x.equalsIgnoreCase("month")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  year,month,type order by year,month";
			calBegin.set(Calendar.DAY_OF_MONTH, 0);
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.DAY_OF_MONTH, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_MONTH_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.MONTH, 1);
			}

		} else if (x.equalsIgnoreCase("week")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  year,week,type order by year,week";
			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_WEEK_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.WEEK_OF_YEAR, 1);
			}

		} else if (x.equalsIgnoreCase("day")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  `date`,type order by `date`";

			calBegin.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.HOUR_OF_DAY, 0);
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_DATE_FORMAT.format(calBegin.getTime()));
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
			}

		} else if (x.equalsIgnoreCase("hour")) {
			sql = "select year,week,month,`date`,hour,type,sum(count) as count from attack_srcstat_hourly "
					+ "where ( datehour between ? and ? ) "
					+ sql_w
					+ "   group by  `date`,hour,type order by `date`,hour";
			while (!calBegin.after(calEnd)) {
				xxx.add(Constant.DEFAULT_DATE_HOUR_FORMAT.format(calBegin
						.getTime()));
				calBegin.add(Calendar.HOUR_OF_DAY, 1);
			}
		} else {
			return list;
		}

		logger.debug("---get_time_list: sql =" + sql);

		Object[] params = new Object[2];
		params[0] = datehour_begin;
		params[1] = datehour_end;

		logger.debug("------params=" + params);

		final List<TypeTimeDetailVO> listtmp = new ArrayList<TypeTimeDetailVO>();
		jdbcTemplate.query(sql, params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				TypeTimeDetailVO config = new TypeTimeDetailVO();
				config.setYear(rs.getString("year"));
				config.setWeek(rs.getString("week"));
				config.setMonth(rs.getString("month"));
				config.setDate(rs.getString("date"));
				config.setHour(rs.getString("hour"));
				config.setTypeCode(TypeListLoad.getInstance().getCodeByName(
						rs.getString("type")));
				;
				config.setTypeName(rs.getString("type"));
				config.setCount(rs.getInt("count"));

				listtmp.add(config);
				return null;
			}
		});

		// 按攻击类型分组
		Map<String, List<TypeTimeDetailVO>> map = new HashMap<String, List<TypeTimeDetailVO>>();

		for (TypeTimeDetailVO vo : listtmp) {
			String code = vo.getTypeCode();
			if (map.containsKey(code)) {
				map.get(code).add(vo);
			} else {
				List<TypeTimeDetailVO> arr = new ArrayList<TypeTimeDetailVO>();
				arr.add(vo);
				map.put(code, arr);
			}
		}

		for (Map.Entry<String, List<TypeTimeDetailVO>> en : map.entrySet()) {
			TypeTimeVO vo = new TypeTimeVO();
			List<Long> yy = new ArrayList<Long>();

			vo.setX(xxx);
			vo.setType(en.getValue().get(0).getTypeName());
			vo.setCode(en.getValue().get(0).getTypeCode());

			if (x.equalsIgnoreCase("year")) {

				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.equalsIgnoreCase(voo.getYear())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("month")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.split("-")[0].equalsIgnoreCase(voo.getYear())
								&& Integer.parseInt(xx.split("-")[1]) == Integer
										.parseInt(voo.getMonth())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("week")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						if (xx.split("-")[0].equalsIgnoreCase(voo.getYear())
								&& Integer.parseInt(xx.split("-")[1]) == Integer
										.parseInt(voo.getWeek())) {
							yy.add(voo.getCount());
							got=true;
							break;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("day")) {
				for (String xx : xxx) {
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						try {
							if (Constant.DEFAULT_DATE_FORMAT.parse(xx).compareTo(
									Constant.DEFAULT_DATE_FORMAT.parse(voo
											.getDate())) == 0) {
								yy.add(voo.getCount());
								got=true;
								break;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}

			} else if (x.equalsIgnoreCase("hour")) {
				List<String> xtmp = new ArrayList<String>();
				for (String xx : xxx) {
					try {
						xtmp.add(String.valueOf(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(xx).getHours()));
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						xtmp.add(xx);
					}
					boolean got = false;
					for (TypeTimeDetailVO voo : en.getValue()) {
						try {
							if (Constant.DEFAULT_DATE_HOUR_FORMAT.parse(xx).compareTo(
									Constant.DEFAULT_DATE_HOUR_FORMAT.parse(voo
											.getDate()+"-"+voo.getHour())) == 0) {
								yy.add(voo.getCount());
								got=true;
								break;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
					}
					if(!got){
						yy.add(0L);						
					}
				}
				vo.setX(xtmp);
			}

			vo.setY(yy);
			list.add(vo);
		}

		return list;
	}

	public List<Object> get_time_sample_list(String x, List<String> range,
			List<String> types, List<String> ips) throws ParseException {
		List<Object> list = new ArrayList<Object>();

		List<Date> rangeDate = new ArrayList<Date>();
		for (String tmp : range) {
			rangeDate.add(Constant.DEFAULT_DATE_HOUR_FORMAT.parse(tmp));
		}
		// sort
		Collections.sort(rangeDate, new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2) {
				// TODO Auto-generated method stub
				return o2.after(o1) ? -1 : 1;
			}
		});

		// x format
		for (int i = 0; i < rangeDate.size(); i++) {
			if (x.equalsIgnoreCase("year")) {
				range.set(i,
						Constant.DEFAULT_YEAR_FORMAT.format(rangeDate.get(i)));

			} else if (x.equalsIgnoreCase("month")) {
				range.set(i,
						Constant.DEFAULT_MONTH_FORMAT.format(rangeDate.get(i)));
			} else if (x.equalsIgnoreCase("week")) {

				range.set(i,
						Constant.DEFAULT_WEEK_FORMAT.format(rangeDate.get(i)));

			} else if (x.equalsIgnoreCase("day")) {
				range.set(i,
						Constant.DEFAULT_DATE_FORMAT.format(rangeDate.get(i)));
			} else if (x.equalsIgnoreCase("hour")) {
				range.set(i, Constant.DEFAULT_DATE_HOUR_FORMAT.format(rangeDate
						.get(i)));
			}
		}

		
		logger.debug("---get_time_sample_list:x=" + x + ",range=" + range);
		String sql = "";
		String sql_w = "";

		// get where sql
		if (types != null && !types.isEmpty() && !types.contains("1000")) {
			String tmp = "type in ( ";
			for (String t : types) {
				String tname= TypeListLoad.getInstance().getNameByID(t);
				if(tname==null){
					continue;
				}
				tmp += "'" + tname + "',";
			}
			tmp = Utils.trimStr(tmp, ",");
			tmp += ") ";
			sql_w += tmp;
		}
		if (ips != null && !ips.isEmpty()) {
			String tmp = Utils.getSqlByIps("lip", ips);
			if (!tmp.isEmpty()) {
				if (!sql_w.isEmpty()) {
					sql_w += " and " + tmp;
				} else {
					sql_w += " " + tmp;
				}

			}

		}
		if (!sql_w.isEmpty()) {
			sql_w = " and " + sql_w;
		}
		
		final Map<String,Map<String,Long>> ymap = new HashMap<String,Map<String,Long>>();
		for(final String xx:range){
			if (x.equalsIgnoreCase("year")) {
				sql = "select type,sum(count) as count from attack_srcstat_hourly "
						+ "where year="+xx
						+ sql_w
						+ "   group by type";

				jdbcTemplate.query(sql, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
						String code = TypeListLoad.getInstance().getCodeByName(
								rs.getString("type"));
						if(ymap.containsKey(code)){
							Map<String,Long> tmap = ymap.get(code);
							tmap.put(xx, rs.getLong("count"));
						}else{
							Map<String,Long> tmap = new HashMap<String,Long>();
							tmap.put(xx, rs.getLong("count"));
							ymap.put(code, tmap);
						}
						return null;
					}
				});

			} else if (x.equalsIgnoreCase("month")) {
				sql = "select type,sum(count) as count from attack_srcstat_hourly "
						+ "where year="+xx.split(",")[0]+" and month="+xx.split(",")[1]
						+ sql_w
						+ "   group by type";

				jdbcTemplate.query(sql, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
						String code = TypeListLoad.getInstance().getCodeByName(
								rs.getString("type"));
						if(ymap.containsKey(code)){
							Map<String,Long> tmap = ymap.get(code);
							tmap.put(xx, rs.getLong("count"));
						}else{
							Map<String,Long> tmap = new HashMap<String,Long>();
							tmap.put(xx, rs.getLong("count"));
							ymap.put(code, tmap);
						}
						return null;
					}
				});

			} else if (x.equalsIgnoreCase("week")) {
				sql = "select type,sum(count) as count from attack_srcstat_hourly "
						+ "where year="+xx.split(",")[0]+" and week="+xx.split(",")[1]
						+ sql_w
						+ "   group by type";

				jdbcTemplate.query(sql, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
						String code = TypeListLoad.getInstance().getCodeByName(
								rs.getString("type"));
						if(ymap.containsKey(code)){
							Map<String,Long> tmap = ymap.get(code);
							tmap.put(xx, rs.getLong("count"));
						}else{
							Map<String,Long> tmap = new HashMap<String,Long>();
							tmap.put(xx, rs.getLong("count"));
							ymap.put(code, tmap);
						}
						return null;
					}
				});

			} else if (x.equalsIgnoreCase("day")) {
				sql = "select type,sum(count) as count from attack_srcstat_hourly "
						+ "where `date`='"+xx+"'"
						+ sql_w
						+ "   group by type";

				logger.debug("---sql:"+sql);
				jdbcTemplate.query(sql, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
						String code = TypeListLoad.getInstance().getCodeByName(
								rs.getString("type"));
						if(ymap.containsKey(code)){
							Map<String,Long> tmap = ymap.get(code);
							tmap.put(xx, rs.getLong("count"));
						}else{
							Map<String,Long> tmap = new HashMap<String,Long>();
							tmap.put(xx, rs.getLong("count"));
							ymap.put(code, tmap);
						}
						return null;
					}
				});

			} else if (x.equalsIgnoreCase("hour")) {
				sql = "select type,sum(count) as count from attack_srcstat_hourly "
						+ "where datehour="+xx.replaceAll("-", "")
						+ sql_w
						+ "   group by type";

				jdbcTemplate.query(sql, new RowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
						String code = TypeListLoad.getInstance().getCodeByName(
								rs.getString("type"));
						if(code==null||code.isEmpty()){
							return null;
						}
						if(ymap.containsKey(code)){
							Map<String,Long> tmap = ymap.get(code);
							tmap.put(xx, rs.getLong("count"));
						}else{
							Map<String,Long> tmap = new HashMap<String,Long>();
							tmap.put(xx, rs.getLong("count"));
							ymap.put(code, tmap);
						}
						return null;
					}
				});

			}
		}
			
			for (Map.Entry<String, Map<String,Long>> en : ymap.entrySet()) {
				TypeTimeVO vo = new TypeTimeVO();
				List<Long> yy = new ArrayList<Long>();
				vo.setCode(en.getKey());
				vo.setType(TypeListLoad.getInstance().getNameByID(en.getKey()));
				vo.setX(range);
				for(String xxx:range){
					if(en.getValue().containsKey(xxx)){
						yy.add(en.getValue().get(xxx));
					}else{
						yy.add(0L);
					}
				}
				vo.setY(yy);
				list.add(vo);
		}
		return list;
	}

	public static void main(String args[]) {
		try {
			String begin = "2014-09-04-02";
			Date be = Constant.DEFAULT_DATE_HOUR_FORMAT.parse(begin);
			Calendar cal = Calendar.getInstance();
			cal.setTime(be);

			System.out.println(Constant.DEFAULT_MONTH_FORMAT.format(be));
			// System.out.println(be);
			// System.out.println(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
