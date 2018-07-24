package com.uas.erp.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

/**
 * 规范controller返回参数格式
 * 
 * @date 2016年11月7日下午2:54:15
 * @author yingp
 *
 */
public class BaseController {
	/**
	 * extjs传回的参数为unicode编码格式时，spring的mvc不能转换过来???貌似因为utf-8为unicode的子格式??? 通过此方法手动将形如\u5800\u5800的字符串转换为汉字
	 * 
	 * @add by yingp 或者前台js用unescape解析成汉字也可以
	 */
	protected String decodeUnicodeString(String param) {
		try {
			return new String(param.getBytes("utf-8"), defultCharset);
		} catch (UnsupportedEncodingException e) {
			return param;
		}
	}

	protected static final String defultCharset = "UTF-8";

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private static final String ERROR_CODE = "errCode";

	private static final String CONTENT = "content";

	private static final String ERROR_MESSAGE = "exceptionInfo";// 与异常处理的exceptionInfo一致

	protected ModelMap success() {
		return new ModelMap(SUCCESS, true);
	}

	protected ModelMap success(Object content) {
		return success().addAttribute(CONTENT, content);
	}

	protected ModelMap error() {
		return new ModelMap(ERROR, true);
	}

	protected ModelMap error(String errMsg) {
		return error().addAttribute(ERROR_MESSAGE, errMsg);
	}

	protected ModelMap error(int errCode, String errMsg) {
		return error(errMsg).addAttribute(ERROR_CODE, errCode);
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
		try {
			headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, defultCharset));
		} catch (UnsupportedEncodingException e) {
		}
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
	}

}
