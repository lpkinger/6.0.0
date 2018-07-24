package com.uas.erp.core.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.encry.HmacUtils;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;

public class SignatureInterceptor extends HandlerInterceptorAdapter {

	private final static String signatureParam = "_signature";

	private final static String timestampParam = "_timestamp";
	
	private static final String master = "master";

	private final static int timeout = 60000;
	// 已使用签名
	private Map<String, Long> signatureCache = new ConcurrentHashMap<>();
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("employee");
		if (obj != null) {
			return super.preHandle(request, response, handler);
		} else {
			String sign = request.getParameter(signatureParam);
			String dbmaster = request.getParameter(master);
			if (sign != null&&master != null) {
				String urlMessage = request.getRequestURL() + "?"
						+ request.getQueryString().substring(0, request.getQueryString().indexOf(signatureParam) - 1);
				Master master = null;
				if(dbmaster!=null){
					master = enterpriseService.getMasterByName(dbmaster);
				}
				if (master!=null) {
					SpObserver.putSp(master.getMa_name());
				}
				String servletPath = request.getServletPath();
				
				boolean check = false;
				if (servletPath.indexOf("applicant")>-1) {
					check = sign.equals(HmacUtils.encode(urlMessage));
				}else if (servletPath.indexOf("factoring")>-1) {
					check = StringUtil.hasText(master.getMa_fssecret())&&sign.equals(HmacUtils.encode(urlMessage,master.getMa_fssecret()));
				}
				
				if (check) {
					String timestamp = request.getParameter(timestampParam);
					long now = System.currentTimeMillis();
					if (!StringUtils.isEmpty(timestamp) && Math.abs(now - Long.parseLong(timestamp)) <= timeout
							&& !signatureCache.containsKey(sign)) {
						// 加入历史记录
						signatureCache.put(sign, now);
						return true;
					}
				}
			}
			response.setStatus(HttpStatus.FORBIDDEN.value());
		}
		return false;
	}

	/**
	 * 清除签名池历史记录
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void clearCache() {
		long now = System.currentTimeMillis();
		for (String key : signatureCache.keySet()) {
			long time = signatureCache.get(key);
			if (now - time > timeout) {
				signatureCache.remove(key);
			}
		}
	}

}
