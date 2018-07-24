package com.uas.erp.core.web;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * 生成csv
 * 
 * @author yingp
 *
 */
public abstract class AbstractCsvView extends AbstractView {

	protected static final String CONTENT_TYPE = "application/octet-stream;charset=UTF-8";
	protected static final String EXTENSION = ".csv";

	protected static final String outputDir = "/tmp/";

	public AbstractCsvView() {
		setContentType(CONTENT_TYPE);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CsvWriter writer = null;
		try {
			File file = new File(outputDir);
			if (!file.exists()) {
				file.mkdir();
			}
			writer = new CsvWriter();
			buildCsvDocument(model, writer, request, response);

			ServletOutputStream out = response.getOutputStream();
			writer.write(out);
			out.flush();
		} finally {
			writer.close();
		}
	}

	protected abstract void buildCsvDocument(Map<String, Object> model, CsvWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
