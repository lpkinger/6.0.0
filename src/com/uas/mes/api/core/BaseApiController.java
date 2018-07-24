package com.uas.mes.api.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import com.uas.erp.core.FlexJsonUtil;

/**
 * API接口的基类Controller
 * 
 * @author suntg
 * @since 2016年12月9日下午3:39:10
 */
public class BaseApiController {

	protected static final String defultCharset = "UTF-8";

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	public static final String KEY_SUCCESS = "success";
	public static final String KEY_DATA = "data";
	public static final String KEY_EXCEPTION_CODE = "exceptionCode";
	public static final String KEY_EXCEPTION_INFO = "exceptionInfo";

	protected static boolean isSuccess(ModelMap map) {
		return Boolean.TRUE.equals(map.get("success"));
	}

	protected static ModelMap success() {
		return new ModelMap(KEY_SUCCESS, true);
	}

	protected static ModelMap success(Object data) {
		return new ModelMap(KEY_SUCCESS, true).addAttribute(KEY_DATA, data);
	}

	protected static ModelMap error(int code, String message) {
		return new ModelMap(KEY_SUCCESS, false).addAttribute(KEY_EXCEPTION_CODE, code).addAttribute(KEY_EXCEPTION_INFO,
				message);
	}

	protected static ModelMap error(int code, Object detail) {
		return new ModelMap(KEY_SUCCESS, false).addAttribute(KEY_EXCEPTION_CODE, code).addAttribute(KEY_EXCEPTION_INFO,
				detail);
	}

	/**
	 * 输出json格式
	 * 
	 * @param obj
	 * @throws IOException
	 */
	protected void printJson(Object obj) throws IOException {
		response.addHeader("Content-Type", "application/json; charset=" + defultCharset);
		PrintWriter printWriter = response.getWriter();
		printWriter.append(FlexJsonUtil.toJson(obj));
		printWriter.flush();
		printWriter.close();
	}

	/**
	 * 响应Ajax请求
	 * 
	 * @param content
	 *            响应内容
	 * @throws IOException
	 */
	protected void printJsonP(String callback, Object content) throws IOException {
		if (!content.getClass().isAssignableFrom(String.class)) {
			content = FlexJsonUtil.toJson(content);
		}
		response.setContentType("text/html;charset=" + defultCharset);
		PrintWriter out = response.getWriter();
		out.print(callback + "(" + content + ")");
		out.flush();
	}

	/**
	 * 输出流
	 * 
	 * @param fileName
	 *            文件名
	 * @param bytes
	 * @throws IOException
	 */
	protected ResponseEntity<byte[]> outputStream(String fileName, byte[] bytes) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
	}
}
