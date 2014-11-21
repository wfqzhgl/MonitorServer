package com.it.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import net.sf.json.JSONObject;

public class HttpReq {

	/**
	 * 发送Get请求
	 * 
	 * @param urlString
	 * @param params
	 * @return
	 */
	public static JSONObject sendGet(String url, Map<String, String> params) {
		return send(url, "GET", params);
	}

	/**
	 * 发送post请求
	 * 
	 * @param urlString
	 *            URL路径
	 * @param params
	 *            传递的参数
	 * @return
	 */
	public static JSONObject sendPost(String url, Map<String, String> params) {
		return send(url, "POST", params);
	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param urlString
	 * @return 请求结果
	 */
	private static JSONObject send(String urlStr, String method,
			Map<String, String> parameters) {
		HttpURLConnection urlConnection = null;

		// 封装Get请求路径，Get为不加密请求路径
		if (method.equalsIgnoreCase("GET") && parameters != null) {
			StringBuffer param = new StringBuffer();
			int i = 0;
			for (String key : parameters.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(parameters.get(key));
				i++;
			}
			urlStr += param;
		}

		try {
			URL url = new URL(urlStr);
			// 请求配置，可根据实际情况采用灵活配置
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(method);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(5000);
			urlConnection.setRequestProperty("Charsert", "UTF-8");
			urlConnection
					.setRequestProperty("Content-Type", "application/json");
			// 封装post请求参数，
			if (method.equals("POST") && parameters != null) {
				StringBuffer param = new StringBuffer();
				param.append("{");
				param.append("\"userid\"").append(":\"")
						.append(parameters.get("userid"));// "\"userid\""
															// \标识强转"号，因为参数传递时需要使用""
				param.append("\"}");

				urlConnection.getOutputStream().write(
						param.toString().getBytes());// 写入参数，必须为byte
														// OutputStream
														// 只能接收byte类型
				urlConnection.getOutputStream().flush();
				urlConnection.getOutputStream().close();
				urlConnection.connect();
			}
			// 调用http请求
			return makeContent(urlConnection);
		} catch (Exception e) {
			e.printStackTrace();
			return null;// 异常返回0000
		}
	}

	/**
	 * 得到响应对象
	 * 
	 * @param urlConnection
	 * @return 响应对象
	 * @throws Exception
	 */
	private static JSONObject makeContent(HttpURLConnection urlConnection) {
		try {
			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, "UTF-8"));
			StringBuffer temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				temp.append(line);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			String jsons = temp.toString().trim();
			JSONObject jsonObject = JSONObject.fromObject(jsons);
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;// 异常返回
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}

	public static void main(String args[]) {

	}
}