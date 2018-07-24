package com.uas.erp.service.excel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.drools.lang.DRLParser.and_constr_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.excel.ExcelDao;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.SheetCellRangeCondition;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelSheetService;

@Service
public class ExcelCellServiceImpl implements ExcelCellService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ExcelDao excelDao;
	
	@Autowired
	private ExcelSheetService excelSheetService;
	@Autowired
	private ExcelElementService excelElementTplService;
	
	//批量更新格子
	@Override
	public void updateBatchCells(String actions, Boolean isTpl) {
		
		List<Object> items = JacksonUtil.fromJsonArray(actions);

		ExcelSheet sheet = null;
		//要增加的cell
		List<ExcelCell> addObjs = new ArrayList<>();
		//要减少的cell
		List<ExcelCell> delObjs = new ArrayList<>();

		// map tablD and really id
		Map<String, ExcelSheet> tabIdMaps = new HashMap<String, ExcelSheet>();

		for (int j = 0, size = items.size(); j < size; j++) {
			Map<String, Object> jsonObj = (Map<String, Object>) items.get(j);

			String _sheetId = jsonObj.containsKey("sheetId") ? jsonObj.get("sheetId").toString() : null;

			if (_sheetId != null) {
				ExcelSheet _sheetTab;
				if (tabIdMaps.containsKey(_sheetId)) {
					_sheetTab = tabIdMaps.get(_sheetId);
				} else {
					_sheetTab = excelSheetService.load(Integer.valueOf(_sheetId));
					tabIdMaps.put(_sheetId, _sheetTab);
				}

				// if this is from different tab, submit existing list and clean
				if (sheet != null && !sheet.equals(_sheetTab)) {
					if (!addObjs.isEmpty()) {
						insertBatchSetCells(addObjs, sheet);
						addObjs.clear();
					}
					if(!delObjs.isEmpty()){
	                    batchDeleteCells(delObjs, sheet);
	                    delObjs.clear();
	                }
				}
				sheet = _sheetTab;
			}

			// ok, start to call service to process action - first we need check
			// by multiple cells ...
			String action = (String) jsonObj.get("action");
			if (action.equalsIgnoreCase("setCell")) { // this is a special case, need batch insert
				if (sheet != null){
					//将jsonObj转化为格子对象
					ExcelCell setCellObj = getCell(jsonObj, sheet);
				    if("{}".equals(setCellObj.getCellcontent())){
				    	delObjs.add(setCellObj);
				    }else{
				    	addObjs.add(setCellObj);
				    }
				}else {
					//LOG.error("Pass data error - not tabId, please investigate: " + jsonObj.toString());
					BaseUtil.showError("数据错误 - 没有sheetid, 请检查: " + jsonObj.toString());
				}
			}else {
				// first submit the list object ...
				if(!addObjs.isEmpty()) {
					insertBatchSetCells(addObjs, sheet);
					addObjs.clear();
				}
				if(!delObjs.isEmpty()){
					batchDeleteCells(delObjs, sheet);
					delObjs.clear();
				}
				//其他更新行为
				handleOtherAction(jsonObj, sheet);
			}
			//如果要插入的大于300,执行批量插入
			// ok, we need do some check before process more ...
			if(addObjs.size() > 300) {
				insertBatchSetCells(addObjs, sheet);
				addObjs.clear();
			}
			//如果要删除的大于300,执行批量删除
			if(delObjs.size() > 300){
				batchDeleteCells(delObjs, sheet);
				delObjs.clear();
			}
		}
		//如果插入的不为空
		// insert last one ...
		if (!addObjs.isEmpty()){
			insertBatchSetCells(addObjs, sheet);
		}
		//如果删除的不为空
		if(!delObjs.isEmpty()){
			batchDeleteCells(delObjs, sheet);
		}
	}
	
	//其他更新行为
	private void handleOtherAction(Map<String, Object> jsonObj, ExcelSheet sheetTab) {

		//获得json中的更新行为
		String action = (String) jsonObj.get("action");

		if (action.equalsIgnoreCase("setActivedSheet")) {
			Integer tabId = new Integer(jsonObj.get("activedSheetId")
					.toString());
			ExcelSheet tab = excelSheetService.load(tabId);
			excelSheetService.activeTab(tab);

		} else if (action.equalsIgnoreCase("updateReCalCell")) {
			Integer sheetId = new Integer(jsonObj.get("sheetId").toString());
			Integer row = new Integer(jsonObj.get("row").toString());
			Integer col = new Integer(jsonObj.get("col").toString());
			String calvalue = jsonObj.get("calvalue").toString();
			updateReCalCell(sheetId,row,col,calvalue,sheetTab.getsheetcelltable());

		}else if (action.equalsIgnoreCase("removeRow")) {
			Integer minRow = new Integer(jsonObj.get("minrow").toString());
			Integer maxrow = new Integer(jsonObj.get("maxrow").toString());
			deleteRows(sheetTab.getsheetid(), minRow, maxrow,
					sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("removeColumn")) {
			Integer mincol = new Integer(jsonObj.get("mincol").toString());
			Integer maxcol = new Integer(jsonObj.get("maxcol").toString());
			deleteCols(sheetTab.getsheetid(), mincol, maxcol,
					sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("removeCell")) {
			Integer minrow = new Integer(jsonObj.get("minrow").toString());
			Integer maxrow = new Integer(jsonObj.get("maxrow").toString());
			Integer mincol = new Integer(jsonObj.get("mincol").toString());
			Integer maxcol = new Integer(jsonObj.get("maxcol").toString());
			String moveDirection = jsonObj.get("moveDir").toString();
			deleteCells(sheetTab.getsheetid(), minrow, maxrow,
					mincol, maxcol, moveDirection, sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("insertRow")) {
			Integer row = new Integer(jsonObj.get("row").toString());
			Integer rowSpan = new Integer(jsonObj.get("rowSpan").toString());
			insertRows(sheetTab.getsheetid(), row, rowSpan,
					sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("insertColumn")) {
			Integer col = new Integer(jsonObj.get("col").toString());
			Integer colSpan = new Integer(jsonObj.get("colSpan").toString());
			insertCols(sheetTab.getsheetid(), col, colSpan,
					sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("insertCell")) {
			Integer row = new Integer(jsonObj.get("row").toString());
			Integer rowSpan = new Integer(jsonObj.get("rowSpan").toString());
			Integer col = new Integer(jsonObj.get("col").toString());
			Integer colSpan = new Integer(jsonObj.get("colSpan").toString());
			String moveDirection = jsonObj.get("moveDir").toString();
			insertCells(sheetTab.getsheetid(), row, rowSpan, col,
					colSpan, moveDirection, sheetTab.getsheetcelltable());

		} else if (action.equalsIgnoreCase("sortSpan")) {
			// sort span ....
			//sortSpan(jsonObj);

		} else if (action.equalsIgnoreCase("moveRows")) {
			//moveRows(jsonObj, sheetTab);

		} else if (action.equalsIgnoreCase("createFloatingItem")) {
			excelElementTplService.create(jsonObj, sheetTab);

		} else if (action.equalsIgnoreCase("updateFloatingItem")) {
			excelElementTplService.update(jsonObj, sheetTab);

		} else if (action.equalsIgnoreCase("removeFloatingItem")) {
			excelElementTplService.remove(jsonObj, sheetTab);

		} else if (action.equalsIgnoreCase("updateHiddens")) {
			excelElementTplService.createUpdate(jsonObj, sheetTab);

		} 
/*		else if (action.equalsIgnoreCase("createFileConfig")) {
			String fileIdStr = jsonObj.get("fileId").toString();
			Integer fileId = new Integer(fileIdStr);
			ExcelFileTemplate file = documentFileService.getById(fileId);
			documentConfigService.create(jsonObj, file);

		} else if (action.equalsIgnoreCase("updateFileConfig")) {
			String fileIdStr = jsonObj.get("fileId").toString();
			Integer fileId = new Integer(fileIdStr);
			ExcelFileTemplate file = documentFileService.getById(fileId);
			documentConfigService.update(jsonObj, file);

		} else if (action.equalsIgnoreCase("removeFileConfig")) {
			String fileIdStr = jsonObj.get("fileId").toString();
			Integer fileId = new Integer(fileIdStr);
			ExcelFileTemplate file = documentFileService.getById(fileId);
			documentConfigService.remove(jsonObj, file);

		}*/
	}
	
	//更新公式格子的实际值
	@Transactional(propagation=Propagation.REQUIRED)
	private void updateReCalCell(Integer sheetId, Integer row, Integer col, String calvalue,
			String getsheetcelltable) {
		baseDao.updateByCondition(getsheetcelltable, "cellcalvalue='"+calvalue+"'", 
		"cellsheetid="+sheetId+" AND cellrow="+row+" AND cellcol="+col);
	}

	//insertCells
	@Transactional(propagation=Propagation.REQUIRED)
	private void insertCells(Integer sheetid_tpl, Integer row, Integer rowSpan, Integer col, Integer colSpan,
			String moveDirection, String sheetcelltable_tpl) {
        int maxrow = row+rowSpan-1;
        int maxcol = col+colSpan-1;

        if (moveDirection.equalsIgnoreCase("down")) {
			/*
	         * move the cells after minrow "rowSpan" step forward in the row direction
	         */
        	excelDao.updateRowsAddSpanYY(sheetid_tpl, rowSpan, row, col, maxcol, sheetcelltable_tpl);
        } else {
			/*
	         * move the cells after mincol "colSpan" step forward in the col direction
	         */
        	excelDao.updateColsAddSpanXX(sheetid_tpl, colSpan, row, maxrow, col, sheetcelltable_tpl);
        }
	}
	
	//insertCols
	@Transactional(propagation=Propagation.REQUIRED)
	private void insertCols(Integer sheetid_tpl, Integer col, Integer colSpan, String sheetcelltable_tpl) {
		excelDao.insertColumns(sheetid_tpl, col, colSpan, sheetcelltable_tpl);
	}
	
	//insertRows
	@Transactional(propagation=Propagation.REQUIRED)
	private void insertRows(Integer sheetid_tpl, Integer row, Integer rowSpan, String sheetcelltable_tpl) {
		excelDao.insertRows(sheetid_tpl, row, rowSpan, sheetcelltable_tpl);
	}
	
	//deleteCells
	@Transactional(propagation=Propagation.REQUIRED)
	private void deleteCells(Integer sheetid_tpl, Integer minrow, Integer maxrow, Integer mincol, Integer maxcol,
			String moveDirection, String sheetcelltable_tpl) {
		// delete all cells ...
		excelDao.deleteCellsXXYY(sheetid_tpl, minrow, maxrow, mincol, maxcol, sheetcelltable_tpl);
        int rowSpan = maxrow-minrow+1;
        int colSpan = maxcol-mincol+1;
        if (moveDirection.equalsIgnoreCase("up")) {
	    	/*
	         * move the cells after maxrow "rowSpan" step forward in the row direction
	         */
        	excelDao.updateRowsWithSpanYY(sheetid_tpl, rowSpan, maxrow, mincol, maxcol, sheetcelltable_tpl);
        } else {
        	/*
	         * move the cells after maxcol "colSpan" step forward in the col direction
	         */
        	excelDao.updateColsWithSpanXX(sheetid_tpl, colSpan, minrow, maxrow, maxcol, sheetcelltable_tpl);
        }
	}
	
	//deleteCols
	@Transactional(propagation=Propagation.REQUIRED)
	private void deleteCols(Integer sheetid_tpl, Integer mincol, Integer maxcol, String sheetcelltable_tpl) {
		int totalSpan = maxcol-mincol+1;
		if (sheetid_tpl>0) {
			baseDao.deleteByCondition(sheetcelltable_tpl,"cellsheetid="+sheetid_tpl+" AND (cellcol between "+mincol+" and "+maxcol+")");
			excelDao.updateColsWithSpan(sheetid_tpl, totalSpan, maxcol, sheetcelltable_tpl);
		}
	}
	
	//deleteRows
	@Transactional(propagation=Propagation.REQUIRED)
	private void deleteRows(Integer sheetid_tpl, Integer minRow, Integer maxrow, String sheetcelltable_tpl) {
        int totalSpan=maxrow-minRow+1;
		if (sheetid_tpl > 0) {
            baseDao.deleteByCondition(sheetcelltable_tpl,"cellsheetid="+sheetid_tpl+" AND (cellrow between "+minRow+" and "+maxrow+")");
            excelDao.updateRowsWithSpan(sheetid_tpl, totalSpan, maxrow, sheetcelltable_tpl);
			}
	}
	
	//批量删除格子
	@Transactional(propagation=Propagation.REQUIRED)
	private void batchDeleteCells(List<ExcelCell> delObjs, ExcelSheet sheet) {
		for (ExcelCell excelCellTemplate : delObjs) {
			baseDao.deleteByCondition(sheet.getsheetcelltable(), 
					"cellsheetid="+excelCellTemplate.getCellsheetid()+" AND "+
					"cellrow="+excelCellTemplate.getCellrow()+" AND "+
					"cellcol="+excelCellTemplate.getCellcol()
					);
		}
	}

	private void insertBatchSetCells(List<ExcelCell> listBatchObjs, ExcelSheet sheet) {
        int size=listBatchObjs.size() ;
        if (size > 0) {
            if(size <= 100){
                batchInsertSheetCell3(listBatchObjs, sheet.getsheetcelltable());
            } else{
                int i = 0;
                for(; i<size ;){
                    if(i+100 <= size){
                        batchInsertSheetCell3(listBatchObjs.subList(i, i+100), sheet.getsheetcelltable());
                        i+=100;
                    }else{
                        batchInsertSheetCell3(listBatchObjs.subList(i, size), sheet.getsheetcelltable());
                        break;
                    }
                }
            }
        }
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void batchInsertSheetCell3(List<ExcelCell> listBatchObjs, String sheetcelltable_tpl) {
		for (ExcelCell excelCell : listBatchObjs) {
			int count = baseDao.getCount("select count(*) from "+sheetcelltable_tpl+
			" where cellsheetid="+excelCell.getCellsheetid()+" AND cellrow="+excelCell.getCellrow()+" AND cellcol="+excelCell.getCellcol());
			if (count==1) {
				//更新
				StringBuffer sb = new StringBuffer();
				sb.append(" cellcontent='"+excelCell.getCellcontent().replace("'", "''")+"',");
				sb.append(" cellrawdata='"+excelCell.getCellrawdata().replace("'", "''")+"',");
				if (excelCell.getCellcalvalue()!=null) {
					sb.append(" cellcalvalue='"+excelCell.getCellcalvalue()+"',");
				}
				sb.append(" cellcal="+(excelCell.getCellcal()==true?1:0));
				baseDao.updateByCondition(sheetcelltable_tpl,sb.toString(), 
						"cellsheetid="+excelCell.getCellsheetid()+" AND cellrow="+excelCell.getCellrow()+" AND cellcol="+excelCell.getCellcol());
			}else if (count==0) {
				//插入
				excelCell.setCellid(baseDao.getSeqId("ExcelCell_SEQ"));
				baseDao.save(excelCell, sheetcelltable_tpl);
			}
		}
	}

	private ExcelCell getCell(Map<String, Object> jsonObj, ExcelSheet sheet) {
		int row = new Integer(jsonObj.get("row").toString());
		int col = new Integer(jsonObj.get("col").toString());
		int sheetId = sheet.getsheetid();
		String content = (String) jsonObj.get("json");
		if(content != null){
			content = content.trim();
		}
		String data = String.valueOf(jsonObj.get("data"));
		boolean cal = (boolean) jsonObj.get("cal");
		ExcelCell cell = new ExcelCell(sheetId, row, col, content, cal, data);
		if (jsonObj.get("calvalue") != null && StringUtil.hasText(jsonObj.get("calvalue").toString())) {
			cell.setCellcalvalue(jsonObj.get("calvalue").toString());
		}
		return cell;
	}
	
	//通过Excel参数 载入所有的格子
	@Override
	public List<ExcelCell> loadAllCellsByExcel(Collection<ExcelSheet> sheets, Integer startCellId,
			Integer size) {
        List<ExcelCell> cells ;
        if (!sheets.isEmpty()) {
            List<Integer> tabIds = new ArrayList<>();
            for(ExcelSheet tab:sheets){
                tabIds.add(tab.getsheetid());
            }
            String cellTable = sheets.iterator().next().getsheetcelltable();
            cells = getAllCellsByExcel(startCellId, size, cellTable, tabIds);
        }else{
           cells = Collections.emptyList();
        }
        return cells;
	}
	
	//通过Excel参数 载入所有的格子
	List<ExcelCell> getAllCellsByExcel(Integer startCellId, Integer size, String cellTable, List<Integer> sheetIds){
		String ids = StringUtils.join(sheetIds.toArray(), ",");
		String sql = " select t.* from (SELECT * FROM "+cellTable+" WHERE cellid > "+startCellId+" and cellsheetid IN ("+ids+") order by cellid asc) t where  rownum<"+size;
		return baseDao.query(sql, ExcelCell.class);
	}


	public List<Integer> getOtherTabIds(Integer fileId, Integer sheetId) {
		return baseDao.queryForList("select sheetid from ExcelSheet where sheetfileid="+fileId+" and sheetid!="+sheetId+" order by sheetorder asc", Integer.class);
	}

	@Override
	public List<ExcelCell> loadTabNonDataCellOfStyle(Integer sheetid_tpl, String sheetcelltable_tpl) {
		String sql="select * from "+sheetcelltable_tpl+" where cellsheetid="+sheetid_tpl+" and (cellrow=0 or cellcol=0)";
		return baseDao.query(sql, ExcelCell.class);
	}

	@Override
	public Integer getTabCellCount(ExcelSheet excelSheetTemplate) {
		String sql = "select count(cellid) as cellCount from "+excelSheetTemplate.getsheetcelltable()+" where cellsheetid = "+excelSheetTemplate.getsheetid();
		return baseDao.queryForObject(sql, Integer.class);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void copySheetCellFromSheet(Integer sourceSpreadTabId, Integer destSpreadTabId, String sourceCellTable,
			String destCellTable) {
		String sql = "insert into "+destCellTable+" a "+
		" select EXCELCELL_SEQ.Nextval,"+destSpreadTabId+",b.cellrow,b.cellcol,b.cellcontent,b.cellcal,b.cellrawdata,b.cellcalvalue  from "+sourceCellTable+" b "
		+" where b.cellsheetid="+sourceSpreadTabId;
		baseDao.execute(sql);
	}

	@Override
	public List<ExcelCell> findCalCellsByTab(Integer sheetid, String _cellTableName) {
		return baseDao.query("select * from "+_cellTableName+" where cellsheetid="+sheetid, ExcelCell.class);
	}

	@Override
	public void updateContentOnly(Integer cellid, String content, String _cellTableName) {
		baseDao.updateByCondition(_cellTableName, "cellcontent='"+content+"'", "cellid="+cellid);
	}

	@Override
	public List<ExcelCell> loadAllCellsByTabAndCalCells(ExcelSheet excelSheet, Integer startCellId, Integer size,
			List<Integer> otherSheetIds) {
	     List<ExcelCell> cells = excelDao.loadAllCellsByTabAndCalCells(startCellId, size,  excelSheet.getsheetcelltable(), excelSheet.getsheetid(), otherSheetIds);
	     return cells;
	}

	@Override
	public List<ExcelCell> getCornerCalCellsWithSize(ExcelSheet activeTab, Collection<ExcelSheet> tabs,
			Integer startCellId, Integer size) {
        List<Integer> tabIds  = new ArrayList<>();
        for(ExcelSheet tab:tabs){
            tabIds.add(tab.getsheetid());
        }
        List<ExcelCell> cells = excelDao.loadCornerCalCellsOnDemand(startCellId, activeTab.getsheetid(), size, activeTab.getsheetcelltable(), tabIds);
		return cells;
	}

	@Override
	public List<ExcelCell> loadTabCellOfRawDataOnDemand(ExcelSheet sheet, Integer startCellId, Integer size,
			Integer fileId, Boolean skipCal) {
        List<ExcelCell> cells;
        if(skipCal){
            cells = excelDao.loadNotCalCellsOfRawDataOnDemand(startCellId, sheet.getsheetid(), size, sheet.getsheetcelltable());
        } else {
            Integer sheetId = sheet.getsheetid();
            List<Integer> otherTabIds = getOtherTabIds(fileId, sheetId);
            if(! otherTabIds.isEmpty()) {
            	String ids = StringUtils.join(otherTabIds.toArray(), ",");
            	cells = excelDao.loadCellsOfRawDataOnDemand(startCellId, sheetId, size, sheet.getsheetcelltable(), ids);
            }
            else {
            	cells = Collections.emptyList();
            }
        }
        return cells;
	}

	@Override
	public List<ExcelCell> loadTabCellOnDemand(ExcelSheet sheet, Integer startCellId, Integer size, Integer fileId,
			Boolean skipCal) {
        List<ExcelCell> cells;
        if(skipCal){
            cells = excelDao.loadNotCalCellsOnDemand(startCellId, sheet.getsheetid(), size, sheet.getsheetcelltable());
        } else {
            Integer sheetId = sheet.getsheetid();
            List<Integer> otherTabIds = getOtherTabIds(fileId, sheetId);
            if(! otherTabIds.isEmpty()) {
            	String ids = StringUtils.join(otherTabIds.toArray(), ",");
            	cells = excelDao.loadCellsOnDemand(startCellId, sheetId, size, sheet.getsheetcelltable(), ids);
            }
            else {
            	cells = Collections.emptyList();
            }
        }
        return cells;
	}

	@Override
	public List<ExcelCell> getCellByRanges(List<SheetCellRangeCondition> ranges, Integer nextCellId, Integer limit) {
		ExcelSheet sheet = excelSheetService.load(ranges.get(0).getTabId());
//		return sheetCellMapper.getCellByRanges(ranges, nextCellId, limit, sheetTab.getCellTable());
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select * from "+sheet.getsheetcelltable()+" where cellid>"+nextCellId);
		if (ranges.size()!=0) {
			sb.append(" and ( ");
			
			for (int i = 0; i < ranges.size(); i++) {
				if (ranges.get(i).getEndRow()==0 && ranges.get(i).getEndCol()==0 ) {
					sb.append("(cellsheetid="+ranges.get(i).getTabId()+")");
				}else if (ranges.get(i).getEndRow()==0) {
					sb.append("(cellsheetid="+ranges.get(i).getTabId()+" and cellcol>="+ranges.get(i).getStartCol()+" and cellcol<="+ranges.get(i).getEndCol()+" )");
				}else if (ranges.get(i).getEndCol()==0) {
					sb.append("(cellsheetid="+ranges.get(i).getTabId()+" and cellrow>="+ranges.get(i).getStartRow()+" and cellrow<="+ranges.get(i).getEndRow()+" )");
				}else {
					sb.append("(cellsheetid="+ranges.get(i).getTabId()+" and cellrow>="+ranges.get(i).getStartRow()+" and cellrow<="+ranges.get(i).getEndRow()+" and cellcol>="+ranges.get(i).getStartCol()+" and cellcol<="+ranges.get(i).getEndCol()+" )");
				}
				if (i!=ranges.size()-1) {
					sb.append(" or ");
				}
			}
			sb.append(" ) ");
		}
		sb.append("order by cellid ASC");
		sb.append(") where rownum<"+limit);
		
		return baseDao.query(sb.toString(), ExcelCell.class);
	}



}
