package com.uas.b2b.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uas.api.crypto.util.FlexJsonUtils;

/**
 * 公共服务HTTP请求
 *
 * @author hejq
 * @date 2018-01-26 14:31
 */
public class PSHttpUtils {

	/**
	 * 发送GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendGetRequest(String url, Map<String, ?> params) throws Exception {
		return sendGetRequest(url, params, false);
	}

	/**
	 * 发送GET请求
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Response sendGetRequest(String url, Map<String, ?> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.GET, url, (Map<String, Object>) params, sign, false);
	}

	/**
	 * 暂时先使用这种方法（短信接口调用）
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("content-type", "application/json");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, Map<String, ?> params) throws Exception {
		return sendPostRequest(url, params, false);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Response sendPostRequest(String url, Map<String, ?> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.POST, url, (Map<String, Object>) params, sign, true);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @param encode
	 *            是否使用URLEncode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Response sendPostRequest(String url, Map<String, ?> params, boolean sign, boolean encode)
			throws Exception {
		return sendRequest(RequestMethod.POST, url, (Map<String, Object>) params, sign, encode);
	}

	/**
	 * 发送Post请求，直接将List类型放入其中
	 *
	 * @param postUrl
	 * @param formData
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String postUrl, String formData) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(postUrl);
		StringEntity postingString = new StringEntity(formData,  HTTP.UTF_8);
		post.setEntity(postingString);
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		String content = EntityUtils.toString(response.getEntity());
		return content;
	}

	/**
	 * 发送DELETE请求
	 * 
	 * @param url
	 * @param params
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Response sendDeleteRequest(String url, Map<String, ?> params) throws Exception {
		return sendDeleteRequest(url, params, false);
	}

	/**
	 * 发送DELETE请求
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Response sendDeleteRequest(String url, Map<String, ?> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.DELETE, url, (Map<String, Object>) params, sign, false);
	}

	/**
	 * 发起http请求
	 *
	 * @param method
	 *            请求方法GET、POST、PUT、DELETE
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param sign
	 *            是否签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendRequest(RequestMethod method, String url, Map<String, Object> params, boolean sign,
                                       boolean encode) throws Exception {
		switch (method) {
		case GET:
			return sendHttpUriRequest(new HttpGet(getRequestUrl(url, params, sign)));
		case POST:
			return sendHttpEntityEnclosingRequest(new HttpPost(getRequestUrl(url, sign)), params, encode);
		case PUT:
			return sendHttpEntityEnclosingRequest(new HttpPut(getRequestUrl(url, sign)), params, encode);
		case DELETE:
			return sendHttpUriRequest(new HttpDelete(getRequestUrl(url, params, sign)));
		default:
			return sendHttpUriRequest(new HttpGet(getRequestUrl(url, params, sign)));
		}
	}

	/**
	 * 发起GET、DELETE请求
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static Response sendHttpUriRequest(HttpRequestBase request) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			return Response.getResponse(response);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 发起POST、PUT请求
	 * 
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendHttpEntityEnclosingRequest(HttpEntityEnclosingRequestBase request,
                                                          Map<String, Object> params, boolean encode) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			if (!encode) {
				request.setEntity(new StringEntity(FlexJsonUtils.toJson(params), ContentType.APPLICATION_JSON));
			} else {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				if (params != null && !params.isEmpty()) {
					Set<Entry<String, Object>> entrys = params.entrySet();
					for (Map.Entry<String, Object> entry : entrys) {
						Object entryValue = entry.getValue();
						String entryValueStr = null;
						if (entryValue instanceof String) {
							entryValueStr = entryValue.toString();
						} else {
							entryValueStr = FlexJsonUtils.toJson(entry.getValue());
						}
						nvps.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entryValueStr, "UTF-8")));
					}
				}
				request.setEntity(new UrlEncodedFormEntity(nvps));
			}
			System.out.println("Post request: "+request);
			response = httpClient.execute(request);
			return Response.getResponse(response);
		} finally {
			request.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 将请求参数添加到链接中
	 *
	 * @param url
	 * @param params
	 * @param sign
	 *            是否签名
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getRequestUrl(String url, Map<String, Object> params, boolean sign)
			throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder(url);
		if (url.indexOf("?") == -1)
			buf.append("?");
		else if (!url.endsWith("&"))
			buf.append("&");
		// 如果是GET请求，则请求参数在URL中
		if (params != null && !params.isEmpty()) {
			Set<Entry<String, Object>> entrys = params.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"))
						.append("&");
			}
		}
		if (sign) {
			// 加时间戳，保持相同请求每次签名均不一样
			buf.append("_timestamp=").append(System.currentTimeMillis());
			String message = buf.toString();
			// 对请求串进行签名
//			buf.append("&_signature=").append(HmacUtils.encode(message));
		} else
			buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}

	/**
	 * 将签名信息添加到链接中
	 *
	 * @param url
	 * @param sign
	 *            是否签名
	 * @return
	 */
	public static String getRequestUrl(String url, boolean sign) {
		try {
			return getRequestUrl(url, null, sign);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	/**
	 * 将输入流转为字节数组
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read2Byte(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 将输入流转为字符串
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static String read2String(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		try {
			outSteam.close();
			inStream.close();
		} catch (Exception e) {

		}
		return new String(outSteam.toByteArray(), "UTF-8");
	}

	/**
	 * 发送xml数据
	 * 
	 * @param path
	 *            请求地址
	 * @param xml
	 *            xml数据
	 * @param encoding
	 *            编码
	 * @return
	 * @throws Exception
	 */
	public static byte[] postXml(String path, String xml, String encoding) throws Exception {
		byte[] data = xml.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "text/xml; charset=" + encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == HttpStatus.OK.value()) {
			return read2Byte(conn.getInputStream());
		}
		return null;
	}

	/**
	 * http上传文件
	 * 
	 * @param postUrl
	 *            请求地址
	 * @param filePath
	 *            附件路径
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static Response upload(String postUrl, String filePath, Map<String, String> params)
			throws IllegalStateException, IOException, Exception {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(postUrl);
			FileBody fileBody = new FileBody(new File(filePath));
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", fileBody);
			if (params != null) {
				for (String paramKey : params.keySet()) {
					StringBody body = new StringBody(params.get(paramKey), ContentType.create("text/plain",
							Consts.UTF_8));
					builder.addPart(paramKey, body);
				}
			}
			HttpEntity reqEntity = builder.build();
			httpPost.setEntity(reqEntity);
			response = httpClient.execute(httpPost);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Response.getResponse(response);
	}

	/**
	 * 下载
	 * 
	 * @param postUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream download(String postUrl) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(postUrl);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		return response.getEntity().getContent();
	}

	/**
	 * 获取请求客户端的IP地址
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * POST发送
	 * 
	 * @param url
	 * @param data
	 * @throws Exception
	 */
	public static String post(String url, String data) throws Exception {
		URL urls = new URL(url);
		HttpURLConnection http = (HttpURLConnection) urls.openConnection();
		http.setRequestMethod("POST");
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		http.setDoOutput(true);
		http.setDoInput(true);
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
		System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
		http.connect();
		OutputStream os = http.getOutputStream();
		os.write(data.getBytes("UTF-8"));// 传入参数
		os.flush();
		os.close();
		// 从服务器读取响应
		String encoding = http.getContentEncoding();
		if (encoding == null)
			encoding = "UTF-8";
		return StreamUtils.copyToString(http.getInputStream(), Charset.forName(encoding));
	}

	public static class Response {
		private int statusCode;
		private String responseText;
		private HttpResponse response;

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getResponseText() {
			return responseText;
		}

		public void setResponseText(String responseText) {
			this.responseText = responseText;
		}

		public HttpResponse getResponse() {
			return response;
		}

		public void setResponse(HttpResponse response) {
			this.response = response;
		}

		public Response() {
		}

		public Response(HttpResponse response) throws IllegalStateException, IOException, Exception {
			this.statusCode = response.getStatusLine().getStatusCode();
			this.responseText = PSHttpUtils.read2String(response.getEntity().getContent());
			this.response = response;
		}

		public static Response getResponse(HttpResponse response) throws IllegalStateException, IOException, Exception {
			if (response != null)
				return new Response(response);
			return null;
		}
	}
}
