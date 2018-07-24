package com.uas.erp.model;

import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.uas.erp.core.DateUtil;

public class ExcelCell implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 单元格对应的值
	private Object d;
	// 内容对齐方式
	private String a;
	// 单元格对应的字体
	private String ff;
	// 字体大小
	private String fs;
	// 背景颜色
	private String bg;
	// 边框样式
	private String bb;

	public Object getD() {
		return d;
	}

	public void setD(Object d) {
		this.d = d;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getFf() {
		return ff;
	}

	public void setFf(String ff) {
		this.ff = ff;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public String getBb() {
		return bb;
	}

	public void setBb(String bb) {
		this.bb = bb;
	}

	public ExcelCell() {
	}

	public ExcelCell(HSSFCell cell) {
		if (cell != null) {
			Object value = cell.toString();
			Workbook wb = cell.getSheet().getWorkbook();
			HSSFCellStyle cellstyle = cell.getCellStyle();
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
				} else {
					value = cell.getNumericCellValue();
				}
				break;
			case HSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				value = cell.getCellFormula() + "";
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				value = "";
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				value = "";
				break;
			default:
				value = "";
				break;
			}
			if (cellstyle.getWrapText()) {
				this.d = value.toString().replaceAll("\n", "<br/>");
			} else
				this.d = value;
			Short align = cellstyle.getAlignment();
			switch (align) {
			case HSSFCellStyle.VERTICAL_CENTER:
				this.a = "middle";
			case HSSFCellStyle.ALIGN_FILL:
				this.a = "fill";
			case HSSFCellStyle.ALIGN_RIGHT:
				this.a = "right";
			case HSSFCellStyle.VERTICAL_BOTTOM:
				this.a = "bottom";
			case HSSFCellStyle.VERTICAL_TOP:
				this.a = "top";
			default:
				this.a = "middle";
			}
			Font font = wb.getFontAt(cellstyle.getFontIndex());
			this.ff = "font-family:" + font.getFontName();
			if (!font.getFontName().equals("Arial")) {
				this.fs = "font-size:" + font.getFontHeightInPoints() + "px;line-height:" + font.getFontHeightInPoints() + "px;";
			} else
				this.fs = "null";
			this.bg = excelColor2UOF(cellstyle.getFillForegroundColorColor()).toRGBHexString();
			if (cellstyle.getBorderBottom() != 0) {
				this.bb = "b-b";
			} else if (cellstyle.getBorderLeft() != 0) {
				this.bb = "l-b";
			} else if (cellstyle.getBorderTop() != 0) {
				this.bb = "t-b";
			} else if (cellstyle.getBorderRight() != 0) {
				this.bb = "r-b";
			} else
				this.bb = "null";
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"d\":\"" + this.getD() + "\",");
		sb.append("\"ff\":\"" + this.getFf() + "\",");
		sb.append("\"a\":\"" + this.getA() + "\",");
		if (this.getFs() != null && !this.getFs().equals("null")) {
			sb.append("\"fs\":\"" + this.getFs() + "\",");
		}
		sb.append("\"bg\":\"" + this.getBg() + "\"");
		if (this.bb != null && !this.bb.equals("null")) {
			sb.append(",");
			sb.append("\"bb\":\"" + this.getBb() + "\"");
		}
		sb.append("}");
		return sb.toString();
	}

	private static ColorInfo excelColor2UOF(Color color) {
		if (color == null) {
			return null;
		}
		ColorInfo ci = null;
		if (color instanceof XSSFColor) {// .xlsx
			XSSFColor xc = (XSSFColor) color;
			byte[] b = xc.getRgb();
			if (b != null) {// 一定是argb
				ci = ColorInfo.fromARGB(b[0], b[1], b[2], b[3]);
			}
		} else if (color instanceof HSSFColor) {// .xls
			HSSFColor hc = (HSSFColor) color;
			short[] s = hc.getTriplet();// 一定是rgb
			if (s != null) {
				ci = ColorInfo.fromARGB(s[0], s[1], s[2]);
			}
		}
		return ci;
	}
}
