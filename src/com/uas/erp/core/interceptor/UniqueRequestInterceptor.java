package com.uas.erp.core.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.uas.erp.core.BaseUtil;

/**
 * 拦截唯一请求
 * 
 * @author yingp
 * 
 */
public class UniqueRequestInterceptor extends HandlerInterceptorAdapter {

	private String[] paths;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private static Map<String, Long> actions = new ConcurrentHashMap<String, Long>();

	private final static int TIMEOUT = 15000;

	/**
	 * 请求之前执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		if (paths.length > 0) {
			String url = urlPathHelper.getLookupPathForRequest(req);
			if (isPathMatch(url)) {
				if (isLimited(url)) {
					BaseUtil.showError("您的请求被拒绝。原因：同一时间段内请求人数过多，请15秒之后再试！");
				} else {
					addLimit(url);
				}
			}
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		if (paths.length > 0) {
			// 完成后去掉
			String url = urlPathHelper.getLookupPathForRequest(request);
			actions.remove(url);
		}
	}

	private boolean isPathMatch(String url) {
		for (String path : paths) {
			if (url.equals(path))
				return true;
		}
		return false;
	}

	private boolean isLimited(String url) {
		Long timeStart = actions.get(url);
		if (timeStart != null) {
			Long now = System.currentTimeMillis();
			return now - timeStart < TIMEOUT;
		}
		return false;
	}

	private void addLimit(String url) {
		actions.put(url, System.currentTimeMillis());
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}
}
