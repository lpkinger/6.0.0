package com.uas.erp.service.common.impl;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.ExcelDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.ConditionItem;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataStoreDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.ExcelCell;
import com.uas.erp.model.ExcelFx;
import com.uas.erp.model.ExcelTemplate;
import com.uas.erp.model.ExcelTemplateDetail;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.LimitFields;
import com.uas.erp.service.common.ExcelService;

@SuppressWarnings("deprecation")
@Service
public class ExcelServiceImpl implements ExcelService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private ExcelDao excelDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private DetailGridDao detailGridDao;
	private static String TODAY = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	private static String TABLENAME = "EXCELDATA";
	private static String CAllER = "ExcelTemplate";
	private static String pattern = "yyyy-MM-dd";

	@Override
	public boolean getJsonData(Workbook wbs, String ExcelName, int fileId) {
		int sheetnum = wbs.getNumberOfSheets();
		StringBuffer sb = new StringBuffer(50000);
		sb.append("{");
		sb.append("\"success\":\"true\",");
		sb.append("\"extraInfo\":null,");
		sb.append("\"isOwner\":true,");
		sb.append("\"isLockedBySelf\":false,");
		sb.append("\"sharing\":\"MYSELF_REPLACE\",");
		sb.append("\"activeSheet\":0,");
		sb.append("\"sheets\":[");
		for (int i = 0; i < sheetnum; i++) {
			Sheet sheet = wbs.getSheetAt(i);
			sb.append("{");
			sb.append("\"name\":\"" + sheet.getSheetName() + "\",");
			sb.append("\"tabId\":\"" + i + "\",");
			sb.append("\"rows\":{");
			Row firstrow = sheet.getRow(0);
			if (firstrow != null) {
				// 处理Excel 列宽
				sb.append("\"0\":{");
				for (int m = 1; m <= firstrow.getLastCellNum(); m++) {
					sb.append("\"" + m + "\":");
					sb.append("{\"cw\":" + (int) (sheet.getColumnWidth(m - 1) / 25.6) + "}");
					if (m < firstrow.getLastCellNum()) {
						sb.append(",");
					}
				}
				sb.append("},");
			}
			for (int k = 0; k <= sheet.getLastRowNum(); k++) {
				Row row = sheet.getRow(k);
				if (row != null) {
					sb.append("\"" + (k + 1) + "\":");
					sb.append("{");
					sb.append("\"0\":{\"hchanged\":true,\"ch\":" + row.getHeight() / 20 + "},");
					for (int j = 0; j < row.getLastCellNum(); j++) {
						HSSFCell cell = (HSSFCell) row.getCell(j);
						ExcelCell ec = new ExcelCell(cell);
						if ((ec.getD() != null && !ec.getD().equals("")) || (ec.getBg() != null && !ec.getBg().equals("null"))) {
							sb.append("\"" + (j + 1) + "\":");
							sb.append(ec.toString());
							if (j < row.getLastCellNum() - 1) {
								sb.append(",");
							}
						}
					}
					sb.append("}");
					if (k <= sheet.getLastRowNum() - 1) {
						sb.append(",");
					}
				}
			}
			sb.append("}}");
			if (i < sheetnum - 1) {
				sb.append(",");
			}
		}
		sb.append("],");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"fileId\":\"" + 0 + "\",");
		sb.append("\"updateDate\":\"" + TODAY + "\",");
		sb.append("\"isPublic\":false,");
		sb.append("\"permission\":2,");
		sb.append("\"exname\":\"myXls\",");
		sb.append("\"lockedBy\":\"\"");
		sb.append("}");
		BufferedWriter bufferedWriter = null;
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(PathUtil.getExcelPath() + File.separator + "LoadJson"),
					"UTF-8");
			// fileWriter 不能处理编码格式
			bufferedWriter = new BufferedWriter(out);
			bufferedWriter.write(sb.toString());
			bufferedWriter.flush();
			bufferedWriter.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBatchCells(String tabId, String celljsons, Employee employee) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> mps = BaseUtil.parseGridStoreToMaps(celljsons);
		int size = mps.size();
		if (size > 0) {
			List<String> insertSqls = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				Map<Object, Object> map = mps.get(i);
				String actiontype = map.get("action").toString();
				if (actiontype.equals("createUpdate")) {
					String findCondition = "tabid='" + tabId + "' AND x=" + map.get("x") + " AND y=" + map.get("y") + " AND emid="
							+ employee.getEm_id();
					boolean isexist = baseDao.checkByCondition(TABLENAME, findCondition);
					if (!isexist) {
						/** 存在执行更新操作 */
						baseDao.updateByCondition(TABLENAME, "content='" + map.get("content") + "'", findCondition);
					} else {
						/** 插入操作 */
						map.put("emid", employee.getEm_id());
						String insertSql = SqlUtil.getInsertSqlByMap(map, TABLENAME, new String[] {}, new String[] {});
						insertSqls.add(insertSql);
					}
				} else if (actiontype.equals("insertRowAt")) {
				} else if (actiontype.equals("insertColumnAt")) {
				} else if (actiontype.equals("deleteRows")) {
				} else if (actiontype.equals("deleteCols")) {
				}
			}
			baseDao.execute(insertSqls);
		}
	}

	@Override
	public boolean saveAsExcel(String name, Employee employee) {
		int x = 0;
		int y = 0;
		HSSFRow row = null;
		HSSFCell cell = null;
		String textValue = null;
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		int emid = employee.getEm_id();
		SqlRowList rs = baseDao.queryForRowSet("select tabid from " + TABLENAME + " where emid= " + emid + " group by tabid");
		/** 多少个sheet */
		while (rs.next()) {
			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet();
			SqlRowList SheetData = baseDao.queryForRowSet("select * from " + TABLENAME + " where emid=" + emid + " AND tabid='"
					+ rs.getObject("tabid") + "'");
			while (SheetData.next()) {
				x = SheetData.getInt("x");
				y = SheetData.getInt("y");
				row = sheet.getRow(x - 1);
				row = (row != null) ? row : sheet.createRow(x - 1);
				cell = row.createCell(y - 1);
				Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(SheetData.getString("content"));
				/** 判断值的类型后进行强制类型转换 */
				Object value = formmap.get("d");
				if (value instanceof Boolean) {
				} else if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					textValue = sdf.format(date);
				} else if (value instanceof byte[]) {
					// 有图片时，设置行高为60px;
					/**
					 * row.setHeightInPoints(60); // 设置图片所在列宽度为80px,注意这里单位的一个换算 sheet.setColumnWidth(i, (short) (35.7 * 80)); // sheet.autoSizeColumn(i); byte[] bsValue = (byte[]) value; HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index); anchor.setAnchorType(2); patriarch.createPicture(anchor, workbook.addPicture( bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
					 **/
				} else if (value instanceof Integer) {

				} else {
					// 其它数据类型都当作字符串简单处理
					textValue = value.toString();
				}
				if (textValue != null) {
					Pattern p = Pattern.compile("-?[0-9]*.?[0-9]*");
					Matcher matcher = p.matcher(textValue);
					if (matcher.matches()) {
						cell.setCellValue(Double.parseDouble(textValue));
					} else {
						HSSFRichTextString richString = new HSSFRichTextString(textValue);
						// HSSFFont font3 = workbook.createFont();
						// font3.setColor(HSSFColor.BLUE.index);
						// richString.applyFont(font3);
						cell.setCellValue(richString);
					}
				}

			}
		}
		OutputStream out;
		try {
			out = new FileOutputStream(PathUtil.getExcelPath() + "\\" + name + ".xls", true);
			workbook.write(out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveAsTemplate(String inJson, Employee employee) {
		// TODO Auto-generated method stub
		/**
		 * 还是拆表
		 */
		String TemplateName = null;
		String TemplateTable = null;
		int DataStoreId = 0;
		String SheetName = null;
		String rowindex = null;
		String colindex = null;
		int celltype = 0;
		JSONObject rowObject = null;
		JSONObject cellObject = null;
		String cellContent = null;
		List<String> insertSqls = new ArrayList<String>();
		JSONObject obj = JSONObject.fromObject(inJson);
		StringBuffer sb = new StringBuffer();
		int et_id = baseDao.getSeqId("EXCELTEMPLATE_SEQ");
		TemplateName = obj.getString("name");
		TemplateTable = obj.getString("tablename");
		DataStoreId = obj.getInt("dsId");
		sb.append("insert into ExcelTemplate ");
		sb.append("(et_tablename,et_id,et_name,et_createdate,et_authorid,et_authorname,et_dsid) values(");
		sb.append("'" + TemplateTable + "','" + et_id + "','" + TemplateName + "',to_date('" + TODAY + "','yyyy-MM-dd HH24:mi:ss'),'"
				+ employee.getEm_id() + "','" + employee.getEm_name() + "','" + DataStoreId + "')");
		insertSqls.add(sb.toString());
		/** 解析传回来的json */
		// 获得sheets
		JSONArray jsonarr = JSONArray.fromObject(obj.getString("sheets"));
		// 遍历sheets
		for (int i = 0; i < jsonarr.size(); i++) {
			JSONObject jsonsheet = jsonarr.getJSONObject(i);
			SheetName = jsonsheet.getString("name").toLowerCase();
			JSONObject rows = JSONObject.fromObject(jsonsheet.get("rows"));
			Iterator iter = rows.keys();
			if (iter.hasNext()) {
				// 遍历rows
				for (iter = rows.keys(); iter.hasNext();) {
					rowindex = iter.next().toString();
					rowObject = JSONObject.fromObject(rows.get(rowindex));
					// 遍历cells
					for (Iterator iter1 = rowObject.keys(); iter1.hasNext();) {
						colindex = iter1.next().toString();
						// cellObject=JSONObject.fromObject(rowObject.get(colindex));
						cellContent = rowObject.getString(colindex);
						cellObject = JSONObject.fromObject(cellContent);
						String celldata = cellObject.getString("data");
						/** 区分是单元格还是列显示 */
						if (celldata.contains("Fn.")) {
							celltype = 1;
						}
						sb.setLength(0);
						sb.append("insert into ExcelTemplateDetail(etd_sheetname,etd_rowindex,etd_colindex,etd_data,etd_mainid,etd_celltype) values(");
						sb.append("'" + SheetName + "','" + rowindex + "','" + colindex + "','" + cellContent + "','" + et_id + "','"
								+ celltype + "')");
						insertSqls.add(sb.toString());
					}

				}
			}
		}
		baseDao.execute(insertSqls);
		return true;
	}

	@Override
	public JSONObject getExcelTemplateByPage(int limit, int start, int count, String query, int enid, Employee employee) {
		DataList dataList = dataListDao.getDataList(CAllER, employee.getEm_master());
		JSONObject obj = new JSONObject();
		obj.put("data", JSONArray.fromObject(BaseUtil.parseGridStore2Str(dataListDao.getDataListData(dataList, query, employee, start
				/ limit + 1, limit, 0, false, null,false))));
		JSONObject metaData = new JSONObject();
		metaData.put("id", "et_id");
		metaData.put("totalProperty", "totalCount");
		metaData.put("root", "results");
		String str = "[{\"name\":\"et_id\"},{\"name\":\"et_name\"},{\"name\":\"exname\"},{\"name\":\"et_authorname\"},{\"name\":\"et_createdate\"},{\"name\":\"size\"},{\"name\":\"fileInfo\"},{\"name\":\"updateDate\"},{\"name\":\"createDate\"}]";
		metaData.put("fields", JSONArray.fromObject(str));
		obj.put("meta", metaData);
		return obj;
	}

	@Override
	public int getTemplateCount(String query, int enid, Employee employee) {
		DataList dataList = dataListDao.getDataList(CAllER, employee.getEm_master());
		return baseDao.getCount(dataList.getSql(query, employee));
	}

	@Override
	public void getJsonDataByTemplate(int id, String colcondition, String cellcondition, String isTemplate, Employee employee) {
		// TODO Auto-generated method stub
		// 要处理可能导出Excel的问题 首先清空数据表
		String BaseMonth = null;
		int emid = employee.getEm_id();
		baseDao.deleteByCondition("ExcelData", "emid=" + emid);
		List<String> insertSqls = new ArrayList<String>();
		ExcelTemplate template = excelDao.getExcelTemplateById(id);
		int ds_id = Integer.parseInt(baseDao.getFieldDataByCondition("DataStore", "ds_id",
				"ds_tablename='" + template.getEt_tablename() + "'").toString());
		if (colcondition != null && !colcondition.equals("null") && !colcondition.equals("")) {
			String[] arr = colcondition.split("#%");
			for (int i = 0; i < arr.length; i++) {
				String[] arr2 = arr[i].split(";");
				if (arr2[0].equals("cm_yearmonth")) {
					BaseMonth = arr2[2];
				}
			}
		}
		SqlRowList sl = null;
		@SuppressWarnings("unchecked")
		List<String> sheets = excelDao.getSheets(id);
		StringBuffer sb = new StringBuffer(50000);
		int index = 1;
		int cellIndex = 0;
		JSONObject realData = new JSONObject();
		sb.append("{");
		sb.append("\"success\":\"true\",");
		sb.append("\"storename\":\"" + template.getEt_tablename() + "\",");
		sb.append("\"storeid\":\"" + ds_id + "\",");
		sb.append("\"extraInfo\":null,");
		sb.append("\"isOwner\":true,");
		sb.append("\"name\":\"" + template.getEt_name() + "\",");
		sb.append("\"isLockedBySelf\":false,");
		sb.append("\"sharing\":\"MYSELF_REPLACE\",");
		sb.append("\"activeSheet\":0,");
		sb.append("\"sheets\":[");
		for (int i = 0; i < sheets.size(); i++) {
			sb.append("{");
			sb.append("\"name\":\"" + sheets.get(i) + "\",");
			sb.append("\"tabId\":\"" + i + "\",");
			sb.append("\"rows\":{");
			if (isTemplate.equals("true")) {
				int rowIndex = 1;
				List<ExcelTemplateDetail> speccoldetails = excelDao.getExcelTemplteDetails(template, sheets.get(i), 0);
				List<ExcelTemplateDetail> speccelldetails = excelDao.getExcelTemplteDetails(template, sheets.get(i), 1);
				// 对单元格数组进行标记
				if (speccoldetails.size() > 0) {
					sl = excelDao.getSqlRowListByDetails(template.getEt_tablename(), colcondition, speccoldetails);
				}
				int size = 0;
				// 由于顺序已经确定好了 所以不需要排序
				if (sl != null) {
					size = sl.getResultList().size();
					while (sl.next()) {
						// 获得抬头
						if (rowIndex == 1) {
							sb.append("\"" + (rowIndex) + "\":");
							sb.append("{");
							for (int col = 0; col < speccoldetails.size(); col++) {
								ExcelTemplateDetail detail = speccoldetails.get(col);
								cellIndex = detail.getEtd_colindex();
								realData = detail.getRealData(detail.getCaption());
								sb.append("\"" + cellIndex + "\":");
								sb.append(detail.getRealData(detail.getCaption()));
								if (col < speccoldetails.size() - 1) {
									sb.append(",");
								}
								insertSqls.add("insert into ExcelData (tabid,x,y,emid,content,action)Values('" + i + "','" + (rowIndex)
										+ "','" + cellIndex + "','" + emid + "','" + realData.toString() + "','createUpdate')");
							}
							sb.append("},");
						}
						sb.append("\"" + (rowIndex + 1) + "\":");
						sb.append("{");
						// 获得当前列号可能存在的特殊单元格
						List<ExcelTemplateDetail> cellList = getContaincells(speccelldetails, rowIndex + 1);
						// 循环列
						for (int col = 0; col < speccoldetails.size(); col++) {
							ExcelTemplateDetail detail = speccoldetails.get(col);
							cellIndex = detail.getEtd_colindex();
							realData = detail.getRealData(sl.getObject(detail.getField()));
							sb.append("\"" + cellIndex + "\":");
							sb.append(realData);
							insertSqls.add("insert into ExcelData (tabid,x,y,emid,content,action)Values('" + i + "','" + (rowIndex + 1)
									+ "','" + cellIndex + "','" + emid + "','" + realData.toString() + "','createUpdate')");
							if (col < speccoldetails.size() - 1) {
								sb.append(",");
							}
						}
						if (cellList.size() > 0) {
							// 已经添加进来的就剔除掉
							speccelldetails.removeAll(cellList);
							sb.append(",");
							for (ExcelTemplateDetail temdetail : cellList) {
								cellIndex = temdetail.getEtd_colindex();
								realData = temdetail.getRealData(excelDao.getExcelFxData(temdetail, cellcondition, BaseMonth));
								// 所以这行开头肯定存在 ","
								// 先往前一格添加函数的标示
								sb.append("\"" + (cellIndex - 1) + "\":");
								sb.append(temdetail.getRealData(temdetail.getFxcaption()));
								sb.append(",");
								sb.append("\"" + cellIndex + "\":");
								sb.append(realData);
								insertSqls.add("insert into ExcelData (tabid,x,y,emid,content,action)Values('" + i + "','"
										+ temdetail.getEtd_rowindex() + "','" + cellIndex + "','" + emid + "','" + realData.toString()
										+ "','createUpdate')");
								if (index < cellList.size()) {
									sb.append(",");
								}
							}
							index++;
						}
						sb.append("}");
						if (rowIndex <= size) {
							sb.append(",");
						}
						rowIndex++;
					}
				}
				// 循环完了 可能还存在未添加进去的单元格
				if (speccelldetails.size() > 0) {
					// 判断是否有结果集
					if (rowIndex > 1) {
						sb.append(",");
					}
					while (speccelldetails.size() > 0) {
						rowIndex = speccelldetails.get(0).getEtd_rowindex();
						sb.append("\"" + speccelldetails.get(0).getEtd_rowindex() + "\":");
						sb.append("{");
						// 存在相同行号的特殊单元格
						List<ExcelTemplateDetail> seconddetails = getContaincells(speccelldetails, rowIndex);
						for (int m = 0; m < seconddetails.size(); m++) {
							ExcelTemplateDetail detail0 = seconddetails.get(m);
							cellIndex = detail0.getEtd_colindex();
							String detaildata = detail0.getData();
							if (detaildata.startsWith("Fn.")) {
								realData = detail0.getRealData(excelDao.getExcelFxData(detail0, cellcondition, BaseMonth));
								sb.append("\"" + (cellIndex - 1) + "\":");
								sb.append(detail0.getRealData(detail0.getFxcaption()));
								sb.append(",");
								sb.append("\"" + cellIndex + "\":");
								sb.append(realData);
							} else {
								sb.append("\"" + (cellIndex) + "\":");
								sb.append(detail0.getRealData(detaildata));
							}
							insertSqls.add("insert into ExcelData (tabid,x,y,emid,content,action)Values('" + i + "','" + rowIndex + "','"
									+ cellIndex + "','" + emid + "','" + realData.toString() + "','createUpdate')");
							if (m < seconddetails.size() - 1) {
								sb.append(",");
							}
						}
						sb.append("}");
						speccelldetails.removeAll(seconddetails);
						if (speccelldetails.size() > 0) {
							sb.append(",");
						}
					}
				}
			} else {
				List<ExcelTemplateDetail> templateDetails = excelDao.getExcelTemplteDetails(template, sheets.get(i));
				Map<Integer, List<ExcelTemplateDetail>> map = GroupByRows(templateDetails);
				for (@SuppressWarnings("rawtypes")
				Iterator iter = map.keySet().iterator(); iter.hasNext();) {
					Integer obj = (Integer) iter.next();
					sb.append("\"" + (obj) + "\":");
					sb.append("{");
					List<ExcelTemplateDetail> smalllists = map.get(obj);
					for (int p = 0; p < smalllists.size(); p++) {
						sb.append("\"" + smalllists.get(p).getEtd_colindex() + "\":");
						sb.append(smalllists.get(p).getRealData(smalllists.get(p).getData()));
						if (p < smalllists.size() - 1) {
							sb.append(",");
						}
					}
					sb.append("}");
					if (iter.hasNext()) {
						sb.append(",");
					}
				}
			}
			sb.append("}}");
			if (i < sheets.size() - 1) {
				sb.append(",");
			}
		}
		for (int i = sheets.size(); i < 3; i++) {
			sb.append(",{\"name\":\"sheet" + (i + 1) + "\",");
			sb.append("\"tabId\":\"" + i + "\",");
			sb.append("\"rows\":{}}");
		}
		sb.append("],");
		sb.append("\"id\":\"" + id + "\",");
		sb.append("\"fileId\":\"" + id + "\",");
		sb.append("\"updateDate\":\"" + TODAY + "\",");
		sb.append("\"isPublic\":false,");
		sb.append("\"permission\":2,");
		sb.append("\"exname\":\"myXls\",");
		sb.append("\"lockedBy\":\"\"");
		sb.append("}");
		BufferedWriter bufferedWriter = null;
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(PathUtil.getExcelPath() + File.separator + "LoadJson"),
					"UTF-8");
			// fileWriter 不能处理编码格式
			bufferedWriter = new BufferedWriter(out);
			bufferedWriter.write(sb.toString());
			bufferedWriter.flush();
			bufferedWriter.close();
			// baseDao.execute(insertSqls);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ConditionItem> getTemplateCondition(int id) {
		List<ConditionItem> items = new ArrayList<ConditionItem>();
		// 导数据测试
		/*
		 * String FindSql="select * from test2  "; SqlRowList sl1=baseDao.queryForRowSet(FindSql); List<String> insert=new ArrayList<String>(); int K=2; ExcelTemplateDetail detail22=excelDao.getExcelTemplteDetails ("etd_rowindex=10 AND etd_colindex=28 AND ETD_mainid=3112").get(0); while(sl1.next()){ if(sl1.getString("args")!=null){ String fxname=sl1.getString("fxname"); String str="Fn."+fxname.substring(0,fxname .lastIndexOf("(")+1)+sl1.getString(
		 * "args").replaceAll("#",",")+")&amp;"+sl1.getString("caption"); JSONObject obj=detail22.getJsonData(); obj.remove("data"); obj.put("data", str); insert.add( "insert into ExcelTemplateDetail (etd_sheetname,etd_rowindex,etd_colindex,etd_data,etd_mainid,etd_celltype)values('sheet1','" +K+"','3','"+obj.toString()+"','3113','1')"); } K++; } baseDao.execute(insert);
		 */
		ExcelTemplate template = excelDao.getExcelTemplateById(id);
		// 列条件
		String colcondition = "dsd_conditionisneed='true' AND dsd_mainid=" + template.getEt_dsid();
		List<DataStoreDetail> details = excelDao.getDataStoreDetails(colcondition);
		if (details.size() > 0) {
			items.add(new ConditionItem("数据条件"));
		}
		for (DataStoreDetail detail : details) {
			items.add(detail.getConditionItem());
		}
		// 单元格的条件
		/**
		 * 查询 template 所有用到的函数 即使相同的函数名可能存在参数不同的情况
		 */
		String cellcondition = "etd_mainid=" + template.getEt_id() + " AND " + "etd_celltype=1";
		List<ExcelTemplateDetail> templatedetails = excelDao.getExcelTemplteDetails(cellcondition);
		int hascellCondtion = 0;
		if (templatedetails.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("ef_name in (");
			for (int i = 0; i < templatedetails.size(); i++) {
				sb.append("'" + templatedetails.get(i).getFxname() + "'");
				if (i < templatedetails.size() - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
			List<ExcelFx> excelFxs = excelDao.getExcelFxs(sb.toString());
			for (int j = 0; j < templatedetails.size(); j++) {
				if (templatedetails.get(j).getData().startsWith("Fn.")) {
					// 模板参数
					String templateargs = templatedetails.get(j).getFxArgs();
					ExcelFx excelfx = getExcelFx(excelFxs, templatedetails.get(j).getFxname());
					String fxargs = excelfx.getEf_args();
					int index = 0;
					if (excelfx.getEf_argnum() > 0) {
						// items.add(excelfx.getConditionItem(templatedetails.get(j).getEtd_rowindex(),
						// templatedetails.get(j).getEtd_colindex()));
						// 如果参数不需要输入 剔除
						String[] templatearg = templateargs.split(",");
						String[] fxarg = fxargs.split(",");
						String[] needarg = new String[] {};
						for (int m = 0; m < excelfx.getEf_argnum(); m++) {
							// 参数没有修改
							if (templatearg[m].equals(fxarg[m].split(";")[0])) {
								needarg[index] = fxarg[m];
								index++;
							}
						}
						if (needarg.length > 0) {
							if (hascellCondtion == 0) {
								items.add(new ConditionItem("单元格条件"));
								hascellCondtion = 1;
							}
							items.add(excelfx.getConditionItem(templatedetails.get(j).getEtd_rowindex(), templatedetails.get(j)
									.getEtd_colindex(), needarg));
						}
					}
				}
			}
		}
		return items;
	}

	@Override
	public boolean ishaveCondition(int id) {
		// TODO Auto-generated method stub
		// 列条件
		// 拿到DataStoreId
		ExcelTemplate template = excelDao.getExcelTemplateById(id);
		String condition = "dsd_conditionisneed='true' AND dsd_mainid=" + template.getEt_dsid();
		List<DataStoreDetail> details = excelDao.getDataStoreDetails(condition);
		if (details.size() > 0) {
			return true;
		}
		// 函数条件
		String TemplateDetailcondition = "etd_celltype=1 AND etd_mainid=" + id;
		List<ExcelTemplateDetail> templatedetails = excelDao.getExcelTemplteDetails(TemplateDetailcondition);
		for (ExcelTemplateDetail detail : templatedetails) {
			String args = detail.getFxArgs();
			String[] temarg = args.split(",");
			if (temarg.length > 1) {
				for (int i = 0; i < temarg.length; i++) {
					if (temarg[i].startsWith("#")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private ExcelFx getExcelFx(List<ExcelFx> lists, String name) {
		for (int i = 0; i < lists.size(); i++) {
			if (lists.get(i).getEf_name().equals(name)) {
				return lists.get(i);
			}
		}
		return null;
	}

	private List<ExcelTemplateDetail> getContaincells(List<ExcelTemplateDetail> lists, int rowindex) {
		List<ExcelTemplateDetail> details = new ArrayList<ExcelTemplateDetail>();
		// 可能存在一列多个函数单元格的情况
		for (int i = 0; i < lists.size(); i++) {
			if (lists.get(i).getEtd_rowindex() == rowindex) {
				details.add(lists.get(i));
			}
		}
		return details;
	}

	private Map<Integer, List<ExcelTemplateDetail>> GroupByRows(List<ExcelTemplateDetail> lists) {
		Map<Integer, List<ExcelTemplateDetail>> map = new HashMap<Integer, List<ExcelTemplateDetail>>();
		for (ExcelTemplateDetail detail : lists) {
			int rownumber = detail.getEtd_rowindex();
			if (map.containsKey(rownumber)) {
				map.get(rownumber).add(detail);
			} else {
				List<ExcelTemplateDetail> small = new ArrayList<ExcelTemplateDetail>();
				small.add(detail);
				map.put(rownumber, small);
			}

		}
		return map;
	}

	/** 下载Excel */
	@Override
	public Object downLoadAsExcel(String type, Employee employee) {
		// TODO Auto-generated method stub
		int emid = employee.getEm_id();
		Object obj = null;
		int x = 0;
		int y = 0;
		Row row = null;
		Cell cell = null;
		String textValue = null;
		SqlRowList rs = baseDao.queryForRowSet("select tabid from " + TABLENAME + " where emid= " + emid + " group by tabid");
		if (type.equals("xls")) {
			// 声明一个工作薄
			HSSFWorkbook workbook = new HSSFWorkbook();
			/** 多少个sheet */
			while (rs.next()) {
				// 生成一个表格
				HSSFSheet sheet = workbook.createSheet();
				SqlRowList SheetData = baseDao.queryForRowSet("select * from " + TABLENAME + " where emid=" + emid + " AND tabid='"
						+ rs.getObject("tabid") + "'");
				while (SheetData.next()) {
					x = SheetData.getInt("x");
					y = SheetData.getInt("y");
					row = sheet.getRow(x - 1);
					row = (row != null) ? row : sheet.createRow(x - 1);
					cell = row.createCell(y);
					Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(SheetData.getString("content"));
					/** 判断值的类型后进行强制类型转换 */
					Object value = formmap.get("d");
					if (value != null) {
						if (value instanceof Boolean) {
						} else if (value instanceof Date) {
							Date date = (Date) value;
							SimpleDateFormat sdf = new SimpleDateFormat(pattern);
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
								// HSSFFont font3 = workbook.createFont();
								// font3.setColor(HSSFColor.BLUE.index);
								// richString.applyFont(font3);
								cell.setCellValue(richString);
							}
						}

					}
				}
			}
			obj = workbook;
		} else if (type.equals("xlsx")) {
			// 声明一个工作薄
			XSSFWorkbook workbook = new XSSFWorkbook();
			/** 多少个sheet */
			while (rs.next()) {
				// 生成一个表格
				XSSFSheet sheet = workbook.createSheet();
				SqlRowList SheetData = baseDao.queryForRowSet("select * from " + TABLENAME + " where emid=" + emid + " AND tabid='"
						+ rs.getObject("tabid") + "'");
				while (SheetData.next()) {
					x = SheetData.getInt("x");
					y = SheetData.getInt("y");
					row = sheet.getRow(x - 1);
					row = (row != null) ? row : sheet.createRow(x - 1);
					cell = row.createCell(y);
					Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(SheetData.getString("content"));
					/** 判断值的类型后进行强制类型转换 */
					Object value = formmap.get("d");
					if (value != null) {
						if (value instanceof Boolean) {
						} else if (value instanceof Date) {
							Date date = (Date) value;
							SimpleDateFormat sdf = new SimpleDateFormat(pattern);
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
								// HSSFFont font3 = workbook.createFont();
								// font3.setColor(HSSFColor.BLUE.index);
								// richString.applyFont(font3);
								cell.setCellValue(richString);
							}
						}

					}
				}
			}
			obj = workbook;
		}
		return obj;
	}

	@Override
	public ByteArrayOutputStream downLoadAsPDF(String title, Employee employee) {
		// TODO Auto-generated method stub
		int emid = employee.getEm_id();
		int maxcolIndex = 0;
		PdfPCell cell = null;
		Object celldata = "";
		SqlRowList sl = baseDao.queryForRowSet("select max(y) from " + TABLENAME + " where emid=" + emid);
		if (sl.next()) {
			maxcolIndex = sl.getInt(1);
		}
		Document document = new Document(PageSize.A4.rotate(), 18f, 18f, 18f, 10f);
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, ba);
			document.open();
			// 处理中文乱码
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
			Font fontChinese = new Font(bfChinese);
			fontChinese.setSize(10f);
			Font fontChinese2 = new Font(bfChinese);
			fontChinese2.setSize(7f);
			float[] widths = new float[maxcolIndex];
			for (int i = 0; i < maxcolIndex; i++) {
				widths[i] = 200f;
			}
			// 创建table列数
			PdfPTable table = new PdfPTable(maxcolIndex);
			table.setWidths(widths);
			table.setWidthPercentage(100);
			table.setSpacingBefore(3f);
			table.getDefaultCell().setBorder(1);// 设置表格边框
			Color bgcolor = new Color(248, 248, 255); // 底色灰色
			SqlRowList sl1 = baseDao.queryForRowSet("select * from " + TABLENAME + " where emid=" + emid + " order by x,y");
			while (sl1.next()) {

				celldata = JSONObject.fromObject(sl1.getString("content")).get("d");
				if (celldata != null) {
					cell = new PdfPCell(new Phrase(celldata.toString(), fontChinese));
				} else
					cell = new PdfPCell();
				cell.setFixedHeight(20);
				cell.setPadding(0);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
				cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setBackgroundColor(bgcolor);
				table.addCell(cell);
			}
			document.add(table);
			document.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ba;
	}

	/**
	 * 加载空页面
	 */
	@Override
	public String getResetData() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer(500);
		sb.append("{");
		sb.append("\"success\":\"true\",");
		sb.append("\"storename\":\"\",");
		sb.append("\"storeid\":\"\",");
		sb.append("\"extraInfo\":null,");
		sb.append("\"isOwner\":true,");
		sb.append("\"name\":\"新建电子表格\",");
		sb.append("\"isLockedBySelf\":false,");
		sb.append("\"sharing\":\"MYSELF_REPLACE\",");
		sb.append("\"activeSheet\":0,");
		sb.append("\"sheets\":[");
		for (int i = 0; i < 3; i++) {
			sb.append("{\"name\":\"Sheet" + i + "\",");
			sb.append("\"tabId\":\"" + i + "\",");
			sb.append("\"rows\":{}}");
			if (i < 2) {
				sb.append(",");
			}
		}
		sb.append("],");
		sb.append("\"id\":\"qgrhDiPSgoc_\",");
		sb.append("\"fileId\":\"qgrhDiPSgoc_\",");
		sb.append("\"updateDate\":\"" + TODAY + "\",");
		sb.append("\"isPublic\":false,");
		sb.append("\"permission\":2,");
		sb.append("\"exname\":\"myXls\",");
		sb.append("\"lockedBy\":\"\"");
		sb.append("}");
		return sb.toString();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteTemplateById(int id) {
		// TODO Auto-generated method stub
		baseDao.deleteByCondition("ExcelTemplate", "et_id=" + id);
		baseDao.deleteByCondition("ExcelTemplateDetail", "etd_mainid=" + id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HSSFWorkbook savePanelAsExcel(String caller, int id, String gridstr, Employee employee, String language) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, employee.getEm_master());
		int rowIndex = 0;
		List<FormDetail> formDetails = form.getFormDetails();
		List<FormDetail> cloneDetails = new ArrayList<FormDetail>();
		Map<String, Object> formData =baseDao.getFormData(form, form.getFo_keyfield() + "=" + id);
		
		//系统权限设置岗位不可看字段过滤
		List<LimitFields> limits = new ArrayList<LimitFields>();
		String master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		if (!"admin".equals(employee.getEm_type())) {
			limits = hrJobDao.getLimitFieldsByType(caller, null, 1, employee.getEm_defaulthsid(), master);
		}
		for (LimitFields lt : limits) {
			for (FormDetail fd : formDetails) {
				if(fd.getFd_field().equals(lt.getLf_field())){
					fd.setFd_columnwidth((float)0);
				}
			}
		}
		
		if (caller.endsWith("$Change")) {
			// 特殊通用表单
			List<FormDetail> addLists = new ArrayList<FormDetail>();
			FormDetail de = null;
			for (FormDetail detail : formDetails) {
				cloneDetails.add(detail);
				if (!"commonchangelog".equals(detail.getFd_table()) && !"changeCodeField".equals(detail.getFd_logictype())) {
					try {
						de = (FormDetail) detail.clone();
						de.setFd_field(de.getFd_field() + "-new");
						de.setFd_group("变更后");
						addLists.add(de);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
			cloneDetails.addAll(addLists);
			String data = baseDao.getFieldValue("commonchangelog", "cl_data", "cl_id=" + id, String.class);
			JSONObject obj = JSONObject.fromObject(data);
			Iterator<String> it=obj.keys();
			String key=null;
			while (it.hasNext()) {
				key=it.next();
				if(!formData.containsKey(key)){
					formData.put(key, obj.get(key));
				}
			}

		} else cloneDetails=formDetails;
			
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.createFreezePane(0, 1);// 固定标题
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < 8; i++) {
			if (i % 2 == 1) {
				sheet.setColumnWidth(i, 8000);
			} else
				sheet.setColumnWidth(i, 4000);
		}
		HSSFCellStyle titleStyle = getCellStyle(workbook, "title");
		row.setHeight((short) 400);
		// 指定合并区域
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (short) 7));
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(form.getFo_title()));
		cell.setCellStyle(titleStyle);
		HSSFCellStyle labelStyle = getCellStyle(workbook, "label");
		// form分组
		Map<String, List<FormDetail>> groupMap = new HashMap<String, List<FormDetail>>();
		List<String> groups = new ArrayList<String>();
		for (FormDetail formDetail : cloneDetails) {
			if (formDetail.getFd_group() != null && !formDetail.getFd_group().trim().equals("")) {
				if (!groupMap.containsKey(formDetail.getFd_group())) {
					List<FormDetail> list = new ArrayList<FormDetail>();
					list.add(formDetail);
					groupMap.put(formDetail.getFd_group(), list);
					groups.add(formDetail.getFd_group());
				} else {
					List<FormDetail> list = groupMap.get(formDetail.getFd_group());
					list.add(formDetail);
				}
			}
		}
		int count = 1;
		if (groupMap.size() > 1) {// 分组写入
			HSSFCellStyle groupStyle = getCellStyle(workbook, "group");
			// 分组先排序
			for (String gtitle : groups) {
				List<FormDetail> list = groupMap.get(gtitle);
				if (rowIndex % 4 != 0)
					rowIndex += 4 - rowIndex % 4;
				row = getRow(sheet, count++ + (rowIndex / 4));
				row.setHeight((short) 350);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, (short) 1));
				HSSFCell groupCell = row.createCell(0);
				groupCell.setCellValue(gtitle);
				groupCell.setCellStyle(groupStyle);
				for (FormDetail detail : list) {
					if (detail.getFd_type() != null && !detail.getFd_type().equals("H") && detail.getFd_columnwidth() != 0) {
						row = getRow(sheet, rowIndex / 4 + count);
						createCellByFormDetail(row, labelStyle, detail, combos, formData.get(detail.getFd_field()), rowIndex, language);
						rowIndex++;
					}
				}
			}
		}
		if (count == 1) {// 无分组写入
			for (FormDetail detail : cloneDetails) {
				if (detail.getFd_type() != null && !detail.getFd_type().equals("H") && detail.getFd_columnwidth() != 0) {
					row = getRow(sheet, rowIndex / 4 + 1);
					createCellByFormDetail(row, labelStyle, detail, combos, formData.get(detail.getFd_field()), rowIndex, language);
					rowIndex++;
				}
			}
		}
		// 显示明细数据
	/*	Object detailfield = form.getFo_detailmainkeyfield();
		if (detailfield != null) {
			List<DetailGrid> detailgrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
			row = sheet.createRow((rowIndex / 4 + 1+count));
			row.setHeight((short) 300);
			HSSFCellStyle cellStyle2 = getCellStyle(workbook, "column");
			createCell(row, detailgrids, combos, cellStyle2, true, null, language);
			List<Map<String, Object>> maps = baseDao
					.getDetailGridData(detailgrids, detailfield.toString() + "=" + id, employee, null, null);
			for (Map<String, Object> map : maps) {
				row = sheet.createRow(row.getRowNum() + 1);
				createCell(row, detailgrids, combos, null, false, map, language);
			}

		}*/
	
		if (!"{}".equals(gridstr) && gridstr != null && !"null".equals(gridstr)) {
			
			
			List<Map<String, String>> listmap= parseStrtoListMap(gridstr);
			for(Map<String,String> m:listmap){
				Set<String> keyset = m.keySet();
			for (String gridcaller : keyset) {

				if (gridcaller != null&&!"".equals(gridcaller)&&!"null".equals(gridcaller) ) {

					List<DetailGrid> detailgrids = detailGridDao.getDetailGridsByCaller(gridcaller.toString(),SpObserver.getSp());		
					
					//系统权限设置岗位不可看字段过滤
					List<LimitFields> gridLimits = new ArrayList<LimitFields>();
					if (!"admin".equals(employee.getEm_type())) {
						gridLimits = hrJobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(), master);
					}
					for (LimitFields lt : gridLimits) {
						for (DetailGrid dg : detailgrids) {
							if(dg.getDg_field().equals(lt.getLf_field())){
								dg.setDg_width(0);
							}
						}
					}
					
					row = sheet.createRow((row.getRowNum() + 2));
					row.setHeight((short) 300);
					HSSFCellStyle cellStyle2 = getCellStyle(workbook, "column");
					String field = m.get(gridcaller);
					createCell(row, detailgrids, combos, cellStyle2, true, null,language);
					if(detailgrids.size()>0){
						List<Map<String, Object>> maps = baseDao
							.getDetailGridData(detailgrids, field + "=" + id,
									employee, null, null);
				
						for (Map<String, Object> map : maps) {
							row = sheet.createRow(row.getRowNum() + 1);
							createCell(row, detailgrids, combos, null, false, map,
									language);
						}
					}
				}
			}
			}
		}
		
		

		
		return workbook;
	}
	
	@SuppressWarnings("unchecked")
	public HSSFWorkbook saveTabPanelAsExcel(String sheetNames, Map<String, Object> grid, String gridTitle, String gridType) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		String[] sheetArray = sheetNames.split(",");
		String[] titles = gridTitle.split(",");
		String[] types = gridType.split(",");
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFCellStyle titleStyle = getCellStyle(workbook, "title");
		HSSFCellStyle numberStyle =workbook.createCellStyle();
		numberStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.######"));
		for(String sheetName : sheetArray){
			sheet = workbook.createSheet(sheetName);
			row = sheet.createRow(0);
			//设置标题
			for(int i = 0; i < titles.length; i++){
				sheet.setColumnWidth(i, 10*512);
				cell = row.createCell(i);
				cell.setCellValue(titles[i]);
				cell.setCellStyle(titleStyle);
			}
			//设置grid数据
			List<Map<String, Object>> gridDataList = (List<Map<String, Object>>) grid.get(sheetName);
			for(int j = 0; j < gridDataList.size(); j++){
				row = sheet.createRow(j+1);
				Iterator it = gridDataList.get(j).values().iterator();
				int i = 0;
				while(it.hasNext()){
					String value = String.valueOf(it.next());
					cell = row.createCell(i);
					if(!StringUtils.isEmpty(value) && !"null".equalsIgnoreCase(value)){
						if(!"text".equals(types[i])){	//设置单元格类型
							cell.setCellStyle(numberStyle);
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(value));
						}else{
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cell.setCellValue(value);
						}
					}
					i++;
				}
			}
		}
		
		return workbook;
	}
	
	private List<Map<String,String>> parseStrtoListMap(String str){
	
		List<Map<String,String>> list=new LinkedList<Map<String,String>>();
		Map<String,String> m;
		String[] str1=str.substring(1, str.length()-1).split(",");
		for(String mstr:str1){
			String[] split = mstr.split(":");
			m=new HashMap<String,String>();
			m.put(split[0].substring(1,split[0].length()-1), split[1].substring(1,split[1].length()-1));
			list.add(m);
		}
		return list;
	}

	private HSSFRow getRow(HSSFSheet sheet, int rowIndex) {
		HSSFRow row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);
		return row;
	}

	private void createCellByFormDetail(HSSFRow row, HSSFCellStyle labelStyle, FormDetail detail, List<DataListCombo> combos, Object value,
			int rowIndex, String language) {
		if(!"EM_PASSWORD".equals(detail.getFd_field().toUpperCase())){
			HSSFCell labelCell = row.createCell(2 * (rowIndex % 4));
			if (language.equals("en_US")) {
				labelCell.setCellValue(detail.getFd_captionen());
			} else if (language.equals("zh_TW")) {
				labelCell.setCellValue(detail.getFd_captionfan());
			} else {
				labelCell.setCellValue(detail.getFd_caption());
			}
			labelCell.setCellStyle(labelStyle);
			HSSFCell valueCell = row.createCell(2 * (rowIndex % 4) + 1);
			setCellValue(valueCell, value, detail.getFd_type(), combos);	
		}
	}

	private void createCell(HSSFRow row, List<DetailGrid> detailgrids, List<DataListCombo> combos, CellStyle cellstyle, boolean istitle,
			Map<String, Object> map, String language) {
		int index = 0;
		HSSFCell cell = null;
		for (DetailGrid detail : detailgrids) {
			if (detail.getDg_width() != 0) {
				cell = row.createCell(index);
				if (istitle) {
					cell.setCellStyle(cellstyle);
					if (language.equals("en_US")) {
						cell.setCellValue(detail.getDg_captionen());
					} else if (language.equals("zh_TW")) {
						cell.setCellValue(detail.getDg_captionfan());
					} else {
						cell.setCellValue(detail.getDg_caption());
					}
				} else {
					setCellValue(cell, map.get(detail.getDg_field()), detail.getDg_type(), combos);
				}
				index++;
			}
		}
	}

	/**
	 * 将数值按配置进行转化后再写入cell
	 * 
	 * @param cell
	 * @param value
	 * @param detail
	 * @param combos
	 */
	private void setCellValue(Cell cell, Object value, String type, List<DataListCombo> combos) {
		if (value != null) {
			if ("C".equals(type) || "EC".equals(type) || "combo".equals(type) || "combocolumn".equals(type)) {
				for (DataListCombo combo : combos) {
					if (value.equals(combo.getDlc_display())) {
						value = combo.getDlc_value();
						break;
					}
				}
			} else if ("B".equals(type) || "checkcolumn".equals(type)) {
				if ("1".equals(value) || "-1".equals(value))
					value = "√";
				else
					value = "×";
			} else if ("YN".equals(type) || "yncolumn".equals(type)) {
				if ("1".equals(value.toString()) || "-1".equals(value.toString()))
					value = "是";
				else
					value = "否";
			}
		}
		setCellValue(cell, value, type);
	}

	private void setCellValue(Cell cell, Object value, String type) {
		String textValue = "";
		if (value != null) {
			if (value instanceof Boolean) {
			} else if (value instanceof Date) {
				Date date = (Date) value;
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				textValue = sdf.format(date);
			} else if (value instanceof byte[]) {
				// 有图片时，设置行高为60px;
			} else if (value instanceof Integer) {
				textValue = value.toString();
			} else {
				// 其它数据类型都当作字符串简单处理
				textValue = value.toString();
			}
			if (textValue != null) {
				//([1-9][0-9]*(\\.\\d+)?)$
				Pattern p = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
				Matcher matcher = p.matcher(textValue);
				if (matcher.matches()
						&& !("S".equals(type) || "text".equals(type) || "AC".equals(type) || "C".equals(type) || "T".equals(type))) {
					cell.setCellValue(Double.parseDouble(textValue));
				} else {
					HSSFRichTextString richString = new HSSFRichTextString(textValue);
					cell.setCellValue(richString);
				}
			}

		}
	}

	private HSSFCellStyle getCellStyle(HSSFWorkbook workbook, String type) {
		HSSFFont font = workbook.createFont();
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (type.equals("title")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 300);
			cellStyle.setFont(font);
		} else if (type.equals("group")) {
			HSSFPalette customPalette = workbook.getCustomPalette();
			customPalette.setColorAtIndex(HSSFColor.ORANGE.index, (byte) 153, (byte) 153, (byte) 204);
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 250);
			cellStyle.setFont(font);
		} else if (type.equals("column")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		} else {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.VIOLET.index);
			cellStyle.setBottomBorderColor(HSSFColor.VIOLET.index);
			cellStyle.setLeftBorderColor(HSSFColor.VIOLET.index);
			cellStyle.setRightBorderColor(HSSFColor.VIOLET.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		}
		return cellStyle;
	}

	@Override
	public String getFormTitle(String caller, int id) {
		// TODO Auto-generated method stub
		String title = "";
		if (caller != null) {
			Form form = formDao.getForm(caller, SpObserver.getSp());
			if (form != null) {
				title += form.getFo_title();
				Object codefield = form.getFo_codefield();
				if (codefield != null) {
					String table = form.getFo_table();
					if (caller.endsWith("$Change"))
						table = "COMMONCHANGELOG";
					Object codevalue = baseDao.getFieldDataByCondition(table, codefield.toString(), form.getFo_keyfield() + "=" + id);
					if (codevalue != null) {
						title += "-" + codevalue.toString();
					}
				}
			}
		}
		if (title.equals("")) {
			title = "导出数据";
		}
		return title;
	}

	@Override
	public HSSFWorkbook twoExport(String data, String columns, Employee employee, String language) {
		// TODO Auto-generated method stub
		try {
			data = new String(data.getBytes("ISO8859_1"), "UTF-8");
			columns = new String(columns.getBytes("ISO8859_1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<Map<Object, Object>> cols = BaseUtil.parseGridStoreToMaps(columns);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		HSSFCell columnCell = null;
		HSSFCell ValueCell = null;
		HSSFCellStyle cellStyle = getCellStyle(workbook, "title");
		row.setHeight((short) 400);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString("缺料表二维查看"));
		cell.setCellStyle(cellStyle);
		// 指定合并区域
		sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) (cols.size() - 1)));
		row = sheet.createRow(1);// header
		row.setHeight((short) 300);
		cellStyle = getCellStyle(workbook, "column");
		for (int i = 0; i < cols.size(); i++) {
			columnCell = row.createCell(i);
			columnCell.setCellStyle(cellStyle);
			columnCell.setCellValue(cols.get(i).get("text").toString());
			sheet.setColumnWidth(i, 5000);
		}
		for (int j = 0; j < maps.size(); j++) {
			row = sheet.createRow(2 + j);
			int index = 0;
			for (int k = 0; k < cols.size(); k++) {
				ValueCell = row.createCell(index);
				setCellValue(ValueCell, maps.get(j).get(cols.get(k).get("dataIndex")), null);
				index++;
			}
		}
		return workbook;
	}

	@Override
	public HSSFWorkbook exportBatchBOMAsExcel(Employee employee, String language) {
		// TODO Auto-generated method stub
		List<String> codelists = new ArrayList<String>();
		SqlRowList codesl = baseDao.queryForRowSet("select bb_prodcode from bombatch  where bb_emid='" + employee.getEm_id()
				+ "' order by bb_prodcode");
		while (codesl.next()) {
			codelists.add(codesl.getString(1));
		}
		List<String> soncodelist = new ArrayList<String>();
		SqlRowList soncodesl = baseDao.queryForRowSet("select distinct bbs_soncode from bombatchstruct where bbs_emid='"
				+ employee.getEm_id() + "' order by bbs_soncode ");
		while (soncodesl.next()) {
			soncodelist.add(soncodesl.getString(1));
		}
		SqlRowList qtysl = null;
		String[] qtyStr = null;
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = getCellStyle(workbook);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		sheet.setColumnWidth(0, 5000);
		cell.setCellStyle(style);
		HSSFCell columnCell = null;
		HSSFCell ValueCell = null;
		String value = "";
		cell.setCellValue("子件编号");
		// 其他的配置信息
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller("ExportArrayBOM", SpObserver.getSp());
		int m = 0;
		String QueryFields = "";
		for (; m < details.size(); m++) {
			QueryFields += details.get(m).getDg_field() + ",";
			if (!details.get(m).getDg_field().equals("pr_code")) {
				columnCell = row.createCell(m + 1);
				columnCell.setCellStyle(style);
				columnCell.setCellValue(details.get(m).getDg_caption());
				sheet.setColumnWidth(m + 1, 5000);
			}
		}
		QueryFields = QueryFields.substring(0, QueryFields.length() - 1);
		String QuerySql = "select * from (select " + QueryFields + " from  " + details.get(0).getDg_table()
				+ " where pr_code in (select distinct bbs_soncode from bombatchstruct where bbs_emid='" + employee.getEm_id()
				+ "')) order by pr_code";
		SqlRowList sl = baseDao.queryForRowSet(QuerySql);
		for (int i = m; i < codelists.size() + m; i++) {
			columnCell = row.createCell(i);
			columnCell.setCellStyle(style);
			columnCell.setCellValue(codelists.get(i - m));
			sheet.setColumnWidth(i, 5000);
		}
		for (int j = 1; j < soncodelist.size() + 1; j++) {
			row = sheet.createRow(j);
			row.createCell(0).setCellValue(soncodelist.get(j - 1));
			qtysl = baseDao
					.queryForRowSet("select wmsys.wm_concat(qty) from (select bbs_topmothercode,sum(qty)qty from  (select  bbs_topmothercode,sum(bbs_baseqty) as qty  from  bombatch left join  bombatchstruct on bombatch.bb_id=bombatchstruct.bbs_bbid   where bbs_soncode ='"
							+ soncodelist.get(j - 1)
							+ "' and bbs_emid='"
							+ employee.getEm_id()
							+ "' group by bbs_topmothercode  union all select bb_prodcode ,0  from bombatch where bb_emid="
							+ employee.getEm_id() + ")   group by bbs_topmothercode order by bbs_topmothercode)");
			if (qtysl.next()) {
				qtyStr = qtysl.getString(1).split(",");
			}
			if (sl.next()) {
				for (int z = 1; z < m + 1; z++) {
					ValueCell = row.createCell(z);
					value = sl.getString(z);
					setCellValue(ValueCell, value, null);
				}
			}
			for (int k = m; k < codelists.size() + m; k++) {
				ValueCell = row.createCell(k);
				if (qtyStr[k - m] != null && qtyStr[k - m].startsWith(".")) {
					value = "0" + qtyStr[k - m];
				} else
					value = qtyStr[k - m];
				setCellValue(ValueCell, value, null);
			}
		}
		return workbook;
	}

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

	@Override
	public HSSFWorkbook exportBOMCheckMesExcel(String bomId, String caller) {
		// TODO Auto-generated method stub
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = getCellStyle(workbook);
		row.setHeight((short) 400);
		// HSSFCell cell =null;
		HSSFCell columnCell = null;
		// HSSFCell ValueCell = null;
		// 其他的配置信息
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		int m = 0;
		String QueryFields = "";
		for (; m < details.size(); m++) {
			QueryFields += details.get(m).getDg_field() + ",";
			columnCell = row.createCell(m);
			columnCell.setCellStyle(style);
			columnCell.setCellValue(details.get(m).getDg_caption());
			sheet.setColumnWidth(m + 1, 5000);

		}

		QueryFields = QueryFields.substring(0, QueryFields.length() - 1);
		String QuerySql = "select " + QueryFields + " from  " + details.get(0).getDg_table() + " where bm_bomid=" + bomId
				+ " order by bm_id";
		SqlRowList sl = baseDao.queryForRowSet(QuerySql);
		int j = 1;
		while (sl.next()) {
			row = sheet.createRow(j);
			for (int k = 1, l = 0; k < details.size(); k++, l++) {
				row.createCell(l).setCellValue(sl.getString(k));
			}
			row.createCell(details.size() - 1).setCellValue(sl.getDate(details.size()).toString());
			j++;
		}
		return workbook;
	}

}
