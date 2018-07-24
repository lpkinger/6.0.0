package com.uas.erp.core.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.StringUtils;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.util.BigExcelViewProcesser;
import com.uas.erp.dao.util.RowViewProcesser;

/**
 * 导出xlsx
 * 
 * @author yingp
 *
 */
public class DefaultBigExcelView extends AbstractResultView<Row> {

	public DefaultBigExcelView(DocumentConfig config, String executable) {
		super(config, executable);
		setContentType("application/vnd.ms-excel");
		setExtension(".xlsx");
	}

	public DefaultBigExcelView(DocumentConfig config, String executable, String fileName) {
		this(config, executable);
		setFileName(fileName);
	}

	private SXSSFWorkbook workbook;
	private Sheet currentSheet;
	private int currentSheetIndex = 0;
	private int currentRowIndex = 0;

	/**
	 * 大标题行
	 */
	private void createTitleRow() {
		Row row = currentSheet.createRow(currentRowIndex);
		row.setHeightInPoints((short) 20);
		currentSheet.setColumnWidth(0, 1000);
		Cell cell = getCell(row, 0);
		cell.setCellValue(getConfig().getTitle());
		currentSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, getConfig().getFields().size() - 1));
	}

	/**
	 * 列抬头
	 */
	private void createHeaderRow() {
		Row row = currentSheet.createRow(currentRowIndex);
		row.setHeightInPoints((short) 20);
		DataFormat format = workbook.createDataFormat();
		CellStyle style = getHeaderCellStyle();
		int colIdx = 0;
		for (String key : getConfig().getFields()) {
			short width = (short) (getConfig().getWidths().get(key) * 35.7);
			width = width == 0 ? 6000 : width;
			currentSheet.setColumnWidth(colIdx, width);
			Boolean lock = getConfig().getLocks().get(key);
			if (lock != null && lock)
				currentSheet.createFreezePane(colIdx + 1, 1);
			Cell cell = getCell(row, colIdx);
			cell.setCellValue(getConfig().getHeaders().get(key));
			cell.setCellStyle(style);

			String type = getConfig().getTypes().get(key);
			if (type != null && !Constant.TYPE_YN.equals(type) && !Constant.TYPE_COMBO.equals(type) && !type.startsWith("0")) {
				CellStyle s = workbook.createCellStyle();
				s.setDataFormat(format.getFormat(type));
				getConfig().getStyles().put(key, s);
			}
			colIdx++;
		}
	}

	/**
	 * excel抬头样式
	 * 
	 * @return
	 */
	private CellStyle getHeaderCellStyle() {
		CellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(CellStyle.LEAST_DOTS);
		style.setFillPattern(CellStyle.LEAST_DOTS);
		Font font = workbook.createFont();
		font.setFontName("仿宋_GB2312");// 字体
		font.setFontHeightInPoints((short) 12);// 字号
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		font.setColor(HSSFColor.GREY_80_PERCENT.index);// 颜色
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(HSSFColor.GREEN.index);
		style.setFillBackgroundColor(HSSFColor.PALE_BLUE.index);
		style.setBorderBottom(CellStyle.BORDER_MEDIUM);
		style.setBorderLeft(CellStyle.BORDER_MEDIUM);
		style.setBorderRight(CellStyle.BORDER_MEDIUM);
		style.setBorderTop(CellStyle.BORDER_MEDIUM);
		return style;
	}

	/**
	 * 插入合计行
	 */
	private void insertSummaryRow() {
		if (getConfig().getSummary().size() > 0 && currentRowIndex > 2) {
			Row row = currentSheet.createRow(currentRowIndex);
			Cell cell = null;
			int colIdx = 0;
			for (String key : getConfig().getFields()) {
				if (getConfig().getSummary().get(key)) {
					cell = getCell(row, colIdx);
					String addr = getColumnAddress(colIdx + 1);
					cell.setCellFormula("sum(" + addr + "2:" + addr + currentRowIndex + ")");
				}
				colIdx++;
			}
		}
	}

	private Cell getCell(Row row, int col) {
		Cell cell = row.getCell(col);
		if (cell == null) {
			cell = row.createCell(col);
		}
		return cell;
	}

	private static String getColumnAddress(int colIdx) {
		int c = colIdx / 26;
		int d = colIdx % 26;
		c = d == 0 ? (c - 1) : c;
		d = d == 0 ? 26 : d;
		return new StringBuffer().append(c >= 1 ? (char) (64 + c) : "").append((char) (64 + d)).toString();
	}

	private void createSheet() {
		currentSheet = workbook.createSheet("Sheet" + currentSheetIndex);
		currentSheet.autoSizeColumn((short) 2);
		currentSheet.createFreezePane(0, StringUtils.hasText(getConfig().getTitle()) ? 2 : 1);// 固定行
		if (StringUtils.hasText(getConfig().getTitle())) {
			createTitleRow();
			currentRowIndex++;
		}
		createHeaderRow();
		currentRowIndex++;
	}

	@Override
	protected void beforeRender(HttpServletRequest request, HttpServletResponse response) throws Exception {
		workbook = new SXSSFWorkbook();
	}

	@Override
	protected RowViewProcesser<Row> getViewProcesser() {
		return new BigExcelViewProcesser(this);
	}

	@Override
	public Row getCurrentRow() {
		if (currentRowIndex % 65535 == 0) {
			currentSheetIndex++;
			currentRowIndex = 0;
			createSheet();
		}
		return currentSheet.createRow(currentRowIndex++);
	}

	@Override
	protected void afterRender(HttpServletRequest request, HttpServletResponse response) throws Exception {
		insertSummaryRow();

		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
		workbook.dispose();
	}

}
