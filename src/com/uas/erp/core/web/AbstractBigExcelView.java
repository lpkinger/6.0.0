package com.uas.erp.core.web;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * 生成大数据量excel
 * 
 * @author yingp
 *
 */
public abstract class AbstractBigExcelView extends AbstractView {

	protected static final String CONTENT_TYPE = "application/vnd.ms-excel";
	protected static final String EXTENSION = ".xlsx";
	private String url;

	public AbstractBigExcelView() {
		setContentType(CONTENT_TYPE);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	protected boolean generatesDownloadContent() {
		return true;
	}

	protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SXSSFWorkbook workbook;
		if (this.url != null) {
			workbook = getTemplateSource(this.url, request);
		} else {
			workbook = new SXSSFWorkbook();
			this.logger.debug("Created Excel Workbook from scratch");
		}

		buildExcelDocument(model, workbook, request, response);

		response.setContentType(getContentType());

		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
		workbook.dispose();
	}

	protected SXSSFWorkbook getTemplateSource(String url, HttpServletRequest request) throws Exception {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(url, EXTENSION, userLocale);

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Loading Excel workbook from " + inputFile);
		}
		return new SXSSFWorkbook(new XSSFWorkbook(inputFile.getInputStream()));
	}

	protected abstract void buildExcelDocument(Map<String, Object> model, SXSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	protected Cell getCell(Sheet sheet, int row, int col) {
		Row sheetRow = sheet.getRow(row);
		if (sheetRow == null) {
			sheetRow = sheet.createRow(row);
		}
		Cell cell = sheetRow.getCell(col);
		if (cell == null) {
			cell = sheetRow.createCell(col);
		}
		return cell;
	}

	protected void setText(Cell cell, String text) {
		cell.setCellType(1);
		cell.setCellValue(text);
	}

}
