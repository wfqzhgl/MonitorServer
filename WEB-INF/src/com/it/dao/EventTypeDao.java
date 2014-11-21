package com.it.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.it.config.BaseConfigLoad;
import com.it.config.IPParserLoad;
import com.it.config.TypeListLoad;
import com.it.util.Constant;
import com.it.vo.AddressVO;
import com.it.vo.TypeAttackVO;
import com.it.vo.TypeSortVO;


@Repository
public class EventTypeDao extends BaseDao{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public  List<Object> get_type_sorttype_list(String begin, String end, long limit) {
		logger.debug("---get_type_sorttype_list:begin="+begin+",end="+end);
		List<Object> list =new ArrayList<Object>();
		final List<TypeSortVO> listtmp =new ArrayList<TypeSortVO>();
		int datehour_begin = Integer.parseInt(begin.replaceAll("-", ""));
		int datehour_end = Integer.parseInt(end.replaceAll("-", ""));
		
		String sql = "select type,sum(count) as count from attack_srcstat_hourly where datehour between ? and ? group by  type order by count desc limit ?";
		Object [] params = new  Object[3];
		params[0] = datehour_begin;
		params[1] = datehour_end;
		params[2] = limit;
		
		int pie_show_count = Integer.parseInt(BaseConfigLoad.getInstance().getConfigData(Constant.PIE_SHOW_COUNT, "6"));
		jdbcTemplate.query(sql,params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				TypeSortVO config = new TypeSortVO();
//				config.setCode(rs.getString("code"));
				config.setCode(TypeListLoad.getInstance().getCodeByName(rs.getString("type")));
				config.setType(rs.getString("type"));
				config.setCount(rs.getInt("count"));
				
				listtmp.add(config);
				return null;
			}
		});
		
		int count=0;
		int other=0;
		for(TypeSortVO vo:listtmp){
			if(count<pie_show_count){
				list.add(vo);
			}else{
				other += vo.getCount();
			}
			count++;
		}
		
		if(other>0){
			list.add(new TypeSortVO("other","other",other));
		}
//		TypeSortVO vo =new TypeSortVO("2","协议异常",300);
//		list.add(vo);
//		vo =new TypeSortVO("3", "恶意攻击",200);
//		list.add(vo);
//		vo =new TypeSortVO("4", "流量异常",100);
//		list.add(vo);
		return  list;
	}
	
	public  List<Object> get_type_sortdst_list(String begin, String end, long limit) {

		final List<Object> list =new ArrayList<Object>();
		final List<TypeAttackVO> listtmp =new ArrayList<TypeAttackVO>();
		int datehour_begin = Integer.parseInt(begin.replaceAll("-", ""));
		int datehour_end = Integer.parseInt(end.replaceAll("-", ""));
		
		String sql = "select dip,sum(count) as count from attack_dststat_hourly where datehour between ? and ? group by  dip order by count desc limit ?";
		Object [] params = new  Object[3];
		params[0] = datehour_begin;
		params[1] = datehour_end;
		params[2] = limit;
		
		int pie_show_count = Integer.parseInt(BaseConfigLoad.getInstance().getConfigData(Constant.PIE_SHOW_COUNT, "6"));
		jdbcTemplate.query(sql,params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				TypeAttackVO config = new TypeAttackVO();
				config.setIp(rs.getString("dip"));
				config.setAddress(IPParserLoad.parseIP(rs.getString("dip")).get_country_province_city_string());
				config.setCount(rs.getInt("count"));
				
				listtmp.add(config);
				return null;
			}
		});
		
		int count=0;
		int other=0;
		for(TypeAttackVO vo:listtmp){
			if(count<pie_show_count){
				list.add(vo);
			}else{
				other += vo.getCount();
			}
			count++;
		}
		
		if(other>0){
			list.add(new TypeAttackVO("other","other",other));
		}
		return  list;
	}
	
	public  List<Object> get_type_sortsrc_list(String begin, String end, long limit) {

		final List<Object> list =new ArrayList<Object>();
		final List<TypeAttackVO> listtmp =new ArrayList<TypeAttackVO>();
		int datehour_begin = Integer.parseInt(begin.replaceAll("-", ""));
		int datehour_end = Integer.parseInt(end.replaceAll("-", ""));
		
		String sql = "select sip,sum(count) as count from attack_srcstat_hourly where datehour between ? and ? group by  sip order by count desc limit ?";
		Object [] params = new  Object[3];
		params[0] = datehour_begin;
		params[1] = datehour_end;
		params[2] = limit;
		
		int pie_show_count = Integer.parseInt(BaseConfigLoad.getInstance().getConfigData(Constant.PIE_SHOW_COUNT, "6"));
		jdbcTemplate.query(sql,params, new RowMapper<byte[]>() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				TypeAttackVO config = new TypeAttackVO();
				config.setIp(rs.getString("sip"));
				config.setAddress(IPParserLoad.parseIP(rs.getString("sip")).get_country_province_city_string());
				config.setCount(rs.getInt("count"));
				
				listtmp.add(config);
				return null;
			}
		});
		
		int count=0;
		int other=0;
		for(TypeAttackVO vo:listtmp){
			if(count<pie_show_count){
				list.add(vo);
			}else{
				other += vo.getCount();
			}
			count++;
		}
		
		if(other>0){
			list.add(new TypeAttackVO("other","other",other));
		}
		return  list;
		
	}


}
