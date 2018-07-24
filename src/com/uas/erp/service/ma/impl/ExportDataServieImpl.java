package com.uas.erp.service.ma.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.ExportData;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.ma.ExportDataService;

@Service
public class ExportDataServieImpl implements ExportDataService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public boolean saveExportData(String formStore) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		map.remove("ed_selectfields");
		// SQl
		String findSQL = map.get("ed_sql").toString();
		try {
			baseDao.queryForRowSet(findSQL);
		} catch (Exception e) {
			return false;
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(map, "EXPORTDATA"));
		return true;
	}

	@Override
	public boolean testExportData(String formStore) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		map.remove("ed_selectfields");
		// SQl 拼SQl 测试能不能行
		String findSQL = " SELECT ";
		String fields = map.get("ed_fields").toString().replaceAll("#", ",");
		String condition = map.get("ed_condition").toString();
		String orderby = map.get("ed_orderby").toString();
		findSQL += fields;
		findSQL = findSQL + " from " + map.get("ed_tablename").toString();
		findSQL = (condition == null || condition.equals("")) ? findSQL : findSQL + " WHERE " + condition;
		findSQL = (orderby == null || orderby.equals("")) ? findSQL : findSQL + " " + orderby;
		try {
			baseDao.queryForRowSet(findSQL);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public GridPanel getExportDetails(int id) {
		GridPanel panel = new GridPanel();
		List<GridColumns> columns = new ArrayList<GridColumns>();
		List<GridFields> fields = new ArrayList<GridFields>();
		ExportData exportData = baseDao.getJdbcTemplate().queryForObject("select * from ExportData where ed_id=?",
				new BeanPropertyRowMapper<ExportData>(ExportData.class), id);
		String edfields = exportData.getEd_fields();
		String descriptions = exportData.getEd_fielddescriptions();
		for (int i = 0; i < edfields.split("#").length; i++) {
			String edfield = edfields.split("#")[i];
			String description = (descriptions.split("#")[i] == null || descriptions.split("#")[i].equals("")) ? "AS " + edfield
					: descriptions.split("#")[i];
			columns.add(new GridColumns(edfield, description, 120));
			fields.add(new GridFields(edfield));
		}
		panel.setGridColumns(columns);
		panel.setGridFields(fields);
		panel.setDataString(getDataString("select * from (" + exportData.getEd_sql() + ") where rownum<=100 ", edfields));
		return panel;
	}

	public String getDataString(String sql, String fields) {
		List<Map<String, Object>> list = baseDao.getJdbcTemplate().queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (String field : fields.split("#")) {
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return BaseUtil.parseGridStore2Str(datas);
	}

	@Override
	public ExportData downLoadAsExcel(int id) {
		Row row = null;
		Cell cell = null;
		String textValue = null;
		ExportData exportData = baseDao.getJdbcTemplate().queryForObject("select * from ExportData where ed_id=?",
				new BeanPropertyRowMapper<ExportData>(ExportData.class), id);
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		/** 多少个sheet */

		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet();
		SqlRowList SheetData = baseDao.queryForRowSet(exportData.getEd_sql());
		int i = 1;
		String caption[] = exportData.getEd_fielddescriptions().split("#");
		String str[] = exportData.getEd_fields().split("#");
		row = sheet.createRow(0);
		// row.setHeightInPoints((short)15);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setColor(HSSFColor.GREEN.index);
		/*
		 * font.setBoldweight((short)16); font.setFontHeight((short)18);
		 */
		font.setFontHeightInPoints((short) 13);
		cellStyle.setFont(font);
		for (int m = 0; m < str.length; m++) {
			cell = row.createCell(m);
			cell.setCellStyle(cellStyle);
			HSSFRichTextString richString = new HSSFRichTextString(caption[m]);
			cell.setCellValue(richString);
			sheet.setColumnWidth(m, 6000);
		}
		while (SheetData.next()) {
			row = sheet.createRow(i);
			for (int j = 0; j < str.length; j++) {
				cell = row.createCell(j);
				/** 判断值的类型后进行强制类型转换 */
				Object value = SheetData.getObject(str[j]);
				if (value != null) {
					if (value instanceof Boolean) {
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						textValue = sdf.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
					} else if (value instanceof Integer) {

					} else {
						// 其它数据类型都当作字符串简单处理
						textValue = value.toString();
					}
					if (textValue != null) {
						Pattern p = Pattern.compile("([1-9][0-9]*(\\.\\d+)?)$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							HSSFRichTextString richString = new HSSFRichTextString(textValue);

							cell.setCellValue(richString);
						}
					}

				} else {
					cell.setCellValue("");
				}
			}
			i++;
		}
		exportData.setWook(workbook);
		return exportData;
	}

	@Override
	public void delteExportData(int id) {
		baseDao.deleteById("ExportData", "ed_id", id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateExportData(String formStore) {
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(formStore, "ExportData", "ed_id"));
	}
}
