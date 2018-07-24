package com.uas.erp.core.interceptor;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;

/**
 * 第三方接口拦截器
 * sign:由公钥加密钥加时间戳组成的加密字符串
 * timestamp:时间戳
 * @author hx
 *
 */
public class PrivacyPolicyInterceptor extends HandlerInterceptorAdapter{
	
	private final static int timeout = 300000;
	
	@Autowired
	private BaseDao baseDao;
	
	private Map<String, Long> signatureCache = new ConcurrentHashMap<>();
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sign = request.getParameter("sign");
		String timestamp = request.getParameter("timestamp");
		String code = request.getParameter("code");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dbmaster = request.getParameter("master");
		Master master = null;
		if(sign!=null&&!"".equals(sign)&&timestamp!=null&&!"".equals(timestamp)) {
			try {
				if(dbmaster!=null&&!"".equals(dbmaster)) {
					master = enterpriseService.getMasterByName(dbmaster);
				}else {
					master = enterpriseService.getMasterByName(BaseUtil.getXmlSetting("defaultSob"));
				}
				SpObserver.putSp(master.getMa_name());
				Object[] data = baseDao.getFieldsDataByCondition("thirdinterfaces", "tif_privatekey", "tif_valid=1 and tif_code='"+code+"'");
				if(data.length>0) {
					Date sysNow = new Date();
					Date timestampDate = df.parse(timestamp);
					if((sysNow.getTime()-timestampDate.getTime())<timeout) {
						String key = MD5Encode(String.valueOf(data[0])+timestamp);
						if(key.equals(sign)) {
							// 加入历史记录
							signatureCache.put(sign, sysNow.getTime());
							return true;
						}
					}
				}
			}catch (Exception e) {
				response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return false;
			}
		}
		response.setStatus(HttpStatus.FORBIDDEN.value());
		return false;
	}
	/**
	 * 清除签名池历史记录
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void clearCache() {
		long now = new Date().getTime();
		for (String key : signatureCache.keySet()) {
			long time = signatureCache.get(key);
			if (now - time > timeout) {
				signatureCache.remove(key);
			}
			System.out.println("remove:"+key+" sysdate:"+new Date());
		}
	}
	private static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));
		} catch (Exception exception) {
		}
		return resultString;
	}
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}
	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
