package com.uas.erp.service.excel.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.ExcelConstant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelParseXlsService;
@Service
public class ExcelParseXlsServiceImpl implements ExcelParseXlsService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ExcelElementService elementService;
	@Autowired
	private ExcelFileTemplateService fileTemplateService;
	
	private Map<String, Object> colwidthMap = new HashMap<String, Object>();
	
	private HSSFWorkbook workbook;
	
	@Override
	public int parseExcelTemplate(MultipartFile file, int subof, Employee employee) {
		int fileId = baseDao.getSeqId("EXCELFILE_COMMON_SEQ");
		try {
			workbook = new HSSFWorkbook(file.getInputStream());
			HSSFSheet sheet = null;
			//保存文件信息
			String filename = file.getOriginalFilename();
			String name = filename.substring(0,filename.lastIndexOf('.'));
			ExcelFileTemplate fileTemplate = new ExcelFileTemplate();
			fileTemplate.setFileid_tpl(fileId);
			fileTemplate.setFilename_tpl(name);
			fileTemplate.setFilecategory_tpl(false);
			fileTemplate.setFileman_tpl(employee.getEm_name());
			fileTemplate.setFilesubof_tpl(subof);
			fileTemplate.setFilecreatetime_tpl(new Date());
			baseDao.save(fileTemplate,"ExcelFile_Template");
	        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
	        	// 获取每个Sheet表
	        	sheet = workbook.getSheetAt(i);
	        	int sheetId = baseDao.getSeqId("EXCELSHEET_SEQ");
	        	//保存sheet信息
	        	workbook.getSheetName(i);
	        	workbook.getActiveSheetIndex();
	        	ExcelSheet excelSheet = new ExcelSheet();
	        	excelSheet.setsheetid(sheetId);
	        	excelSheet.setsheetfileid(fileId);
	        	excelSheet.setsheetname(workbook.getSheetName(i));
	        	excelSheet.setsheetorder(i);
	        	excelSheet.setsheetcelltable("EXCELCELL");
	        	excelSheet.setsheetactive(i==workbook.getActiveSheetIndex());
	        	baseDao.save(excelSheet,"ExcelSheet");
	            for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
	            	HSSFRow row = sheet.getRow(j);
	            	
	                if (row != null) {
	                	//处理行高度
	                	dealRowHeight(sheet,excelSheet,row);
	                	
	                    for (int col = 0; col < row.getLastCellNum(); col++) {
	                    	//处理列宽度
	                    	dealColWidth(sheet,excelSheet,col);
	                    	
	                    	if (row.getCell(col) != null) {
	                    		HSSFCell cell = row.getCell(col);
	                    		Boolean bool =  isMergedRegion(sheet,cell.getRowIndex(),cell.getColumnIndex());
	                    		if (bool) {
									dealMergedRegion(sheet,cell,excelSheet);
								}else {
									saveCell(cell, excelSheet);
								}
	                        }
	                    }
	                }
	            }
	        }		
		} catch (IOException e) {
			e.printStackTrace();
		};
		return fileId;
	}
	
	@Override
	public int parseExcelFile(MultipartFile file, String filecaller, Employee employee) {
		int fileId = baseDao.getSeqId("EXCELFILE_COMMON_SEQ");
		ExcelFileTemplate fileTemplate = fileTemplateService.getByCaller(filecaller);		
		try {
			workbook = new HSSFWorkbook(file.getInputStream());
			HSSFSheet sheet = null;
			//保存文件信息
			String filename = file.getOriginalFilename();
			String name = filename.substring(0,filename.lastIndexOf('.'));
			ExcelFile excelFile = new ExcelFile();
			excelFile.setFileid(fileId);
			excelFile.setFilename(name);
			excelFile.setFilecreatetime(new Date());
			excelFile.setFileman(employee.getEm_name());
			excelFile.setFilestatus("在录入");
			excelFile.setFilestatuscode("ENTERING");
			excelFile.setFiletplsource(fileTemplate.getFileid_tpl());
			baseDao.save(excelFile,"ExcelFile");
	        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
	        	// 获取每个Sheet表
	        	sheet = workbook.getSheetAt(i);
	        	int sheetId = baseDao.getSeqId("EXCELSHEET_SEQ");
	        	//保存sheet信息
	        	workbook.getSheetName(i);
	        	workbook.getActiveSheetIndex();
	        	ExcelSheet excelSheet = new ExcelSheet();
	        	excelSheet.setsheetid(sheetId);
	        	excelSheet.setsheetfileid(fileId);
	        	excelSheet.setsheetname(workbook.getSheetName(i));
	        	excelSheet.setsheetorder(i);
	        	excelSheet.setsheetcelltable("EXCELCELL");
	        	excelSheet.setsheetactive(i==workbook.getActiveSheetIndex());
	        	baseDao.save(excelSheet,"ExcelSheet");
	            for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
	            	HSSFRow row = sheet.getRow(j);
	            	
	                if (row != null) {
	                	//处理行高度
	                	dealRowHeight(sheet,excelSheet,row);
	                	
	                    for (int col = 0; col < row.getLastCellNum(); col++) {
	                    	//处理列宽度
	                    	dealColWidth(sheet,excelSheet,col);
	                    	
	                    	if (row.getCell(col) != null) {
	                    		HSSFCell cell = row.getCell(col);
	                    		Boolean bool =  isMergedRegion(sheet,cell.getRowIndex(),cell.getColumnIndex());
	                    		if (bool) {
									dealMergedRegion(sheet,cell,excelSheet);
								}else {
									saveCell(cell, excelSheet);
								}
	                        }
	                    }
	                }
	            }
	        }		
		} catch (IOException e) {
			e.printStackTrace();
		};
		return fileId;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void dealColWidth(HSSFSheet sheet,ExcelSheet excelSheet, int col) {
    	Double width = (double) sheet.getColumnWidth(col);
    	Double Dfwidth = (double) (sheet.getDefaultColumnWidth()*256);
    	if (!width.equals(Dfwidth)) {
    		//判断是否已经解析过该Column的宽度;
    		Boolean bool = colwidthMap.containsKey(excelSheet.getsheetid()+","+col+1);
    		if (!bool) {
        		ExcelCell excelCell = new ExcelCell();
        		excelCell.setCellid(baseDao.getSeqId("EXCELCELL_SEQ"));
        		excelCell.setCellsheetid(excelSheet.getsheetid());
        		excelCell.setCellcol(col+1);
        		excelCell.setCellrow(0);
    			JSONObject o = new JSONObject();
    			o.put("width", Math.round((width/Dfwidth)*80));
        		excelCell.setCellcontent(o.toJSONString());
        		excelCell.setCellcal(false);
        		baseDao.save(excelCell, excelSheet.getsheetcelltable());
        		colwidthMap.put(excelSheet.getsheetid()+","+col+1, excelCell);
			}
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void dealRowHeight(HSSFSheet sheet,ExcelSheet excelSheet, HSSFRow row) {
		Double height = (double) row.getHeight();
		Double Dfheight = (double) sheet.getDefaultRowHeight();
		if (!height.equals(Dfheight)) {
			ExcelCell excelCell = new ExcelCell();
			excelCell.setCellid(baseDao.getSeqId("EXCELCELL_SEQ"));
			excelCell.setCellsheetid(excelSheet.getsheetid());
			excelCell.setCellcol(0);
			excelCell.setCellrow(row.getRowNum()+1);
			JSONObject o = new JSONObject();
			o.put("height", Math.round((height/Dfheight)*20));
			excelCell.setCellcontent(o.toJSONString());
			excelCell.setCellcal(false);
			baseDao.save(excelCell, excelSheet.getsheetcelltable());
		}
	}

	public void dealMergedRegion(HSSFSheet sheet, HSSFCell cell, ExcelSheet excelSheet) {
        int sheetMergeCount = sheet.getNumMergedRegions();  
        for (int i = 0; i < sheetMergeCount; i++) {  
            CellRangeAddress range = sheet.getMergedRegion(i);  
            int firstColumn = range.getFirstColumn();  
            int lastColumn = range.getLastColumn();  
            int firstRow = range.getFirstRow();  
            int lastRow = range.getLastRow(); 
            if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow) {  
                if (cell.getColumnIndex() >= firstColumn && cell.getColumnIndex() <= lastColumn) {
                	//是第一个合并单元格才处理
                    if (cell.getRowIndex() == firstRow && cell.getColumnIndex() == firstColumn) {  
                    	//保存至excelelement表
                    	Map<String, Object> map = new HashMap<String, Object>();
                    	map.put("name", excelSheet.getsheetid()+"$"+firstRow+1+"$"+firstColumn+1+"$"+lastRow+1+"$"+lastColumn+1);
                    	map.put("ftype", "meg");
                    	map.put("json", Arrays.toString(new Integer[] {firstRow+1,firstColumn+1,lastRow+1,lastColumn+1}));
                    	elementService.create(map, excelSheet);
                        //处理格子
                    }
            		saveCell(cell, excelSheet);
            		return;
                }  
            } 
        } 
    }

	public static boolean isMergedRegion(HSSFSheet sheet, int row, int column) {  
        int sheetMergeCount = sheet.getNumMergedRegions();  
        for (int i = 0; i < sheetMergeCount; i++) {  
            CellRangeAddress range = sheet.getMergedRegion(i);  
            int firstColumn = range.getFirstColumn();  
            int lastColumn = range.getLastColumn();  
            int firstRow = range.getFirstRow();  
            int lastRow = range.getLastRow();  
            if (row >= firstRow && row <= lastRow) {  
                if (column >= firstColumn && column <= lastColumn) {  
                    return true;  
                }  
            }  
        }  
        return false;  
    }
	
	//保存Cell数据
	@Transactional(propagation=Propagation.REQUIRED)
	private void saveCell(HSSFCell cell,ExcelSheet excelSheet) {
		ExcelCell excelCell = new ExcelCell();
		excelCell.setCellid(baseDao.getSeqId("EXCELCELL_SEQ"));
		excelCell.setCellsheetid(excelSheet.getsheetid());
		excelCell.setCellcol(cell.getColumnIndex()+1);
		excelCell.setCellrow(cell.getRowIndex()+1);
		excelCell.setCellcontent(createCellcontent(cell));
		if (cell.getCellType()==Cell.CELL_TYPE_FORMULA) {
			excelCell.setCellcal(true);
			excelCell.setCellrawdata("="+cell.getCellFormula());
			String cellValue = "";
            try {  
                cellValue = cell.getStringCellValue();  
            } catch (IllegalStateException e) {
            	try {
            		cellValue = String.valueOf(cell.getNumericCellValue());  
				} catch (Exception e2) {
					// TODO: handle exception
//					BaseUtil.showError("存在错误的公式！");
				}
            }  
			excelCell.setCellcalvalue(cellValue);
		}else {
			excelCell.setCellcal(false);
			excelCell.setCellrawdata(cell.toString());
		}

		baseDao.save(excelCell, excelSheet.getsheetcelltable());
	}
	
	private String createCellcontent(HSSFCell cell){
		HSSFCellStyle cellStyle = cell.getCellStyle();
		JSONObject o = new JSONObject();
		//数据
		//判断数据类型
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			o.put("data", cell.getRichStringCellValue().getString());
			break;
		case Cell.CELL_TYPE_FORMULA:
			o.put("data", "="+cell.getCellFormula());
			o.put("cal", true);
			
			if (!StringUtil.hasText(cellStyle.getDataFormatString())) {
				break;
			}
			//百分数
			if (cellStyle.getDataFormatString().equals("0.00%")) {
				o.put("fm", "percent");
				o.put("dfm", ExcelConstant.getDataFormat(cellStyle.getDataFormatString()));
			//分数
			}else if (cellStyle.getDataFormatString().equals("#\\ ?/?")) {
				o.put("fm", "fraction");
			//逗号？
			}else if (cellStyle.getDataFormatString().equals("_ * #,##0_ ;_ * \\-#,##0_ ;_ * \"-\"_ ;_ @_")||
					cellStyle.getDataFormatString().equals("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ")) {
				o.put("fm", "comma");
			}		
			break;
		case Cell.CELL_TYPE_NUMERIC:
/*			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				System.out.println("日期"+cell.getDateCellValue());
			}else {
			}*/
			String value="";
/*			System.out.println("DataFormatString:"+cellStyle.getDataFormatString());
			System.out.println(cellStyle.getDataFormatString().equals("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_"));
			System.out.println("DataFormat:"+cellStyle.getDataFormat());*/
			//区分时间类型和普通数字
			
			if (!StringUtil.hasText(cellStyle.getDataFormatString())) {
				break;
			}
			
			if (cellStyle.getDataFormatString().equals("yyyy\"年\"m\"月\"d\"日\";@")|| 
				cellStyle.getDataFormatString().equals("yyyy/m/d;@") || 
				cellStyle.getDataFormatString().equals("m\"月\"d\"日\";@")) {
				o.put("fm", "date");
				value = DateUtil.format(cell.getDateCellValue(), "yyyy-MM-dd");
				o.put("dfm", ExcelConstant.getDataFormat(cellStyle.getDataFormatString()));
			}else {
				Double d = cell.getNumericCellValue();
				DecimalFormat df = new DecimalFormat("#.##");  
				value = df.format(d);
				//百分数
				if (cellStyle.getDataFormatString().equals("0.00%")) {
					o.put("fm", "percent");
					o.put("dfm", ExcelConstant.getDataFormat(cellStyle.getDataFormatString()));
				//分数
				}else if (cellStyle.getDataFormatString().equals("#\\ ?/?")) {
					o.put("fm", "fraction");
				//逗号？
				}else if (cellStyle.getDataFormatString().equals("_ * #,##0_ ;_ * \\-#,##0_ ;_ * \"-\"_ ;_ @_")||
						cellStyle.getDataFormatString().equals("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ")) {
					o.put("fm", "comma");
				}
			}
			o.put("data", value);
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_ERROR:
			break;
		default:
			break;
		}
		
		//水平，垂直样式
		o.put("ta",ExcelConstant.getTextAlign(cellStyle.getAlignment()) );
		o.put("va", ExcelConstant.getVerticalAlign(cellStyle.getVerticalAlignment()));
		//边框及边框颜色
		if (cellStyle.getBorderTop()!=0) {
			o.put("xbts", ExcelConstant.getBorder(cellStyle.getBorderTop()));
			if (cellStyle.getTopBorderColor()!=0) {
				o.put("xbtc", "black");
			}
		}
		if (cellStyle.getBorderBottom()!=0) {
			o.put("xbbs", ExcelConstant.getBorder(cellStyle.getBorderBottom()));
			if (cellStyle.getBottomBorderColor()!=0) {
				o.put("xbbc", "black");
			}
		}
		if (cellStyle.getBorderLeft()!=0) {
			o.put("xbls", ExcelConstant.getBorder(cellStyle.getBorderLeft()));
			if (cellStyle.getLeftBorderColor()!=0) {
				o.put("xblc", "black");
			}
		}
		if (cellStyle.getBorderRight()!=0) {
			o.put("xbrs", ExcelConstant.getBorder(cellStyle.getBorderRight()));
			if (cellStyle.getRightBorderColor()!=0) {
				o.put("xbrc", "black");
			}
		}
		
		//字体
		//字体大小
		HSSFFont font = cellStyle.getFont(workbook);
		o.put("ff", font.getFontName());
		o.put("fz", font.getFontHeightInPoints());
		if (font.getBold()) {
			o.put("fw", "bold");
		}
		return o.toJSONString();
	}
}
