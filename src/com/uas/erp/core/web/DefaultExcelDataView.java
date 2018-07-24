package com.uas.erp.core.web;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.StringUtils;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.util.ExcelViewProcesser;
import com.uas.erp.dao.util.RowViewProcesser;

/**
 * 导出xls
 * 
 * @author yingp
 *
 */
public class DefaultExcelDataView extends AbstractDataView<HSSFRow> {

	public DefaultExcelDataView(DocumentConfig config, List<Map<String, Object>> datas) {
		super(config, datas);
		setContentType("application/vnd.ms-excel");
		setExtension(".xls");
	}

	public DefaultExcelDataView(DocumentConfig config, List<Map<String, Object>> datas, String fileName) {
		this(config, datas);
		setFileName(fileName);
	}

	private HSSFWorkbook workbook;
	private HSSFSheet currentSheet;
	private int currentSheetIndex = 0;
	private int currentRowIndex = 0;

	/**
	 * 大标题行
	 */
	private void createTitleRow() {
		HSSFRow row = currentSheet.createRow(currentRowIndex);
		row.setHeightInPoints((short) 20);
		currentSheet.setColumnWidth(0, 1000);
		HSSFCell cell = getCell(row, 0);
		cell.setCellValue(getConfig().getTitle());
		currentSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, getConfig().getFields().size() - 1));
	}

	/**
	 * 列抬头
	 */
	private void createHeaderRow() {
		HSSFRow row = currentSheet.createRow(currentRowIndex);
		row.setHeightInPoints((short) 20);
		HSSFDataFormat format = workbook.createDataFormat();
		HSSFCellStyle style = getHeaderCellStyle();
		//必填字段表头样式
		HSSFCellStyle necessaryStyle = getNecessaryHeaderCellStyle();
		int colIdx = 0;
		for (String key : getConfig().getFields()) {
			short width = (short) (getConfig().getWidths().get(key) * 35.7);
			width = width == 0 ? 6000 : width;
			currentSheet.setColumnWidth(colIdx, width);
			Boolean lock = getConfig().getLocks().get(key);
			if (lock != null && lock)
				currentSheet.createFreezePane(colIdx + 1, 1);
			HSSFCell cell = getCell(row, colIdx);
			cell.setCellValue(getConfig().getHeaders().get(key));
			//判断是否是必填字段
			Boolean necessary = getConfig().getNecessary().get(key);
			if(necessary!=null&&necessary){
				cell.setCellStyle(necessaryStyle);
			}else{				
				cell.setCellStyle(style);
			}

			String type = getConfig().getTypes().get(key);
			if (type != null && !Constant.TYPE_YN.equals(type) && !Constant.TYPE_COMBO.equals(type) && !type.startsWith("0")) {
				HSSFCellStyle s = workbook.createCellStyle();
				s.setDataFormat(format.getFormat(type));
				getConfig().getStyles().put(key, s);
			}
			//插入批注
			String commentStr = getConfig().getComments().get(key);
			if (commentStr != null ) {
				Drawing drawing =currentSheet.createDrawingPatriarch();			
				ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, colIdx,row.getRowNum(),colIdx+1,row.getRowNum()+1);//创建批注位置
				Comment comment = drawing.createCellComment(anchor);//创建批注
				comment.setString(new HSSFRichTextString(commentStr));//设置批注内容
				comment.setAuthor("Admin");//设置批注作者
				cell.setCellComment(comment);//把批注赋值给单元格
			}
			colIdx++;
		}
	}

	/**
	 * excel抬头样式
	 * 
	 * @return
	 */
	private HSSFCellStyle getHeaderCellStyle() {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
		style.setFillPattern(HSSFCellStyle.LEAST_DOTS);
		HSSFFont font = workbook.createFont();
		font.setFontName("仿宋_GB2312");// 字体
		font.setFontHeightInPoints((short) 12);// 字号
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		font.setColor(HSSFColor.GREY_80_PERCENT.index);// 颜色
		style.setFont(font);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(HSSFColor.GREEN.index);
		style.setFillBackgroundColor(HSSFColor.PALE_BLUE.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		return style;
	}
	
	/**
	 * excel抬头样式（必填字段）
	 * 
	 * @return
	 */
	private HSSFCellStyle getNecessaryHeaderCellStyle() {
		HSSFCellStyle style = getHeaderCellStyle();
		HSSFFont font = style.getFont(workbook);
		font.setColor(HSSFColor.RED.index);  // 设置字体颜色为红色
		style.setFont(font);
		return style;
	}

	/**
	 * 插入合计行
	 */
	private void insertSummaryRow() {
		if (getConfig().getSummary().size() > 0 && currentRowIndex > 2) {
			HSSFRow row = currentSheet.createRow(currentRowIndex);
			HSSFCell cell = null;
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

	private HSSFCell getCell(HSSFRow row, int col) {
		HSSFCell cell = row.getCell(col);
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
	protected RowViewProcesser<HSSFRow> getViewProcesser() {
		return new ExcelViewProcesser(this);
	}

	@Override
	public HSSFRow getCurrentRow() {
		if (currentRowIndex % 65535 == 0) {
			currentSheetIndex++;
			currentRowIndex = 0;
			createSheet();
		}
		return currentSheet.createRow(currentRowIndex++);
	}

	@Override
	protected void beforeRender(HttpServletRequest request, HttpServletResponse response) throws Exception {
		workbook = new HSSFWorkbook();
	}

	@Override
	protected void afterRender(HttpServletRequest request, HttpServletResponse response) throws Exception {
		insertSummaryRow();

		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}

}
