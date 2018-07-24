package com.uas.erp.dao.util;

import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.web.AbstractResultView;
import com.uas.erp.core.web.DocumentConfig;

/**
 * 处理每行结果写到excel2007
 * 
 * @author yingp
 * 
 */
public class BigExcelViewProcesser extends RowViewProcesser<Row> {

	public BigExcelViewProcesser(AbstractResultView<Row> view) {
		super(view);
	}

	@Override
	public void processResult(Map<String, Object> param, AbstractResultView<Row> view, Row row) throws Exception {
		int columnIdx = 0;
		row.setHeightInPoints((short) 16);
		String valueStr = null;
		for (String field : view.getConfig().getFields()) {
			Object value = param.get(field);
			Cell cell = getCell(row, columnIdx);
			CellStyle style = view.getConfig().getStyles().get(field);
			if (style != null) {
				cell.setCellStyle(style);
			}
			String type = view.getConfig().getTypes().get(field);
			if (type.length() == 0) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(value == null ? "" : String.valueOf(value));
			} else if (type.startsWith("yyyy")) {
				if (value != null) {
					if (value instanceof String) {
						// 某些字段保存的字符串格式
						cell.setCellValue(DateUtil.parse(value.toString(), type.indexOf(":") > 0 ? Constant.YMD_HMS : Constant.YMD));
					} else {
						// 数据库实际保存的可能包含时分秒
						cell.setCellValue(DateUtils.truncate(value, type.indexOf(":") > 0 ? 13 : 5));
					}
				}
			} else if (type.equals(Constant.TYPE_YN)) {
				valueStr = String.valueOf(value);
				if ("1".equals(valueStr) || "-1".equals(valueStr)) {
					cell.setCellValue("是");
				} else if ("0".equals(valueStr)) {
					cell.setCellValue("否");
				} else {
					cell.setCellValue("");
				}
			} else if (type.equals(Constant.TYPE_COMBO)) {
				// 以字段+存储值获取实际显示值
				Object actual = view.getConfig().getCombos().get(new DocumentConfig.MixedKey(new Object[] { field, value }));
				cell.setCellValue(actual == null ? (value == null ? "" : value.toString()) : actual.toString());
			} else {
				if (value != null) {
					valueStr = String.valueOf(value);
					if ("".equals(valueStr) || "null".equals(valueStr)) {
						cell.setCellValue(0);
					} else if (!valueStr.matches(Constant.REG_NUM)) {
						cell.setCellValue(valueStr);
					} else {
						cell.setCellValue(Double.parseDouble(valueStr.replace(",", "")));
					}
				} else {
					cell.setCellValue(0);
				}
			}
			columnIdx++;
		}
	}

	private Cell getCell(Row row, int col) {
		Cell cell = row.getCell(col);
		if (cell == null) {
			cell = row.createCell(col);
		}
		return cell;
	}

}
