package com.uas.erp.service.excel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uas.erp.core.ExcelJsonUtil;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.ExcelSheetObj;
import com.uas.erp.model.excel.ExcelSheetSpan;
import com.uas.erp.model.excel.SheetCellRangeCondition;
import com.uas.erp.model.excel.Triple;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelCommonService;
import com.uas.erp.service.excel.ExcelConfService;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelSheetService;
@Service
public class ExcelCommonServiceImpl implements ExcelCommonService {
	
	@Autowired
	private ExcelFileTemplateService excelFileTplService;
	@Autowired
	private ExcelFileService excelFileService;
	@Autowired
	private ExcelSheetService excelSheetService;
	@Autowired
	private ExcelCellService excelCellService;
	@Autowired
	private ExcelElementService excelElementService;
	@Autowired
	private ExcelConfService excelConfService;
	
	
	
	@Override
	public Map<String, Object> loadExcelInfo(Integer fileId, Integer startCellId, Integer size, int throttleOfBigFile, Boolean isTpl) {
		//暂时写死参数
		size=100000;
//		throttleOfBigFile = 500;
		
		String filename="";
		if (isTpl) {
			ExcelFileTemplate fileTemplate=excelFileTplService.getById(fileId);
			filename = fileTemplate.getFilename_tpl();
		}else {
			ExcelFile excelFile=excelFileService.getById(fileId);
			filename = excelFile.getFilename();
		}
        List<ExcelCell> cells = new ArrayList<ExcelCell>();
        Integer nextStartCellId = 0;
        //格子总数
        int totalCellCount = 0; 
        
        Triple<Integer, Map<Integer, ExcelSheet>, Map<Integer,ExcelSheetSpan>> activeTabIdAndTabAndSpan = excelSheetService.getTabsWithSpan(fileId);
		
        final Integer activeTabId = activeTabIdAndTabAndSpan.getT1();
        ExcelSheet activeTab = activeTabIdAndTabAndSpan.getT2().get(activeTabId);
        
        for (ExcelSheetSpan sheetSpan : activeTabIdAndTabAndSpan.getT3().values()) {
        	totalCellCount += sheetSpan.getTotalCellNum();
		}
        
//        System.out.println("格子总数:"+totalCellCount);
        
        int activedCellCount = activeTabIdAndTabAndSpan.getT3().get(activeTabId).getTotalCellNum();
        
        List<ExcelSheetObj> SheetObjs = mergeTabWithSpan(activeTabIdAndTabAndSpan.getT2(), activeTabIdAndTabAndSpan.getT3());
        ExcelSheetObj activedTabObj = Collections2.filter(SheetObjs, new Predicate<ExcelSheetObj>(){
            public boolean apply(ExcelSheetObj tabObj) {
                return activeTabId.equals(tabObj.getId());
            }
        }).iterator().next();	        
        String loadWay = "";
        if (totalCellCount < throttleOfBigFile) {
	        loadWay = "doc";
	        cells = excelCellService.loadAllCellsByExcel(activeTabIdAndTabAndSpan.getT2().values(), startCellId, size);
//	        System.out.println("loadSheetInfo2:"+cells.size());
	        for(ExcelSheetObj SheetObj:SheetObjs){
	        	SheetObj.setAllCellDataLoaded(true);
	        }
		}else if (activedCellCount < throttleOfBigFile) {
			loadWay = "tab";
			List<Integer> otherSheetIds = excelCellService.getOtherTabIds(fileId, activeTabId);
            cells = excelCellService.loadAllCellsByTabAndCalCells(activeTab, startCellId, size, otherSheetIds);
//            System.out.println("loadSheetInfo2:"+cells.size());
            activedTabObj.setAllCellDataLoaded(true);
		}else {
			loadWay = "cor_cal";
			cells = excelCellService.getCornerCalCellsWithSize(activeTab,activeTabIdAndTabAndSpan.getT2().values(), startCellId, size);
//			System.out.println("loadSheetInfo2:"+cells.size());
		}

        if (cells.size() >= size){
            nextStartCellId = cells.get(cells.size() - 1).getCellid();
        }
        List<Object> transformedCells = tranCells(cells);
        Integer totalSize = activeTabIdAndTabAndSpan.getT3().get(activeTabId).getTotalCellNum();
        Map<String, Object> messages = Maps.newHashMap();
        messages.put("fileName", filename);
        messages.put("exname", "xls");
        messages.put("loadWay", loadWay);
        messages.put("floatingTotal", excelElementService.getCountBySheetElement(activeTabId));
        messages.put("sheets", SheetObjs);
        messages.put("fileConfig", excelConfService.find(fileId));
        messages.put("startCellId", nextStartCellId);
        Map<String, Object> results = Maps.newHashMap();
        results.put("success", true);
        results.put("total", totalSize);
        results.put("results", transformedCells);
        results.put("message", messages);
        results.put("updateDate", new Date());
        return results;
	}
	
	//cell转化核心方法
	public List<Object> tranCells(List<ExcelCell> cells){
		List<Object> list = new ArrayList<>();
		for (ExcelCell excelCellTemplate : cells) {
			list.add(tranCell(excelCellTemplate));
		}
		return list;
	}
	
    public Object[] tranCell(ExcelCell cell) {
        Object[] result;
        //?
        String payload =  cell.getCellcontent();
        if(cell.getCellcal()!= null && cell.getCellcal())
            result = new Object[]{cell.getCellsheetid(),cell.getCellrow(), cell.getCellcol(), payload, 1};
        else{
            result = new Object[]{cell.getCellsheetid(),cell.getCellrow(), cell.getCellcol(), payload};
        }
        return result;
    }
	
	
	//生成sheetObjList
    private List<ExcelSheetObj> mergeTabWithSpan(Map<Integer,ExcelSheet> sheetMap, Map<Integer, ExcelSheetSpan> sheetSpanMap){
        List<ExcelSheetObj> sheetObjList = new ArrayList<>(sheetMap.size());
        List<ExcelSheet> sheetList = new ArrayList<>(sheetMap.values());
        Collections.sort(sheetList, new Comparator<ExcelSheet>(){
            @Override
            public int compare(ExcelSheet o1, ExcelSheet o2) {
                int order =  o1.getsheetorder() - o2.getsheetorder();
                return order < 0 ? -1 : order==0 ? 0: 1;
            }
            
        });
        for(ExcelSheet sheet:sheetList){
            ExcelSheetObj sheetObj = new ExcelSheetObj(sheet);
            ExcelSheetSpan sheetSpan =sheetSpanMap.get(sheet.getsheetid());
            sheetObj.setMaxRow(sheetSpan.getMaxRow());
            sheetObj.setMaxCol(sheetSpan.getMaxCol());
            sheetObj.setTotalCellNum(sheetSpan.getTotalCellNum());
            sheetObjList.add(sheetObj);
        }
        return sheetObjList;
    }
    
	public Map<String, Object> loadExcelInfo5(Integer tabId, Boolean notActiveTabFlag, Integer throttleOfBigFile,
			Integer size) {
		
    	 ExcelSheet excelSheetTemplate = excelSheetService.load(tabId);
    	 ExcelSheetSpan sheetSpan = excelSheetService.getSheetSpan(excelSheetTemplate);
    	
//    	 Integer totalCellNum = sheetSpan.getTotalCellNum();
    	 Map<String,Object> results = new HashMap<String,Object>();
/*     	 if(totalCellNum <= throttleOfBigFile){
    		results = doLoadSheet(tabId, notActiveTabFlag, size, true, false);
    		results.put("loadWay", "tab");
    	 }else{
    		results = doLoadSheetRowCol(tabId, notActiveTabFlag, size);
    		results.put("loadWay", "rowcol");
    	 }*/
 		 results = doLoadSheet(tabId, notActiveTabFlag, size, true, false);
	 	 results.put("loadWay", "tab");
	     return results;
	}
	
	
	private Map<String, Object> doLoadSheet(Integer tabId, Boolean notActiveTabFlag, Integer size, boolean loadNonDataCell, boolean withRawData) {
		ExcelSheet excelSheet = excelSheetService.load(tabId);
		Integer nextStartCellId = null;
		List<ExcelCell> cells;
		cells = excelCellService.loadTabCellOnDemand(excelSheet, 0,size, null, true);
		List<ExcelCell> tabNonDataCellOfStyle = new ArrayList<>() ;
        
        if(loadNonDataCell)
            tabNonDataCellOfStyle = excelCellService.loadTabNonDataCellOfStyle(excelSheet.getsheetid(), excelSheet.getsheetcelltable());
        
        if (cells.size() >= size){
            nextStartCellId = cells.get(cells.size() - 1).getCellid();
        }
        
        List<ExcelCell> tempCells = new ArrayList<ExcelCell>(cells.size() + tabNonDataCellOfStyle.size());
        tempCells.addAll(cells);
        cells.clear();
        tempCells.addAll(tabNonDataCellOfStyle);
        tabNonDataCellOfStyle.clear();
        cells = tempCells;
        
        // ok, now we need load data ...
        Map<String, Object> messages = Maps.newHashMap();
        int floatingTotal = (int)excelElementService.getCountBySheetElement(tabId);
        messages.put("floatingTotal", floatingTotal);
        // set this tab as active ...
        if(false == notActiveTabFlag){
            excelSheetService.activeTab(excelSheet);
        }
        List<Object> transformedCells = tranCells(cells);
        Integer totalSize = excelCellService.getTabCellCount(excelSheet);
        
        Map<String, Object> results = Maps.newHashMap();
        results.put("success", true);
        results.put("total", totalSize);
        results.put("results", transformedCells);
        results.put("startCellId", nextStartCellId);
        results.put("message", messages);
        return results;
	}	
	
	private Map<String, Object> doLoadSheetRowCol(Integer tabId, Boolean notActiveTabFlag, Integer size) {
		ExcelSheet excelSheet = excelSheetService.load(tabId);
		Integer nextStartCellId = null;
		List<ExcelCell> cells;
		cells = excelSheetService.loadTabCornerCalCellOnDemand(excelSheet, 0,size, null, true);
		
		return null;
	}

	@Override
	public Map<String, Object> loadCellOnDemand3(Integer fileId, Integer sheetId, Integer startCellId, Integer size,
			Boolean skipCal) {
        List<ExcelCell> cells;
        Integer nextStartCellId = 0;
        	ExcelSheet sheet = excelSheetService.load(sheetId);
            if(skipCal)
                cells = excelCellService.loadTabCellOfRawDataOnDemand(sheet,startCellId, size,fileId, skipCal);
            else
                cells = excelCellService.loadTabCellOnDemand(sheet, startCellId, size, fileId, skipCal);
            if (cells.size() == size)
                nextStartCellId = cells.get(cells.size() - 1).getCellid();
        
        List<Object> transformedCells = tranCells(cells);
        Map<String, Object> results = Maps.newHashMap();
        results.put("success", true);
        results.put("total", transformedCells.size());
        results.put("results", transformedCells);
        results.put("startCellId", nextStartCellId);
        return results;
	}

	@Override
	public Map<String, Object> loadRange3(String range, Integer nextCellId, Integer limit, boolean withRawData) {
        Map<String, Object> results = Maps.newHashMap();
        List<List<Integer>> rangeParams = ExcelJsonUtil.fromJson(range,new TypeReference<List<List<Integer>>>(){});
        Integer lastCellId = null;
        List<Object> transformedCells = Collections.emptyList();
        if(rangeParams.size() > 0) {
            List<SheetCellRangeCondition> rangeConditions = Lists.transform(rangeParams, new Function<List<Integer>,SheetCellRangeCondition>(){
                public SheetCellRangeCondition apply(List<Integer> input) {
                    Integer tabId = input.get(0);
                    Integer startRow =  input.get(1);
                    Integer startCol = input.get(2);
                    Integer endRow =  input.get(3);
                    Integer endCol =input.get(4);
                    return new SheetCellRangeCondition(tabId, startRow, startCol, endRow, endCol);
                }
                
            });
            List<ExcelCell> cells;
/*            if(withRawData) {
            	cells = excelCellService.getCellOfRawDataByRanges(rangeConditions, nextCellId, limit);
            }
            else {
            	
            }*/
            cells = excelCellService.getCellByRanges(rangeConditions, nextCellId, limit);
//            System.out.println("loadRange3:"+cells.size());
            if(cells.size()>0){
                transformedCells = tranCells(cells);
                if(cells.size() == limit){
                    lastCellId= cells.get(cells.size() -1).getCellid();
                }
            }
        }
        results.put("results", transformedCells);
        results.put("nextCellId", lastCellId);
        results.put("success", true);
        return results;
	}


}
