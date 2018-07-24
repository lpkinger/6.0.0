package com.uas.erp.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.encry.HmacUtils;

/**
 * HTTP工具类，封装http请求
 * 
 * @author suntg
 * @date 2015年3月5日14:20:40
 */
@SuppressWarnings("deprecation")
public class HttpUtil {
	
	/** 
	 * 绕过验证 
	 *   
	 * @return 
	 * @throws NoSuchAlgorithmException  
	 * @throws KeyManagementException  
	 */  
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {  
	    SSLContext sc = SSLContext.getInstance("SSLv3");  
	  
	    // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
	    X509TrustManager trustManager = new X509TrustManager() {  
	        @Override  
	        public void checkClientTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                String paramString) throws CertificateException {  
	        }  
	  
	        @Override  
	        public void checkServerTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                String paramString) throws CertificateException {  
	        }  
	  
	        @Override  
	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
	            return null;  
	        }  
	    };  
	  
	    sc.init(null, new TrustManager[] { trustManager }, null);  
	    return sc;  
	}  

	/**
	 * 发送GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendGetRequest(String url, Map<String, String> params) throws Exception {
		return sendGetRequest(url, params, false, null);
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
	public static Response sendGetRequest(String url, Map<String, String> params, boolean sign, String signKey) throws Exception {
		return sendRequest(RequestMethod.GET, url, params, sign, signKey);
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
	public static Response sendGetRequest(String url, Map<String, String> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.GET, url, params, sign, null);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, Map<String, String> params) throws Exception {
		return sendPostRequest(url, params, false, null);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, List<?> datas) throws Exception {
		return sendPostRequest(url, datas, false, null);
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
	public static Response sendPostRequest(String url, Map<String, String> params, boolean sign, String signKey) throws Exception {
		return sendRequest(RequestMethod.POST, url, params, sign, signKey);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param datas
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, List<?> datas, boolean sign, String signKey) throws Exception {
		return sendRequest(RequestMethod.POST, url, datas, sign, signKey);
	}

	/**
	 * 发送POST请求
	 * 
	 * <pre>
	 * 使用默认密钥
	 * </pre>
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, Map<String, String> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.POST, url, params, sign, null);
	}

	/**
	 * 发送POST请求
	 * 
	 * <pre>
	 * 使用默认密钥
	 * </pre>
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPostRequest(String url, List<?> datas, boolean sign) throws Exception {
		return sendRequest(RequestMethod.POST, url, datas, sign, null);
	}
	
	/**
	 * 发送post请求
	 * 
	 * @param postUrl
	 * @param formData
	 * @return
	 * @throws Exception
	 */
	public static Response doPost(String postUrl, String formData,  boolean sign, String signKey) throws Exception {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
	      
	    // 设置协议http和https对应的处理socket链接工厂的对象  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
           .register("http", PlainConnectionSocketFactory.INSTANCE)  
           .register("https", new SSLConnectionSocketFactory(sslcontext))  
           .build();  
       	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
       	HttpClients.custom().setConnectionManager(connManager);  
       	
       	//创建自定义的httpclient对象  
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        postUrl = getUrl(postUrl, sign, signKey);
        HttpPost post = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(formData, HTTP.UTF_8);
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = httpClient.execute(post);
        return Response.getResponse(response);
    }
	
	public static Response doPost(String postUrl, String formData,  boolean sign, String signKey, int timeout) throws Exception {
		URL url = new URL(postUrl);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		try {
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			urlConn.setUseCaches(false);
			urlConn.setInstanceFollowRedirects(true);
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(timeout);
			urlConn.setReadTimeout(timeout);
			if (null != formData) {
				OutputStreamWriter osw = new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8");
				osw.write(formData);
				osw.flush();
				osw.close();
			}
			return new Response(urlConn.getResponseCode() == 200, streamToString(urlConn.getInputStream()));
		} catch (Exception e) {
			return new Response(false, e.getMessage());
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
    }
	
	public static String streamToString(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder buf = new StringBuilder();

		try {
			char[] chars = new char[2048];
			for (;;) {
				int len = reader.read(chars, 0, chars.length);
				if (len < 0) {
					break;
				}
				buf.append(chars, 0, len);
			}
		} catch (Exception ex) {
			throw new Exception("read string from reader error", ex);
		}

		return buf.toString();
	}

	/**
	 * 封装加密
	 * 
	 * @param postUrl
	 * @param sign
	 * @param signKey
	 * @return
	 */
	private static String getUrl(String url, boolean sign, String signKey) {
		StringBuilder buf = new StringBuilder(url);
		if (url.indexOf("?") == -1)
			buf.append("?");
		else if (!url.endsWith("&"))
			buf.append("&");
		if (sign) {
			// 加时间戳，保持相同请求每次签名均不一样
			buf.append("_timestamp=").append(System.currentTimeMillis());
			String message = buf.toString();
			// 对请求串进行签名
			buf.append("&_signature=").append(HmacUtils.encode(message, signKey));
		} else
			buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}

	/**
	 * 发送PUT请求
	 * 
	 * <pre>
	 * 使用默认密钥
	 * </pre>
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPutRequest(String url, List<?> datas, boolean sign) throws Exception {
		return sendRequest(RequestMethod.PUT, url, datas, sign, null);
	}

	/**
	 * 发送PUT请求
	 * 
	 * <pre>
	 * 使用默认密钥
	 * </pre>
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPutRequest(String url, Map<String, String> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.PUT, url, params, sign, null);
	}

	/**
	 * 发送PUT请求
	 * 
	 * @param url
	 * @param datas
	 * @param sign
	 *            是否发送签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendPutRequest(String url, List<?> datas, boolean sign, String signKey) throws Exception {
		return sendRequest(RequestMethod.PUT, url, datas, sign, signKey);
	}

	/**
	 * 发送PUT请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response sendPutRequest(String url, List<?> datas) throws Exception {
		return sendPutRequest(url, datas, false, null);
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
	public static Response sendDeleteRequest(String url, Map<String, String> params) throws Exception {
		return sendDeleteRequest(url, params, false, null);
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
	public static Response sendDeleteRequest(String url, Map<String, String> params, boolean sign, String signKey) throws Exception {
		return sendRequest(RequestMethod.DELETE, url, params, sign, signKey);
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
	public static Response sendDeleteRequest(String url, Map<String, String> params, boolean sign) throws Exception {
		return sendRequest(RequestMethod.DELETE, url, params, sign, null);
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
	public static Response sendRequest(RequestMethod method, String url, Map<String, String> params, boolean sign, String signKey)
			throws Exception {
		switch (method) {
		case GET:
			return sendHttpUriRequest(new HttpGet(getRequestUrl(url, params, sign, signKey)));
		case POST:
			return sendHttpEntityEnclosingRequest(new HttpPost(getRequestUrl(url, sign, signKey)), params);
		case PUT:
			return sendHttpEntityEnclosingRequest(new HttpPut(getRequestUrl(url, sign, signKey)), params);
		case DELETE:
			return sendHttpUriRequest(new HttpDelete(getRequestUrl(url, params, sign, signKey)));
		default:
			return sendHttpUriRequest(new HttpGet(getRequestUrl(url, params, sign, signKey)));
		}
	}

	/**
	 * 发起http请求
	 * 
	 * @param method
	 *            请求方法POST、PUT
	 * @param url
	 *            请求链接
	 * @param datas
	 *            参数
	 * @param sign
	 *            是否签名
	 * @return
	 * @throws Exception
	 */
	public static Response sendRequest(RequestMethod method, String url, List<?> datas, boolean sign, String signKey) throws Exception {
		switch (method) {
		case POST:
			return sendHttpEntityEnclosingRequest(new HttpPost(getRequestUrl(url, sign, signKey)), datas);
		case PUT:
			return sendHttpEntityEnclosingRequest(new HttpPut(getRequestUrl(url, sign, signKey)), datas);
		default:
			return sendHttpEntityEnclosingRequest(new HttpPost(getRequestUrl(url, sign, signKey)), datas);
		}
	}

	/**
	 * 发起GET、DELETE请求
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static Response sendHttpUriRequest(HttpRequestBase request) throws Exception {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
	      
	    // 设置协议http和https对应的处理socket链接工厂的对象  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
           .register("http", PlainConnectionSocketFactory.INSTANCE)  
           .register("https", new SSLConnectionSocketFactory(sslcontext))  
           .build();  
       	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
       	HttpClients.custom().setConnectionManager(connManager);  
       	
       	//创建自定义的httpclient对象  
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
		//CloseableHttpClient httpClient = HttpClients.createDefault();
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
	private static Response sendHttpEntityEnclosingRequest(HttpEntityEnclosingRequestBase request, Map<String, String> params)
			throws Exception {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
	      
	    // 设置协议http和https对应的处理socket链接工厂的对象  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
           .register("http", PlainConnectionSocketFactory.INSTANCE)  
           .register("https", new SSLConnectionSocketFactory(sslcontext))  
           .build();  
       	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
       	HttpClients.custom().setConnectionManager(connManager);  
       	
       	//创建自定义的httpclient对象  
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				Set<Entry<String, String>> entrys = params.entrySet();
				for (Map.Entry<String, String> entry : entrys) {
					nvps.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8")));
				}
			}
			request.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpClient.execute(request);
			System.out.println(request);
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
	 * 发起POST、PUT请求
	 * 
	 * @param request
	 * @param datas
	 * @return
	 * @throws Exception
	 */
	private static Response sendHttpEntityEnclosingRequest(HttpEntityEnclosingRequestBase request, List<?> datas) throws Exception {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
	      
	    // 设置协议http和https对应的处理socket链接工厂的对象  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
           .register("http", PlainConnectionSocketFactory.INSTANCE)  
           .register("https", new SSLConnectionSocketFactory(sslcontext))  
           .build();  
       	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
       	HttpClients.custom().setConnectionManager(connManager);  
       	
       	//创建自定义的httpclient对象  
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			if (datas != null && !datas.isEmpty()) {
				request.setEntity(new StringEntity(FlexJsonUtil.toJsonArrayDeep(datas), ContentType.create("text/plain", Consts.UTF_8)));
			}
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
	public static String getRequestUrl(String url, Map<String, String> params, boolean sign) throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder(url);
		if (url.indexOf("?") == -1)
			buf.append("?");
		else if (!url.endsWith("&"))
			buf.append("&");
		// 如果是GET请求，则请求参数在URL中
		if (params != null && !params.isEmpty()) {
			Set<Entry<String, String>> entrys = params.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
			}
		}
		if (sign) {
			// 加时间戳，保持相同请求每次签名均不一样
			buf.append("_timestamp=").append(System.currentTimeMillis());
			String message = buf.toString();
			// 对请求串进行签名
			buf.append("&_signature=").append(HmacUtils.encode(message));
		} else
			buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}

	/**
	 * 将请求参数添加到链接中
	 * 
	 * @param url
	 * @param params
	 * @param sign
	 *            是否签名
	 * @param signKey
	 *            签名密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getRequestUrl(String url, Map<String, String> params, boolean sign, String signKey)
			throws UnsupportedEncodingException {
		if (sign && signKey == null)
			return getRequestUrl(url, params, sign);
		StringBuilder buf = new StringBuilder(url);
		if (url.indexOf("?") == -1)
			buf.append("?");
		else if (!url.endsWith("&"))
			buf.append("&");
		// 如果是GET请求，则请求参数在URL中
		if (params != null && !params.isEmpty()) {
			Set<Entry<String, String>> entrys = params.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
			}
		}
		if (sign) {
			// 加时间戳，保持相同请求每次签名均不一样
			buf.append("_timestamp=").append(System.currentTimeMillis());
			String message = buf.toString();
			// 对请求串进行签名
			buf.append("&_signature=").append(HmacUtils.encode(message, signKey));
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
	 * @throws UnsupportedEncodingException
	 */
	private static String getRequestUrl(String url, boolean sign, String signKey) throws UnsupportedEncodingException {
		return getRequestUrl(url, null, sign, signKey);
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
	public static Response upload(String postUrl, String filePath, Map<String, String> params, boolean sign, String signKey)
			throws IllegalStateException, IOException, Exception {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(getRequestUrl(postUrl, sign, signKey));
			FileBody fileBody = new FileBody(new File(filePath));
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", fileBody);
			if (params != null) {
				for (String paramKey : params.keySet()) {
					StringBody body = new StringBody(params.get(paramKey), ContentType.create("text/plain", Consts.UTF_8));
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
	 * http上传文件
	 * 
	 * @param postUrl
	 *            请求地址
	 * @param file
	 *            附件
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static Response upload(String postUrl, MultipartFile file, Map<String, String> params, boolean sign, String signKey)
			throws IllegalStateException, IOException, Exception {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(getRequestUrl(postUrl, sign, signKey));
			InputStreamBody sbody = new InputStreamBody(file.getInputStream(), file.getOriginalFilename());
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", sbody);
			if (params != null) {
				for (String paramKey : params.keySet()) {
					StringBody body = new StringBody(params.get(paramKey), ContentType.create("text/plain", Consts.UTF_8));
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
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static InputStream download(String postUrl) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
	      
	    // 设置协议http和https对应的处理socket链接工厂的对象  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
           .register("http", PlainConnectionSocketFactory.INSTANCE)  
           .register("https", new SSLConnectionSocketFactory(sslcontext))  
           .build();  
       	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
       	HttpClients.custom().setConnectionManager(connManager);  
       	
       	//创建自定义的httpclient对象  
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(postUrl);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		return response.getEntity().getContent();
	}

	public static class Response {
		private int statusCode;
		private String responseText;

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

		public Response() {
		}

		public Response(boolean success, String content) {
			super();
			this.statusCode = success ? 200 : 404;
			this.responseText = content;
		}
		
		public Response(HttpResponse response) throws IllegalStateException, IOException, Exception {
			this.statusCode = response.getStatusLine().getStatusCode();
			this.responseText = HttpUtil.read2String(response.getEntity().getContent());
		}

		public static Response getResponse(HttpResponse response) throws IllegalStateException, IOException, Exception {
			if (response != null)
				return new Response(response);
			return null;
		}
	}
}
