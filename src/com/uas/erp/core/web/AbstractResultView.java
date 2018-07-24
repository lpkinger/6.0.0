package com.uas.erp.core.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.util.ResultQueueUtils;
import com.uas.erp.dao.util.RowViewProcesser;

public abstract class AbstractResultView<T> implements View {

	private String contentType;
	private String fileName;
	private String extension;
	private final DocumentConfig config;
	private String executable;

	public AbstractResultView(DocumentConfig config) {
		this.config = config;
	}

	public AbstractResultView(DocumentConfig config, String executable) {
		this.config = config;
		this.executable = executable;
	}

	protected abstract RowViewProcesser<T> getViewProcesser();

	public abstract T getCurrentRow();

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public DocumentConfig getConfig() {
		return config;
	}

	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Pragma", "private");
		response.setHeader("Cache-Control", "private, must-revalidate");
		response.setContentType(getContentType());
		response.setHeader("Content-disposition", "attachment;filename=" + getFileName() + getExtension());
	}

	/**
	 * 在写入结果前执行
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected abstract void beforeRender(HttpServletRequest request, HttpServletResponse response) throws Exception;

	protected void onRender() {
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		ResultQueueUtils.execute(baseDao.getJdbcTemplate(), executable, getViewProcesser());
	}

	/**
	 * 在写入结果后执行
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected abstract void afterRender(HttpServletRequest request, HttpServletResponse response) throws Exception;

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		prepareResponse(request, response);

		beforeRender(request, response);

		onRender();

		afterRender(request, response);
	}

}
