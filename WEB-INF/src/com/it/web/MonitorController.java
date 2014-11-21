package com.it.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.it.service.EventService;
import com.it.service.MonitorService;
import com.it.util.Constant;
import com.it.vo.BaseVO;

/**
 * 
 */
@SuppressWarnings("serial")
public class MonitorController extends HttpServlet {
	private Logger logger = Logger.getLogger(MonitorController.class);

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
		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		String model = request.getParameter(Constant.PARAMETER_NAME_MODEL);
		model = model == null ? "" : model;
		String func = request.getParameter(Constant.PARAMETER_NAME_FUNCTION);
		func = func == null ? "" : func;

		String limit = request.getParameter(Constant.LIMIT_KEY);
		limit = limit == null ? Constant.LIMIT_DEFAULT : limit;

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

		// yyyy-mm-dd-hh
		String begin = request.getParameter(Constant.QUERY_BEGIN);
		begin = begin == null ? "" : begin;
		String end = request.getParameter(Constant.QUERY_END);
		end = end == null ? "" : end;

		String x = request.getParameter(Constant.QUERY_X);
		x = (x == null || x.isEmpty()) ? "day" : x;

		String range = request.getParameter(Constant.QUERY_RANGE);
		range = (range == null || range.isEmpty()) ? "0" : range;

		String protocols = request.getParameter(Constant.QUERY_PROTOCOLS);
		protocols = protocols == null? "" : protocols;
		
		String ports = request.getParameter(Constant.QUERY_PORTS);
		ports = ports == null? "" : ports;
		
		
		String fromHbase = request.getParameter("fromHbase");
		fromHbase = (fromHbase == null || fromHbase.isEmpty()) ? "true"
				: fromHbase;
		
		
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

		List<Object> list = null;
		int code = 0;

		// 探针
		if (model.equalsIgnoreCase("device")) {
			list = MonitorService.getInstance().get_device_status_list(device_id,fromHbase);

		}else if(model.equalsIgnoreCase("devicelist")){
			list = MonitorService.getInstance().get_device_list();
			
		}
		else if (model.equalsIgnoreCase("portlist")) {
			list = MonitorService.getInstance().get_port_list();
		}
		else if (model.equalsIgnoreCase("protocollist")) {
			list = MonitorService.getInstance().get_protocol_list();
		}
		//流量
		else if (model.equalsIgnoreCase("flow")) {
			if (func.equalsIgnoreCase("sum")) {
				//range
				list = MonitorService.getInstance().get_flow_sum_list(range);

			} else if (func.equalsIgnoreCase("protocol")) {
				list = MonitorService.getInstance().get_flow_protocol_list(range,protocols);

			} else if (func.equalsIgnoreCase("port")) {
				list = MonitorService.getInstance().get_flow_port_list(range,ports);

			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("");
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.addHeader("P3P", "CP=CAO PSA OUR");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			BaseVO bvo = new BaseVO(code, list);
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
