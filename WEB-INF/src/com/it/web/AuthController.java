package com.it.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.it.service.AuthService;
import com.it.util.Constant;
import com.it.vo.AuthVO;

/**
 * 
 */
@SuppressWarnings("serial")
public class AuthController extends HttpServlet {
	private Logger logger = Logger.getLogger(AuthController.class);

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

		/**
		 * model: login 登陆 new 新建用户 change 修改密码
		 */
		String model = request.getParameter(Constant.PARAMETER_NAME_MODEL);
		model = model == null ? "" : model;

		String username = request
				.getParameter(Constant.PARAMETER_NAME_USERNAME);
		username = username == null ? "" : username;
		String passwd = request.getParameter(Constant.PARAMETER_NAME_PASSWD);
		passwd = passwd == null ? "" : passwd;
		String passwd1 = request.getParameter(Constant.PARAMETER_NAME_PASSWD1);
		passwd1 = passwd1 == null ? "" : passwd1;
		String passwd2 = request.getParameter(Constant.PARAMETER_NAME_PASSWD2);
		passwd2 = passwd2 == null ? "" : passwd2;
		String admin = request.getParameter(Constant.PARAMETER_NAME_IS_ADMIN);
		admin = admin == null ? "0" : admin;

		AuthVO res = AuthService.getInstance().serve(model, username, passwd,
				passwd1, passwd2,admin);
		if(res==null){
			res= new AuthVO(3, "参数异常",0);
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
			writer.write(res.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
