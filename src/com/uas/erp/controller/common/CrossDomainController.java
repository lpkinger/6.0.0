package com.uas.erp.controller.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 访问外部资源
 * 
 * @author yingp
 * 
 */
@Controller
public class CrossDomainController {

	/**
	 * 解决浏览器无法跨资源访问的问题 Access-Control-Allow-Origin <br>
	 * 可以在javascript使用jsonp来实现跨资源访问，但是返回结果必须是json格式，否则会出错
	 * 
	 * @param map
	 * @param path
	 * @return
	 */
	@RequestMapping("/common/cross.action")
	@ResponseBody
	public String getCrossDomain(String path) {
		String result = null;
		try {
			URL url = new URL(path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			bufferedReader.close();
			reader.close();
			inputStream.close();
			connection.disconnect();
			result = sb.toString();
		} catch (Exception e) {

		}
		return result;
	}

}
