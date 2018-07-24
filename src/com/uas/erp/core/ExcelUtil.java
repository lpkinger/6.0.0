package com.uas.erp.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.web.AbstractBigExcelView;
import com.uas.erp.core.web.AbstractCsvView;
import com.uas.erp.core.web.CsvWriter;
import com.uas.erp.core.web.ExcelViewUtils;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;

/**
 * 利用spring，数据库数据导出Excel
 * 
 * @since 2016-6-6 16:13:41 Deprecated
 * @see ExcelViewUtils
 */
@Deprecated
public class ExcelUtil {

	private Map<String, String> headers;
	private Map<String, Integer> widths;
	private Map<String, String> types;
	private Map<String, Boolean> locks;
	private Map<String, Boolean> summary;
	private List<DataListCombo> combos;
	private SqlRowList list;
	private String title;
	private List<Map<Object, Object>> datas;
	private Employee employee;
	private String remark;
	public static int maxSize = 100000;// 导出上限

	public ExcelUtil() {

	}

	public ExcelUtil(String caller, String type, String condition, String title, String fields, boolean self, Employee employee) {
		this();
		headers = new LinkedHashMap<String, String>();
		widths = new HashMap<String, Integer>();
		types = new HashMap<String, String>();
		locks = new HashMap<String, Boolean>();
		summary = new HashMap<String, Boolean>();
		combos = new ArrayList<DataListCombo>();
		this.title = title;
		this.employee = employee;
		if ("datalist".equals(type)) {
			getBookOfDataList(caller, condition, fields, self);
		} else if ("detailgrid".equals(type)) {
			getBookOfDetailGrid(caller, condition, fields);
		}
	}

	public ExcelUtil(Map<String, String> headers, Map<String, Integer> widths, Map<String, String> types, List<Map<Object, Object>> datas,
			String title, Employee employee) {
		this();
		this.headers = headers;
		this.widths = widths;
		this.types = types;
		this.datas = datas;
		this.title = title;
		this.employee = employee;
		locks = new HashMap<String, Boolean>();
		summary = new HashMap<String, Boolean>();
	}

	public ExcelUtil(Map<String, String> headers, Map<String, Integer> widths, Map<String, String> types, SqlRowList list, String title,
			Employee employee) {
		this();
		this.headers = headers;
		this.widths = widths;
		this.types = types;
		this.list = list;
		this.title = title;
		this.employee = employee;
		locks = new HashMap<String, Boolean>();
		summary = new HashMap<String, Boolean>();
	}

	/**
	 * @param columns
	 *            Grid列属性
	 * @param datas
	 *            Grid数据
	 * @param title
	 *            xls文件名
	 */
	public ExcelUtil(List<Map<Object, Object>> columns, List<Map<Object, Object>> datas, String title, Employee employee) {
		this();
		parseColumns(columns);
		this.datas = datas;
		this.title = title;
		this.employee = employee;
	}

	/**
	 * @param remark
	 *            表头上面的注释
	 * @param columns
	 *            Grid列属性
	 * @param datas
	 *            Grid数据
	 * @param title
	 *            xls文件名
	 */
	public ExcelUtil(List<Map<Object, Object>> columns, List<Map<Object, Object>> datas, String title, Employee employee, String remark) {
		this();
		this.datas = datas;
		parseColumns(columns);
		this.remark = remark;
		this.title = title;
		this.employee = employee;
	}

	/**
	 * @param remark
	 *            表头上面的注释
	 * @param columns
	 *            Grid列属性
	 * @param list
	 *            Grid数据
	 * @param title
	 *            xls文件名
	 */
	public ExcelUtil(List<Map<Object, Object>> columns, SqlRowList list, String title, Employee employee, String remark) {
		this();
		this.list = list;
		parseColumns(columns);
		this.remark = remark;
		this.title = title;
		this.employee = employee;
	}

	public AbstractView getView() {
		boolean isBigSize = (this.datas != null && this.datas.size() > 10000) || (this.list != null && this.list.size() > 10000);
		// isBigSize ? createCsvView() : createExcelView();
		return isBigSize ? createBigExcelView() : createExcelView();
	}

	public AbstractExcelView createExcelView() {
		return new AbstractExcelView() {

			private HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
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
			 * 生成excel工作表
			 * 
			 * @param headers
			 *            抬头
			 * @param list
			 *            数据
			 * @throws ParseException
			 */
			private void createWorkbook(HSSFWorkbook workbook, int sheetIdx) throws ParseException {
				HSSFSheet sheet = workbook.createSheet("sheet" + sheetIdx);
				HSSFCellStyle style = getCellStyle(workbook);
				sheet.autoSizeColumn((short) 2);
				sheet.createFreezePane(0, 1);// 固定列
				HSSFCell cell = null;
				int rIdx = 0;
				int cIdx = 0;
				short width = 0;
				HSSFDataFormat format = workbook.createDataFormat();
				Set<String> keys = headers.keySet();
				HSSFRow row = null;
				if (remark != null) {
					row = sheet.createRow(rIdx);
					row.setHeightInPoints((short) 20);
					sheet.setColumnWidth(cIdx, 1000);
					cell = getCell(sheet, rIdx, cIdx);
					cell.setCellValue(remark);
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, keys.size() - 1));
					rIdx++;
				}
				row = sheet.createRow(rIdx);
				row.setHeightInPoints((short) 20);
				Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>();
				for (String key : keys) {
					width = (short) (widths.get(key) * 35.7);
					width = width == 0 ? 6000 : width;
					sheet.setColumnWidth(cIdx, width);
					Boolean lock = locks.get(key);
					if (lock != null && lock)
						sheet.createFreezePane(cIdx + 1, 1);
					cell = getCell(sheet, rIdx, cIdx);
					cell.setCellValue(headers.get(key));
					cell.setCellStyle(style);
					/**
					 * 数值型的不加format
					 * 
					 * @since 2016-5-4 14:16:34
					 */
					if (types != null && !Constant.TYPE_YN.equals(types.get(key)) && !Constant.TYPE_COMBO.equals(types.get(key))
							&& !types.get(key).startsWith("0")) {
						HSSFCellStyle s = workbook.createCellStyle();
						s.setDataFormat(format.getFormat(types.get(key)));
						styles.put(key, s);
					}
					cIdx++;
				}
				rIdx++;
				String type = null;
				if (list != null) {
					while (list.next()) {
						cIdx = 0;
						row = sheet.createRow(rIdx);
						row.setHeightInPoints((short) 16);
						for (String key : keys) {
							HSSFCell c = getCell(sheet, rIdx, cIdx);
							if (styles.get(key) != null)
								c.setCellStyle(styles.get(key));
							type = types.get(key);
							if (type.length() == 0) {
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
								c.setCellValue(list.getGeneralString(key));
							} else if (type.startsWith("yyyy")) {
								if (!"".equals(list.getGeneralString(key))) {
									// c.setCellValue((Date) dateformat.parseObject(list.getGeneralString(key)));
									c.setCellValue(DateUtil.parse(list.getGeneralString(key), type.indexOf(":") > 0 ? Constant.YMD_HMS
											: Constant.YMD));
								}

							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(list.getGeneralString(key)) || "-1".equals(list.getGeneralString(key))) {
									c.setCellValue("是");
								} else if ("0".equals(list.getGeneralString(key))) {
									c.setCellValue("否");
								} else {
									c.setCellValue("");
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								c.setCellValue(getValueByCombo(key, list.getGeneralString(key)));
							} else {
								c.setCellValue(list.getGeneralDouble(key));
							}
							cIdx++;
						}
						rIdx++;
						if (rIdx == 65535) {
							createWorkbook(workbook, sheetIdx + 1);
						}
					}
				} else if (datas != null && datas.size() > 0) {
					for (Map<Object, Object> d : datas) {
						cIdx = 0;
						row = sheet.createRow(rIdx);
						row.setHeightInPoints((short) 16);
						for (String key : keys) {
							HSSFCell c = getCell(sheet, rIdx, cIdx);
							if (styles.get(key) != null)
								c.setCellStyle(styles.get(key));
							type = types.get(key);
							if (type.length() == 0) {
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
								c.setCellValue(d.get(key) != null ? String.valueOf(d.get(key)) : "");
							} else if (type.startsWith("yyyy")) {
								if (d.get(key) != null) {
									// c.setCellValue((Date) dateformat.parseObject(d.get(key).toString()));
									c.setCellValue(DateUtil.parse(d.get(key).toString(), type.indexOf(":") > 0 ? Constant.YMD_HMS
											: Constant.YMD));
								}

							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(String.valueOf(d.get(key))) || "-1".equals(String.valueOf(d.get(key)))) {
									c.setCellValue("是");
								} else if ("0".equals(String.valueOf(d.get(key)))) {
									c.setCellValue("否");
								} else {
									c.setCellValue("");
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								c.setCellValue(getValueByCombo(key, String.valueOf(d.get(key))));
							} else {
								if (d.get(key) != null) {
									String v = String.valueOf(d.get(key));
									if ("".equals(v) || "null".equals(v)) {
										c.setCellValue(0);
									} else if (!v.matches(Constant.REG_NUM)) {
										c.setCellValue(v);
									} else {
										c.setCellValue(Double.parseDouble(v.replace(",", "")));
									}
								} else {
									c.setCellValue(0);
								}
							}
							cIdx++;
						}
						rIdx++;
						if (rIdx == 65535) {
							datas.removeAll(datas.subList(0, 65534));
							createWorkbook(workbook, sheetIdx + 1);
						}
					}
				}
				// CellRangeAddress range = CellRangeAddress.valueOf("A1:" +
				// getColumnAddress(cIdx) + rIdx);// 自动筛选
				// sheet.setAutoFilter(range);// (不支持WPS的筛选格式)
				insertSummary(sheet, keys, rIdx);// 求和
			}

			/**
			 * 插入合计行
			 * 
			 * @param sheet
			 * @param keys
			 * @param rIdx
			 */
			private void insertSummary(HSSFSheet sheet, Set<String> keys, int rIdx) {
				if (summary.size() > 0 && rIdx > 2) {
					sheet.createRow(rIdx);
					HSSFCell cell = null;
					int cIdx = 0;
					for (String key : keys) {
						if (summary.containsKey(key) && summary.get(key)) {
							cell = getCell(sheet, rIdx, cIdx);
							String addr = getColumnAddress(cIdx + 1);
							cell.setCellFormula("sum(" + addr + "2:" + addr + rIdx + ")");
						}
						cIdx++;
					}
				}
			}

			@Override
			protected void buildExcelDocument(Map<String, Object> arg0, HSSFWorkbook workbook, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				createWorkbook(workbook, 1);
				ExportObserve.getInstance().remove(request.getSession().getId());
				String filename = title + ".xls";// 设置下载时客户端Excel的名称
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
			}
		};
	}

	public AbstractBigExcelView createBigExcelView() {
		return new AbstractBigExcelView() {

			private CellStyle getCellStyle(SXSSFWorkbook workbook) {
				CellStyle style = workbook.createCellStyle();
				style.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
				style.setFillPattern(HSSFCellStyle.LEAST_DOTS);
				Font font = workbook.createFont();
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
			 * 生成excel工作表
			 * 
			 * @param headers
			 *            抬头
			 * @param list
			 *            数据
			 * @throws ParseException
			 */
			private void createWorkbook(SXSSFWorkbook workbook, int sheetIdx) throws ParseException {
				Sheet sheet = workbook.createSheet("sheet" + sheetIdx);
				CellStyle style = getCellStyle(workbook);
				sheet.autoSizeColumn((short) 2);
				sheet.createFreezePane(0, 1);// 固定列
				Cell cell = null;
				int rIdx = 0;
				int cIdx = 0;
				short width = 0;
				DataFormat format = workbook.createDataFormat();
				Set<String> keys = headers.keySet();
				Row row = null;
				if (remark != null) {
					row = sheet.createRow(rIdx);
					row.setHeightInPoints((short) 20);
					sheet.setColumnWidth(cIdx, 1000);
					cell = getCell(sheet, rIdx, cIdx);
					cell.setCellValue(remark);
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, keys.size() - 1));
					rIdx++;
				}
				row = sheet.createRow(rIdx);
				row.setHeightInPoints((short) 20);
				Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
				for (String key : keys) {
					width = (short) (widths.get(key) * 35.7);
					width = width == 0 ? 6000 : width;
					sheet.setColumnWidth(cIdx, width);
					Boolean lock = locks.get(key);
					if (lock != null && lock)
						sheet.createFreezePane(cIdx + 1, 1);
					cell = getCell(sheet, rIdx, cIdx);
					cell.setCellValue(headers.get(key));
					cell.setCellStyle(style);
					/**
					 * 数值型的不加format
					 * 
					 * @since 2016-5-4 14:16:34
					 */
					if (types != null && !Constant.TYPE_YN.equals(types.get(key)) && !Constant.TYPE_COMBO.equals(types.get(key))
							&& !types.get(key).startsWith("0")) {
						CellStyle s = workbook.createCellStyle();
						s.setDataFormat(format.getFormat(types.get(key)));
						styles.put(key, s);
					}
					cIdx++;
				}
				rIdx++;
				String type = null;
				if (list != null) {
					while (list.next()) {
						cIdx = 0;
						row = sheet.createRow(rIdx);
						row.setHeightInPoints((short) 16);
						for (String key : keys) {
							Cell c = getCell(sheet, rIdx, cIdx);
							if (styles.get(key) != null)
								c.setCellStyle(styles.get(key));
							type = types.get(key);
							if (type.length() == 0) {
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
								c.setCellValue(list.getGeneralString(key));
							} else if (type.startsWith("yyyy")) {
								if (!"".equals(list.getGeneralString(key))) {
									// c.setCellValue((Date) dateformat.parseObject(list.getGeneralString(key)));
									c.setCellValue(DateUtil.parse(list.getGeneralString(key), type.indexOf(":") > 0 ? Constant.YMD_HMS
											: Constant.YMD));
								}

							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(list.getGeneralString(key)) || "-1".equals(list.getGeneralString(key))) {
									c.setCellValue("是");
								} else if ("0".equals(list.getGeneralString(key))) {
									c.setCellValue("否");
								} else {
									c.setCellValue("");
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								c.setCellValue(getValueByCombo(key, list.getGeneralString(key)));
							} else {
								c.setCellValue(list.getGeneralDouble(key));
							}
							cIdx++;
						}
						rIdx++;
						if (rIdx == 65535) {
							createWorkbook(workbook, sheetIdx + 1);
						}
					}
				} else if (datas != null && datas.size() > 0) {
					for (Map<Object, Object> d : datas) {
						cIdx = 0;
						row = sheet.createRow(rIdx);
						row.setHeightInPoints((short) 16);
						for (String key : keys) {
							Cell c = getCell(sheet, rIdx, cIdx);
							if (styles.get(key) != null)
								c.setCellStyle(styles.get(key));
							type = types.get(key);
							if (type.length() == 0) {
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
								c.setCellValue(String.valueOf(d.get(key)));
							} else if (type.startsWith("yyyy")) {
								if (d.get(key) != null) {
									// c.setCellValue((Date) dateformat.parseObject(d.get(key).toString()));
									c.setCellValue(DateUtil.parse(d.get(key).toString(), type.indexOf(":") > 0 ? Constant.YMD_HMS
											: Constant.YMD));
								}

							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(String.valueOf(d.get(key))) || "-1".equals(String.valueOf(d.get(key)))) {
									c.setCellValue("是");
								} else if ("0".equals(String.valueOf(d.get(key)))) {
									c.setCellValue("否");
								} else {
									c.setCellValue("");
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								c.setCellValue(getValueByCombo(key, String.valueOf(d.get(key))));
							} else {
								if (d.get(key) != null) {
									String v = String.valueOf(d.get(key));
									if ("".equals(v) || "null".equals(v)) {
										c.setCellValue(0);
									} else if (!v.matches(Constant.REG_NUM)) {
										c.setCellValue(v);
									} else {
										c.setCellValue(Double.parseDouble(v.replace(",", "")));
									}
								} else {
									c.setCellValue(0);
								}
							}
							cIdx++;
						}
						rIdx++;
						if (rIdx == 65535) {
							datas.removeAll(datas.subList(0, 65534));
							createWorkbook(workbook, sheetIdx + 1);
						}
					}
				}
				// CellRangeAddress range = CellRangeAddress.valueOf("A1:" +
				// getColumnAddress(cIdx) + rIdx);// 自动筛选
				// sheet.setAutoFilter(range);// (不支持WPS的筛选格式)
				insertSummary(sheet, keys, rIdx);// 求和
			}

			/**
			 * 插入合计行
			 * 
			 * @param sheet
			 * @param keys
			 * @param rIdx
			 */
			private void insertSummary(Sheet sheet, Set<String> keys, int rIdx) {
				if (summary.size() > 0 && rIdx > 2) {
					sheet.createRow(rIdx);
					Cell cell = null;
					int cIdx = 0;
					for (String key : keys) {
						if (summary.containsKey(key) && summary.get(key)) {
							cell = getCell(sheet, rIdx, cIdx);
							String addr = getColumnAddress(cIdx + 1);
							cell.setCellFormula("sum(" + addr + "2:" + addr + rIdx + ")");
						}
						cIdx++;
					}
				}
			}

			@Override
			protected void buildExcelDocument(Map<String, Object> model, SXSSFWorkbook workbook, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				createWorkbook(workbook, 1);
				ExportObserve.getInstance().remove(request.getSession().getId());
				String filename = title + EXTENSION;
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
			}
		};
	}

	public AbstractCsvView createCsvView() {
		return new AbstractCsvView() {

			private void createCsv(CsvWriter writer) throws IOException {
				Set<String> keys = headers.keySet();
				for (String key : keys) {
					writer.newHeaderCell(headers.get(key));
				}
				writer.newLine();
				String type = null;
				Object value = null;
				if (list != null) {
					while (list.next()) {
						for (String key : keys) {
							type = types.get(key);
							if (type.length() == 0 || type.startsWith("yyyy")) {
								value = list.getGeneralString(key);
							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(list.getGeneralString(key)) || "-1".equals(list.getGeneralString(key))) {
									value = "是";
								} else if ("0".equals(list.getGeneralString(key))) {
									value = "否";
								} else {
									value = "";
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								value = getValueByCombo(key, list.getGeneralString(key));
							} else {
								value = list.getGeneralDouble(key);
							}
							writer.newCell(value);
						}
						writer.newLine();
					}
				} else if (datas != null && datas.size() > 0) {
					for (Map<Object, Object> d : datas) {
						for (String key : keys) {
							type = types.get(key);
							if (type.length() == 0 || type.startsWith("yyyy")) {
								value = list.getGeneralString(key);
							} else if (type.equals(Constant.TYPE_YN)) {
								if ("1".equals(String.valueOf(d.get(key))) || "-1".equals(String.valueOf(d.get(key)))) {
									value = "是";
								} else if ("0".equals(String.valueOf(d.get(key)))) {
									value = "否";
								} else {
									value = "";
								}
							} else if (type.equals(Constant.TYPE_COMBO)) {
								value = getValueByCombo(key, String.valueOf(d.get(key)));
							} else {
								if (d.get(key) != null) {
									String v = String.valueOf(d.get(key));
									if ("".equals(v) || "null".equals(v)) {
										value = 0;
									} else if (!v.matches(Constant.REG_NUM)) {
										value = v;
									} else {
										value = Double.parseDouble(v.replace(",", ""));
									}
								} else {
									value = 0;
								}
							}
							writer.newCell(value);
						}
						writer.newLine();
					}
				}
			}

			@Override
			protected void buildCsvDocument(Map<String, Object> model, CsvWriter writer, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				createCsv(writer);
				String filename = title + EXTENSION;
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
			}
		};
	}

	private void parseColumns(List<Map<Object, Object>> columns) {
		headers = new LinkedHashMap<String, String>();
		widths = new HashMap<String, Integer>();
		types = new HashMap<String, String>();
		locks = new HashMap<String, Boolean>();
		summary = new HashMap<String, Boolean>();
		Object cm = null;
		for (Map<Object, Object> m : columns) {
			cm = m.get("dataIndex");
			if (cm != null) {
				headers.put(cm.toString(), m.get("text").toString());
				widths.put(cm.toString(), Integer.parseInt(String.valueOf(m.get("width"))));
				locks.put(cm.toString(), "true".equals(String.valueOf(m.get("locked"))));
				summary.put(cm.toString(), "true".equals(String.valueOf(m.get("summary"))));
				if ("numbercolumn".equals(String.valueOf(m.get("xtype")))) {
					String format = String.valueOf(m.get("format"));
					if (format != null && !format.equals("null")) {
						if (format.indexOf("0.") > -1) {
							types.put(cm.toString(), format.substring(format.indexOf("0.")));
						} else {
							types.put(cm.toString(), "0");
						}
					} else
						types.put(cm.toString(), "NUMBER");
				} else if ("datecolumn".equals(String.valueOf(m.get("xtype")))) {
					String format = String.valueOf(m.get("format"));
					if (format != null && !format.equals("null")) {
						if ("Y-m-d".equals(format)) {
							format = "yyyy-m-d";
						} else if ("Y-m-d H:i:s".equals(format)) {
							format = "yyyy-m-d hh:MM:ss";
						} else {
							format = "yyyy-m-d";
						}
					} else {
						format = "yyyy-m-d";
					}
					types.put(cm.toString(), format);
				} else if ("yncolumn".equals(String.valueOf(m.get("xtype")))) {
					types.put(cm.toString(), Constant.TYPE_YN);
				} else {
					types.put(cm.toString(), "");
				}
			}
		}
	}

	private String getValueByCombo(String field, String actualValue) {
		if (combos != null) {
			for (DataListCombo combo : combos) {
				if (field.equals(combo.getDlc_fieldname()) && actualValue.equals(combo.getDlc_display())) {
					return combo.getDlc_value();
				}
			}
		}
		return actualValue == null ? "" : actualValue;
	}

	private static String getColumnAddress(int cIdx) {
		int c = cIdx / 26;
		int d = cIdx % 26;
		c = d == 0 ? (c - 1) : c;
		d = d == 0 ? 26 : d;
		return new StringBuffer().append(c >= 1 ? (char) (64 + c) : "").append((char) (64 + d)).toString();
	}

	public void getBookOfDataList(String caller, String condition, String fields, boolean self) {
		DataListDao dataListDao = (DataListDao) ContextUtil.getBean("dataListDao");
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		boolean bool = baseDao.checkIf("DataListDetailEmps", "dde_caller='" + caller + "' and dde_emid=" + employee.getEm_id());
		DataList dataList = bool ? dataListDao.getDataListByEm(caller, employee) : dataListDao.getDataList(caller, employee.getEm_master());
		Setting setting = getSetting(caller, fields, employee, dataList, false);
		headers = setting.getHeaders();
		widths = setting.getWidths();
		types = setting.getTypes();
		locks = setting.getLocks();
		summary = setting.getSummary();
		combos = setting.getCombos();
		String con = self ? parseSelfCondition(dataList) : dataList.getDl_condition();
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")" + ((condition == null || "".equals(condition)) ? ""
				: " AND (" + condition + ")"));
		list = baseDao.queryForRowSet(dataList.getSearchSql(condition, dataList.getDl_orderby(), 1, maxSize));
	}

	private String parseSelfCondition(DataList dataList) {
		String condition = dataList.getDl_condition();
		String f = dataList.getDl_entryfield();
		if (StringUtils.hasText(f)) {
			Object emVal = employee.getEm_id(); // recorderfield默认与em_id对应
			if (f.endsWith("@C")) { // recorderfield与em_code对应
				f = f.substring(0, f.lastIndexOf("@C"));
				emVal = employee.getEm_code();
			} else if (f.endsWith("@N")) { // recorderfield与em_name对应
				f = f.substring(0, f.lastIndexOf("@N"));
				emVal = employee.getEm_name();
			}
			if (StringUtils.hasText(condition)) {
				condition += " AND ";
			} else
				condition = "";
			condition += f + "='" + emVal + "'";
		}
		return condition;
	}

	/**
	 * @param caller
	 * @param fields
	 * @param employee
	 * @param dataList
	 * @param alias
	 *            字段别名
	 * @return
	 */
	private static Setting getSetting(String caller, String fields, Employee employee, DataList dataList, boolean alias) {
		Setting setting = new Setting();
		DataListComboDao dataListComboDao = (DataListComboDao) ContextUtil.getBean("dataListComboDao");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<DataListCombo> aliaCombos = new ArrayList<DataListCombo>();
		List<DataListDetail> details = dataList.getDataListDetails();
		Set<String> limits = null;
		if (!"admin".equals(employee.getEm_type())) {
			HrJobDao hrjobDao = (HrJobDao) ContextUtil.getBean("hrjobDao");
			List<LimitFields> limitFields = hrjobDao.getLimitFieldsByType(caller, dataList.getDl_relative(), 2,
					employee.getEm_defaulthsid(), employee.getEm_master());
			if (!CollectionUtil.isEmpty(limitFields)) {
				limits = new HashSet<String>();
				for (LimitFields field : limitFields) {
					limits.add(field.getLf_field());
				}
			}
		}
		String[] ff = null;
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			setting.getHeaders().put("CURRENTMASTER", "账套");
			setting.getWidths().put("CURRENTMASTER", 80);
			setting.getTypes().put("CURRENTMASTER", "");
			setting.getLocks().put("CURRENTMASTER", true);
		}
		String[] customFields = null;
		if (fields != null && !"".equals(fields)) {
			customFields = fields.split(",");
		}
		int index = 0;
		for (DataListDetail detail : details) {
			if ((detail.getDld_width() != 0 || detail.getDld_flex() != 0)) {
				ff = detail.getDld_field().split(" ");
				String field = ff[ff.length - 1];// 别名
				if ((customFields != null && !StringUtil.isInArray(customFields, field)) || (limits != null && limits.contains(field))) {
					continue;
				}
				if (alias) {
					field = String.valueOf((char) (48 + ++index));
					for (DataListCombo combo : combos) {
						if (combo.getDlc_fieldname().equals(detail.getDld_field())) {
							aliaCombos.add(new DataListCombo(combo, field));
						}
					}
				}
				setting.getHeaders().put(field, detail.getDld_caption());
				setting.getWidths().put(field, detail.getDld_width());
				setting.getTypes().put(field, getType(detail));
				setting.getLocks().put(field, false);
			}
		}
		setting.setCombos(alias ? aliaCombos : combos);
		return setting;
	}

	private static String getType(DataListDetail detail) {
		String type = detail.getDld_fieldtype();
		String format = "";
		if ("N".equals(type)) {
			format = "0";
		} else if ("F".equals(type)) {
			format = "0.00";
		} else if ("D".equals(type)) {
			format = "yyyy-m-d";
		} else if ("DT".equals(type)) {
			format = "yyyy-m-d hh:MM:ss";
		} else if (type.matches("^F\\d{1}$")) {
			int length = Integer.parseInt(type.replace("F", ""));
			format = "0.";
			for (int i = 0; i < length; i++) {
				format += "0";
			}
		} else if ("C".equals(type)) {
			format = Constant.TYPE_COMBO;
		}
		return format;
	}

	public void getBookOfDetailGrid(String caller, String condition, String fields) {
		DetailGridDao detailGridDao = (DetailGridDao) ContextUtil.getBean("detailGridDao");
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
		Setting setting = getSetting(caller, fields, employee, detailGrids, false);
		headers = setting.getHeaders();
		widths = setting.getWidths();
		types = setting.getTypes();
		locks = setting.getLocks();
		summary = setting.getSummary();
		combos = setting.getCombos();
		Object[] objs = baseDao.getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition,fo_detailgridorderby", "fo_caller='"
				+ caller + "'");
		Object table = detailGrids.get(0).getDg_table();
		if (objs != null) {// 优先用Form的配置
			if (objs[0] != null)
				table = objs[0];
			if (objs[1] != null) {
				if ("".equals(condition)) {
					condition = objs[1].toString();
				} else {
					int index = condition.toLowerCase().indexOf("order by");
					if (index > -1) {
						condition = condition.substring(0, index) + " AND " + objs[1] + " " + condition.substring(index);
					} else {
						condition += " AND " + objs[1];
					}
				}
			}
			if (condition.toLowerCase().indexOf("order by") == -1 && objs[2] != null
					&& objs[2].toString().toLowerCase().indexOf("order by") > -1) {
				condition += " " + objs[2];
			}
		}
		list = baseDao.queryForRowSet(SqlUtil.getQuerySqlByDetailGrid(detailGrids, String.valueOf(table), condition, employee, 1, maxSize));
	}

	/**
	 * @param caller
	 * @param fields
	 * @param employee
	 * @param detailGrids
	 * @param alias
	 *            字段别名
	 * @return
	 */
	private static Setting getSetting(String caller, String fields, Employee employee, List<DetailGrid> detailGrids, boolean alias) {
		Setting setting = new Setting();
		DataListComboDao dataListComboDao = (DataListComboDao) ContextUtil.getBean("dataListComboDao");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<DataListCombo> aliaCombos = new ArrayList<DataListCombo>();
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			setting.getHeaders().put("CURRENTMASTER", "账套");
			setting.getWidths().put("CURRENTMASTER", 80);
			setting.getTypes().put("CURRENTMASTER", "");
			setting.getLocks().put("CURRENTMASTER", true);
		}
		Set<String> limits = null;
		if (!"admin".equals(employee.getEm_type())) {
			HrJobDao hrjobDao = (HrJobDao) ContextUtil.getBean("hrjobDao");
			List<LimitFields> limitFields = hrjobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(),
					employee.getEm_master());
			if (!CollectionUtil.isEmpty(limitFields)) {
				limits = new HashSet<String>();
				for (LimitFields field : limitFields) {
					limits.add(field.getLf_field());
				}
			}
		}
		String[] ff = null;
		String[] customFields = null;
		if (fields != null && !"".equals(fields)) {
			customFields = fields.split(",");
		}
		int index = 0;
		for (DetailGrid grid : detailGrids) {
			if (grid.getDg_width() != 0) {
				ff = grid.getDg_field().split(" ");
				String field = ff[ff.length - 1];// 别名
				if ((customFields != null && !StringUtil.isInArray(customFields, field)) || (limits != null && limits.contains(field))) {
					continue;
				}
				if (alias) {
					field = String.valueOf((char) (48 + ++index));
					for (DataListCombo combo : combos) {
						if (combo.getDlc_fieldname().equals(grid.getDg_field())) {
							aliaCombos.add(new DataListCombo(combo, field));
						}
					}
				}
				setting.getHeaders().put(field, grid.getDg_caption());
				setting.getWidths().put(field, grid.getDg_width());
				setting.getTypes().put(field, getType(grid));
				setting.getLocks().put(field, grid.getDg_locked() == 1);
				if ("sum".equals(grid.getDg_summarytype())) {
					setting.getSummary().put(field, true);
				}
			}
		}
		setting.setCombos(alias ? aliaCombos : combos);
		return setting;
	}

	/**
	 * 按datalist或detailgrid配置，转化成需要的配置参数
	 * 
	 * @param caller
	 * @param type
	 * @param fields
	 * @param employee
	 * @param alias
	 *            是否使用字段别名简化模式
	 * @return
	 */
	public static Setting getSetting(String caller, String type, String fields, Employee employee, boolean alias) {
		if ("datalist".equals(type)) {
			DataListDao dataListDao = (DataListDao) ContextUtil.getBean("dataListDao");
			DataList dataList = dataListDao.getDataList(caller, employee.getEm_master());
			return getSetting(caller, fields, employee, dataList, alias);
		} else if ("detailgrid".equals(type)) {
			DetailGridDao detailGridDao = (DetailGridDao) ContextUtil.getBean("detailGridDao");
			List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
			return getSetting(caller, fields, employee, detailGrids, alias);
		}
		return null;
	}

	private static String getType(DetailGrid detailGrid) {
		String type = detailGrid.getDg_type();
		String format = "";
		if ("numbercolumn".equals(type)) {
			format = "0";
		} else if ("floatcolumn".equals(type)) {
			format = "0.00";
		} else if ("datecolumn".equals(type)) {
			format = "yyyy-m-d";
		} else if ("datetimecolumn".equals(type)) {
			format = "yyyy-m-d hh:MM:ss";
		} else if (type.matches("^floatcolumn\\d{1}$")) {
			format = "0.";
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				format += "0";
			}
		} else if ("yncolumn".equals(type)) {
			format = Constant.TYPE_YN;
		} else if ("combo".equals(type))
			format = Constant.TYPE_COMBO;
		return format;
	}

	private static class Setting {
		private Map<String, String> headers;
		private Map<String, Integer> widths;
		private Map<String, String> types;
		private Map<String, Boolean> locks;
		private Map<String, Boolean> summary;
		private List<DataListCombo> combos;

		public Setting() {
			headers = new LinkedHashMap<String, String>();
			widths = new HashMap<String, Integer>();
			types = new HashMap<String, String>();
			locks = new HashMap<String, Boolean>();
			summary = new HashMap<String, Boolean>();
			combos = new ArrayList<DataListCombo>();
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public Map<String, Integer> getWidths() {
			return widths;
		}

		public Map<String, String> getTypes() {
			return types;
		}

		public Map<String, Boolean> getLocks() {
			return locks;
		}

		public Map<String, Boolean> getSummary() {
			return summary;
		}

		public List<DataListCombo> getCombos() {
			return combos;
		}

		public void setCombos(List<DataListCombo> combos) {
			this.combos = combos;
		}

	}
}