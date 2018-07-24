package com.uas.erp.service.excel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.excel.ExcelDao;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelElement;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.model.excel.ExcelSheetSpan;
import com.uas.erp.model.excel.SheetTabIdOrderInfo;
import com.uas.erp.model.excel.Triple;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelSheetService;

@Service
public class ExcelSheetServiceImpl implements ExcelSheetService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ExcelDao excelDao;
	
	@Autowired
	private ExcelElementService excelElementService;
	@Autowired
	private ExcelCellService excelCellService;	
	
	@Override
	public ExcelSheet load(Integer sheetId) {
		return baseDao.queryBean("select * from ExcelSheet where sheetid="+sheetId, ExcelSheet.class);
	}
	
	@Override
	public List<ExcelSheet> getExcelSheetsByFileId(int fileid) {
		return baseDao.query("select * from EXCELSHEET where sheetfileid="+fileid,ExcelSheet.class);
	}


	public List<ExcelSheetSpan> getTabSpans(Set<Integer> keySet, String sheetcelltable_tpl) {
	   String ids = StringUtils.join(keySet.toArray(), ",");
	   String sql = "select cellsheetid AS tabId, count(cellid) AS totalCellNum, max(cellrow) AS maxRow, max(cellcol) AS maxCol " + 
				" from "+sheetcelltable_tpl+" where cellsheetid in ("+ids+") " + 
				" group by cellsheetid";
	   return baseDao.query(sql,ExcelSheetSpan.class);
	}

	
    public Triple<Integer, Map<Integer, ExcelSheet>,  Map<Integer, ExcelSheetSpan>> getTabsWithSpan(Integer fileId) {
        List<ExcelSheet> sheets = getExcelSheetsByFileId(fileId);
        if(sheets.size() > 0){
            boolean firstTabActive = true;
            Integer activeTabId = sheets.get(0).getsheetid();
            
            Map<Integer,ExcelSheet> tabMap = new HashMap<>();
            for(ExcelSheet sheet:sheets){
                tabMap.put(sheet.getsheetid(), sheet);
                if(sheet.getsheetactive()!=null && sheet.getsheetactive()){
                    activeTabId = sheet.getsheetid();
                    firstTabActive = false;
                }
            }
            
            List<ExcelSheetSpan> tabSpans = getTabSpans(tabMap.keySet(), sheets.get(0).getsheetcelltable());
            Map<Integer, ExcelSheetSpan> tabSpanMap = new HashMap<>();
            if(tabSpans.isEmpty()){
                for(Integer tabId:tabMap.keySet()){
                    tabSpanMap.put(tabId, new ExcelSheetSpan(tabId, 0, 0, 0));
                }
            }else{
                for(ExcelSheetSpan tabSpan:tabSpans){
                    tabSpanMap.put(tabSpan.getTabId(), tabSpan);
                }
                
                for(Integer tabId: Sets.difference(tabMap.keySet(), tabSpanMap.keySet())){
                    tabSpanMap.put(tabId, new ExcelSheetSpan(tabId, 0, 0, 0));
                }
                
            }
            
            if(firstTabActive){
                activeSheetTab(fileId, activeTabId);
            }
            return new Triple<>(activeTabId, tabMap, tabSpanMap);
        }else{
            return null;
        }
       
    }

    @Transactional(propagation=Propagation.REQUIRED)
	public void activeSheetTab(Integer fileId, Integer activeTabId) {
		baseDao.updateByCondition("EXCELSHEET", "SHEETACTIVE=1", "SHEETID="+activeTabId+" AND SHEETFILEID="+fileId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void activeTab(ExcelSheet sheet) {
		if (sheet !=null && sheet.getsheetid()!=null) {
			baseDao.updateByCondition("EXCELSHEET", "sheetactive=1", "sheetfileid="+sheet.getsheetfileid()+" AND sheetid="+sheet.getsheetid());
			baseDao.updateByCondition("EXCELSHEET", "sheetactive=0", "sheetfileid="+sheet.getsheetfileid()+" AND sheetid!="+sheet.getsheetid());
		}
		
	}
	
	
	//sheet相关
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void renameSheet(String sheetId, String name) {
		baseDao.updateByCondition("EXCELSHEET", "sheetname='"+name+"'", "sheetid="+sheetId);
	}

	@Override
	public ExcelSheetSpan getSheetSpan(ExcelSheet sheet) {
		String sql = "select cellsheetid AS tabId, count(cellid) AS totalCellNum, max(cellrow) AS maxRow, max(cellcol) AS maxCol " + 
				" from "+sheet.getsheetcelltable()+" where cellsheetid ="+sheet.getsheetid()+" group by cellsheetid";
		return baseDao.queryBean(sql, ExcelSheetSpan.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int createSheet(String fileId, Integer position, String name, String color) {
//		ExcelFileTemplate ExcelFileTemplate = excelFileTplService.getById(new Integer((fileId)));
//		int newTabOrder = adjustSpreadTabOrder(ExcelFileTemplate,position);
		//找最大的order+1
		String celltable = baseDao.queryForObject("select distinct sheetcelltable from ExcelSheet where sheetfileid="+fileId, String.class);
		
		int newTabOrder = baseDao.queryForObject(
				"select max(sheetorder)+1 from ExcelSheet where sheetfileid="+fileId
				, Integer.class);
		int id = baseDao.getSeqId("ExcelSheet_SEQ");
		ExcelSheet newSheet = new ExcelSheet(id, new Integer(fileId), name, newTabOrder, false, celltable);
		baseDao.save(newSheet,"ExcelSheet");
//		ExcelSheetTemplate newSheet = sheetTabService.insert(document.getId(), name, newTabOrder,true, color);
		return id;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteSheet(String sheetId) {
		ExcelSheet sheet = load(new Integer(sheetId));
		//删sheet
		baseDao.deleteById("ExcelSheet", "sheetid", sheet.getsheetid());
		//删element
		baseDao.deleteByCondition("ExcelElement", "elesheetid="+sheet.getsheetid());
		//删cell
		baseDao.deleteByCondition(sheet.getsheetcelltable(), "cellsheetid="+sheet.getsheetid());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void copyFile(ExcelFileTemplate fromFile, ExcelFile toFile) {
		List<ExcelSheet> sheets = getExcelSheetsByFileId(fromFile.getFileid_tpl());
		
		String _cellTableName="ExcelCell";
		Map<String, String> tabMap = new HashMap<String, String>();
		for(ExcelSheet sourceSheet : sheets) {
			ExcelSheet targetSheet = new ExcelSheet();
			targetSheet.setsheetid(baseDao.getSeqId("ExcelSheet_SEQ"));
			targetSheet.setsheetfileid(toFile.getFileid());
			targetSheet.setsheetname(sourceSheet.getsheetname());
			targetSheet.setsheetorder(sourceSheet.getsheetorder());
			targetSheet.setsheetactive(sourceSheet.getsheetactive());
			targetSheet.setsheetcolor(sourceSheet.getsheetcolor());
			targetSheet.setsheetcelltable(_cellTableName);
			//插入sheet
			baseDao.save(targetSheet,"ExcelSheet");
			//插入格子
			excelCellService.copySheetCellFromSheet(sourceSheet.getsheetid(), targetSheet.getsheetid(), sourceSheet.getsheetcelltable(), _cellTableName);
			
			//插入ExcelElement 特殊元素
			List<ExcelElement> elements = excelElementService.findElementsBySheet(sourceSheet.getsheetid());
			for(ExcelElement element : elements) {
//				element.setTab(targetTab);
			    element.setElesheetid(targetSheet.getsheetid());
				String oldItemString = "\"span\":[" + sourceSheet.getsheetid().toString() + ",";
				String newItemString = "\"span\":[" + targetSheet.getsheetid().toString() + ",";
				String json = element.getElecontent().replace(oldItemString, newItemString);
				
				oldItemString = "span:[" + sourceSheet.getsheetid().toString() + ",";
				newItemString = "span:[" + targetSheet.getsheetid().toString() + ",";
				json = json.replace(oldItemString, newItemString);
				
				oldItemString = "sheetId:" + sourceSheet.getsheetid().toString();
				newItemString = "sheetId:" + targetSheet.getsheetid().toString();
				json = json.replace(oldItemString, newItemString);
				
				oldItemString = "\"sheetId\":" + sourceSheet.getsheetid().toString();
				newItemString = "\"sheetId\":" + targetSheet.getsheetid().toString();
				json = json.replace(oldItemString, newItemString);
				
				element.setElecontent(json);
				element.setEleid(baseDao.getSeqId("EXCELELEMENT_SEQ"));
				baseDao.save(element,"ExcelElement");
			}
			
			tabMap.put(sourceSheet.getsheetid().toString(), targetSheet.getsheetid().toString());
		}
		
		// ok, we need check whether cell field include cross reference...: span:[tabId,
		updateCellCrossSheetRef(tabMap, _cellTableName);
		
		// ok, need copy file config too - which save name manager ...
/*        List<DocumentConfig> allConfigs = documentConfigMapper.find(fromFile.getId());		
		for(DocumentConfig config : allConfigs) {
			// ok, need replace tabId ... for this case:
			// "span":[566,27,9,30,9] TO "span":[tabId,....
			String json = config.getJson();			
			for (String key : tabMap.keySet()) {
				// check whether it is match ...
				String oldItemString = "\"span\":[" + key + ",";
				String newItemString = "\"span\":[" + tabMap.get(key) + ",";
				json = json.replace(oldItemString, newItemString);
				
				oldItemString = "span:[" + key + ",";
				newItemString = "span:[" + tabMap.get(key) + ",";
				json = json.replace(oldItemString, newItemString);
				
				// check whether includes sheetId ...
				oldItemString = "sheetId:" + key;
				newItemString = "sheetId:" + tabMap.get(key);
				json = json.replace(oldItemString, newItemString);
				
				oldItemString = "\"sheetId\":" + key;
				newItemString = "\"sheetId\":" + tabMap.get(key);
				json = json.replace(oldItemString, newItemString);
				
				// check whether includes sheetId ...
				oldItemString = "scope:" + key;
				newItemString = "scope:" + tabMap.get(key);
				json = json.replace(oldItemString, newItemString);
				
				oldItemString = "\"scope\":" + key;
				newItemString = "\"scope\":" + tabMap.get(key);
				json = json.replace(oldItemString, newItemString);
			}
			config.setJson(json);
			config.setDocumentFile(toFile);
			documentConfigMapper.insert(config);
		}*/
	}

	@Transactional(propagation=Propagation.REQUIRED)
	private void updateCellCrossSheetRef(Map<String, String> tabIdMap, String _cellTableName) {
		Map<String, String> spanMaps = new HashMap<String, String>();
		for (String key : tabIdMap.keySet()) {
			// check whether it is match ...
			String oldItemString = "\\\"span\\\":[" + key + ",";
			String newItemString = "\\\"span\\\":[" + tabIdMap.get(key) + ",";
			spanMaps.put(oldItemString, newItemString);
		}
		
		for (String fromTabId : tabIdMap.keySet()) {		
			String toTabId = tabIdMap.get(fromTabId);
			List<ExcelCell> calCells = excelCellService.findCalCellsByTab(new Integer(toTabId), _cellTableName);	
			for (ExcelCell cell : calCells) {			
				String content = cell.getCellcontent();
				// loop each ...
				for (String oldStr : spanMaps.keySet()) {
					if (content.contains(oldStr)) {
						content = content.replace(oldStr, spanMaps.get(oldStr));
						excelCellService.updateContentOnly(cell.getCellid(), content, _cellTableName);
					}
				}
			}
		}	
	}

	@Override
	public List<ExcelCell> loadTabCornerCalCellOnDemand(ExcelSheet excelSheet, Integer startCellId, Integer size,
			Integer fileId, Boolean skipCal) {
		List<ExcelCell> cells = new ArrayList<ExcelCell>();
		Integer sheetId = excelSheet.getsheetid();
/*       if(skipCal){
            cells = sheetCellMapper.loadCornerCellsOnDemand(startCellId, tabId, size, tab.getCellTable());
        }else{
            List<Integer> tabIds = this.getAllTabIds(fileId);
            if(! tabIds.isEmpty())
                cells = sheetCellMapper.loadCornerCalCellsOnDemand(startCellId, tabId, size, tab.getCellTable(), tabIds);
            else
                cells = Collections.emptyList();
        }	*/	
		return cells;
	}

	@Override
	public void changeSheetOrder(String sheetId, Integer prePos, Integer curPos) {
		ExcelSheet sheet = load(new Integer(sheetId));
		
		List<SheetTabIdOrderInfo> tabIdOrderInfoList = excelDao
				.findSheetTabIdOrderInfoByDocument(sheet.getsheetfileid());
		
		List<SheetTabIdOrderInfo> shiftList = new ArrayList<SheetTabIdOrderInfo>();
		int _tabNewOrder = -1;
		for (int i = 0; i < tabIdOrderInfoList.size(); i++) {
			SheetTabIdOrderInfo _info = tabIdOrderInfoList.get(i);
			if (i == curPos) {			
				if (curPos > prePos) {
					_tabNewOrder = _info.getTabOrder() + 1;	
				} else {	
					_tabNewOrder = _info.getTabOrder();
					shiftList.add(_info);
				}
			}
			
			if (i > curPos && _info.getTabId().intValue() != sheet.getsheetid().intValue()) {
				shiftList.add(_info);
			}			
		}
		
		if (_tabNewOrder > 0) {
			baseDao.updateByCondition("ExcelSheet", "sheetorder="+_tabNewOrder, "sheetid="+sheet.getsheetid());
		}
		
		if (shiftList.size() > 0) {
			String ids = "";
			for (SheetTabIdOrderInfo sheetTabIdOrderInfo : shiftList) {
				ids=ids+sheetTabIdOrderInfo.getTabId()+",";
			}
			ids=ids.substring(0,ids.length()-1);
			baseDao.updateByCondition("ExcelSheet", "sheetorder=sheetorder+1",
			"sheetfileid="+sheet.getsheetfileid()+" and sheetid in("+ids+")");
		}
	}

	@Override
	public Map<String, Object> copySheet(String oldSheetId, String newSheetName, Integer pos) {
		ExcelSheet oldSheet = load(Integer.parseInt(oldSheetId));
		int newTabOrder = baseDao.queryForObject(
				"select max(sheetorder)+1 from ExcelSheet where sheetfileid="+oldSheet.getsheetfileid()
				, Integer.class);
		ExcelSheet newSheet = new ExcelSheet(baseDao.getSeqId("EXCELSHEET_SEQ"), oldSheet.getsheetfileid(), newSheetName, newTabOrder, false, oldSheet.getsheetcelltable());
		//保存新sheet
		baseDao.save(newSheet,"Excelsheet");
		//拷贝数据到新sheet
		copyTab(oldSheet, newSheet,oldSheet.getsheetcelltable(), newSheet.getsheetcelltable());
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		results.put("info", "Changes Saved");
		results.put("id", newSheet.getsheetid().toString());
		return results;
	}

	private void copyTab(ExcelSheet oldSheet, ExcelSheet newSheet, String oldsheetcelltable,
			String newsheetcelltable) {
		//拷贝sheet下的格子数据
		excelDao.copySheetCellFromSheet(oldSheet.getsheetid(), newSheet.getsheetid(), oldsheetcelltable, newsheetcelltable);
		
		List<ExcelElement> elements = excelElementService.findElementsBySheet(oldSheet.getsheetid());
		
		List<ExcelElement> copiedElements = new ArrayList<>(500);
		for(ExcelElement element : elements) {
		    element.setEleid(baseDao.getSeqId("EXCELELEMENT_SEQ"));
		    element.setElesheetid(newSheet.getsheetid());
			String oldItemString = "\"span\":[" + oldSheet.getsheetid().toString() + ",";
			String newItemString = "\"span\":[" + newSheet.getsheetid().toString() + ",";
			String json = element.getElecontent().replace(oldItemString, newItemString);
			element.setElecontent(json);
			oldItemString = oldSheet.getsheetid().toString() + "$";
			newItemString = newSheet.getsheetid().toString() + "$";
			json = element.getElename().replace(oldItemString, newItemString);
			element.setElename(json);
			copiedElements.add(element);
			if(copiedElements.size() == 500){
			    excelElementService.batchInsert(copiedElements);
			    copiedElements.clear();
			}
		}
		if(!copiedElements.isEmpty()){
			excelElementService.batchInsert(copiedElements);
		}		
	}
}
