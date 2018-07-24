package com.uas.erp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.uas.erp.core.DateUtil;

public class GridPanel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<GridFields> gridFields;
	private List<GridFields> childGridFields;// 子grid
	private List<GridColumns> gridColumns;
	private List<GridColumns> childGridColumns;
	private List<LimitFields> limits;// 权限外字段
	private List<GridColumns> baseColumns;
	private List<Dbfind> dbfinds;// dbfind时，从dbfindGrid传递数据到grid的配置
	private String dataString;// 字符串格式数据
	private List<Map<String, Object>> data;// 数据
	private List<String> jsonList;// records是字符串格式
	private int dataCount = 0;
	private String childDataString;
	private String keyField;// datalist:单表key字段，例如pu_id;//detailgrid:主键字段
	private String pfField;// datalist:从表key字段，例如pd_puid
	private String url;// datalist行选择链接的页面
	private String relative;// 关联列表的caller
	private String detno;// detailgrid:排序字段
	private String mainField;// detailgrid:对应主表主键字段
	private String necessaryField;// detailgrid:必填字段
	private String orNecessField;
	private String groupField;// group
	private String vastbutton;
	private boolean allowreset;// 重置条件按钮
	private boolean autoHeight;// 内容自适应高度
	private List<Map<String,Object>> summarydata;//汇总数据
	private String defaultFilterCondition; //默认筛选方案
	
	public String getDefaultFilterCondition() {
		return defaultFilterCondition;
	}

	public void setDefaultFilterCondition(String defaultFilterCondition) {
		this.defaultFilterCondition = defaultFilterCondition;
	}


	public String getOrNecessField() {
		return orNecessField;
	}

	public void setOrNecessField(String orNecessField) {
		this.orNecessField = orNecessField;
	}

	public List<GridFields> getGridFields() {
		return gridFields;
	}

	public void setGridFields(List<GridFields> gridFields) {
		this.gridFields = gridFields;
	}

	public List<GridColumns> getGridColumns() {
		return gridColumns;
	}

	public void setGridColumns(List<GridColumns> gridColumns) {
		this.gridColumns = gridColumns;
	}

	public List<Dbfind> getDbfinds() {
		return dbfinds;
	}

	public void setDbfinds(List<Dbfind> dbfinds) {
		this.dbfinds = dbfinds;
	}

	public boolean isAllowreset() {
		return allowreset;
	}

	public void setAllowreset(boolean allowreset) {
		this.allowreset = allowreset;
	}

	public String getGroupField() {
		return groupField;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public List<LimitFields> getLimits() {
		return limits;
	}

	public void setLimits(List<LimitFields> limits) {
		this.limits = limits;
	}   
	public List<GridColumns> getBaseColumns() {
		return baseColumns;
	}

	public void setBaseColumns(List<GridColumns> baseColumns) {
		this.baseColumns = baseColumns;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRelative() {
		return relative;
	}

	public void setRelative(String relative) {
		this.relative = relative;
	}

	public String getDetno() {
		return detno;
	}

	public void setDetno(String detno) {
		this.detno = detno;
	}

	public String getMainField() {
		return mainField;
	}

	public void setMainField(String mainField) {
		this.mainField = mainField;
	}

	public String getNecessaryField() {
		return necessaryField;
	}

	public void setNecessaryField(String necessaryField) {
		this.necessaryField = necessaryField;
	}

	public String getPfField() {
		return pfField;
	}

	public void setPfField(String pfField) {
		this.pfField = pfField;
	}

	public List<GridFields> getChildGridFields() {
		return childGridFields;
	}

	public void setChildGridFields(List<GridFields> childGridFields) {
		this.childGridFields = childGridFields;
	}

	public List<GridColumns> getChildGridColumns() {
		return childGridColumns;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public void setChildGridColumns(List<GridColumns> childGridColumns) {
		this.childGridColumns = childGridColumns;
	}

	public String getChildDataString() {
		return childDataString;
	}

	public void setChildDataString(String childDataString) {
		this.childDataString = childDataString;
	}

	public String getVastbutton() {
		return vastbutton;
	}

	public void setVastbutton(String vastbutton) {
		this.vastbutton = vastbutton;
	}

	public boolean isAutoHeight() {
		return autoHeight;
	}

	public void setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;
	}

	public List<String> getJsonList() {
		return jsonList;
	}

	public void setJsonList(List<String> jsonList) {
		this.jsonList = jsonList;
	}	

	public List<Map<String, Object>> getSummarydata() {
		return summarydata;
	}

	public void setSummarydata(List<Map<String, Object>> summarydata) {
		this.summarydata = summarydata;
	}

	public GridPanel() {

	}

	/**
	 * excel数据解析成grid数据
	 */
	public GridPanel(HSSFSheet sheet) {
		GridColumns column = null;
		GridFields field = null;
		this.gridColumns = new ArrayList<GridColumns>();
		this.gridFields = new ArrayList<GridFields>();
		StringBuffer sb = new StringBuffer();
		DecimalFormat df = new DecimalFormat("0.###########");
		sb.append("[");
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				if (i > 0) {
					sb.append("{");
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								String str = cell.getNumericCellValue() + "";
								if(str != null && !"".equals(str)){
									if(str.contains("E")){
										value = df.format(cell.getNumericCellValue());
									}else{
										value = cell.getNumericCellValue();
									}
								}else{
									value = cell.getNumericCellValue();
								}
//								value = cell.getNumericCellValue();
							}
							break;
						case HSSFCell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case HSSFCell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case HSSFCell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							column = new GridColumns();
							column.setHeader(value.toString());
							column.setText(value.toString());
							column.setDataIndex("column" + j);
							int width = sheet.getColumnWidth(j);
							width = width > 300 ? 100 : width;
							column.setWidth(width);
							this.gridColumns.add(column);
							field = new GridFields();
							field.setName("column" + j);
							this.gridFields.add(field);
						} else {
							sb.append("column" + j);
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("},");
				}
			}
		}
		sb.append("]");
		this.dataString = replaceBlank(sb.toString());
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * txt数据解析
	 */
	public GridPanel(BufferedReader br) {
		String str = null;
		int i = 0;
		int j = 0;
		GridColumns column = null;
		GridFields field = null;
		StringBuffer sb = null;
		try {
			this.gridColumns = new ArrayList<GridColumns>();
			this.gridFields = new ArrayList<GridFields>();
			sb = new StringBuffer();
			sb.append("[");
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				j = 0;
				if (i == 0) {
					i++;
					for (String s : strs) {
						if (s != null && !s.trim().equals("")) {
							j++;
							column = new GridColumns();
							column.setHeader(s);
							column.setText(s);
							column.setDataIndex("column" + j);
							column.setWidth(100);
							this.gridColumns.add(column);
							field = new GridFields();
							field.setName("column" + j);
							this.gridFields.add(field);
						}
					}
				} else {
					sb.append("{");
					for (String s : strs) {
						j++;
						sb.append("column" + j);
						sb.append(":\"");
						sb.append(s);
						sb.append("\",");
					}
					sb.append("},");
				}
			}
			sb.append("]");
			this.dataString = sb.toString();
		} catch (IOException e) {

		}
	}

	/**
	 * excel数据解析成grid数据 保存到initdata
	 */
	public GridPanel(HSSFSheet sheet, List<InitDetail> inits) {
		List<String> fields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		this.jsonList = new ArrayList<String>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sb = new StringBuffer();
				if (i > 0) {
					sb.append("{");
					this.dataCount += 1;
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								value = cell.getNumericCellValue();
							}
							break;
						case HSSFCell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case HSSFCell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case HSSFCell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							for (InitDetail detail : inits) {
								if (detail.getId_caption().equals(value.toString())) {
									fields.add(detail.getId_field());
								}
							}
						} else {
							sb.append(fields.get(j));
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("}");
					this.jsonList.add(sb.toString());
				}
			}
		}
	}

	/**
	 * excel数据解析成grid数据 保存到initdata
	 */
	public GridPanel(Sheet sheet, List<InitDetail> inits) {
		List<String> fields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		this.jsonList = new ArrayList<String>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sb = new StringBuffer();
				if (i > 0) {
					sb.append("{");
					this.dataCount += 1;
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								value = cell.getNumericCellValue();
							}
							break;
						case HSSFCell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case HSSFCell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case HSSFCell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							for (InitDetail detail : inits) {
								if (detail.getId_caption().equals(value.toString())) {
									fields.add(detail.getId_field());
								}
							}
						} else {
							sb.append(fields.get(j));
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("}");
					this.jsonList.add(sb.toString());
				}
			}
		}
	}

	public GridPanel(HSSFSheet sheet, List<DetailGrid> details,int keyValue) {
		List<String> fields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		this.jsonList = new ArrayList<String>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sb = new StringBuffer();
				if (i > 0) {
					sb.append("{");
					this.dataCount += 1;
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								value = cell.getNumericCellValue();
							}
							break;
						case HSSFCell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case HSSFCell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case HSSFCell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							for (DetailGrid detail : details) {
								if (detail.getDg_caption().equals(value.toString())) {
									fields.add(detail.getDg_field());
								}
							}
						} else {
							sb.append(fields.get(j));
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("}");
					this.jsonList.add(sb.toString());
				}
			}
		}
	}
	
	/**
	 * excel(xlsx)数据解析成grid数据
	 */
	public GridPanel(Sheet sheet) {
		GridColumns column = null;
		GridFields field = null;
		this.gridColumns = new ArrayList<GridColumns>();
		this.gridFields = new ArrayList<GridFields>();
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				if (i > 0) {
					sb.append("{");
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								value = cell.getNumericCellValue();
							}
							break;
						case Cell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case Cell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case Cell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case Cell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case Cell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							column = new GridColumns();
							column.setHeader(value.toString());
							column.setText(value.toString());
							column.setDataIndex("column" + j);
							int width = sheet.getColumnWidth(j);
							width = width > 300 ? 100 : width;
							column.setWidth(width);
							this.gridColumns.add(column);
							field = new GridFields();
							field.setName("column" + j);
							this.gridFields.add(field);
						} else {
							sb.append("column" + j);
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("},");
				}
			}
		}
		sb.append("]");
		this.dataString = replaceBlank(sb.toString());
	}
	
	/**
	 * excel(xlsx)数据解析成能直接插入从表grid数据
	 */
	public GridPanel(Sheet sheet, List<DetailGrid> details,int keyValue) {
		List<String> fields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		this.jsonList = new ArrayList<String>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				sb = new StringBuffer();
				if (i > 0) {
					sb.append("{");
					this.dataCount += 1;
				}
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell cell = row.getCell(j);
					if (cell != null) {
						Object value = cell.toString();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: // 数字
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {
								value = cell.getNumericCellValue();
							}
							break;
						case Cell.CELL_TYPE_STRING: // 字符串
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;
						case Cell.CELL_TYPE_BOOLEAN: // Boolean
							value = cell.getBooleanCellValue();
							break;
						case Cell.CELL_TYPE_FORMULA: // 公式
							value = cell.getCellFormula() + "";
							break;
						case Cell.CELL_TYPE_BLANK: // 空值
							value = "";
							break;
						case Cell.CELL_TYPE_ERROR: // 故障
							value = "";
							break;
						default:
							value = "";
							break;
						}
						if (i == 0) {
							for (DetailGrid detail : details) {
								if (detail.getDg_caption().equals(value.toString())) {
									fields.add(detail.getDg_field());
								}
							}
						} else {
							sb.append(fields.get(j));
							sb.append(":\"");
							sb.append(value);
							sb.append("\",");
						}
					}
				}
				if (i > 0) {
					sb.append("}");
					this.jsonList.add(sb.toString());
				}
			}
		}
	}
}
