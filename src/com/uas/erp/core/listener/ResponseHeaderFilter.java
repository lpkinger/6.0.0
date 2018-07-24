package com.uas.erp.core.listener;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.util.UrlPathHelper;

import com.uas.erp.core.ExportObserve;

public class ResponseHeaderFilter implements Filter {

	private FilterConfig fc;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		for (@SuppressWarnings("unchecked")
		Enumeration<String> e = fc.getInitParameterNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			response.addHeader(headerName, fc.getInitParameter(headerName));
		}
		// 测试下导出
		HttpSession session = request.getSession();
		String url = urlPathHelper.getLookupPathForRequest(request);
		boolean isExport = "/common/excel/create.xls".equals(url);

		chain.doFilter(req, response);

		if (isExport) {
			ExportObserve.getInstance().remove(session.getId());
		}
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}

}
