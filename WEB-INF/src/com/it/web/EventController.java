package com.it.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.log4j.Logger;

import com.it.hadoop.test.HbaseTest;
import com.it.service.EventService;
import com.it.util.Constant;
import com.it.vo.BaseVO;
import com.it.vo.PageVO;

/**
 * 
 */
@SuppressWarnings("serial")
public class EventController extends HttpServlet {
	private Logger logger = Logger.getLogger(EventController.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doEvent(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doEvent(request, response);
	}

	/*
	 * 处理
	 */
	public String doEvent(HttpServletRequest request,
			HttpServletResponse response) {

		List<Object> list = null;
		int code = 0;
		int PAGE = 0;
		int ROWS = 0;
		int PAGES = 0;
		int TOTAL = 0;

		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		String model = request.getParameter(Constant.PARAMETER_NAME_MODEL);
		model = model == null ? "" : model;
		String func = request.getParameter(Constant.PARAMETER_NAME_FUNCTION);
		func = func == null ? "" : func;

		String limit = request.getParameter(Constant.LIMIT_KEY);
		limit = (limit == null || limit.isEmpty()) ? Constant.LIMIT_DEFAULT
				: limit;

		String fromHbase = request.getParameter("fromHbase");
		fromHbase = (fromHbase == null || fromHbase.isEmpty()) ? "true"
				: fromHbase;

		// 单位编码
		String src_address = request.getParameter(Constant.SRC_ADDRESS);
		src_address = src_address == null ? "" : src_address;
		String dst_address = request.getParameter(Constant.DST_ADDRESS);
		dst_address = dst_address == null ? "" : dst_address;

		// 重点监测参数
		String device_id = request.getParameter(Constant.DEVICE_ID_KEY);
		device_id = device_id == null ? "" : device_id;
		String type_id = request.getParameter(Constant.TYPE_ID_KEY);
		type_id = type_id == null ? "" : type_id;
		String src_ip = request.getParameter(Constant.SRC_IP_KEY);
		src_ip = src_ip == null ? "" : src_ip;
		String dst_ip = request.getParameter(Constant.DST_IP_KEY);
		dst_ip = dst_ip == null ? "" : dst_ip;
		String level = request.getParameter(Constant.QUERY_LEVEL);
		level = level == null ? "" : level;
		// yyyy-mm-dd-hh
		String begin = request.getParameter(Constant.QUERY_BEGIN);
		begin = begin == null ? "" : begin;
		String end = request.getParameter(Constant.QUERY_END);
		end = end == null ? "" : end;

		String x = request.getParameter(Constant.QUERY_X);
		x = (x == null || x.isEmpty()) ? "day" : x;

		// 目标ip范围
		String ip = request.getParameter(Constant.QUERY_IP);
		ip = ip == null ? "" : ip;
		// 目标单位
		String address = request.getParameter(Constant.QUERY_ADDRESS_ID);
		address = address == null ? "" : address;

		String range = request.getParameter(Constant.QUERY_RANGE);
		range = range == null ? "" : range;

		String page = request.getParameter(Constant.QUERY_PAGE);
		page = (page == null || page.isEmpty()) ? "1" : page;
		PAGE = Integer.parseInt(page);

		// rows per page
		String rows = request.getParameter(Constant.QUERY_ROWS);
		rows = (rows == null || rows.isEmpty()) ? "10" : rows;
		ROWS = Integer.parseInt(rows);

		Cookie[] cookies = request.getCookies();
		String sessionid = request.getParameter(Constant.SESSIONID);
		if (sessionid == null && cookies != null) {
			for (Cookie cookie : cookies) {
				if (logger.isDebugEnabled()) {
					logger.debug(cookie.getName() + "--------------------"
							+ cookie.getValue());
				}
				if (Constant.SESSIONID.equals(cookie.getName())) {
					sessionid = cookie.getValue();
					break;
				}
			}
		}

		// 生成新的sessionId
		if (sessionid == null || sessionid.trim().isEmpty()) {
			sessionid = UUID.randomUUID().toString();
		}

		if (begin.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -2);
			begin = Constant.DEFAULT_DATE_HOUR_FORMAT.format(cal.getTime());
		}

		String begintime = request
				.getParameter(Constant.PARAMETER_MAPLIST_BEGINTIME);
		
		// 空间展示
		if (model.equalsIgnoreCase("space")) {
			
			if (func.equalsIgnoreCase("maplist")) {
				Map<String, String> params = new HashMap<String, String>();
				String showtype = request
						.getParameter(Constant.PARAMETER_MAPLIST_SHOWTYPE);
				showtype = showtype == null ? "1" : showtype;
				String eventtype = request
						.getParameter(Constant.PARAMETER_MAPLIST_EVENTTYPE);

				eventtype = eventtype == null ? "" : eventtype;
				if (eventtype.equalsIgnoreCase("1000")) {
					eventtype = "";
				}
				
				if (begintime != null && !begintime.isEmpty()) {
					params.put(Constant.PARAMETER_MAPLIST_BEGINTIME, begintime);
				}
				params.put(Constant.PARAMETER_MAPLIST_SHOWTYPE, showtype);
				params.put(Constant.PARAMETER_MAPLIST_EVENTTYPE, eventtype);
				params.put(Constant.LIMIT_KEY, limit);

				try {
					list = EventService.getInstance().get_space_map_list(
							sessionid, params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					list = null;
					code = 1;
				}

			} else if (func.equalsIgnoreCase("global")) {

				try {
					list = EventService.getInstance().get_space_global_list(sessionid,
							begintime, Long.parseLong(limit), fromHbase);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (func.equalsIgnoreCase("addresslist")) {
				list = EventService.getInstance().get_address_list();

			} else if (func.equalsIgnoreCase("sourcelist")) {
				list = EventService.getInstance().get_address_source_list();

			} else if (func.equalsIgnoreCase("typelist")) {
				list = EventService.getInstance().get_type_list();

			} else if (func.equalsIgnoreCase("part")) {// 区域监测
				try {
					list = EventService.getInstance().get_space_part_list(sessionid,
							begintime, Long.parseLong(limit), src_address,
							dst_address, fromHbase);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (func.equalsIgnoreCase("key")) {// 重点监测
				try {
					if(type_id.contains("1000")){
						type_id="";
					}
					list = EventService.getInstance().get_space_key_list(sessionid,
							begintime, Long.parseLong(limit), device_id,
							type_id, src_ip, dst_ip, fromHbase);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (model.equalsIgnoreCase("type")) {
			if(end.isEmpty()){
				end = Constant.DEFAULT_DATE_HOUR_FORMAT.format(Calendar.getInstance().getTime());
			}
			
			if (func.equalsIgnoreCase("sort_type")) {
				list = EventService.getInstance().get_type_sorttype_list(begin,
						end, Long.parseLong(limit));

			} else if (func.equalsIgnoreCase("sort_dst")) {
				list = EventService.getInstance().get_type_sortdst_list(begin,
						end, Long.parseLong(limit));

			} else if (func.equalsIgnoreCase("sort_src")) {
				list = EventService.getInstance().get_type_sortsrc_list(begin,
						end, Long.parseLong(limit));

			}
		} else if (model.equalsIgnoreCase("time")) {
			if (func.equalsIgnoreCase("list")) {
				list = EventService.getInstance().get_time_list(begin, end, x,
						type_id, address, ip);

			} else if (func.equalsIgnoreCase("compare")) {
				try {
					list = EventService.getInstance().get_time_compare_list(
							begin, end, x, type_id, address, ip);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (func.equalsIgnoreCase("sample")) {
				List<String> ranges = Arrays.asList(range.split(","));
				try {
					list = EventService.getInstance().get_time_sample_list(x,
							ranges, type_id, address, ip);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (model.equalsIgnoreCase("global")) {
			if (func.equalsIgnoreCase("list")) {

				list = EventService.getInstance().get_global_list(type_id,
						level, src_ip, dst_ip, begin, end,
						Long.parseLong(limit),fromHbase);
				PageVO vo = new PageVO(PAGE, ROWS, list);
				PAGES = vo.getTotalPages();
				TOTAL = vo.getTotal();
				list = vo.getCurrentPage();

			} else if (func.equalsIgnoreCase("flow")) {
				list = EventService.getInstance().get_global_flow_list();

			}
		} else if (model.equalsIgnoreCase("testHbase")) {

			EventService.getInstance().testHbase();

		}

		// String referer = request.getHeader(Constant.REQUEST_TYPE);

		String timeStamp = yyyyMMddHHmmss.format(new Date());

		if (logger.isDebugEnabled()) {
			logger.debug("");
		}

		Cookie cookie = new Cookie(Constant.SESSIONID, sessionid);
		// int maxAge = -1;
		// if (maxAge > 0) {
		// cookie.setMaxAge(maxAge);
		// }
		response.addCookie(cookie);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.addHeader("P3P", "CP=CAO PSA OUR");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			BaseVO bvo = new BaseVO(code, list, PAGE, PAGES, TOTAL);
			writer.write(bvo.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	// /**
	// * 读取用户IP
	// * @param request
	// * @return
	// */
	// private String getIpAddr(HttpServletRequest request) {
	// String ip = request.getHeader("x-forwarded-for");
	// if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	// ip = request.getHeader("Proxy-Client-IP");
	// }
	//
	// if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	// ip = request.getHeader("WL-Proxy-Client-IP");
	// }
	//
	// if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	// ip = request.getRemoteAddr();
	// }
	// return ip;
	//
	// }

}
