package com.uas.erp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Configs;

public class UserAgentUtil {

	static final String[] MOBILE_SPECIFIC_SUBSTRING = { "iPad", "iPhone", "Android", "MIDP", "Opera Mobi", "Opera Mini", "BlackBerry",
			"HP iPAQ", "IEMobile", "MSIEMobile", "Windows Phone", "HTC", "LG", "MOT", "Nokia", "Symbian", "Fennec", "Maemo", "Tear",
			"Midori", "armv", "Windows CE", "WindowsCE", "Smartphone", "240x320", "176x220", "320x320", "160x160", "webOS", "Sagem",
			"Samsung", "SonyEricsson", "UCWEB" };

	/**
	 * 判断是否为移动设备登录
	 * 
	 * @param userAgent
	 * @return
	 */
	public static boolean isMobile(String userAgent) {
		userAgent = userAgent.toLowerCase();
		for (String mobile : MOBILE_SPECIFIC_SUBSTRING) {
			if (userAgent.indexOf(mobile.toLowerCase()) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是内网IP <br>
	 * 私有IP： <br>
	 * 10.0.0.0-10.255.255.255 <br>
	 * 172.16.0.0-172.31.255.255 <br>
	 * 192.168.0.0-192.168.255.255
	 * 
	 * @param ipAddress
	 * @return
	 */
	private static boolean isInnerIP(String ipAddress) {
		if (ipAddress == null) {
			return true;
		}
		boolean isInnerIp = false;
		long ipNum = getIpNum(ipAddress);
		long aBegin = 167772160L;// 10.0.0.0
		long aEnd = 184549375L;// 10.255.255.255
		long bBegin = 2886729728L;// 172.16.0.0
		long bEnd = 2887778303L;// 172.31.255.255
		long cBegin = 3232235520L;// 192.168.0.0
		long cEnd = 3232301055L;// 192.168.255.255
		isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
				|| ipAddress.equals("127.0.0.1") || isSpecialInner(ipAddress);
		return isInnerIp;
	}
	
	/**
	 * 取系统参数配置，判断是否在特殊ip段内
	 * @param ipAddress
	 * @return
	 */
	private static boolean isSpecialInner(String ipAddress){
		Configs configs = null;
		try {
			BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
			configs = baseDao.getJdbcTemplate().queryForObject("select *  from configs where caller=? and code=?",
					new BeanPropertyRowMapper<Configs>(Configs.class), "sys", "SYS_INNERIPS");
			String ipConfig = configs.getData();
			if(ipConfig != null && !"".equals(ipConfig.trim()) && !"null".equals(ipConfig.trim())){
				String[] ipConfigs = ipConfig.split(";");
				for(String ip : ipConfigs){
					if(ipAddress.substring(0, ipAddress.lastIndexOf(".")).equals(ip)){
						return true;
					}
				}
			}
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
		return false;
	}

	private static boolean isLocalhost(String ipAddress) {
		return ipAddress.startsWith("127.0.0.") || ipAddress.startsWith("0:0:0:0");
	}

	/**
	 * 判断是否内网访问
	 * 
	 * <pre>
	 * 打印程序调用的时候要谨慎，因为即使是内网地址，也存在网关不一样的情况
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	private static boolean isInner(HttpServletRequest request) {
		String requestServer = request.getServerName();
		if (requestServer.matches(Constant.REG_IPV4)) {
			return UserAgentUtil.isInnerIP(requestServer);
		}
		String ip = getIpAddr(request);
		boolean isInner = UserAgentUtil.isInnerIP(ip);
		// 本地调试，或者网络参数错误导致获取到的ip为网关地址情况下，采用外网访问模式
		if (isInner && (isLocalhost(ip) || ip.equals(getGateway()))) {
			isInner = false;
		}
		return isInner;
	}

	/**
	 * 例如从http://1.1.1.1:8080/a/b中获取1.1.1.1
	 * 
	 * @param requestUrl
	 * @return
	 */
	private static String getServerName(String requestUrl) {
		Pattern p = Pattern.compile("^(http(s){0,1}:(\\/){0,2})(.+?)((:|\\/)(.*))*$");
		Matcher m = p.matcher(requestUrl);
		if (m.find()) {
			return m.group(4);
		}
		return null;
	}

	/**
	 * 判断用户是否能访问{@code accessUrl}
	 * 
	 * @param request
	 * @param accessUrl
	 *            比如内网打印地址
	 * @return
	 */
	public static boolean accessible(HttpServletRequest request, String accessUrl) {
		String accessServer = getServerName(accessUrl);
		if (accessServer.matches(Constant.REG_IPV4) && isInnerIP(accessServer)) {
			if (isInner(request)) {
				String requestServer = request.getServerName();
				if (requestServer.matches(Constant.REG_IPV4)) {
					// 看是否处于同一网关
					return requestServer.substring(0, requestServer.lastIndexOf(".")).equals(
							accessServer.substring(0, accessServer.lastIndexOf(".")));
				} else {
					// 内网访问，而不是ip地址访问，说明有内网dns服务，或修改了本地host
					return true;
				}
			} else {
				// 当前本身是外网方式访问的
				return false;
			}
		} else {
			// 如果accessUrl是域名或外网IP，默认可访问
			return true;
		}
	}

	/**
	 * 获取IP数
	 * 
	 * @param ipAddress
	 * @return
	 */
	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);
		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

	/**
	 * request base path
	 * 
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	}

	/**
	 * 获取客户端IP
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		if (ipAddress != null && "0:0:0:0:0:0:0:1".equals(ipAddress)) {// window7系统下,用localhost访问时,ip会变成0:0:0:0:0:0:0:1
			ipAddress = "127.0.0.1";
		}
		return ipAddress;
	}

	/**
	 * 获取网关地址
	 * 
	 * @return
	 */
	public static String getGateway() {
		try {
			if (isWin()) {
				Process pro = Runtime.getRuntime().exec("ipconfig");
				BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream(), getSystemEncoding()));
				String temp;
				while ((temp = br.readLine()) != null) {
					if (temp.indexOf("Default Gateway") != -1 || temp.indexOf("默认网关") != -1) {
						Matcher mc = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(temp);
						if (mc.find()) {
							return mc.group();
						}
					}
				}
			} else {
				String[] cmd = new String[] { "/bin/sh", "-c", "route | grep -P \"^default.*$\" | awk '{print $2}'" };
				String gateway = null;
				Process process;
				try {
					process = Runtime.getRuntime().exec(cmd);
					InputStreamReader r = new InputStreamReader(process.getInputStream());
					LineNumberReader returnData = new LineNumberReader(r);
					gateway = returnData.readLine();
				} catch (IOException ex) {
				}
				return gateway;
			}
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 判断本机操作系统是否为windows
	 * 
	 * @return
	 */
	public static boolean isWin() {
		String os = System.getProperty("os.name");
		return os.toLowerCase().startsWith("win");
	}

	/**
	 * 获得系统编码方式
	 * 
	 * @return
	 */
	public static String getSystemEncoding() {
		if (isWin()) {
			return "GBK";
		}
		return System.getProperty("file.encoding");
	}

}
